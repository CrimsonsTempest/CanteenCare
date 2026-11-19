from functools import lru_cache

from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Konfigurasi aplikasi, dibaca dari environment variable atau file .env."""

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8", extra="ignore")

    # Wajib diisi di .env, tidak ada nilai default agar tidak ada secret yang di-hardcode.
    SECRET_KEY: str
    TOKEN_EXPIRE_DAYS: int = 7
    # Semakin besar semakin aman tapi lambat. Tes memakai nilai kecil agar cepat.
    BCRYPT_ROUNDS: int = 12
    DATABASE_URL: str = "sqlite:///./canteencare.db"
    FIREBASE_CREDENTIALS: str = "firebase-service-account.json"
    TIMEZONE: str = "Asia/Jakarta"
    # Folder penyimpanan gambar menu yang di-upload
    UPLOAD_DIR: str = "uploads"


@lru_cache
def get_settings() -> Settings:
    return Settings()
