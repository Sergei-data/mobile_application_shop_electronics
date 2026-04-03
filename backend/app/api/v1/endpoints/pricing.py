from fastapi import APIRouter, Depends
from sqlalchemy import func, select
from sqlalchemy.ext.asyncio import AsyncSession

from app.api.deps import get_db
from app.models.order import Order, OrderItem
from app.models.product import Product
from app.schemas.pricing import (
    PricingPreviewRequest,
    PricingPreviewResponse,
    ProductPricingPreview,
)
from app.services.pricing import PricingInput, calculate_price

router = APIRouter(tags=["pricing"])


@router.post("/preview", response_model=PricingPreviewResponse)
async def preview_dynamic_prices(
    payload: PricingPreviewRequest,
    db: AsyncSession = Depends(get_db),
):
    now_ms_stmt = select(func.extract("epoch", func.now()) * 1000)
    now_ms_result = await db.execute(now_ms_stmt)
    now_ms = int(float(now_ms_result.scalar_one()))

    period_start_ms = now_ms - payload.period_days * 24 * 60 * 60 * 1000

    products_stmt = (
        select(Product)
        .order_by(Product.id)
        .limit(payload.limit)
    )
    products_result = await db.execute(products_stmt)
    products = products_result.scalars().all()

    items: list[ProductPricingPreview] = []

    for product in products:
        sold_stmt = (
            select(func.coalesce(func.sum(OrderItem.qty), 0))
            .select_from(OrderItem)
            .join(Order, Order.id == OrderItem.order_id)
            .where(OrderItem.product_id == product.id)
            .where(Order.created_at >= period_start_ms)
            .where(Order.status.in_(["paid", "completed", "delivered"]))
        )

        sold_result = await db.execute(sold_stmt)
        sold_for_period = int(sold_result.scalar_one() or 0)

        pricing_result = calculate_price(
            PricingInput(
                base_price_rub=product.price_rub,
                current_discount_percent=product.discount_percent,
                stock_qty=product.stock_qty,
                sold_for_period=sold_for_period,
                period_days=payload.period_days,
                target_sellout_days=product.target_sellout_days,
                sensitivity_step_percent=payload.sensitivity_step_percent,
                cost_price_rub=product.cost_price_rub,
                min_margin=payload.min_margin,
                market_price_rub=product.market_price_rub,
                max_market_deviation=payload.max_market_deviation,
                max_discount_percent=product.max_discount_percent,
            )
        )

        items.append(
            ProductPricingPreview(
                product_id=product.id,
                title=product.title,
                base_price_rub=pricing_result.base_price_rub,
                current_discount_percent=pricing_result.current_discount_percent,
                new_discount_percent=pricing_result.new_discount_percent,
                current_price_rub=pricing_result.current_price_rub,
                new_price_rub=pricing_result.new_price_rub,
                stock_qty=pricing_result.stock_qty,
                sold_for_period=pricing_result.sold_for_period,
                sales_velocity=pricing_result.sales_velocity,
                predicted_days_to_sell_out=pricing_result.predicted_days_to_sell_out,
                target_sellout_days=pricing_result.target_sellout_days,
                planned_qty_for_period=pricing_result.planned_qty_for_period,
                plan_completion_ratio=pricing_result.plan_completion_ratio,
                min_price_rub=pricing_result.min_price_rub,
                max_price_rub=pricing_result.max_price_rub,
            )
        )

    return PricingPreviewResponse(items=items)