package com.kelompok10.canteencare.ui.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kelompok10.canteencare.core.notification.NotificationTarget
import com.kelompok10.canteencare.domain.model.Role
import com.kelompok10.canteencare.ui.auth.LoginRoute
import com.kelompok10.canteencare.ui.auth.RegisterRoute
import com.kelompok10.canteencare.ui.auth.SplashRoute
import com.kelompok10.canteencare.ui.components.PlaceholderAction
import com.kelompok10.canteencare.ui.components.NotificationPermissionRequest
import com.kelompok10.canteencare.ui.components.PlaceholderScreen
import com.kelompok10.canteencare.ui.pembeli.history.HistoryRoute
import com.kelompok10.canteencare.ui.pembeli.cart.CartRoute
import com.kelompok10.canteencare.ui.pembeli.home.HomeRoute
import com.kelompok10.canteencare.ui.pembeli.order.OrderDetailRoute
import com.kelompok10.canteencare.ui.pembeli.vendor.VendorRoute
import com.kelompok10.canteencare.ui.profile.ProfileRoute
import kotlinx.coroutines.launch

/**
 * Peta navigasi seluruh aplikasi.
 * Bottom bar hanya muncul di layar tab utama, dan isinya menyesuaikan peran dari sesi login.
 */
@Composable
fun CanteenCareNavHost(
    notificationTarget: NotificationTarget?,
    onNotificationTargetHandled: () -> Unit,
    navController: NavHostController = rememberNavController(),
    appViewModel: AppViewModel = hiltViewModel(),
) {
    val role by appViewModel.role.collectAsStateWithLifecycle()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val tabs = role?.let(::tabsFor).orEmpty()
    val showBottomBar = tabs.any { it.route == currentRoute }
    val snackbarHostState = remember { SnackbarHostState() }

    // Pindah ke layar Login dan hapus seluruh back stack (tombol Back tidak bisa kembali)
    fun goToLogin() {
        navController.navigate(Routes.LOGIN) {
            popUpTo(navController.graph.id) { inclusive = true }
        }
    }

    // Server membalas 401 → sesi sudah dibersihkan AppViewModel → kembali ke Login dengan pesan
    LaunchedEffect(Unit) {
        appViewModel.sessionExpired.collect {
            goToLogin()
            launch { snackbarHostState.showSnackbar("Sesi berakhir, silakan login lagi") }
        }
    }

    // Notifikasi diketuk → buka detail pesanan sesuai peran.
    // Ditunda sampai user sudah login dan sudah melewati layar splash/login.
    LaunchedEffect(notificationTarget, role, currentRoute) {
        val target = notificationTarget ?: return@LaunchedEffect
        val currentRole = role ?: return@LaunchedEffect
        if (currentRoute == null || currentRoute in AUTH_ROUTES) return@LaunchedEffect
        val route = when (currentRole) {
            Role.PEMBELI -> Routes.order(target.orderId)
            Role.PENJUAL -> Routes.sellerOrder(target.orderId)
            Role.ADMIN -> null // admin tidak menerima notifikasi pesanan
        }
        route?.let { navController.navigate(it) { launchSingleTop = true } }
        onNotificationTargetHandled()
    }

    // Setelah login, minta izin notifikasi (Android 13+)
    val loggedInRole = role
    if (loggedInRole != null && currentRoute != null && currentRoute !in AUTH_ROUTES) {
        NotificationPermissionRequest(loggedInRole)
    }

    Scaffold(
        // Inset sistem diurus oleh Scaffold di tiap layar, bukan di sini
        contentWindowInsets = WindowInsets(0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val currentRole = role
            if (showBottomBar && currentRole != null) {
                RoleBottomBar(tabs = tabs, currentRoute = currentRoute) { tab ->
                    navController.navigateToTab(tab.route, homeRoute = Routes.homeFor(currentRole))
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
                SplashRoute(onNavigate = { destination ->
                    navController.navigate(destination) {
                        popUpTo(Routes.SPLASH) { inclusive = true }
                    }
                })
            }
            composable(Routes.LOGIN) {
                LoginRoute(
                    onLoggedIn = { loggedInRole ->
                        // Setelah login, langsung ke home sesuai peran dan hapus layar login dari back stack
                        navController.navigate(Routes.homeFor(loggedInRole)) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Routes.REGISTER) },
                )
            }
            composable(Routes.REGISTER) {
                RegisterRoute(
                    onRegistered = {
                        // Registrasi publik selalu menjadi PEMBELI
                        navController.navigate(Routes.HOME) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    onBack = navController::popBackStack,
                )
            }
            composable(Routes.PROFILE) {
                ProfileRoute(onLoggedOut = ::goToLogin)
            }

            // ---------- PEMBELI ----------
            composable(Routes.HOME) {
                HomeRoute(
                    onVendorClick = { vendorId -> navController.navigate(Routes.vendor(vendorId)) },
                    onMenuClick = { vendorId, menuId -> navController.navigate(Routes.vendor(vendorId, menuId)) },
                    onCartClick = { navController.navigate(Routes.CART) },
                )
            }
            composable(
                route = Routes.VENDOR,
                arguments = listOf(
                    navArgument(Routes.ARG_VENDOR_ID) { type = NavType.IntType },
                    // Argumen opsional: -1 berarti tidak ada menu yang langsung dibuka
                    navArgument(Routes.ARG_MENU_ID) {
                        type = NavType.IntType
                        defaultValue = -1
                    },
                ),
            ) {
                // vendorId dan menuId dibaca VendorViewModel lewat SavedStateHandle
                VendorRoute(
                    onBack = navController::popBackStack,
                    onCartClick = { navController.navigate(Routes.CART) },
                )
            }
            composable(Routes.CART) {
                CartRoute(
                    onBack = navController::popBackStack,
                    onOrderPlaced = { orderId ->
                        // Ganti layar keranjang dengan detail pesanan, jadi Back tidak kembali ke keranjang kosong
                        navController.navigate(Routes.order(orderId)) {
                            popUpTo(Routes.CART) { inclusive = true }
                        }
                    },
                )
            }
            composable(
                route = Routes.ORDER,
                arguments = listOf(navArgument(Routes.ARG_ORDER_ID) { type = NavType.IntType }),
            ) {
                OrderDetailRoute(onBack = navController::popBackStack)
            }
            composable(Routes.HISTORY) {
                HistoryRoute(onOrderClick = { orderId -> navController.navigate(Routes.order(orderId)) })
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

/** Layar sebelum/selama login; navigasi dari notifikasi menunggu sampai user keluar dari sini. */
private val AUTH_ROUTES = setOf(Routes.SPLASH, Routes.LOGIN, Routes.REGISTER)

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
