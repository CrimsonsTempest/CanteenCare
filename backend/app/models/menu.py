from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import ForeignKey, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.time import utcnow
from app.db import Base, UTCDateTime
from app.models.enums import MenuStatus

if TYPE_CHECKING:
    from app.models.nutrition import Nutrition
    from app.models.vendor import Vendor


class Menu(Base):
    __tablename__ = "menus"

    id: Mapped[int] = mapped_column(primary_key=True)
    vendor_id: Mapped[int] = mapped_column(ForeignKey("vendors.id"), index=True)
    name: Mapped[str] = mapped_column(String(100))
    description: Mapped[str | None] = mapped_column(String(500))
    category: Mapped[str] = mapped_column(String(10))
    price: Mapped[int]  # rupiah, bilangan bulat
    stock: Mapped[int]
    image_url: Mapped[str | None] = mapped_column(String(500))
    # Hanya menu APPROVED yang terlihat oleh pembeli
    status: Mapped[str] = mapped_column(String(10), default=MenuStatus.PENDING)
    updated_at: Mapped[datetime] = mapped_column(UTCDateTime, default=utcnow, onupdate=utcnow)

    vendor: Mapped["Vendor"] = relationship(back_populates="menus")
    nutrition: Mapped["Nutrition | None"] = relationship(back_populates="menu")
