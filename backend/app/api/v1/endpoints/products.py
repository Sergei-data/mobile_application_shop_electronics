from fastapi import APIRouter, Depends, HTTPException, status
from sqlalchemy.ext.asyncio import AsyncSession
from app.api.deps import get_db

from app.crud.product import (
    create_product,
    delete_product,
    get_product,
    list_products,
    update_product,
)
from app.schemas.product import ProductCreate, ProductRead, ProductUpdate

router = APIRouter(tags=["products"])


@router.get("", response_model=list[ProductRead])
async def get_products(
    limit: int = 50,
    offset: int = 0,
    db: AsyncSession = Depends(get_db),
):
    limit = max(1, min(limit, 200))
    offset = max(0, offset)
    return await list_products(db, limit=limit, offset=offset)


@router.get("/{product_id}", response_model=ProductRead)
async def get_product_by_id(
    product_id: int,
    db: AsyncSession = Depends(get_db),
):
    obj = await get_product(db, product_id)
    if not obj:
        raise HTTPException(status_code=404, detail="Product not found")
    return obj


@router.post("", response_model=ProductRead, status_code=status.HTTP_201_CREATED)
async def create_product_endpoint(
    payload: ProductCreate,
    db: AsyncSession = Depends(get_db),
):
    return await create_product(db, payload)


@router.put("/{product_id}", response_model=ProductRead)
async def put_product(
    product_id: int,
    payload: ProductUpdate,
    db: AsyncSession = Depends(get_db),
):
    obj = await get_product(db, product_id)
    if not obj:
        raise HTTPException(status_code=404, detail="Product not found")
    return await update_product(db, obj, payload)


@router.patch("/{product_id}", response_model=ProductRead)
async def patch_product(
    product_id: int,
    payload: ProductUpdate,
    db: AsyncSession = Depends(get_db),
):
    obj = await get_product(db, product_id)
    if not obj:
        raise HTTPException(status_code=404, detail="Product not found")
    return await update_product(db, obj, payload)


@router.delete("/{product_id}", status_code=status.HTTP_204_NO_CONTENT)
async def delete_product_endpoint(
    product_id: int,
    db: AsyncSession = Depends(get_db),
):
    obj = await get_product(db, product_id)
    if not obj:
        raise HTTPException(status_code=404, detail="Product not found")
    await delete_product(db, obj)
    return None