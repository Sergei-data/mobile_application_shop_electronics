from fastapi import APIRouter

from app.services.order_events import publish_order_created

router = APIRouter()


@router.post("/emit-order-created")
async def emit_order_created_demo():
    await publish_order_created(
        order_id=1001,
        user_id=1,
        total_rub=99990,
    )
    return {"status": "queued"}