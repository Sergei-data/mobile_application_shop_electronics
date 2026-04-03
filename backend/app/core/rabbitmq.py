from __future__ import annotations

import json
from typing import Any

import aio_pika
from aio_pika import DeliveryMode, ExchangeType, Message
from aio_pika.abc import (
    AbstractExchange,
    AbstractQueue,
    AbstractRobustChannel,
    AbstractRobustConnection,
)

from app.core.config import settings


class RabbitMQManager:
    def __init__(self) -> None:
        self.connection: AbstractRobustConnection | None = None
        self.channel: AbstractRobustChannel | None = None
        self.exchange: AbstractExchange | None = None
        self.order_created_queue: AbstractQueue | None = None

    async def connect(self) -> None:
        self.connection = await aio_pika.connect_robust(
            settings.RABBITMQ_URL,
            client_properties={"connection_name": "shop-api"},
        )

        self.channel = await self.connection.channel()
        await self.channel.set_qos(prefetch_count=10)

        self.exchange = await self.channel.declare_exchange(
            settings.RABBITMQ_EXCHANGE_NAME,
            ExchangeType.DIRECT,
            durable=True,
        )

        self.order_created_queue = await self.channel.declare_queue(
            settings.RABBITMQ_ORDER_CREATED_QUEUE,
            durable=True,
        )

        await self.order_created_queue.bind(
            self.exchange,
            routing_key=settings.RABBITMQ_ORDER_CREATED_ROUTING_KEY,
        )

    async def close(self) -> None:
        if self.connection:
            await self.connection.close()

    async def publish_json(self, routing_key: str, payload: dict[str, Any]) -> None:
        if not self.exchange:
            raise RuntimeError("RabbitMQ is not initialized")

        body = json.dumps(payload, ensure_ascii=False).encode("utf-8")

        await self.exchange.publish(
            Message(
                body=body,
                content_type="application/json",
                delivery_mode=DeliveryMode.PERSISTENT,
            ),
            routing_key=routing_key,
        )


rabbitmq_manager = RabbitMQManager()