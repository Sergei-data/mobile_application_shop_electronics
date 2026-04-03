from contextlib import asynccontextmanager

from fastapi import FastAPI
from prometheus_fastapi_instrumentator import Instrumentator
from sqlalchemy.exc import ProgrammingError

from app.api.v1.router import api_router
from app.core.config import settings
from app.core.rabbitmq import rabbitmq_manager
from app.db.session import async_session_maker
from seed.seed import seed_products


@asynccontextmanager
async def lifespan(app: FastAPI):
    await rabbitmq_manager.connect()

    if settings.ENV == "dev" and settings.SEED_ON_STARTUP:
        try:
            async with async_session_maker() as session:
                await seed_products(session)
        except ProgrammingError:
            pass

    yield

    await rabbitmq_manager.close()


app = FastAPI(title="Shop API", lifespan=lifespan)
app.include_router(api_router, prefix="/api/v1")
Instrumentator().instrument(app).expose(app, include_in_schema=False, endpoint="/metrics")