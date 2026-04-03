from typing import Optional
from pydantic import BaseModel, Field, ConfigDict


class ProductBase(BaseModel):
    category_id: int = Field(..., ge=1)
    title: str = Field(..., min_length=1, max_length=255)
    description: str = Field(..., min_length=1)

    price_rub: int = Field(..., ge=0)
    rating: float = Field(0, ge=0, le=5)
    discount_percent: int = Field(0, ge=0, le=100)

    stock_qty: int = Field(0, ge=0)
    cost_price_rub: int = Field(0, ge=0)
    market_price_rub: Optional[int] = Field(None, ge=0)

    target_sellout_days: int = Field(180, ge=1, le=3650)
    max_discount_percent: int = Field(30, ge=0, le=100)

    created_at: Optional[int] = None
    image_url: Optional[str] = None


class ProductCreate(ProductBase):
    pass


class ProductUpdate(BaseModel):
    category_id: Optional[int] = Field(None, ge=1)
    title: Optional[str] = Field(None, min_length=1, max_length=255)
    description: Optional[str] = Field(None, min_length=1)

    price_rub: Optional[int] = Field(None, ge=0)
    rating: Optional[float] = Field(None, ge=0, le=5)
    discount_percent: Optional[int] = Field(None, ge=0, le=100)

    stock_qty: Optional[int] = Field(None, ge=0)
    cost_price_rub: Optional[int] = Field(None, ge=0)
    market_price_rub: Optional[int] = Field(None, ge=0)

    target_sellout_days: Optional[int] = Field(None, ge=1, le=3650)
    max_discount_percent: Optional[int] = Field(None, ge=0, le=100)

    created_at: Optional[int] = None
    image_url: Optional[str] = None


class ProductRead(ProductBase):
    id: int

    model_config = ConfigDict(from_attributes=True)