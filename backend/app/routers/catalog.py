from fastapi import APIRouter
from sqlalchemy import select

from app.core.deps import CurrentUser, DbSession
from app.core.errors import AppError
from app.models import Menu, User, Vendor
from app.models.enums import MenuCategory, MenuStatus, Role
from app.schemas.catalog import MenuOut, VendorOut

router = APIRouter(tags=["Katalog"])


def _visible_vendor(db: DbSession, vendor_id: int, user: User) -> Vendor:
    """Stan nonaktif disembunyikan dari pembeli."""
    vendor = db.get(Vendor, vendor_id)
    if vendor is None or (not vendor.is_active and user.role == Role.PEMBELI):
        raise AppError(404, "NOT_FOUND", "Stan tidak ditemukan.")
    return vendor


def _approved_menus_query():
    # Pembeli hanya boleh melihat menu APPROVED dari stan yang aktif
    return (
        select(Menu)
        .join(Menu.vendor)
        .where(Menu.status == MenuStatus.APPROVED, Vendor.is_active.is_(True))
        .order_by(Menu.name)
    )


@router.get("/vendors", response_model=list[VendorOut], summary="Daftar stan aktif")
def list_vendors(db: DbSession, _: CurrentUser, search: str | None = None) -> list[Vendor]:
    query = select(Vendor).where(Vendor.is_active.is_(True)).order_by(Vendor.name)
    if search:
        query = query.where(Vendor.name.ilike(f"%{search.strip()}%"))
    return list(db.scalars(query))


@router.get("/vendors/{vendor_id}", response_model=VendorOut, summary="Detail stan")
def get_vendor(vendor_id: int, db: DbSession, user: CurrentUser) -> Vendor:
    return _visible_vendor(db, vendor_id, user)


@router.get("/vendors/{vendor_id}/menus", response_model=list[MenuOut], summary="Menu APPROVED milik stan")
def list_vendor_menus(
    vendor_id: int, db: DbSession, user: CurrentUser, category: MenuCategory | None = None
) -> list[MenuOut]:
    vendor = _visible_vendor(db, vendor_id, user)
    query = _approved_menus_query().where(Menu.vendor_id == vendor.id)
    if category is not None:
        query = query.where(Menu.category == category)
    return [MenuOut.from_menu(menu) for menu in db.scalars(query)]


@router.get("/menus", response_model=list[MenuOut], summary="Cari menu di semua stan")
def search_menus(db: DbSession, _: CurrentUser, search: str | None = None) -> list[MenuOut]:
    query = _approved_menus_query()
    if search:
        query = query.where(Menu.name.ilike(f"%{search.strip()}%"))
    return [MenuOut.from_menu(menu) for menu in db.scalars(query)]


@router.get("/menus/{menu_id}", response_model=MenuOut, summary="Detail menu")
def get_menu(menu_id: int, db: DbSession, user: CurrentUser) -> MenuOut:
    menu = db.get(Menu, menu_id)
    if menu is None or not _can_see_menu(menu, user):
        raise AppError(404, "NOT_FOUND", "Menu tidak ditemukan.")
    return MenuOut.from_menu(menu)


def _can_see_menu(menu: Menu, user: User) -> bool:
    if user.role == Role.ADMIN:
        return True
    if user.role == Role.PENJUAL and user.vendor is not None and menu.vendor_id == user.vendor.id:
        return True  # penjual boleh melihat semua menu miliknya, apa pun statusnya
    return menu.status == MenuStatus.APPROVED and menu.vendor.is_active
