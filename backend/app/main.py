from fastapi import FastAPI
from app.db.session import async_session_maker
from seed.seed import seed_products
from app.api.v1.router import api_router
from app.core.config import settings
from sqlalchemy.exc import ProgrammingError
from prometheus_fastapi_instrumentator import Instrumentator

app = FastAPI(title="Shop API")
app.include_router(api_router, prefix="/api/v1")
Instrumentator().instrument(app).expose(app, include_in_schema=False, endpoint="/metrics")

@app.on_event("startup")
async def on_startup():
    if settings.ENV == "dev":
        try:
            async with async_session_maker() as session:
                await seed_products(session)
        except ProgrammingError:
            return