package com.kelompok10.canteencare.ui.pembeli.vendor

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelompok10.canteencare.core.util.Formatters
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Vendor
import com.kelompok10.canteencare.ui.components.CartButton
import com.kelompok10.canteencare.ui.components.EmptyView
import com.kelompok10.canteencare.ui.components.ErrorView
import com.kelompok10.canteencare.ui.components.FoodImage
import com.kelompok10.canteencare.ui.components.ImageKind
import com.kelompok10.canteencare.ui.components.LoadingView
import com.kelompok10.canteencare.ui.components.imageKind
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import kotlinx.coroutines.launch

@Composable
fun VendorRoute(
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: VendorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    // Scope ini tidak ikut dibatalkan saat LaunchedEffect di bawah berganti key
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            viewModel.onMessageShown()
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    VendorScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onCartClick = onCartClick,
        onRetry = viewModel::load,
        onCategorySelected = viewModel::onCategorySelected,
        onMenuClick = viewModel::onMenuClick,
    )

    uiState.selectedMenu?.let { menu ->
        MenuDetailSheet(
            menu = menu,
            quantity = uiState.quantity,
            onDecrease = viewModel::onDecreaseQuantity,
            onIncrease = viewModel::onIncreaseQuantity,
            onAddToCart = viewModel::onAddToCart,
            onDismiss = viewModel::onDismissMenu,
        )
    }

    uiState.replaceCartVendorName?.let { otherVendor ->
        AlertDialog(
            onDismissRequest = viewModel::onCancelReplaceCart,
            title = { Text("Ganti isi keranjang?") },
            text = { Text("Keranjang berisi menu dari $otherVendor. Kosongkan dan ganti?") },
            confirmButton = { TextButton(onClick = viewModel::onConfirmReplaceCart) { Text("Kosongkan & Ganti") } },
            dismissButton = { TextButton(onClick = viewModel::onCancelReplaceCart) { Text("Batal") } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorScreen(
    uiState: VendorUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    onRetry: () -> Unit,
    onCategorySelected: (MenuCategory?) -> Unit,
    onMenuClick: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.vendor?.name ?: "Stan", maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = { CartButton(itemCount = uiState.cartCount, onClick = onCartClick) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        val vendor = uiState.vendor
        when {
            uiState.isLoading -> LoadingView(modifier)
            uiState.errorMessage != null || vendor == null ->
                ErrorView(uiState.errorMessage ?: "Stan tidak ditemukan.", onRetry, modifier)
            else -> LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 160.dp),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = modifier.fillMaxSize(),
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) { VendorHeader(vendor) }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    CategoryChips(uiState.selectedCategory, onCategorySelected)
                }
                if (uiState.visibleMenus.isEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EmptyView("Belum ada menu di kategori ini.", Icons.Filled.RestaurantMenu)
                    }
                }
                items(uiState.visibleMenus, key = { it.id }) { menu ->
                    MenuCard(menu = menu, onClick = { onMenuClick(menu.id) })
                }
            }
        }
    }
}

@Composable
private fun VendorHeader(vendor: Vendor) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        FoodImage(vendor.imageUrl, ImageKind.STALL, Modifier.size(72.dp))
        Column(
            Modifier
                .padding(start = 16.dp)
                .weight(1f),
        ) {
            Text(vendor.name, style = MaterialTheme.typography.titleLarge)
            vendor.description?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            vendor.openHours?.let {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        "Buka $it",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryChips(selected: MenuCategory?, onSelected: (MenuCategory?) -> Unit) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FilterChip(selected = selected == null, onClick = { onSelected(null) }, label = { Text("Semua") })
        MenuCategory.entries.forEach { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelected(category) },
                label = { Text(category.label) },
            )
        }
    }
}

@Composable
private fun MenuCard(menu: Menu, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        FoodImage(
            imageUrl = menu.imageUrl,
            kind = menu.category.imageKind(),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f),
        )
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(menu.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                Formatters.rupiah(menu.price),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                Formatters.nutrient(menu.nutrition?.calories ?: 0.0, "kkal"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                if (menu.isAvailable) "Stok ${menu.stock}" else "Stok habis",
                style = MaterialTheme.typography.labelSmall,
                color = if (menu.isAvailable) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VendorScreenPreview() {
    CanteenCareTheme {
        VendorScreen(
            uiState = VendorUiState(
                isLoading = false,
                vendor = Vendor(1, "Dapur Gapura", "Aneka mie dan nasi rumahan", null, "07.00 - 16.00"),
                menus = listOf(
                    Menu(
                        1, 1, "Dapur Gapura", "Mie Ayam Solo", null, MenuCategory.MAKANAN, 20000, 25, null,
                        MenuStatus.APPROVED, Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0), true,
                    ),
                    Menu(
                        4, 1, "Dapur Gapura", "Es Teh Manis", null, MenuCategory.MINUMAN, 4000, 0, null,
                        MenuStatus.APPROVED, Nutrition(120.0, 30.0, 0.0, 0.0, 10.0, 30.0), true,
                    ),
                ),
                cartCount = 2,
            ),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onCartClick = {}, onRetry = {}, onCategorySelected = {}, onMenuClick = {},
        )
    }
}
