from fastapi import APIRouter, Query, status
from sqlalchemy import select

from app.core.deps import AdminUser, DbSession
from app.core.errors import AppError
from app.core.security import hash_password
from app.models import Menu, Nutrition, User, Vendor
from app.models.enums import MenuStatus, Role
from app.schemas.admin import SellerCreate, SellerOut
from app.schemas.catalog import MenuOut, MenuUpdate
from app.schemas.common import MessageResponse, NutritionValues
from app.seed import reset_database

router = APIRouter(prefix="/admin", tags=["Admin"])
dev_router = APIRouter(prefix="/admin/dev", tags=["Dev"])


def _get_menu(db: DbSession, menu_id: int) -> Menu:
    menu = db.get(Menu, menu_id)
    if menu is None:
        raise AppError(404, "NOT_FOUND", "Menu tidak ditemukan.")
    return menu


def _ensure_not_archived(menu: Menu) -> None:
    if menu.status == MenuStatus.ARCHIVED:
        raise AppError(409, "INVALID_MENU_STATE", f"Menu {menu.name} sudah diarsipkan penjual.")


@router.post("/sellers", response_model=SellerOut, status_code=status.HTTP_201_CREATED, summary="Buat akun penjual + stan")
def create_seller(body: SellerCreate, db: DbSession, _: AdminUser) -> SellerOut:
    if db.scalar(select(User).where(User.email == body.email)):
        raise AppError(409, "EMAIL_EXISTS", "Email sudah terdaftar.")

    seller = User(
        full_name=body.full_name.strip(),
        email=body.email,
        password_hash=hash_password(body.password),
        role=Role.PENJUAL,
    )
    # Akun penjual dan stannya dibuat dalam satu transaksi
    seller.vendor = Vendor(
        name=body.vendor_name.strip(),
        description=body.vendor_description,
        image_url=body.vendor_image_url,
        open_hours=body.open_hours,
    )
    db.add(seller)
    db.commit()
    return SellerOut.from_user(seller)


@router.get("/sellers", response_model=list[SellerOut], summary="Daftar penjual dan stannya")
def list_sellers(db: DbSession, _: AdminUser) -> list[SellerOut]:
    sellers = db.scalars(select(User).where(User.role == Role.PENJUAL).order_by(User.full_name))
    return [SellerOut.from_user(seller) for seller in sellers if seller.vendor is not None]


@router.get("/menus", response_model=list[MenuOut], summary="Semua menu")
def list_menus(
    db: DbSession,
    _: AdminUser,
    status_filter: MenuStatus | None = Query(default=None, alias="status"),
) -> list[MenuOut]:
    query = select(Menu).order_by(Menu.updated_at.desc(), Menu.id.desc())
    if status_filter is not None:
        query = query.where(Menu.status == status_filter)
    return [MenuOut.from_menu(menu) for menu in db.scalars(query)]


@router.put("/menus/{menu_id}", response_model=MenuOut, summary="Ubah data menu")
def update_menu(menu_id: int, body: MenuUpdate, db: DbSession, _: AdminUser) -> MenuOut:
    menu = _get_menu(db, menu_id)
    for field, value in body.model_dump().items():
        setattr(menu, field, value)
    db.commit()
    return MenuOut.from_menu(menu)


@router.put("/menus/{menu_id}/nutrition", response_model=MenuOut, summary="Isi dan verifikasi gizi menu")
def verify_nutrition(menu_id: int, body: NutritionValues, db: DbSession, admin: AdminUser) -> MenuOut:
    menu = _get_menu(db, menu_id)
    if menu.nutrition is None:
        menu.nutrition = Nutrition(**body.model_dump())
    else:
        for field, value in body.model_dump().items():
            setattr(menu.nutrition, field, value)
    # Menandai gizi sudah diverifikasi oleh admin ini
    menu.nutrition.verified_by = admin.id
    db.commit()
    return MenuOut.from_menu(menu)


@router.post("/menus/{menu_id}/approve", response_model=MenuOut, summary="Setujui menu (tampil ke pembeli)")
def approve_menu(menu_id: int, db: DbSession, _: AdminUser) -> MenuOut:
    menu = _get_menu(db, menu_id)
    _ensure_not_archived(menu)
    # Menu hanya boleh tampil ke pembeli jika gizinya sudah diverifikasi admin
    if menu.nutrition is None or menu.nutrition.verified_by is None:
        raise AppError(
            409, "NUTRITION_NOT_VERIFIED", f"Gizi menu {menu.name} belum diverifikasi. Verifikasi gizi terlebih dahulu."
        )
    menu.status = MenuStatus.APPROVED
    db.commit()
    return MenuOut.from_menu(menu)


@router.post("/menus/{menu_id}/reject", response_model=MenuOut, summary="Tolak menu")
def reject_menu(menu_id: int, db: DbSession, _: AdminUser) -> MenuOut:
    menu = _get_menu(db, menu_id)
    _ensure_not_archived(menu)
    menu.status = MenuStatus.REJECTED
    db.commit()
    return MenuOut.from_menu(menu)


@dev_router.post("/reset-demo", response_model=MessageResponse, summary="Reset database ke data demo (ADMIN)")
def reset_demo(db: DbSession, _: AdminUser) -> MessageResponse:
    # Tutup koneksi request ini dulu agar SQLite tidak terkunci saat tabel dihapus
    db.close()
    reset_database()
    return MessageResponse(message="Database berhasil direset ke data demo. Silakan login ulang.")
