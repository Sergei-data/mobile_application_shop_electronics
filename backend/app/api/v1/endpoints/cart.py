from fastapi import APIRouter

router = APIRouter()

# Позже тут будет user-cart после Keycloak/JWT
@router.get("")
async def get_cart_stub():
    return {"items": [], "total_rub": 0}