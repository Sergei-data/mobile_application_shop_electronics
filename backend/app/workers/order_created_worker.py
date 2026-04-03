from __future__ import annotations

import asyncio
import json
import logging

import aio_pika
from aio_pika import ExchangeType
from aio_pika.abc import AbstractIncomingMessage

from app.core.config import settings

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("order_created_worker")


async def handle_message(message: AbstractIncomingMessage) -> None:
    async with message.process():
        payload = json.loads(message.body.decode("utf-8"))
        logger.info("Received order.created: %s", payload)

        # Демо-обработка для диплома
        await asyncio.sleep(1)

        logger.info(
            "Processed order.created for order_id=%s",
            payload.get("order_id"),
        )


async def main() -> None:
    connection = await aio_pika.connect_robust(
        settings.RABBITMQ_URL,
        client_properties={"connection_name": "shop-order-worker"},
    )
    channel = await connection.channel()
    await channel.set_qos(prefetch_count=10)

    exchange = await channel.declare_exchange(
        settings.RABBITMQ_EXCHANGE_NAME,
        ExchangeType.DIRECT,
        durable=True,
    )

    queue = await channel.declare_queue(
        settings.RABBITMQ_ORDER_CREATED_QUEUE,
        durable=True,
    )

    await queue.bind(
        exchange,
        routing_key=settings.RABBITMQ_ORDER_CREATED_ROUTING_KEY,
    )

    await queue.consume(handle_message)
    logger.info("order_created_worker started")

    try:
        await asyncio.Future()
    finally:
        await connection.close()


if __name__ == "__main__":
    asyncio.run(main())