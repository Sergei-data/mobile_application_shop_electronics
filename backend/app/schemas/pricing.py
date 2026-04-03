from pydantic import BaseModel, Field


class PricingPreviewRequest(BaseModel):
    period_days: int = Field(30, ge=1, le=365)
    sensitivity_step_percent: int = Field(5, ge=1, le=50)
    min_margin: float = Field(0.05, ge=0.0, le=1.0)
    max_market_deviation: float = Field(0.10, ge=0.0, le=1.0)
    limit: int = Field(100, ge=1, le=1000)


class ProductPricingPreview(BaseModel):
    product_id: int
    title: str

    base_price_rub: int
    current_discount_percent: int
    new_discount_percent: int

    current_price_rub: int
    new_price_rub: int

    stock_qty: int
    sold_for_period: int

    sales_velocity: float
    predicted_days_to_sell_out: float
    target_sellout_days: int

    planned_qty_for_period: float
    plan_completion_ratio: float

    min_price_rub: int
    max_price_rub: int


class PricingPreviewResponse(BaseModel):
    items: list[ProductPricingPreview]