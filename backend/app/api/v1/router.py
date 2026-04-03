from fastapi import APIRouter
from app.api.v1.endpoints.health import router as health_router
from app.api.v1.endpoints.products import router as products_router
from app.api.v1.endpoints.categories import router as categories_router
from app.api.v1.endpoints.auth import router as auth_router
from app.api.v1.endpoints.pricing import router as pricing_router
from app.api.v1.endpoints import broker_demo

api_router = APIRouter()

api_router.include_router(health_router, prefix="/health", tags=["health"])
api_router.include_router(products_router, prefix="/products", tags=["products"])
api_router.include_router(categories_router, prefix="/categories", tags=["categories"])
api_router.include_router(auth_router, prefix="/auth", tags=["auth"])
api_router.include_router(broker_demo.router, prefix="/broker-demo", tags=["broker-demo"])
api_router.include_router(pricing_router, prefix="/pricing", tags=["pricing"])