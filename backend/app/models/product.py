from sqlalchemy import BigInteger, Integer, String, Text, text
from sqlalchemy.orm import Mapped, mapped_column
import time

from app.db.base import Base


class Product(Base):
    __tablename__ = "products"

    id: Mapped[int] = mapped_column(Integer, primary_key=True)
    category_id: Mapped[int] = mapped_column(Integer, index=True, nullable=False)

    title: Mapped[str] = mapped_column(String(255), nullable=False)
    description: Mapped[str] = mapped_column(Text, nullable=False)

    price_rub: Mapped[int] = mapped_column(Integer, nullable=False)
    rating: Mapped[float] = mapped_column(nullable=False, default=0.0)
    discount_percent: Mapped[int] = mapped_column(Integer, nullable=False, default=0)

    created_at: Mapped[int] = mapped_column(
        BigInteger,
        nullable=False,
        default=lambda: int(time.time() * 1000),
        server_default=text("(extract(epoch from now()) * 1000)::bigint"),
    )

    image_url: Mapped[str | None] = mapped_column(String(1024), nullable=True)