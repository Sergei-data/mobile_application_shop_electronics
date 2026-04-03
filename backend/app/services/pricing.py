from dataclasses import dataclass


@dataclass
class PricingInput:
    base_price_rub: int
    current_discount_percent: int
    stock_qty: int
    sold_for_period: int
    period_days: int
    target_sellout_days: int
    sensitivity_step_percent: int
    cost_price_rub: int
    min_margin: float
    market_price_rub: int | None
    max_market_deviation: float
    max_discount_percent: int


@dataclass
class PricingResult:
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


def clamp(value: float, min_value: float, max_value: float) -> float:
    return max(min_value, min(value, max_value))


def price_with_discount(base_price_rub: int, discount_percent: int) -> int:
    return int(round(base_price_rub * (1 - discount_percent / 100.0)))


def discount_for_price(base_price_rub: int, target_price_rub: float) -> int:
    if base_price_rub <= 0:
        return 0
    discount = (1 - target_price_rub / base_price_rub) * 100
    return int(round(discount))


def calculate_price(payload: PricingInput) -> PricingResult:
    base_price = float(payload.base_price_rub)
    stock_qty = max(payload.stock_qty, 0)
    sold_for_period = max(payload.sold_for_period, 0)
    period_days = max(payload.period_days, 1)
    target_sellout_days = max(payload.target_sellout_days, 1)
    current_discount = max(0, min(payload.current_discount_percent, 100))
    max_discount = max(0, min(payload.max_discount_percent, 100))

    effective_cost_price = payload.cost_price_rub if payload.cost_price_rub > 0 else int(base_price * 0.7)
    min_price = effective_cost_price * (1 + payload.min_margin)

    if payload.market_price_rub is not None and payload.market_price_rub > 0:
        max_price = payload.market_price_rub * (1 + payload.max_market_deviation)
    else:
        max_price = base_price

    current_price = price_with_discount(int(round(base_price)), current_discount)

    planned_qty_for_period = stock_qty * (period_days / target_sellout_days)

    if stock_qty <= 0:
        sales_velocity = 0.0
        predicted_days = 0.0
        plan_completion_ratio = 1.0
        new_discount = current_discount
    else:
        if sold_for_period <= 0:
            sales_velocity = 0.0
            predicted_days = target_sellout_days * 3.0
            plan_completion_ratio = 0.0
        else:
            sales_velocity = sold_for_period / period_days
            predicted_days = stock_qty / sales_velocity if sales_velocity > 0 else target_sellout_days * 3.0
            plan_completion_ratio = sold_for_period / planned_qty_for_period if planned_qty_for_period > 0 else 1.0

        if plan_completion_ratio >= 1.0:
            new_discount = current_discount
        elif plan_completion_ratio >= 0.8:
            new_discount = current_discount + payload.sensitivity_step_percent
        elif plan_completion_ratio >= 0.5:
            new_discount = current_discount + payload.sensitivity_step_percent * 2
        else:
            new_discount = current_discount + payload.sensitivity_step_percent * 3

    new_discount = int(clamp(new_discount, 0, max_discount))

    raw_new_price = price_with_discount(int(round(base_price)), new_discount)
    final_new_price = int(round(clamp(raw_new_price, min_price, max_price)))

    adjusted_discount = discount_for_price(int(round(base_price)), final_new_price)
    adjusted_discount = int(clamp(adjusted_discount, 0, max_discount))

    return PricingResult(
        base_price_rub=int(round(base_price)),
        current_discount_percent=current_discount,
        new_discount_percent=adjusted_discount,
        current_price_rub=current_price,
        new_price_rub=final_new_price,
        stock_qty=stock_qty,
        sold_for_period=sold_for_period,
        sales_velocity=round(sales_velocity, 4),
        predicted_days_to_sell_out=round(predicted_days, 2),
        target_sellout_days=target_sellout_days,
        planned_qty_for_period=round(planned_qty_for_period, 2),
        plan_completion_ratio=round(plan_completion_ratio, 4),
        min_price_rub=int(round(min_price)),
        max_price_rub=int(round(max_price)),
    )