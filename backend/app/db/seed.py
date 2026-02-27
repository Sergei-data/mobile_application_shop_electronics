from sqlalchemy import select
from sqlalchemy.ext.asyncio import AsyncSession
from app.core.config import settings
from app.models.product import Product
import os




def s3_url(key: str) -> str:
    # ключ типа "phones/phone1.jpg"
    base = settings.S3_PUBLIC_BASE_URL.rstrip("/")
    bucket = settings.S3_BUCKET_NAME
    return f"{base}/{bucket}/{key}"


SEED_PRODUCTS = [
    dict(
        category_id=1,
        title="Смартфон Pixel 8",
        price_rub=69990,
        description="Компактный флагман, 128 ГБ.",
        rating=4.8,
        discount_percent=10,
        image_url=s3_url("phones/phone1.png")
    ),
    dict(
        category_id=2,
        title="Ноутбук ASUS VivoBook 15",
        price_rub=55990,
        description='15.6", 16 ГБ RAM, 512 ГБ SSD.',
        rating=4.4,
        discount_percent=0,
        image_url=s3_url("laptops/laptop1.jpg"),
    ),
    dict(
        category_id=3,
        title="Наушники Sony WH-1000XM5",
        price_rub=34990,
        description="Шумоподавление, Bluetooth.",
        rating=4.9,
        discount_percent=5,
        image_url=s3_url("audio/audio1.jpeg"),
    ),
    dict(
        category_id=4,
        title='Телевизор LG 55"',
        price_rub=62990,
        description="4K, WebOS, 120 Гц.",
        rating=4.6,
        discount_percent=20,
        image_url=s3_url("tv/tv1.jpg"),
    ),
]


async def seed_products(session: AsyncSession) -> int:
    exists = await session.execute(select(Product.id).limit(1))
    if exists.scalar_one_or_none() is not None:
        return 0

    clean = []
    for p in SEED_PRODUCTS:
        p = dict(p)
        if p.get("created_at") is None:
            p.pop("created_at", None)
        clean.append(Product(**p))

    session.add_all(clean)
    await session.commit()
    return len(clean)