from tests.conftest import ADMIN, BUSRI, DAFFA, GAPURA, login

API = "/api/v1"
DAPUR_GAPURA = 1
MIE_AYAM, GEPREK_PENDING = 1, 5
NEW_MENU = {
    "name": "Nasi Rawon", "description": "Rawon daging", "category": "MAKANAN", "price": 20000, "stock": 15,
    "image_url": None,
    "nutrition": {"calories": 550, "carbs": 60, "protein": 30, "fat": 20, "salt": 1200, "sugar": 4},
}
VERIFIED_NUTRITION = {"calories": 560, "carbs": 62, "protein": 31, "fat": 21, "salt": 1250, "sugar": 5}


def _buyer_menu_names(client):
    menus = client.get(f"{API}/vendors/{DAPUR_GAPURA}/menus", headers=login(client, *DAFFA)).json()
    return {m["name"] for m in menus}


# ---------- Penjual: menu ----------

def test_seller_lists_own_menus_all_statuses(client):
    menus = client.get(f"{API}/seller/menus", headers=login(client, *GAPURA)).json()

    assert len(menus) == 5
    assert {m["vendor_name"] for m in menus} == {"Dapur Gapura"}
    assert "PENDING" in {m["status"] for m in menus}


def test_seller_submits_menu_as_pending_unverified(client):
    response = client.post(f"{API}/seller/menus", headers=login(client, *GAPURA), json=NEW_MENU)

    assert response.status_code == 201
    menu = response.json()
    assert menu["status"] == "PENDING"
    assert menu["vendor_id"] == DAPUR_GAPURA
    assert menu["nutrition"]["is_verified"] is False
    assert "Nasi Rawon" not in _buyer_menu_names(client)


def test_seller_updates_stock(client):
    seller = login(client, *GAPURA)

    ok = client.patch(f"{API}/seller/menus/{MIE_AYAM}/stock", headers=seller, json={"stock": 3})
    negative = client.patch(f"{API}/seller/menus/{MIE_AYAM}/stock", headers=seller, json={"stock": -1})
    other_stall = client.patch(f"{API}/seller/menus/{MIE_AYAM}/stock", headers=login(client, *BUSRI), json={"stock": 1})

    assert ok.status_code == 200 and ok.json()["stock"] == 3
    assert negative.status_code == 422
    assert other_stall.status_code == 404


def test_seller_archives_menu(client):
    response = client.delete(f"{API}/seller/menus/{MIE_AYAM}", headers=login(client, *GAPURA))

    assert response.status_code == 200
    assert response.json()["status"] == "ARCHIVED"
    assert "Mie Ayam Solo" not in _buyer_menu_names(client)
    # Riwayat pesanan lama tetap utuh karena menu tidak dihapus
    history = client.get(f"{API}/orders/me", headers=login(client, *DAFFA)).json()
    assert "Mie Ayam Solo" in {item["menu_name"] for o in history for item in o["items"]}


# ---------- Admin: verifikasi menu ----------

def test_approve_requires_verified_nutrition(client):
    admin = login(client, *ADMIN)

    response = client.post(f"{API}/admin/menus/{GEPREK_PENDING}/approve", headers=admin)

    assert response.status_code == 409
    assert response.json()["detail"]["code"] == "NUTRITION_NOT_VERIFIED"


def test_admin_verifies_then_approves_menu(client):
    admin = login(client, *ADMIN)

    pending = client.get(f"{API}/admin/menus", headers=admin, params={"status": "PENDING"}).json()
    verified = client.put(f"{API}/admin/menus/{GEPREK_PENDING}/nutrition", headers=admin, json=VERIFIED_NUTRITION)
    approved = client.post(f"{API}/admin/menus/{GEPREK_PENDING}/approve", headers=admin)

    assert [m["name"] for m in pending] == ["Nasi Ayam Geprek"]
    assert verified.json()["nutrition"] == {**VERIFIED_NUTRITION, "is_verified": True}
    assert approved.status_code == 200 and approved.json()["status"] == "APPROVED"
    assert "Nasi Ayam Geprek" in _buyer_menu_names(client)


def test_admin_edits_and_rejects_menu(client):
    admin = login(client, *ADMIN)
    update = {"name": "Geprek Level 5", "description": "Pedas", "category": "MAKANAN", "price": 19000, "image_url": None}

    edited = client.put(f"{API}/admin/menus/{GEPREK_PENDING}", headers=admin, json=update)
    rejected = client.post(f"{API}/admin/menus/{GEPREK_PENDING}/reject", headers=admin)

    assert edited.json()["name"] == "Geprek Level 5" and edited.json()["price"] == 19000
    assert rejected.json()["status"] == "REJECTED"


def test_archived_menu_cannot_be_approved(client):
    client.delete(f"{API}/seller/menus/{MIE_AYAM}", headers=login(client, *GAPURA))

    response = client.post(f"{API}/admin/menus/{MIE_AYAM}/approve", headers=login(client, *ADMIN))

    assert response.status_code == 409
    assert response.json()["detail"]["code"] == "INVALID_MENU_STATE"


# ---------- Admin: penjual ----------

def test_admin_creates_seller_with_stall(client):
    admin = login(client, *ADMIN)
    body = {"full_name": "Pak Joko", "email": "Joko@CanteenCare.id", "password": "penjual123",
            "vendor_name": "Soto Pak Joko", "open_hours": "07.00 - 14.00"}

    response = client.post(f"{API}/admin/sellers", headers=admin, json=body)
    duplicate = client.post(f"{API}/admin/sellers", headers=admin, json=body)
    sellers = client.get(f"{API}/admin/sellers", headers=admin).json()

    assert response.status_code == 201
    assert response.json()["vendor"]["name"] == "Soto Pak Joko"
    assert duplicate.status_code == 409
    assert len(sellers) == 4
    # Penjual baru bisa langsung login dan melihat stannya
    me = client.get(f"{API}/auth/me", headers=login(client, "joko@canteencare.id", "penjual123")).json()
    assert me["role"] == "PENJUAL" and me["vendor_name"] == "Soto Pak Joko"


def test_non_admin_cannot_use_admin_endpoints(client):
    assert client.get(f"{API}/admin/menus", headers=login(client, *GAPURA)).status_code == 403
    assert client.get(f"{API}/admin/sellers", headers=login(client, *DAFFA)).status_code == 403


# ---------- Dev ----------

def test_reset_demo_restores_seed(client):
    admin = login(client, *ADMIN)
    client.patch(f"{API}/seller/menus/{MIE_AYAM}/stock", headers=login(client, *GAPURA), json={"stock": 0})

    response = client.post(f"{API}/admin/dev/reset-demo", headers=admin)

    assert response.status_code == 200
    assert client.get(f"{API}/menus/{MIE_AYAM}", headers=login(client, *DAFFA)).json()["stock"] == 25


def test_reset_demo_is_admin_only(client):
    assert client.post(f"{API}/admin/dev/reset-demo", headers=login(client, *DAFFA)).status_code == 403
