from app.db import SessionLocal
from app.models import Vendor
from tests.conftest import ADMIN, BUSRI, DAFFA, GAPURA, login

API = "/api/v1"
# ID dari data seed
DAPUR_GAPURA, WARUNG_BU_SRI = 1, 2
MIE_AYAM, GEPREK_PENDING = 1, 5


def test_catalog_requires_login(client):
    assert client.get(f"{API}/vendors").status_code == 401


def test_list_vendors_and_search(client):
    headers = login(client, *DAFFA)

    all_vendors = client.get(f"{API}/vendors", headers=headers).json()
    found = client.get(f"{API}/vendors", headers=headers, params={"search": "bu sri"}).json()

    assert [v["name"] for v in all_vendors] == ["Dapur Gapura", "Kedai Segar", "Warung Bu Sri"]
    assert [v["name"] for v in found] == ["Warung Bu Sri"]


def test_inactive_vendor_hidden_from_buyer(client):
    with SessionLocal() as db:
        db.get(Vendor, WARUNG_BU_SRI).is_active = False
        db.commit()
    headers = login(client, *DAFFA)

    names = [v["name"] for v in client.get(f"{API}/vendors", headers=headers).json()]
    detail = client.get(f"{API}/vendors/{WARUNG_BU_SRI}", headers=headers)
    search = client.get(f"{API}/menus", headers=headers, params={"search": "pecel"}).json()

    assert "Warung Bu Sri" not in names
    assert detail.status_code == 404
    assert search == []


def test_vendor_menus_only_approved_and_filter_category(client):
    headers = login(client, *DAFFA)

    menus = client.get(f"{API}/vendors/{DAPUR_GAPURA}/menus", headers=headers).json()
    drinks = client.get(
        f"{API}/vendors/{DAPUR_GAPURA}/menus", headers=headers, params={"category": "MINUMAN"}
    ).json()

    names = {m["name"] for m in menus}
    assert "Nasi Ayam Geprek" not in names  # masih PENDING
    assert names == {"Mie Ayam Solo", "Tahu Telur", "Nasi Goreng Ayam", "Es Teh Manis"}
    assert [m["name"] for m in drinks] == ["Es Teh Manis"]


def test_menu_response_shape(client):
    headers = login(client, *DAFFA)

    menu = client.get(f"{API}/menus/{MIE_AYAM}", headers=headers).json()

    assert menu == {
        "id": 1, "vendor_id": 1, "vendor_name": "Dapur Gapura",
        "name": "Mie Ayam Solo", "description": None, "category": "MAKANAN",
        "price": 20000, "stock": 25, "image_url": None, "status": "APPROVED",
        "nutrition": {"calories": 650, "carbs": 85, "protein": 30, "fat": 25, "salt": 1800, "sugar": 6,
                      "is_verified": True},
    }


def test_search_menus_across_vendors(client):
    headers = login(client, *DAFFA)

    results = client.get(f"{API}/menus", headers=headers, params={"search": "es "}).json()

    assert {(m["name"], m["vendor_name"]) for m in results} == {
        ("Es Teh Manis", "Dapur Gapura"),
        ("Es Jeruk", "Warung Bu Sri"),
    }


def test_pending_menu_visibility_per_role(client):
    url = f"{API}/menus/{GEPREK_PENDING}"

    buyer = client.get(url, headers=login(client, *DAFFA))
    owner = client.get(url, headers=login(client, *GAPURA))
    other_seller = client.get(url, headers=login(client, *BUSRI))
    admin = client.get(url, headers=login(client, *ADMIN))

    assert buyer.status_code == 404
    assert owner.status_code == 200
    assert other_seller.status_code == 404
    assert admin.status_code == 200
    assert admin.json()["nutrition"]["is_verified"] is False
