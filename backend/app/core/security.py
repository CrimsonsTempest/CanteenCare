from datetime import timedelta

import bcrypt
import jwt

from app.core.config import get_settings
from app.core.time import utcnow

ALGORITHM = "HS256"


def hash_password(password: str) -> str:
    salt = bcrypt.gensalt(rounds=get_settings().BCRYPT_ROUNDS)
    return bcrypt.hashpw(password.encode("utf-8"), salt).decode("utf-8")


def verify_password(password: str, password_hash: str) -> bool:
    try:
        return bcrypt.checkpw(password.encode("utf-8"), password_hash.encode("utf-8"))
    except ValueError:
        # bcrypt menolak password lebih dari 72 byte; anggap saja salah
        return False


def create_access_token(user_id: int, role: str) -> str:
    settings = get_settings()
    payload = {
        "sub": str(user_id),  # JWT mewajibkan "sub" berupa string
        "role": role,
        "exp": utcnow() + timedelta(days=settings.TOKEN_EXPIRE_DAYS),
    }
    return jwt.encode(payload, settings.SECRET_KEY, algorithm=ALGORITHM)


def decode_access_token(token: str) -> dict | None:
    """Mengembalikan isi token, atau None jika token salah atau kedaluwarsa."""
    try:
        return jwt.decode(token, get_settings().SECRET_KEY, algorithms=[ALGORITHM])
    except jwt.PyJWTError:
        return None
