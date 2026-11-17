from pydantic import BaseModel, ConfigDict, EmailStr, Field, field_validator

from app.models import User
from app.schemas.catalog import VendorOut


class SellerCreate(BaseModel):
    """Membuat akun PENJUAL sekaligus stannya."""

    model_config = ConfigDict(
        json_schema_extra={
            "examples": [{
                "full_name": "Pak Joko", "email": "joko@canteencare.id", "password": "penjual123",
                "vendor_name": "Soto Pak Joko", "vendor_description": "Soto ayam dan soto daging",
                "vendor_image_url": None, "open_hours": "07.00 - 14.00",
            }]
        }
    )

    full_name: str = Field(min_length=1, max_length=100)
    email: EmailStr
    password: str = Field(min_length=6, max_length=72)
    vendor_name: str = Field(min_length=1, max_length=100)
    vendor_description: str | None = Field(default=None, max_length=500)
    vendor_image_url: str | None = Field(default=None, max_length=500)
    open_hours: str | None = Field(default=None, max_length=50)

    @field_validator("email")
    @classmethod
    def _lower_email(cls, value: str) -> str:
        return value.strip().lower()


class SellerOut(BaseModel):
    id: int
    full_name: str
    email: str
    vendor: VendorOut

    @classmethod
    def from_user(cls, user: User) -> "SellerOut":
        return cls(id=user.id, full_name=user.full_name, email=user.email, vendor=VendorOut.model_validate(user.vendor))
