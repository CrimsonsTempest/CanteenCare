import pytest

from app.models.nutrition_target import DEFAULT_TARGETS
from tests.conftest import DAFFA, GAPURA, RAFAEL, login

API = "/api/v1"
NEW_TARGET = {
    "target_calories": 2000, "target_carbs": 300, "target_protein": 70,
    "target_fat": 60, "target_salt": 1500, "target_sugar": 40,
}


def test_get_default_and_custom_target(client):
    daffa = client.get(f"{API}/nutrition-target", headers=login(client, *DAFFA)).json()
    rafael = client.get(f"{API}/nutrition-target", headers=login(client, *RAFAEL)).json()

    assert daffa == DEFAULT_TARGETS
    assert rafael["target_calories"] == 2500


def test_update_target(client):
    headers = login(client, *DAFFA)

    response = client.put(f"{API}/nutrition-target", headers=headers, json=NEW_TARGET)

    assert response.status_code == 200
    assert client.get(f"{API}/nutrition-target", headers=headers).json() == NEW_TARGET


@pytest.mark.parametrize("bad_value", [0, -10])
def test_target_must_be_positive(client, bad_value):
    body = {**NEW_TARGET, "target_sugar": bad_value}

    response = client.put(f"{API}/nutrition-target", headers=login(client, *DAFFA), json=body)

    assert response.status_code == 422
    assert response.json()["detail"]["message"] == "Field target_sugar harus lebih dari 0."


def test_target_requires_all_fields(client):
    body = {k: v for k, v in NEW_TARGET.items() if k != "target_fat"}

    response = client.put(f"{API}/nutrition-target", headers=login(client, *DAFFA), json=body)

    assert response.status_code == 422


def test_seller_has_no_target(client):
    assert client.get(f"{API}/nutrition-target", headers=login(client, *GAPURA)).status_code == 403
