from datetime import datetime
from typing import Annotated

from pydantic import BaseModel, Field, PlainSerializer

from app.core.time import WIB

# Datetime dikirim ke client dalam WIB dengan offset, contoh: 2026-11-17T12:00:00+07:00
WibDatetime = Annotated[datetime, PlainSerializer(lambda value: value.astimezone(WIB).isoformat(), return_type=str)]


class NutritionValues(BaseModel):
    """Enam nilai gizi. Satuan: kalori kkal, garam mg natrium, sisanya gram."""

    calories: float = Field(ge=0, examples=[650])
    carbs: float = Field(ge=0, examples=[85])
    protein: float = Field(ge=0, examples=[30])
    fat: float = Field(ge=0, examples=[25])
    salt: float = Field(ge=0, examples=[1800])
    sugar: float = Field(ge=0, examples=[6])


class MessageResponse(BaseModel):
    message: str
