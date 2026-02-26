from sqlalchemy import BigInteger, Float, Integer, String, Text, Index
from sqlalchemy.orm import Mapped, mapped_column
from sqlalchemy.sql import text

from app.db.base import Base


class Product(Base):
    __tablename__ = "products"

    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)

    category_id: Mapped[int] = mapped_column(Integer, index=True)

    title: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=False)

    price_rub: Mapped[int] = mapped_column(Integer, nullable=False)

    rating: Mapped[float] = mapped_column(Float, nullable=False, server_default=text("0"))
    discount_percent: Mapped[int] = mapped_column(Integer, nullable=False, server_default=text("0"))

    # epoch millis
    created_at: Mapped[int] = mapped_column(
        BigInteger,
        nullable=False,
        server_default=text("(extract(epoch from now()) * 1000)::bigint"),
    )

    image_url: Mapped[str | None] = mapped_column(String(1024), nullable=True)

    __table_args__ = (
        Index("ix_products_category_id", "category_id"),
    )