from datetime import date, datetime, time, timezone
from zoneinfo import ZoneInfo

from app.core.config import get_settings

# Semua "hari ini" dan filter tanggal memakai WIB (Asia/Jakarta).
WIB = ZoneInfo(get_settings().TIMEZONE)


def utcnow() -> datetime:
    """Waktu sekarang dalam UTC, timezone-aware. Semua data disimpan dalam UTC."""
    return datetime.now(timezone.utc)


def wib_today() -> date:
    return utcnow().astimezone(WIB).date()


def wib_day_range_utc(day: date) -> tuple[datetime, datetime]:
    """Rentang [awal, akhir) satu hari WIB, dikonversi ke UTC untuk query database."""
    start = datetime.combine(day, time.min, tzinfo=WIB)
    end = datetime.combine(day, time.max, tzinfo=WIB)
    return start.astimezone(timezone.utc), end.astimezone(timezone.utc)
