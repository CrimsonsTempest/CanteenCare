from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import String
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.time import utcnow
from app.db import Base, UTCDateTime

if TYPE_CHECKING:
    from app.models.nutrition_target import NutritionTarget
    from app.models.vendor import Vendor


class User(Base):
    __tablename__ = "users"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    full_name: Mapped[str] = mapped_column(String(100))
    email: Mapped[str] = mapped_column(String(255), unique=True, index=True)
    password_hash: Mapped[str] = mapped_column(String(255))
    role: Mapped[str] = mapped_column(String(10))
    fcm_token: Mapped[str | None] = mapped_column(String(512))
    created_at: Mapped[datetime] = mapped_column(UTCDateTime, default=utcnow)

    # Hanya terisi untuk PENJUAL (satu penjual = satu stan)
    vendor: Mapped["Vendor | None"] = relationship(back_populates="owner")
    # Hanya terisi untuk PEMBELI
    nutrition_target: Mapped["NutritionTarget | None"] = relationship(back_populates="user")
