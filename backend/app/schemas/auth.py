from pydantic import BaseModel, ConfigDict, EmailStr, Field, field_validator

from app.models import User
from app.models.enums import Role


def _normalize_email(value: str) -> str:
    # Email tidak membedakan huruf besar/kecil, jadi selalu disimpan lowercase
    return value.strip().lower()


class RegisterRequest(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={
            "examples": [{"full_name": "Budi Santoso", "email": "budi@student.ub.ac.id", "password": "rahasia123"}]
        }
    )

    full_name: str = Field(min_length=1, max_length=100)
    email: EmailStr
    password: str = Field(min_length=6, max_length=72)

    _lower_email = field_validator("email")(_normalize_email)

    @field_validator("full_name")
    @classmethod
    def _strip_name(cls, value: str) -> str:
        value = value.strip()
        if not value:
            raise ValueError("Nama wajib diisi")
        return value


class LoginRequest(BaseModel):
    model_config = ConfigDict(
        json_schema_extra={"examples": [{"email": "daffa@student.ub.ac.id", "password": "pembeli123"}]}
    )

    email: EmailStr
    password: str = Field(min_length=1)

    _lower_email = field_validator("email")(_normalize_email)


class UserOut(BaseModel):
    id: int
    full_name: str
    email: str
    role: Role
    vendor_id: int | None = None
    vendor_name: str | None = None

    @classmethod
    def from_user(cls, user: User) -> "UserOut":
        vendor = user.vendor
        return cls(
            id=user.id,
            full_name=user.full_name,
            email=user.email,
            role=Role(user.role),
            vendor_id=vendor.id if vendor else None,
            vendor_name=vendor.name if vendor else None,
        )


class TokenResponse(BaseModel):
    access_token: str
    token_type: str = "bearer"
    user: UserOut


class FcmTokenRequest(BaseModel):
    model_config = ConfigDict(json_schema_extra={"examples": [{"fcm_token": "token-perangkat-dari-firebase"}]})

    fcm_token: str = Field(min_length=1, max_length=512)

