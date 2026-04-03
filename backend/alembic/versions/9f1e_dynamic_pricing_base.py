"""dynamic pricing base

Revision ID: 9f1e_dynamic_pricing_base
Revises: 8b50fedd7bb7
Create Date: 2026-04-03
"""

from alembic import op
import sqlalchemy as sa


revision = "9f1e_dynamic_pricing_base"
down_revision = "8b50fedd7bb7"
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.add_column(
        "products",
        sa.Column("stock_qty", sa.Integer(), nullable=False, server_default="0"),
    )
    op.add_column(
        "products",
        sa.Column("cost_price_rub", sa.Integer(), nullable=False, server_default="0"),
    )
    op.add_column(
        "products",
        sa.Column("market_price_rub", sa.Integer(), nullable=True),
    )

    op.create_table(
        "orders",
        sa.Column("id", sa.Integer(), primary_key=True),
        sa.Column("status", sa.String(length=50), nullable=False),
        sa.Column("created_at", sa.BigInteger(), nullable=False),
    )
    op.create_index("ix_orders_id", "orders", ["id"], unique=False)

    op.create_table(
        "order_items",
        sa.Column("id", sa.Integer(), primary_key=True),
        sa.Column("order_id", sa.Integer(), sa.ForeignKey("orders.id", ondelete="CASCADE"), nullable=False),
        sa.Column("product_id", sa.Integer(), sa.ForeignKey("products.id", ondelete="CASCADE"), nullable=False),
        sa.Column("qty", sa.Integer(), nullable=False),
        sa.Column("price_rub", sa.Integer(), nullable=False),
    )
    op.create_index("ix_order_items_id", "order_items", ["id"], unique=False)
    op.create_index("ix_order_items_order_id", "order_items", ["order_id"], unique=False)
    op.create_index("ix_order_items_product_id", "order_items", ["product_id"], unique=False)


def downgrade() -> None:
    op.drop_index("ix_order_items_product_id", table_name="order_items")
    op.drop_index("ix_order_items_order_id", table_name="order_items")
    op.drop_index("ix_order_items_id", table_name="order_items")
    op.drop_table("order_items")

    op.drop_index("ix_orders_id", table_name="orders")
    op.drop_table("orders")

    op.drop_column("products", "market_price_rub")
    op.drop_column("products", "cost_price_rub")
    op.drop_column("products", "stock_qty")