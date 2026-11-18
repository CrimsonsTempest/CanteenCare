package com.kelompok10.canteencare.domain.model

/** Peran user. Satu APK dipakai semua peran; tampilan menyesuaikan peran setelah login. */
enum class Role(val label: String) {
    PEMBELI("Pembeli"),
    PENJUAL("Penjual"),
    ADMIN("Admin"),
}
