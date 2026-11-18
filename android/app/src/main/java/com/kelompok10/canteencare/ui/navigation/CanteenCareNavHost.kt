package com.kelompok10.canteencare.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kelompok10.canteencare.domain.model.Role
import com.kelompok10.canteencare.ui.components.PlaceholderAction
import com.kelompok10.canteencare.ui.components.PlaceholderScreen
import kotlinx.coroutines.delay

/**
 * Peta navigasi seluruh aplikasi.
 * Bottom bar hanya muncul di layar tab utama, dan isinya menyesuaikan peran user.
 *
 * Catatan Phase 4: peran masih disimpan sementara di sini dan dipilih lewat tombol di layar Login.
 * Mulai Phase 5, peran diambil dari sesi login (DataStore).
 */
@Composable
fun CanteenCareNavHost(navController: NavHostController = rememberNavController()) {
    var role by rememberSaveable { mutableStateOf<Role?>(null) }
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val tabs = role?.let(::tabsFor).orEmpty()
    val showBottomBar = tabs.any { it.route == currentRoute }

    fun loginAs(selected: Role) {
        role = selected
        navController.navigate(Routes.homeFor(selected)) {
            // Hapus layar login dari back stack supaya tombol Back tidak kembali ke login
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    fun logout() {
        role = null
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    Scaffold(
        // Inset sistem diurus oleh Scaffold di tiap layar, bukan di sini
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) {
                RoleBottomBar(tabs = tabs, currentRoute = currentRoute) { tab ->
                    navController.navigateToTab(tab.route, homeRoute = Routes.homeFor(role!!))
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier.padding(padding).consumeWindowInsets(padding),
        ) {
            // ---------- Bersama ----------
            composable(Routes.SPLASH) {
                LaunchedEffect(Unit) {
                    delay(800)
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                }
                PlaceholderScreen("CanteenCare", "Memuat... Splash sungguhan dibangun di Phase 5.")
            }
            composable(Routes.LOGIN) {
                PlaceholderScreen(
                    title = "Masuk",
                    description = "Login sungguhan dibangun di Phase 5. Untuk sementara, pilih peran:",
                    actions = listOf(
                        PlaceholderAction("Masuk sebagai Pembeli") { loginAs(Role.PEMBELI) },
                        PlaceholderAction("Masuk sebagai Penjual") { loginAs(Role.PENJUAL) },
                        PlaceholderAction("Masuk sebagai Admin") { loginAs(Role.ADMIN) },
                        PlaceholderAction("Belum punya akun? Daftar") { navController.navigate(Routes.REGISTER) },
                    ),
                )
            }
            composable(Routes.REGISTER) {
                PlaceholderScreen("Daftar", "Dibangun di Phase 5.", onBack = navController::popBackStack)
            }
            composable(Routes.PROFILE) {
                PlaceholderScreen(
                    title = "Profil",
                    description = "Peran saat ini: ${role?.name ?: "-"}. Profil lengkap dibangun di Phase 5.",
                    actions = listOf(PlaceholderAction("Keluar") { logout() }),
                )
            }

            // ---------- PEMBELI ----------
            composable(Routes.HOME) {
                PlaceholderScreen(
                    title = "Beranda",
                    description = "Daftar stan dan pencarian menu dibangun di Phase 6.",
                    actions = listOf(
                        PlaceholderAction("Buka stan #1") { navController.navigate(Routes.vendor(1)) },
                        PlaceholderAction("Keranjang") { navController.navigate(Routes.CART) },
                    ),
                )
            }
            composable(
                route = Routes.VENDOR,
                arguments = listOf(navArgument(Routes.ARG_VENDOR_ID) { type = NavType.IntType }),
            ) { entry ->
                val vendorId = entry.arguments?.getInt(Routes.ARG_VENDOR_ID)
                PlaceholderScreen(
                    title = "Stan #$vendorId",
                    description = "Menu stan dan detail gizi dibangun di Phase 6.",
                    onBack = navController::popBackStack,
                )
            }
            composable(Routes.CART) {
                PlaceholderScreen(
                    title = "Keranjang",
                    description = "Total harga dan total gizi dibangun di Phase 7.",
                    onBack = navController::popBackStack,
                    actions = listOf(PlaceholderAction("Lihat pesanan #1") { navController.navigate(Routes.order(1)) }),
                )
            }
            composable(
                route = Routes.ORDER,
                arguments = listOf(navArgument(Routes.ARG_ORDER_ID) { type = NavType.IntType }),
            ) { entry ->
                PlaceholderScreen(
                    title = "Pesanan #${entry.arguments?.getInt(Routes.ARG_ORDER_ID)}",
                    description = "Detail pesanan dibangun di Phase 7.",
                    onBack = navController::popBackStack,
                )
            }
            composable(Routes.HISTORY) {
                PlaceholderScreen(
                    title = "Riwayat",
                    description = "Riwayat pesanan dibangun di Phase 8.",
                    actions = listOf(PlaceholderAction("Lihat pesanan #1") { navController.navigate(Routes.order(1)) }),
                )
            }
            composable(Routes.NUTRITION) {
                PlaceholderScreen(
                    title = "Gizi Hari Ini",
                    description = "Progres gizi harian dibangun di Phase 9.",
                    actions = listOf(PlaceholderAction("Ubah target") { navController.navigate(Routes.NUTRITION_TARGET) }),
                )
            }
            composable(Routes.NUTRITION_TARGET) {
                PlaceholderScreen("Target Gizi", "Dibangun di Phase 9.", onBack = navController::popBackStack)
            }

            // ---------- PENJUAL ----------
            composable(Routes.SELLER_ORDERS) {
                PlaceholderScreen(
                    title = "Pesanan Masuk",
                    description = "Daftar pesanan penjual dibangun di Phase 10.",
                    actions = listOf(
                        PlaceholderAction("Lihat pesanan #1") { navController.navigate(Routes.sellerOrder(1)) },
                    ),
                )
            }
            composable(
                route = Routes.SELLER_ORDER,
                arguments = listOf(navArgument(Routes.ARG_ORDER_ID) { type = NavType.IntType }),
            ) { entry ->
                PlaceholderScreen(
                    title = "Pesanan #${entry.arguments?.getInt(Routes.ARG_ORDER_ID)}",
                    description = "Konfirmasi dan tolak pesanan dibangun di Phase 10.",
                    onBack = navController::popBackStack,
                )
            }
            composable(Routes.SELLER_MENUS) {
                PlaceholderScreen(
                    title = "Menu & Stok",
                    description = "Kelola menu dan stok dibangun di Phase 10.",
                    actions = listOf(PlaceholderAction("Tambah menu") { navController.navigate(Routes.SELLER_MENU_NEW) }),
                )
            }
            composable(Routes.SELLER_MENU_NEW) {
                PlaceholderScreen("Menu Baru", "Dibangun di Phase 10.", onBack = navController::popBackStack)
            }

            // ---------- ADMIN ----------
            composable(Routes.ADMIN_PENDING) {
                PlaceholderScreen(
                    title = "Verifikasi Menu",
                    description = "Daftar menu menunggu verifikasi dibangun di Phase 11.",
                    actions = listOf(PlaceholderAction("Lihat menu #5") { navController.navigate(Routes.adminMenu(5)) }),
                )
            }
            composable(Routes.ADMIN_MENUS) {
                PlaceholderScreen("Semua Menu", "Dibangun di Phase 11.")
            }
            composable(
                route = Routes.ADMIN_MENU,
                arguments = listOf(navArgument(Routes.ARG_MENU_ID) { type = NavType.IntType }),
            ) { entry ->
                PlaceholderScreen(
                    title = "Menu #${entry.arguments?.getInt(Routes.ARG_MENU_ID)}",
                    description = "Edit menu dan verifikasi gizi dibangun di Phase 11.",
                    onBack = navController::popBackStack,
                )
            }
            composable(Routes.ADMIN_SELLERS) {
                PlaceholderScreen(
                    title = "Penjual",
                    description = "Daftar penjual dibangun di Phase 11.",
                    actions = listOf(
                        PlaceholderAction("Tambah penjual") { navController.navigate(Routes.ADMIN_SELLER_NEW) },
                    ),
                )
            }
            composable(Routes.ADMIN_SELLER_NEW) {
                PlaceholderScreen("Penjual Baru", "Dibangun di Phase 11.", onBack = navController::popBackStack)
            }
        }
    }
}

/**
 * Pindah tab bottom bar tanpa menumpuk layar: kembali ke home peran,
 * simpan state tab lama, dan pulihkan state tab tujuan.
 */
private fun NavHostController.navigateToTab(route: String, homeRoute: String) {
    navigate(route) {
        popUpTo(homeRoute) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
