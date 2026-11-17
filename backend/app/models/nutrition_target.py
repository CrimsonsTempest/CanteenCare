from datetime import datetime
from typing import TYPE_CHECKING

from sqlalchemy import ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from app.core.time import utcnow
from app.db import Base, UTCDateTime

if TYPE_CHECKING:
    from app.models.user import User

# Target default untuk pembeli baru.
# calories, carbs, protein = GOAL (ingin dicapai)
# fat, salt, sugar = LIMIT (batas maksimum)
DEFAULT_TARGETS = {
    "target_calories": 2150.0,
    "target_carbs": 325.0,
    "target_protein": 60.0,
    "target_fat": 67.0,
    "target_salt": 2000.0,  # mg
    "target_sugar": 50.0,
}


class NutritionTarget(Base):
    __tablename__ = "nutrition_targets"

    id: Mapped[int] = mapped_column(primary_key=True)
    user_id: Mapped[int] = mapped_column(ForeignKey("users.id"), unique=True)
    target_calories: Mapped[float] = mapped_column(default=DEFAULT_TARGETS["target_calories"])
    target_carbs: Mapped[float] = mapped_column(default=DEFAULT_TARGETS["target_carbs"])
    target_protein: Mapped[float] = mapped_column(default=DEFAULT_TARGETS["target_protein"])
    target_fat: Mapped[float] = mapped_column(default=DEFAULT_TARGETS["target_fat"])
    target_salt: Mapped[float] = mapped_column(default=DEFAULT_TARGETS["target_salt"])
    target_sugar: Mapped[float] = mapped_column(default=DEFAULT_TARGETS["target_sugar"])
    updated_at: Mapped[datetime] = mapped_column(UTCDateTime, default=utcnow, onupdate=utcnow)

    user: Mapped["User"] = relationship(back_populates="nutrition_target")
