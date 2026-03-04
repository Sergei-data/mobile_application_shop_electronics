from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel
from sqlalchemy import text
from sqlalchemy.ext.asyncio import AsyncSession

try:
    from app.db.session import get_db
except ImportError:
    from app.db.session import get_session as get_db


router = APIRouter()


class CategoryRead(BaseModel):
    id: int
    title: str


@router.get("", response_model=list[CategoryRead])
async def get_categories(session: AsyncSession = Depends(get_db)):
    res = await session.execute(text("select id, title from categories order by id"))
    rows = res.mappings().all()
    return [CategoryRead(**r) for r in rows]


@router.get("/{category_id}", response_model=CategoryRead)
async def get_category_by_id(category_id: int, session: AsyncSession = Depends(get_db)):
    res = await session.execute(
        text("select id, title from categories where id = :id"),
        {"id": category_id},
    )
    row = res.mappings().first()
    if not row:
        raise HTTPException(status_code=404, detail="Category not found")
    return CategoryRead(**row)