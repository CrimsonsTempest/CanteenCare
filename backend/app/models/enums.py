from enum import StrEnum


class Role(StrEnum):
    PEMBELI = "PEMBELI"
    PENJUAL = "PENJUAL"
    ADMIN = "ADMIN"


class MenuCategory(StrEnum):
    MAKANAN = "MAKANAN"
    MINUMAN = "MINUMAN"


class MenuStatus(StrEnum):
    PENDING = "PENDING"
    APPROVED = "APPROVED"
    REJECTED = "REJECTED"
    ARCHIVED = "ARCHIVED"


class OrderStatus(StrEnum):
    MENUNGGU = "MENUNGGU"
    DIKONFIRMASI = "DIKONFIRMASI"
    DITOLAK = "DITOLAK"


class PaymentMethod(StrEnum):
    TUNAI = "TUNAI"
    QRIS = "QRIS"
