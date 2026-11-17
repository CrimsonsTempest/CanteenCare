from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.time import utcnow
from app.db import Base, UTCDateTime

if TYPE_CHECKING:
    from app.models.menu import Menu


class Nutrition(Base):
    """Kandungan gizi untuk SATU porsi menu."""

    __tablename__ = "nutritions"

    id: Mapped[int] = mapped_column(primary_key=True)
    menu_id: Mapped[int] = mapped_column(ForeignKey("menus.id"), unique=True)
    calories: Mapped[float]  # kkal
    carbs: Mapped[float]  # gram
    protein: Mapped[float]  # gram
    fat: Mapped[float]  # gram
    salt: Mapped[float]  # mg natrium
    sugar: Mapped[float]  # gram
    # Diisi id admin yang memverifikasi; null berarti belum terverifikasi
    verified_by: Mapped[int | None] = mapped_column(ForeignKey("users.id"))
    updated_at: Mapped[datetime] = mapped_column(UTCDateTime, default=utcnow, onupdate=utcnow)

    menu: Mapped["Menu"] = relationship(back_populates="nutrition")
