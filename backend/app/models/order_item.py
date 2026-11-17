from typing import TYPE_CHECKING

from sqlalchemy import ForeignKey, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.db import Base

if TYPE_CHECKING:
    from app.models.order import Order


class OrderItem(Base):
    """Snapshot menu saat dipesan.

    Nama, harga, dan gizi disalin ke sini supaya riwayat pesanan tidak ikut berubah
    ketika menu diedit di kemudian hari.
    """

    __tablename__ = "order_items"

    id: Mapped[int] = mapped_column(primary_key=True)
    order_id: Mapped[int] = mapped_column(ForeignKey("orders.id"), index=True)
    menu_id: Mapped[int] = mapped_column(ForeignKey("menus.id"))
    menu_name: Mapped[str] = mapped_column(String(100))
    unit_price: Mapped[int]
    quantity: Mapped[int]
    # Gizi per porsi
    calories: Mapped[float]
    carbs: Mapped[float]
    protein: Mapped[float]
    fat: Mapped[float]
    salt: Mapped[float]
    sugar: Mapped[float]

    order: Mapped["Order"] = relationship(back_populates="items")
