# CanteenCare Backend

FastAPI + SQLite. Dokumentasi endpoint ada di [../docs/api.md](../docs/api.md) dan Swagger UI di `/docs`.

## Setup pertama kali (Windows)

Jalankan dari folder `backend/`:

```
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
copy .env.example .env
```

Lalu buka `.env` dan ganti `SECRET_KEY` dengan string acak. Untuk membuatnya:

```
python -c "import secrets; print(secrets.token_hex(32))"
```

## Menjalankan server

```
.venv\Scripts\activate
uvicorn app.main:app --reload --host 0.0.0.0 --port 8000
```

- Swagger UI: http://localhost:8000/docs
- `--host 0.0.0.0` diperlukan supaya HP di Wi‑Fi yang sama bisa mengakses laptop.
- Saat pertama kali menyala, tabel dibuat otomatis dan data demo diisi jika database masih kosong.
- Untuk mengulang data demo dari awal, hentikan server lalu hapus file `canteencare.db`.

## Mencoba di Swagger

1. Buka `POST /api/v1/auth/login`, klik **Try it out**, isi email dan password akun demo, lalu **Execute**.
2. Salin nilai `access_token` dari respons.
3. Klik tombol **Authorize** di kanan atas, tempel token, lalu **Authorize**.
4. Sekarang endpoint lain seperti `GET /api/v1/auth/me` bisa dicoba.

## Menjalankan tes

```
.venv\Scripts\activate
python -m pytest -q
```

Tes memakai database sementara yang terpisah, jadi `canteencare.db` tidak tersentuh.

## Struktur

| Path | Isi |
|---|---|
| `app/main.py` | Membuat aplikasi FastAPI, CORS, router, dan membuat tabel + seed saat start |
| `app/db.py` | Koneksi SQLite, `Base`, tipe `UTCDateTime`, dependency `get_db` |
| `app/core/config.py` | Pengaturan dari `.env` |
| `app/core/security.py` | Hash password bcrypt dan JWT |
| `app/core/deps.py` | `get_current_user` dan `require_role(...)` |
| `app/core/errors.py` | `AppError` dan format error standar |
| `app/core/time.py` | Waktu UTC dan helper tanggal WIB |
| `app/models/` | Tabel database, satu file per tabel |
| `app/schemas/` | Model request dan response Pydantic |
| `app/routers/` | Endpoint API |
| `app/services/` | Logika bisnis, misalnya perhitungan total pesanan |
| `app/seed.py` | Data demo |
| `tests/` | Tes pytest |
