import os
import tempfile
from pathlib import Path

import pytest

# Tes memakai database SQLite sementara yang terpisah, jadi canteencare.db asli tidak tersentuh.
# Environment harus diatur SEBELUM modul app diimport.
_TEST_DB = Path(tempfile.mkdtemp()) / "test.db"
os.environ["DATABASE_URL"] = f"sqlite:///{_TEST_DB.as_posix()}"
os.environ["SECRET_KEY"] = "test-secret-key-yang-cukup-panjang-untuk-hs256"

from fastapi.testclient import TestClient  # noqa: E402

from app.main import app  # noqa: E402
from app.seed import reset_database  # noqa: E402


@pytest.fixture
def client() -> TestClient:
    # Setiap tes mulai dari data demo yang bersih
    reset_database()
    with TestClient(app) as test_client:
        yield test_client


def login(client: TestClient, email: str, password: str) -> dict[str, str]:
    """Login lalu kembalikan header Authorization siap pakai."""
    response = client.post("/api/v1/auth/login", json={"email": email, "password": password})
    assert response.status_code == 200, response.text
    return {"Authorization": f"Bearer {response.json()['access_token']}"}
