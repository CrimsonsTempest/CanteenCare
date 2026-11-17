from pydantic import BaseModel, ConfigDict, Field

from app.models.nutrition_target import DEFAULT_TARGETS


class NutritionTargetIn(BaseModel):
    """Target gizi harian. Semua nilai wajib lebih dari 0."""

    model_config = ConfigDict(json_schema_extra={"examples": [DEFAULT_TARGETS]})

    target_calories: float = Field(gt=0)
    target_carbs: float = Field(gt=0)
    target_protein: float = Field(gt=0)
    target_fat: float = Field(gt=0)
    target_salt: float = Field(gt=0)
    target_sugar: float = Field(gt=0)


class NutritionTargetOut(NutritionTargetIn):
    model_config = ConfigDict(from_attributes=True)
