from sqlalchemy import select, text
from sqlalchemy.ext.asyncio import AsyncSession
from app.models.product import Product
import os


PUBLIC_S3_BASE = os.getenv("PUBLIC_S3_BASE", "http://localhost:9000")
BUCKET = os.getenv("S3_BUCKET", "product-images")


def s3_url(key: str) -> str:
    key = key.lstrip("/")
    return f"{PUBLIC_S3_BASE}/{BUCKET}/{key}"


def build_pricing(price_rub: int, stock_qty: int) -> dict:
    return {
        "stock_qty": stock_qty,
        "cost_price_rub": int(price_rub * 0.72),
        "market_price_rub": int(price_rub * 1.05),
    }


SEED_PRODUCTS = [
    # category_id=1 -> Смартфоны
    dict(
        category_id=1,
        title="Смартфон Pixel 8",
        price_rub=69990,
        description="Компактный смартфон Google, 128 ГБ, OLED.",
        rating=4.8,
        discount_percent=10,
        reviews_count=124,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(69990, 14),
    ),
    dict(
        category_id=1,
        title="Смартфон Samsung Galaxy S24",
        price_rub=84990,
        description="Флагманский смартфон Samsung с AMOLED-дисплеем.",
        rating=4.9,
        discount_percent=7,
        reviews_count=182,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(84990, 9),
    ),
    dict(
        category_id=1,
        title="Смартфон iPhone 15",
        price_rub=91990,
        description="Популярный смартфон Apple, 128 ГБ.",
        rating=4.9,
        discount_percent=5,
        reviews_count=210,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(91990, 6),
    ),
    dict(
        category_id=1,
        title="Смартфон Xiaomi 14",
        price_rub=64990,
        description="Производительный смартфон Xiaomi для повседневного использования.",
        rating=4.7,
        discount_percent=12,
        reviews_count=97,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(64990, 18),
    ),
    dict(
        category_id=1,
        title="Смартфон Redmi Note 13 Pro",
        price_rub=32990,
        description="Доступный смартфон Redmi с хорошей камерой.",
        rating=4.5,
        discount_percent=15,
        reviews_count=144,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(32990, 25),
    ),
    dict(
        category_id=1,
        title="Смартфон HONOR 200",
        price_rub=45990,
        description="Смартфон HONOR с ярким экраном и быстрой зарядкой.",
        rating=4.6,
        discount_percent=8,
        reviews_count=76,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(45990, 17),
    ),
    dict(
        category_id=1,
        title="Смартфон realme GT 6",
        price_rub=53990,
        description="Мощный смартфон realme для игр и мультимедиа.",
        rating=4.6,
        discount_percent=11,
        reviews_count=69,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(53990, 11),
    ),
    dict(
        category_id=1,
        title="Смартфон TECNO Camon 30",
        price_rub=27990,
        description="Недорогой смартфон TECNO для повседневных задач.",
        rating=4.3,
        discount_percent=18,
        reviews_count=41,
        image_url=s3_url("phones/phone1.png"),
        **build_pricing(27990, 29),
    ),

    # category_id=2 -> Ноутбуки
    dict(
        category_id=2,
        title="Ноутбук ASUS VivoBook 15",
        price_rub=55990,
        description='Ноутбук 15.6", 16 ГБ RAM, 512 ГБ SSD.',
        rating=4.4,
        discount_percent=0,
        reviews_count=88,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(55990, 13),
    ),
    dict(
        category_id=2,
        title="Ноутбук Lenovo IdeaPad Slim 5",
        price_rub=63990,
        description="Ноутбук Lenovo для работы и учебы, IPS, SSD.",
        rating=4.6,
        discount_percent=6,
        reviews_count=111,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(63990, 8),
    ),
    dict(
        category_id=2,
        title="Ноутбук HP Pavilion 14",
        price_rub=68990,
        description="Компактный ноутбук HP с хорошей автономностью.",
        rating=4.5,
        discount_percent=9,
        reviews_count=93,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(68990, 10),
    ),
    dict(
        category_id=2,
        title="Ноутбук Acer Aspire 5",
        price_rub=57990,
        description="Универсальный ноутбук Acer для дома и офиса.",
        rating=4.4,
        discount_percent=7,
        reviews_count=84,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(57990, 15),
    ),
    dict(
        category_id=2,
        title='Ноутбук Apple MacBook Air 13"',
        price_rub=119990,
        description="Легкий ноутбук Apple на чипе M-серии.",
        rating=4.9,
        discount_percent=3,
        reviews_count=156,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(119990, 5),
    ),
    dict(
        category_id=2,
        title="Ноутбук MSI Modern 15",
        price_rub=74990,
        description="Современный ноутбук MSI для работы и мультимедиа.",
        rating=4.5,
        discount_percent=10,
        reviews_count=57,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(74990, 12),
    ),
    dict(
        category_id=2,
        title="Ноутбук ASUS TUF Gaming A15",
        price_rub=94990,
        description="Игровой ноутбук ASUS TUF с мощной графикой.",
        rating=4.8,
        discount_percent=5,
        reviews_count=72,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(94990, 7),
    ),
    dict(
        category_id=2,
        title="Ноутбук Huawei MateBook D 16",
        price_rub=79990,
        description="Ноутбук Huawei с большим экраном и металлическим корпусом.",
        rating=4.7,
        discount_percent=4,
        reviews_count=64,
        image_url=s3_url("laptops/laptop1.jpg"),
        **build_pricing(79990, 9),
    ),

    # category_id=3 -> Аудио / наушники
    dict(
        category_id=3,
        title="Наушники Sony WH-1000XM5",
        price_rub=34990,
        description="Беспроводные наушники Sony с шумоподавлением.",
        rating=4.9,
        discount_percent=5,
        reviews_count=170,
        image_url=s3_url("audio/audio1.jpeg"),
        **build_pricing(34990, 16),
    ),
    dict(
        category_id=3,
        title="Наушники Apple AirPods Pro 2",
        price_rub=27990,
        description="Популярные TWS-наушники Apple с ANC.",
        rating=4.8,
        discount_percent=4,
        reviews_count=205,
        image_url=s3_url("audio/audio1.jpeg"),
        **build_pricing(27990, 8),
    ),
    dict(
        category_id=3,
        title="Наушники JBL Tune 770NC",
        price_rub=11990,
        description="Полноразмерные наушники JBL для музыки и поездок.",
        rating=4.6,
        discount_percent=14,
        reviews_count=95,
        image_url=s3_url("audio/audio1.jpeg"),
        **build_pricing(11990, 24),
    ),
    dict(
        category_id=3,
        title="Наушники Marshall Major V",
        price_rub=18990,
        description="Стильные Bluetooth-наушники Marshall.",
        rating=4.7,
        discount_percent=9,
        reviews_count=61,
        image_url=s3_url("audio/audio1.jpeg"),
        **build_pricing(18990, 14),
    ),
    dict(
        category_id=3,
        title="Наушники Anker Soundcore Liberty 4",
        price_rub=13990,
        description="TWS-наушники Soundcore с хорошим звучанием.",
        rating=4.5,
        discount_percent=16,
        reviews_count=58,
        image_url=s3_url("audio/audio1.jpeg"),
        **build_pricing(13990, 18),
    ),
    dict(
        category_id=3,
        title="Колонка JBL Charge 5",
        price_rub=14990,
        description="Портативная аудио-колонка JBL с мощным звуком.",
        rating=4.8,
        discount_percent=12,
        reviews_count=149,
        image_url=s3_url("audio/audio1.jpeg"),
        **build_pricing(14990, 20),
    ),
    dict(
        category_id=3,
        title="Колонка Яндекс Станция Мини",
        price_rub=7990,
        description="Умная колонка с голосовым помощником.",
        rating=4.4,
        discount_percent=11,
        reviews_count=132,
        image_url=s3_url("audio/audio1.jpeg"),
        **build_pricing(7990, 26),
    ),

    # category_id=4 -> Телевизоры
    dict(
        category_id=4,
        title='Телевизор LG 55" 4K',
        price_rub=62990,
        description="Телевизор LG 4K, WebOS, 120 Гц.",
        rating=4.6,
        discount_percent=20,
        reviews_count=83,
        image_url=s3_url("tv/tv1.jpg"),
        **build_pricing(62990, 10),
    ),
    dict(
        category_id=4,
        title='Телевизор Samsung 50" Crystal UHD',
        price_rub=58990,
        description="Телевизор Samsung с 4K-панелью и HDR.",
        rating=4.7,
        discount_percent=13,
        reviews_count=91,
        image_url=s3_url("tv/tv1.jpg"),
        **build_pricing(58990, 11),
    ),
    dict(
        category_id=4,
        title='Телевизор Xiaomi TV A Pro 43"',
        price_rub=34990,
        description="Доступный телевизор Xiaomi для дома.",
        rating=4.3,
        discount_percent=17,
        reviews_count=55,
        image_url=s3_url("tv/tv1.jpg"),
        **build_pricing(34990, 16),
    ),
    dict(
        category_id=4,
        title='Телевизор TCL 55" QLED',
        price_rub=51990,
        description="Телевизор TCL QLED с насыщенными цветами.",
        rating=4.5,
        discount_percent=15,
        reviews_count=48,
        image_url=s3_url("tv/tv1.jpg"),
        **build_pricing(51990, 13),
    ),
    dict(
        category_id=4,
        title='Телевизор Hisense 65" 4K Smart TV',
        price_rub=69990,
        description="Большой телевизор Hisense для гостиной.",
        rating=4.6,
        discount_percent=10,
        reviews_count=39,
        image_url=s3_url("tv/tv1.jpg"),
        **build_pricing(69990, 8),
    ),
    dict(
        category_id=4,
        title='Телевизор Philips 50" Ambilight',
        price_rub=60990,
        description="Телевизор Philips с Ambilight-подсветкой.",
        rating=4.7,
        discount_percent=9,
        reviews_count=44,
        image_url=s3_url("tv/tv1.jpg"),
        **build_pricing(60990, 9),
    ),
    dict(
        category_id=4,
        title='Телевизор Haier 43" Smart TV',
        price_rub=29990,
        description="Недорогой телевизор Haier для кухни или спальни.",
        rating=4.2,
        discount_percent=19,
        reviews_count=36,
        image_url=s3_url("tv/tv1.jpg"),
        **build_pricing(29990, 19),
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


async def seed_categories(session):
    data = [
        (1, "Смартфоны"),
        (2, "Ноутбуки"),
        (3, "Аудио"),
        (4, "Телевизоры"),
    ]

    for cid, title in data:
        await session.execute(
            text(
                """
                insert into categories (id, title)
                values (:id, :title)
                on conflict (id) do update set title = excluded.title
                """
            ),
            {"id": cid, "title": title},
        )

    await session.commit()
    return len(data)