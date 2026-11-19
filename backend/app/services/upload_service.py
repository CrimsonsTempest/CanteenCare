import uuid
from pathlib import Path

from app.core.config import get_settings
from app.core.errors import AppError

UPLOAD_URL_PREFIX = "/uploads"
MENU_IMAGE_FOLDER = "menus"
MAX_IMAGE_BYTES = 2 * 1024 * 1024  # 2 MB

# Jenis file dikenali dari beberapa byte pertamanya (magic bytes), bukan dari nama file,
# supaya file lain yang hanya diganti namanya menjadi .png tetap ditolak.
_SIGNATURES = {
    b"\x89PNG\r\n\x1a\n": ".png",
    b"\xff\xd8\xff": ".jpg",
}


def _detect_extension(data: bytes) -> str | None:
    for signature, extension in _SIGNATURES.items():
        if data.startswith(signature):
            return extension
    # WEBP: "RIFF" + 4 byte ukuran + "WEBP"
    if data[:4] == b"RIFF" and data[8:12] == b"WEBP":
        return ".webp"
    return None


def save_menu_image(data: bytes) -> str:
    """Simpan gambar menu ke folder upload, lalu kembalikan path URL relatifnya.

    Path relatif (contoh `/uploads/menus/abc.png`) dipakai, bukan URL lengkap, karena alamat
    server berbeda antara emulator (10.0.2.2) dan HP (IP laptop). Aplikasi yang melengkapinya.
    """
    if len(data) == 0:
        raise AppError(400, "INVALID_IMAGE", "File gambar kosong.")
    if len(data) > MAX_IMAGE_BYTES:
        raise AppError(400, "IMAGE_TOO_LARGE", "Ukuran gambar maksimal 2 MB.")
    extension = _detect_extension(data)
    if extension is None:
        raise AppError(400, "INVALID_IMAGE", "Format gambar harus PNG, JPG, atau WEBP.")

    folder = Path(get_settings().UPLOAD_DIR) / MENU_IMAGE_FOLDER
    folder.mkdir(parents=True, exist_ok=True)
    # Nama acak supaya tidak bentrok dan tidak bisa ditebak
    filename = f"{uuid.uuid4().hex}{extension}"
    (folder / filename).write_bytes(data)
    return f"{UPLOAD_URL_PREFIX}/{MENU_IMAGE_FOLDER}/{filename}"
