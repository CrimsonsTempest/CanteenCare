from fastapi import FastAPI, Request
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException


class AppError(Exception):
    """Error aplikasi dengan kode dan pesan berbahasa Indonesia.

    Semua error dikirim ke client dengan bentuk yang sama:
    {"detail": {"code": "...", "message": "..."}}
    sehingga aplikasi Android bisa langsung menampilkan pesannya.
    """

    def __init__(self, status_code: int, code: str, message: str) -> None:
        super().__init__(message)
        self.status_code = status_code
        self.code = code
        self.message = message


def _error_body(code: str, message: str) -> dict:
    return {"detail": {"code": code, "message": message}}


# Terjemahan jenis error validasi Pydantic yang paling sering muncul.
_VALIDATION_MESSAGES = {
    "missing": "wajib diisi",
    "string_too_short": "minimal {min_length} karakter",
    "string_too_long": "maksimal {max_length} karakter",
    "greater_than": "harus lebih dari {gt}",
    "greater_than_equal": "minimal {ge}",
    "less_than_equal": "maksimal {le}",
    "int_parsing": "harus berupa bilangan bulat",
    "float_parsing": "harus berupa angka",
    "enum": "nilai tidak valid",
    "literal_error": "nilai tidak valid",
    "value_error": "format tidak valid",
    "json_invalid": "format JSON tidak valid",
}

_STATUS_CODES = {
    401: ("UNAUTHORIZED", "Sesi tidak valid, silakan login lagi."),
    403: ("FORBIDDEN", "Akses ditolak."),
    404: ("NOT_FOUND", "Data tidak ditemukan."),
    405: ("METHOD_NOT_ALLOWED", "Metode tidak diizinkan."),
}


def _validation_message(error: dict) -> str:
    # loc contoh: ("body", "password") → ambil nama field terakhir yang berupa teks
    field = next((str(part) for part in reversed(error["loc"]) if isinstance(part, str)), "data")
    template = _VALIDATION_MESSAGES.get(error["type"], "tidak valid")
    # Tampilkan 0.0 sebagai 0 supaya pesan lebih enak dibaca
    ctx = {k: int(v) if isinstance(v, float) and v.is_integer() else v for k, v in error.get("ctx", {}).items()}
    return f"Field {field} {template.format(**ctx)}."


def register_error_handlers(app: FastAPI) -> None:
    @app.exception_handler(AppError)
    async def handle_app_error(_: Request, exc: AppError) -> JSONResponse:
        return JSONResponse(status_code=exc.status_code, content=_error_body(exc.code, exc.message))

    @app.exception_handler(RequestValidationError)
    async def handle_validation_error(_: Request, exc: RequestValidationError) -> JSONResponse:
        message = _validation_message(exc.errors()[0])
        return JSONResponse(status_code=422, content=_error_body("VALIDATION_ERROR", message))

    @app.exception_handler(StarletteHTTPException)
    async def handle_http_error(_: Request, exc: StarletteHTTPException) -> JSONResponse:
        code, message = _STATUS_CODES.get(exc.status_code, ("HTTP_ERROR", "Terjadi kesalahan."))
        return JSONResponse(status_code=exc.status_code, content=_error_body(code, message))
