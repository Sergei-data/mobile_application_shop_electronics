from fastapi import APIRouter, Depends
from app.api.deps import get_current_principal
from app.core.security import Principal

router = APIRouter()

@router.get("/me")
async def me(p: Principal = Depends(get_current_principal)):
    return {
        "sub": p.sub,
        "email": p.email,
        "username": p.username,
        "realm_roles": p.realm_roles,
        "client_roles": p.client_roles,
    }