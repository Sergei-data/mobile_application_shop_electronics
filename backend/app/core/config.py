from pydantic_settings import BaseSettings, SettingsConfigDict
from pydantic import field_validator
import os

class Settings(BaseSettings):
    DATABASE_URL: str = "postgresql+asyncpg://postgres:postgres@db:5432/app"
    SEED_ON_STARTUP: bool = False

    S3_PUBLIC_BASE_URL: str = "http://localhost:9000"
    S3_BUCKET_NAME: str = "product-images"

    # Keycloak / OIDC
    KEYCLOAK_REALM: str = "shop"
    # можно несколько issuer'ов для dev: localhost + 10.0.2.2
    KEYCLOAK_ISSUER_URLS: list[str] = []
    # internal base url (docker network) для JWKS
    KEYCLOAK_INTERNAL_BASE_URL: str | None = None

    JWT_AUDIENCE: str | None = None
    JWT_ALGORITHMS: str = "RS256"
    JWKS_CACHE_TTL_SECONDS: int = 300
    HTTP_TIMEOUT_SECONDS: int = 5

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")
    ENV: str = os.getenv("ENV", "dev")

    @field_validator("KEYCLOAK_ISSUER_URLS", mode="before")
    @classmethod
    def _split_issuers(cls, v):
        if v is None:
            return []
        if isinstance(v, list):
            return [str(x).rstrip("/") for x in v]
        if isinstance(v, str):
            return [s.strip().rstrip("/") for s in v.split(",") if s.strip()]
        return []

settings = Settings()