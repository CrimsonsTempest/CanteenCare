from collections.abc import Iterator
from datetime import datetime, timezone

from sqlalchemy import DateTime, create_engine, event
from sqlalchemy.orm import DeclarativeBase, Session, sessionmaker
from sqlalchemy.types import TypeDecorator

from app.core.config import get_settings

settings = get_settings()

# check_same_thread=False diperlukan SQLite karena FastAPI memakai banyak thread.
engine = create_engine(settings.DATABASE_URL, connect_args={"check_same_thread": False})
SessionLocal = sessionmaker(bind=engine, autoflush=False, expire_on_commit=False)


@event.listens_for(engine, "connect")
def _enable_sqlite_foreign_keys(dbapi_connection, _record) -> None:
    # SQLite secara default tidak mengecek foreign key, jadi diaktifkan manual.
    cursor = dbapi_connection.cursor()
    cursor.execute("PRAGMA foreign_keys=ON")
    cursor.close()


class UTCDateTime(TypeDecorator):
    """Menyimpan datetime sebagai UTC.

    SQLite tidak menyimpan informasi zona waktu, jadi saat disimpan waktu diubah ke UTC,
    dan saat dibaca diberi label UTC lagi supaya tetap timezone-aware.
    """

    impl = DateTime
    cache_ok = True

    def process_bind_param(self, value: datetime | None, dialect) -> datetime | None:
        if value is None:
            return None
        if value.tzinfo is None:
            raise ValueError("Datetime harus timezone-aware")
        return value.astimezone(timezone.utc).replace(tzinfo=None)

    def process_result_value(self, value: datetime | None, dialect) -> datetime | None:
        return None if value is None else value.replace(tzinfo=timezone.utc)


class Base(DeclarativeBase):
    pass


def get_db() -> Iterator[Session]:
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
