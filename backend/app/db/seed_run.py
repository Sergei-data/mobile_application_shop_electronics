import asyncio

from app.db.session import async_session_maker
from app.db.seed import seed_products


async def main():
    async with async_session_maker() as session:
        n = await seed_products(session)
        print(f"seeded={n}")


if __name__ == "__main__":
    asyncio.run(main())