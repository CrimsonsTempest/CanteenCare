from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import ForeignKey, String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.time import utcnow
from app.db import Base, UTCDateTime

if TYPE_CHECKING:
    from app.models.menu import Menu
    from app.models.user import User


class Vendor(Base):
    __tablename__ = "vendors"

    id: Mapped[int] = mapped_column(primary_key=True)
    owner_id: Mapped[int] = mapped_column(ForeignKey("users.id"), unique=True)
    name: Mapped[str] = mapped_column(String(100))
    description: Mapped[str | None] = mapped_column(String(500))
    image_url: Mapped[str | None] = mapped_column(String(500))
    open_hours: Mapped[str | None] = mapped_column(String(50))
    is_active: Mapped[bool] = mapped_column(default=True)
    created_at: Mapped[datetime] = mapped_column(UTCDateTime, default=utcnow)

    owner: Mapped["User"] = relationship(back_populates="vendor")
    menus: Mapped[list["Menu"]] = relationship(back_populates="vendor")
