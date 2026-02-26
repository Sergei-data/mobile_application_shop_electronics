import os

from sqlalchemy.ext.asyncio import AsyncSession, async_sessionmaker, create_async_engine

DATABASE_URL = os.getenv("DATABASE_URL", "")

# Engine
engine = create_async_engine(
    DATABASE_URL,
    future=True,
    echo=False,
)

# Session factory (то, что ты хочешь импортировать как async_session_maker)
async_session_maker = async_sessionmaker(
    bind=engine,
    class_=AsyncSession,
    expire_on_commit=False,
)