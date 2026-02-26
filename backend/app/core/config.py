from pydantic_settings import BaseSettings, SettingsConfigDict
import os


class Settings(BaseSettings):
    # В docker compose обычно так:
    DATABASE_URL: str = "postgresql+asyncpg://postgres:postgres@db:5432/app"

    # для будущей авторизации
    JWT_ISSUER: str | None = None
    JWT_AUDIENCE: str | None = None
    KEYCLOAK_URL: str | None = None

    model_config = SettingsConfigDict(env_file=".env", extra="ignore")
    ENV: str = os.getenv("ENV", "dev")


settings = Settings()