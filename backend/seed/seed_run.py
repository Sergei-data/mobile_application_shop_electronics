import asyncio
from seed.seed import seed_categories, seed_products
from app.db.session import async_session_maker



async def main():
    async with async_session_maker() as session:
        n = await seed_products(session)
        print(f"seeded={n}")


async def main():
    async with async_session_maker() as session:
        c = await seed_categories(session)
        p = await seed_products(session)
        print(f"seeded_categories={c} seeded_products={p}")

if __name__ == "__main__":
    asyncio.run(main())