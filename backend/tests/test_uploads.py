from tests.conftest import ADMIN, DAFFA, GAPURA, login

API = "/api/v1"
URL = f"{API}/uploads/menu-image"
# Cukup header PNG yang valid; isi gambarnya tidak diperiksa
PNG_BYTES = b"\x89PNG\r\n\x1a\n" + b"\x00" * 64


def _upload(client, headers, data, filename="menu.png", content_type="image/png"):
    return client.post(URL, headers=headers, files={"file": (filename, data, content_type)})


def test_seller_uploads_image_and_it_is_served(client):
    response = _upload(client, login(client, *GAPURA), PNG_BYTES)

    assert response.status_code == 201
    image_url = response.json()["image_url"]
    assert image_url.startswith("/uploads/menus/") and image_url.endswith(".png")
    served = client.get(image_url)
    assert served.status_code == 200 and served.content == PNG_BYTES


def test_uploaded_image_can_be_used_in_new_menu(client):
    seller = login(client, *GAPURA)
    image_url = _upload(client, seller, PNG_BYTES).json()["image_url"]
    body = {
        "name": "Nasi Rawon", "category": "MAKANAN", "price": 20000, "stock": 15, "image_url": image_url,
        "nutrition": {"calories": 550, "carbs": 60, "protein": 30, "fat": 20, "salt": 1200, "sugar": 4},
    }

    menu = client.post(f"{API}/seller/menus", headers=seller, json=body).json()

    assert menu["image_url"] == image_url


def test_admin_can_upload_buyer_cannot(client):
    assert _upload(client, login(client, *ADMIN), PNG_BYTES).status_code == 201
    response = _upload(client, login(client, *DAFFA), PNG_BYTES)
    assert response.status_code == 403
    assert response.json()["detail"]["code"] == "FORBIDDEN_ROLE"


def test_rejects_non_image_even_if_named_png(client):
    response = _upload(client, login(client, *GAPURA), b"bukan gambar sama sekali")

    assert response.status_code == 400
    assert response.json()["detail"]["code"] == "INVALID_IMAGE"


def test_rejects_image_larger_than_2mb(client):
    big = b"\x89PNG\r\n\x1a\n" + b"\x00" * (2 * 1024 * 1024)

    response = _upload(client, login(client, *GAPURA), big)

    assert response.status_code == 400
    assert response.json()["detail"]["code"] == "IMAGE_TOO_LARGE"
