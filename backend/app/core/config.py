from pydantic_settings import BaseSettings, SettingsConfigDict
import os


class Settings(BaseSettings):
    DATABASE_URL: str = "postgresql+asyncpg://postgres:postgres@db:5432/app"
    SEED_ON_STARTUP: bool = False

    S3_PUBLIC_BASE_URL: str = "http://localhost:9000"
    S3_BUCKET_NAME: str = "product-images"

    # для будущей авторизации
    JWT_ISSUER: str | None = None
    JWT_AUDIENCE: str | None = None
    KEYCLOAK_URL: str | None = None

    # pydantic-settings конфиг
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    ENV: str = os.getenv("ENV", "dev")

    


settings = Settings()