from app.core.config import settings
from app.core.rabbitmq import rabbitmq_manager


async def publish_order_created(
    order_id: int,
    user_id: int | None,
    total_rub: int,
) -> None:
    await rabbitmq_manager.publish_json(
        routing_key=settings.RABBITMQ_ORDER_CREATED_ROUTING_KEY,
        payload={
            "event": "order.created",
            "order_id": order_id,
            "user_id": user_id,
            "total_rub": total_rub,
        },
    )