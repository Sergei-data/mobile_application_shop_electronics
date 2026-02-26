from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession

from app.models.product import Product


SEED_PRODUCTS = [
    dict(
        category_id=1,
        title="Смартфон Pixel 8",
        price_rub=69990,
        description="Компактный флагман, 128 ГБ.",
        rating=4.8,
        discount_percent=10,
        image_url="https://.../pixel8.png",
    ),
    dict(
        category_id=2,
        title="Ноутбук ASUS VivoBook 15",
        price_rub=55990,
        description='15.6", 16 ГБ RAM, 512 ГБ SSD.',
        rating=4.4,
        discount_percent=0,
        image_url="https://.../vivobook.png",
    ),
    dict(
        category_id=3,
        title="Наушники Sony WH-1000XM5",
        price_rub=34990,
        description="Шумоподавление, Bluetooth.",
        rating=4.9,
        discount_percent=5,
        image_url="https://.../sony.png",
    ),
    dict(
        category_id=4,
        title='Телевизор LG 55"',
        price_rub=62990,
        description="4K, WebOS, 120 Гц.",
        rating=4.6,
        discount_percent=20,
        image_url="https://.../lg55.png",
    ),
]


async def seed_products(session: AsyncSession) -> int:
    # чтобы не дублировать — проверяем, есть ли уже хоть один товар
    exists = await session.execute(select(Product.id).limit(1))
    if exists.scalar_one_or_none() is not None:
        return 0

    session.add_all([Product(**p) for p in SEED_PRODUCTS])
    await session.commit()
    return len(SEED_PRODUCTS)