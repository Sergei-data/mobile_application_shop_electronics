"""products created_at default

Revision ID: 21df61f2d0b0
Revises: 7ae03f524a1d
Create Date: 2026-03-03 22:13:09.556032

"""
from typing import Sequence, Union

from alembic import op
import sqlalchemy as sa


# revision identifiers, used by Alembic.
revision: str = '21df61f2d0b0'
down_revision: Union[str, Sequence[str], None] = '7ae03f524a1d'
branch_labels: Union[str, Sequence[str], None] = None
depends_on: Union[str, Sequence[str], None] = None


def upgrade() -> None:
    op.execute(
        "UPDATE products "
        "SET created_at = (extract(epoch from now()) * 1000)::bigint "
        "WHERE created_at IS NULL"
    )

    op.alter_column(
        "products",
        "created_at",
        existing_type=sa.BigInteger(),
        nullable=False,
        server_default=sa.text("(extract(epoch from now()) * 1000)::bigint"),
    )


def downgrade() -> None:
    op.alter_column(
        "products",
        "created_at",
        existing_type=sa.BigInteger(),
        server_default=None,
    )
