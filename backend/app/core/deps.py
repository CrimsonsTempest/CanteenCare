from collections.abc import Callable
from typing import Annotated

from fastapi import Depends
from fastapi.security import HTTPAuthorizationCredentials, HTTPBearer
from sqlalchemy.orm import Session

from app.core.errors import AppError
from app.core.security import decode_access_token
from app.db import get_db
from app.models import User
from app.models.enums import Role

# auto_error=False supaya error 401 tetap memakai format error standar kita.
# HTTPBearer juga memunculkan tombol "Authorize" di Swagger UI.
bearer_scheme = HTTPBearer(auto_error=False, description="Tempel access_token dari /auth/login")

DbSession = Annotated[Session, Depends(get_db)]


def get_current_user(
    db: DbSession,
    credentials: Annotated[HTTPAuthorizationCredentials | None, Depends(bearer_scheme)],
) -> User:
    unauthorized = AppError(401, "UNAUTHORIZED", "Sesi tidak valid, silakan login lagi.")
    if credentials is None:
        raise unauthorized

    payload = decode_access_token(credentials.credentials)
    if payload is None:
        raise unauthorized

    user = db.get(User, int(payload["sub"]))
    if user is None:
        raise unauthorized
    return user


CurrentUser = Annotated[User, Depends(get_current_user)]


def require_role(*roles: Role) -> Callable[[User], User]:
    """Dependency yang hanya meloloskan user dengan role tertentu.

    Contoh: `user: Annotated[User, Depends(require_role(Role.PENJUAL))]`
    """

    def checker(user: CurrentUser) -> User:
        if user.role not in roles:
            raise AppError(403, "FORBIDDEN_ROLE", "Anda tidak memiliki akses ke fitur ini.")
        return user

    return checker
