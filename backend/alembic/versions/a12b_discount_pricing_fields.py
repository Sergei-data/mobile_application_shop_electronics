"""discount pricing fields

Revision ID: a12b_discount_pricing_fields
Revises: 9f1e_dynamic_pricing_base
Create Date: 2026-04-04
"""

from alembic import op
import sqlalchemy as sa


revision = "a12b_discount_pricing_fields"
down_revision = "9f1e_dynamic_pricing_base"
branch_labels = None
depends_on = None


def upgrade() -> None:
    op.add_column(
        "products",
        sa.Column("target_sellout_days", sa.Integer(), nullable=False, server_default="180"),
    )
    op.add_column(
        "products",
        sa.Column("max_discount_percent", sa.Integer(), nullable=False, server_default="30"),
    )


def downgrade() -> None:
    op.drop_column("products", "max_discount_percent")
    op.drop_column("products", "target_sellout_days")