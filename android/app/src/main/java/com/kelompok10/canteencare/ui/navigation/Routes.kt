package com.kelompok10.canteencare.ui.navigation

import com.kelompok10.canteencare.domain.model.Role

/**
 * Semua route (alamat layar) aplikasi dalam bentuk string, seperti di modul praktikum.
 * Route yang butuh argumen memakai placeholder {nama}, dan disediakan fungsi pembuat route-nya.
 */
object Routes {
    // Bersama
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"

    // Argumen
    const val ARG_VENDOR_ID = "vendorId"
    const val ARG_ORDER_ID = "orderId"
    const val ARG_MENU_ID = "menuId"

    // PEMBELI
    const val HOME = "home"
    /** menuId opsional: jika diisi, detail menu itu langsung dibuka (dari hasil pencarian). */
    const val VENDOR = "vendor/{$ARG_VENDOR_ID}?$ARG_MENU_ID={$ARG_MENU_ID}"
    const val CART = "cart"
    const val ORDER = "order/{$ARG_ORDER_ID}"
    const val HISTORY = "history"
    const val NUTRITION = "nutrition"
    const val NUTRITION_TARGET = "nutrition_target"

    // PENJUAL
    const val SELLER_ORDERS = "seller_orders"
    const val SELLER_ORDER = "seller_order/{$ARG_ORDER_ID}"
    const val SELLER_MENUS = "seller_menus"
    const val SELLER_MENU_NEW = "seller_menu_new"

    // ADMIN
    const val ADMIN_PENDING = "admin_pending"
    const val ADMIN_MENUS = "admin_menus"
    const val ADMIN_MENU = "admin_menu/{$ARG_MENU_ID}"
    const val ADMIN_SELLERS = "admin_sellers"
    const val ADMIN_SELLER_NEW = "admin_seller_new"

    fun vendor(vendorId: Int, menuId: Int? = null) =
        if (menuId == null) "vendor/$vendorId" else "vendor/$vendorId?$ARG_MENU_ID=$menuId"
    fun order(orderId: Int) = "order/$orderId"
    fun sellerOrder(orderId: Int) = "seller_order/$orderId"
    fun adminMenu(menuId: Int) = "admin_menu/$menuId"

    /** Layar pertama setelah login, sesuai peran. */
    fun homeFor(role: Role): String = when (role) {
        Role.PEMBELI -> HOME
        Role.PENJUAL -> SELLER_ORDERS
        Role.ADMIN -> ADMIN_PENDING
    }
}
