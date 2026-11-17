from fastapi import APIRouter, status
from sqlalchemy import select

from app.core.deps import CurrentUser, DbSession
from app.core.errors import AppError
from app.core.security import create_access_token, hash_password, verify_password
from app.models import NutritionTarget, User
from app.models.enums import Role
from app.schemas.auth import FcmTokenRequest, LoginRequest, MessageResponse, RegisterRequest, TokenResponse, UserOut

router = APIRouter(tags=["Auth"])


def _token_response(user: User) -> TokenResponse:
    return TokenResponse(access_token=create_access_token(user.id, user.role), user=UserOut.from_user(user))


@router.post(
    "/auth/register",
    response_model=TokenResponse,
    status_code=status.HTTP_201_CREATED,
    summary="Daftar akun pembeli baru",
)
def register(body: RegisterRequest, db: DbSession) -> TokenResponse:
    if db.scalar(select(User).where(User.email == body.email)):
        raise AppError(409, "EMAIL_EXISTS", "Email sudah terdaftar.")

    # Registrasi publik selalu menjadi PEMBELI, dan langsung dibuatkan target gizi default
    user = User(
        full_name=body.full_name,
        email=body.email,
        password_hash=hash_password(body.password),
        role=Role.PEMBELI,
        nutrition_target=NutritionTarget(),
    )
    db.add(user)
    db.commit()
    return _token_response(user)


@router.post("/auth/login", response_model=TokenResponse, summary="Login semua role")
def login(body: LoginRequest, db: DbSession) -> TokenResponse:
    user = db.scalar(select(User).where(User.email == body.email))
    # Pesan sengaja sama untuk email salah maupun password salah, agar tidak membocorkan info akun
    if user is None or not verify_password(body.password, user.password_hash):
        raise AppError(401, "INVALID_CREDENTIALS", "Email atau password salah.")
    return _token_response(user)


@router.get("/auth/me", response_model=UserOut, summary="Data user yang sedang login")
def me(user: CurrentUser) -> UserOut:
    return UserOut.from_user(user)


@router.put("/users/me/fcm-token", response_model=MessageResponse, summary="Simpan token FCM perangkat")
def update_fcm_token(body: FcmTokenRequest, user: CurrentUser, db: DbSession) -> MessageResponse:
    user.fcm_token = body.fcm_token
    db.commit()
    return MessageResponse(message="Token notifikasi tersimpan.")
