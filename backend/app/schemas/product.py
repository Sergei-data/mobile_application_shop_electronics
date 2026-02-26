from pydantic import BaseModel


class ProductOut(BaseModel):
    id: int
    category_id: int
    title: str
    description: str
    price_rub: int
    rating: float
    discount_percent: int
    created_at: int
    image_url: str | None = None

    class Config:
        from_attributes = True