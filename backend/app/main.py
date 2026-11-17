import logging
from collections.abc import AsyncIterator
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from app import models  # noqa: F401  (memastikan semua tabel terdaftar di Base.metadata)
from app.core.errors import register_error_handlers
from app.db import Base, SessionLocal, engine
from app.routers import admin, auth, catalog, nutrition_target, orders, seller
from app.seed import seed_if_empty

API_PREFIX = "/api/v1"

logging.basicConfig(level=logging.INFO)


@asynccontextmanager
async def lifespan(_: FastAPI) -> AsyncIterator[None]:
    # Saat server menyala: buat tabel jika belum ada, lalu isi data demo jika database kosong
    Base.metadata.create_all(engine)
    with SessionLocal() as db:
        seed_if_empty(db)
    yield


def create_app() -> FastAPI:
    app = FastAPI(
        title="CanteenCare API",
        description="Backend aplikasi pemesanan makanan kantin dengan pelacakan gizi. Kelompok 10 PAPB 2026.",
        version="1.0.0",
        lifespan=lifespan,
    )
    # Untuk pengembangan lokal semua origin diizinkan
    app.add_middleware(CORSMiddleware, allow_origins=["*"], allow_methods=["*"], allow_headers=["*"])
    register_error_handlers(app)
    routers = (
        auth.router, catalog.router, orders.router, nutrition_target.router,
        seller.router, admin.router, admin.dev_router,
    )
    for router in routers:
        app.include_router(router, prefix=API_PREFIX)
    return app


app = create_app()
