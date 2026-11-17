from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import ForeignKey, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.time import utcnow
from app.db import Base, UTCDateTime
from app.models.enums import OrderStatus

if TYPE_CHECKING:
    from app.models.order_item import OrderItem
    from app.models.user import User
    from app.models.vendor import Vendor


class Order(Base):
    __tablename__ = "orders"

    id: Mapped[int] = mapped_column(primary_key=True)
    order_number: Mapped[str] = mapped_column(String(20), unique=True)
    buyer_id: Mapped[int] = mapped_column(ForeignKey("users.id"), index=True)
    vendor_id: Mapped[int] = mapped_column(ForeignKey("vendors.id"), index=True)
    status: Mapped[str] = mapped_column(String(15), default=OrderStatus.MENUNGGU)
    payment_method: Mapped[str] = mapped_column(String(10))
    # Total dihitung ulang oleh server dari data database, bukan dari kiriman client
    total_price: Mapped[int]
    total_calories: Mapped[float]
    total_carbs: Mapped[float]
    total_protein: Mapped[float]
    total_fat: Mapped[float]
    total_salt: Mapped[float]
    total_sugar: Mapped[float]
    note: Mapped[str | None] = mapped_column(String(500))
    rejection_reason: Mapped[str | None] = mapped_column(String(500))
    created_at: Mapped[datetime] = mapped_column(UTCDateTime, default=utcnow, index=True)
    resolved_at: Mapped[datetime | None] = mapped_column(UTCDateTime)

    buyer: Mapped["User"] = relationship()
    vendor: Mapped["Vendor"] = relationship()
    items: Mapped[list["OrderItem"]] = relationship(back_populates="order", cascade="all, delete-orphan")
