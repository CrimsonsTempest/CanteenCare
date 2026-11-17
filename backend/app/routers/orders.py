from datetime import date

from fastapi import APIRouter, Query, status

from app.core.deps import BuyerUser, CurrentUser, DbSession
from app.core.errors import AppError
from app.models import Order, User
from app.models.enums import OrderStatus, Role
from app.schemas.order import OrderCreate, OrderOut
from app.services import order_service

router = APIRouter(tags=["Pesanan (Pembeli)"])


@router.post("/orders", response_model=OrderOut, status_code=status.HTTP_201_CREATED, summary="Buat pesanan")
def create_order(body: OrderCreate, db: DbSession, buyer: BuyerUser) -> OrderOut:
    order = order_service.create_order(
        db,
        buyer=buyer,
        vendor_id=body.vendor_id,
        payment_method=body.payment_method,
        note=body.note,
        items=[(item.menu_id, item.quantity) for item in body.items],
    )
    return OrderOut.from_order(order)


@router.get("/orders/me", response_model=list[OrderOut], summary="Riwayat pesanan saya")
def my_orders(
    db: DbSession,
    buyer: BuyerUser,
    status_filter: OrderStatus | None = Query(default=None, alias="status"),
    day: date | None = Query(default=None, alias="date", description="Tanggal WIB, format YYYY-MM-DD"),
) -> list[OrderOut]:
    orders = order_service.list_orders(db, buyer_id=buyer.id, status=status_filter, day=day)
    return [OrderOut.from_order(order) for order in orders]


@router.get("/orders/{order_id}", response_model=OrderOut, summary="Detail pesanan")
def get_order(order_id: int, db: DbSession, user: CurrentUser) -> OrderOut:
    order = db.get(Order, order_id)
    if order is None or not _can_see_order(order, user):
        # 404 (bukan 403) supaya tidak membocorkan bahwa pesanan itu ada
        raise AppError(404, "NOT_FOUND", "Pesanan tidak ditemukan.")
    return OrderOut.from_order(order)


def _can_see_order(order: Order, user: User) -> bool:
    if user.role == Role.ADMIN:
        return True
    if user.role == Role.PENJUAL:
        return user.vendor is not None and order.vendor_id == user.vendor.id
    return order.buyer_id == user.id
