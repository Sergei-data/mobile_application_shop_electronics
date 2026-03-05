from __future__ import annotations

import asyncio
import time
from dataclasses import dataclass
from typing import Any

import httpx
from fastapi import HTTPException, status
from jose import jwt
from jose.exceptions import JWTError

from app.core.config import settings


@dataclass(frozen=True, slots=True)
class Principal:
    sub: str
    email: str | None
    username: str | None
    realm_roles: tuple[str, ...]
    client_roles: tuple[str, ...]
    claims: dict[str, Any]


class _JWKSCache:
    def __init__(self) -> None:
        self._lock = asyncio.Lock()
        self._jwks: dict[str, Any] | None = None
        self._expires_at: float = 0.0

    async def get(self) -> dict[str, Any]:
        now = time.time()
        if self._jwks is not None and now < self._expires_at:
            return self._jwks

        async with self._lock:
            now = time.time()
            if self._jwks is not None and now < self._expires_at:
                return self._jwks

            jwks_uri = _jwks_uri()
            timeout = httpx.Timeout(settings.HTTP_TIMEOUT_SECONDS)

            async with httpx.AsyncClient(timeout=timeout) as client:
                r = await client.get(jwks_uri)
                r.raise_for_status()
                self._jwks = r.json()

            self._expires_at = now + settings.JWKS_CACHE_TTL_SECONDS
            return self._jwks


_jwks_cache = _JWKSCache()


def _jwks_uri() -> str:
    if not settings.KEYCLOAK_INTERNAL_BASE_URL:
        raise RuntimeError("KEYCLOAK_INTERNAL_BASE_URL is not configured")
    base = settings.KEYCLOAK_INTERNAL_BASE_URL.rstrip("/")
    realm = settings.KEYCLOAK_REALM
    return f"{base}/realms/{realm}/protocol/openid-connect/certs"


def _extract_roles(claims: dict[str, Any]) -> tuple[tuple[str, ...], tuple[str, ...]]:
    realm_roles: list[str] = []
    client_roles: list[str] = []

    ra = claims.get("realm_access") or {}
    rr = ra.get("roles") or []
    if isinstance(rr, list):
        realm_roles.extend([str(x) for x in rr])

    rsrc = claims.get("resource_access") or {}
    if isinstance(rsrc, dict):
        for _client, payload in rsrc.items():
            if isinstance(payload, dict):
                roles = payload.get("roles") or []
                if isinstance(roles, list):
                    client_roles.extend([str(x) for x in roles])

    return tuple(sorted(set(realm_roles))), tuple(sorted(set(client_roles)))


def _ensure_issuer_allowed(claims: dict[str, Any]) -> None:
    iss = (claims.get("iss") or "").rstrip("/")
    allowed = set(settings.KEYCLOAK_ISSUER_URLS)
    if not allowed:
        raise RuntimeError("KEYCLOAK_ISSUER_URLS is empty (configure at least one issuer)")
    if iss not in allowed:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail=f"Invalid issuer: {iss}",
        )


async def verify_bearer_token(token: str) -> Principal:
    try:
        headers = jwt.get_unverified_header(token)
        claims_unverified = jwt.get_unverified_claims(token)
    except JWTError as e:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid token") from e

    _ensure_issuer_allowed(claims_unverified)

    kid = headers.get("kid")
    if not kid:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Missing kid in token header")

    jwks = await _jwks_cache.get()
    keys = jwks.get("keys") or []
    key_dict = next((k for k in keys if k.get("kid") == kid), None)

    # если ключи провернулись — обновим кэш и попробуем ещё раз
    if key_dict is None:
        _jwks_cache._expires_at = 0.0  # dev-friendly invalidate
        jwks = await _jwks_cache.get()
        keys = jwks.get("keys") or []
        key_dict = next((k for k in keys if k.get("kid") == kid), None)

    if key_dict is None:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Signing key not found")

    # aud проверяем только если задан
    aud = settings.JWT_AUDIENCE or None
    options = {"verify_aud": bool(aud), "verify_iss": False}  # issuer уже проверили allowlist'ом

    try:
        claims = jwt.decode(
            token,
            key_dict,
            algorithms=[settings.JWT_ALGORITHMS],
            audience=aud,
            options=options,
        )
    except JWTError as e:
        raise HTTPException(status_code=status.HTTP_401_UNAUTHORIZED, detail="Invalid or expired token") from e

    realm_roles, client_roles = _extract_roles(claims)

    return Principal(
        sub=str(claims.get("sub")),
        email=claims.get("email"),
        username=claims.get("preferred_username") or claims.get("name"),
        realm_roles=realm_roles,
        client_roles=client_roles,
        claims=claims,
    )


def require_roles(*roles: str):
    wanted = set(roles)

    async def _checker(p: Principal) -> Principal:
        have = set(p.realm_roles) | set(p.client_roles)
        if wanted and not (have & wanted):
            raise HTTPException(status_code=status.HTTP_403_FORBIDDEN, detail="Insufficient permissions")
        return p

    return _checker