from typing import Sequence

from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.product import Product
from app.schemas.product import ProductCreate, ProductUpdate


async def list_products(
    session: AsyncSession,
    limit: int = 50,
    offset: int = 0,
) -> Sequence[Product]:
    stmt = select(Product).order_by(Product.id).limit(limit).offset(offset)
    res = await session.execute(stmt)
    return res.scalars().all()


async def get_product(session: AsyncSession, product_id: int) -> Product | None:
    res = await session.execute(select(Product).where(Product.id == product_id))
    return res.scalars().first()


async def create_product(session: AsyncSession, data: ProductCreate) -> Product:
    obj = Product(**data.model_dump(exclude_unset=True))
    session.add(obj)
    await session.commit()
    await session.refresh(obj)
    return obj


async def update_product(session: AsyncSession, obj: Product, data: ProductUpdate) -> Product:
    patch = data.model_dump(exclude_unset=True)
    for k, v in patch.items():
        setattr(obj, k, v)

    session.add(obj)
    await session.commit()
    await session.refresh(obj)
    return obj


async def delete_product(session: AsyncSession, obj: Product) -> None:
    await session.delete(obj)
    await session.commit()