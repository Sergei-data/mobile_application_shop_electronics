from fastapi import FastAPI
from app.db.session import async_session_maker
from app.db.seed import seed_products
from app.api.v1.router import api_router
from app.core.config import settings

app = FastAPI(title="Diplom API")
app.include_router(api_router, prefix="/api/v1")


@app.on_event("startup")
async def on_startup():
    if settings.ENV == "dev":
        async with async_session_maker() as session:
            await seed_products(session)