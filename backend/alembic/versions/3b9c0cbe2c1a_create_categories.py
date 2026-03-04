"""create categories

Revision ID: 3b9c0cbe2c1a
Revises: 21df61f2d0b0
Create Date: 2026-03-04 00:00:00
"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


revision: str = "3b9c0cbe2c1a"
down_revision: Union[str, Sequence[str], None] = "21df61f2d0b0"
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.create_table(
        "categories",
        sa.Column("id", sa.Integer(), primary_key=True),
        sa.Column("title", sa.String(length=255), nullable=False),
    )

    op.execute(
        """
        insert into categories (id, title)
        select distinct category_id, ('Категория ' || category_id::text)
        from products
        where category_id is not null
        on conflict (id) do nothing
        """
    )

    op.create_foreign_key(
        "fk_products_category_id_categories",
        "products",
        "categories",
        ["category_id"],
        ["id"],
        ondelete="RESTRICT",
    )


def downgrade() -> None:
    op.drop_constraint("fk_products_category_id_categories", "products", type_="foreignkey")
    op.drop_table("categories")