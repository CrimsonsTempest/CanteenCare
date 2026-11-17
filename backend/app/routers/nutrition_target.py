from fastapi import APIRouter

from app.core.deps import BuyerUser, DbSession
from app.models import NutritionTarget, User
from app.schemas.nutrition_target import NutritionTargetIn, NutritionTargetOut

router = APIRouter(tags=["Target Gizi (Pembeli)"])


def _get_or_create_target(db: DbSession, buyer: User) -> NutritionTarget:
    # Normalnya target sudah dibuat saat registrasi; ini hanya pengaman
    if buyer.nutrition_target is None:
        buyer.nutrition_target = NutritionTarget()
        db.commit()
    return buyer.nutrition_target


@router.get("/nutrition-target", response_model=NutritionTargetOut, summary="Target gizi harian saya")
def get_target(db: DbSession, buyer: BuyerUser) -> NutritionTarget:
    return _get_or_create_target(db, buyer)


@router.put("/nutrition-target", response_model=NutritionTargetOut, summary="Ubah target gizi harian")
def update_target(body: NutritionTargetIn, db: DbSession, buyer: BuyerUser) -> NutritionTarget:
    target = _get_or_create_target(db, buyer)
    for field, value in body.model_dump().items():
        setattr(target, field, value)
    db.commit()
    return target
