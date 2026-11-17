import secrets
from datetime import datetime

from app.core.time import WIB
from app.models import Menu, Order, OrderItem

NUTRIENT_FIELDS = ("calories", "carbs", "protein", "fat", "salt", "sugar")


def generate_order_number(created_at: datetime) -> str:
    """Format CC-YYYYMMDD-XXXX. Tanggal memakai WIB, XXXX = 4 digit hex acak huruf besar."""
    date_part = created_at.astimezone(WIB).strftime("%Y%m%d")
    return f"CC-{date_part}-{secrets.token_hex(2).upper()}"


def snapshot_item(menu: Menu, quantity: int) -> OrderItem:
    """Menyalin nama, harga, dan gizi per porsi dari menu ke item pesanan."""
    nutrition = menu.nutrition
    return OrderItem(
        menu_id=menu.id,
        menu_name=menu.name,
        unit_price=menu.price,
        quantity=quantity,
        **{field: getattr(nutrition, field) for field in NUTRIENT_FIELDS},
    )


def apply_totals(order: Order) -> None:
    """Menghitung ulang total harga dan total gizi pesanan dari item-itemnya.

    total = Σ(nilai per porsi × jumlah). Server tidak pernah memakai total dari client.
    """
    order.total_price = sum(item.unit_price * item.quantity for item in order.items)
    for field in NUTRIENT_FIELDS:
        total = sum(getattr(item, field) * item.quantity for item in order.items)
        setattr(order, f"total_{field}", round(total, 2))
