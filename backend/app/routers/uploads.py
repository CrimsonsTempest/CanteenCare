from typing import Annotated

from fastapi import APIRouter, Depends, File, UploadFile, status

from app.core.deps import require_role
from app.models import User
from app.models.enums import Role
from app.schemas.upload import ImageUploadOut
from app.services import upload_service

router = APIRouter(prefix="/uploads", tags=["Upload"])

# Gambar menu boleh di-upload penjual (menu baru) dan admin (edit menu)
MenuEditor = Annotated[User, Depends(require_role(Role.PENJUAL, Role.ADMIN))]


@router.post(
    "/menu-image",
    response_model=ImageUploadOut,
    status_code=status.HTTP_201_CREATED,
    summary="Upload gambar menu (PNG/JPG/WEBP, maks 2 MB)",
)
def upload_menu_image(
    _: MenuEditor, file: Annotated[UploadFile, File(description="File gambar menu")]
) -> ImageUploadOut:
    # Baca satu byte lebih dari batas, cukup untuk tahu file kebesaran tanpa membaca semuanya
    data = file.file.read(upload_service.MAX_IMAGE_BYTES + 1)
    return ImageUploadOut(image_url=upload_service.save_menu_image(data))
