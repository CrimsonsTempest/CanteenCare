package com.kelompok10.canteencare.ui.penjual.menus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelompok10.canteencare.core.util.Formatters
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.ui.components.EmptyView
import com.kelompok10.canteencare.ui.components.ErrorView
import com.kelompok10.canteencare.ui.components.FoodImage
import com.kelompok10.canteencare.ui.components.LoadingView
import com.kelompok10.canteencare.ui.components.MenuStatusChip
import com.kelompok10.canteencare.ui.components.imageKind
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import kotlinx.coroutines.launch

@Composable
fun SellerMenusRoute(
    onAddMenu: () -> Unit,
    viewModel: SellerMenusViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LifecycleResumeEffect(Unit) {
        viewModel.load()
        onPauseOrDispose { }
    }
    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            viewModel.onMessageShown()
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    SellerMenusScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onRetry = viewModel::load,
        onAddMenu = onAddMenu,
        onEditStock = viewModel::onEditStock,
        onStockInputChange = viewModel::onStockInputChange,
        onSaveStock = viewModel::onSaveStock,
        onCancelEditStock = viewModel::onCancelEditStock,
        onArchiveClick = viewModel::onArchiveClick,
    )

    uiState.archiveCandidate?.let { menu ->
        AlertDialog(
            onDismissRequest = viewModel::onDismissArchive,
            title = { Text("Arsipkan ${menu.name}?") },
            text = { Text("Menu tidak akan tampil lagi untuk pembeli. Riwayat pesanan yang memuat menu ini tetap aman.") },
            confirmButton = { TextButton(onClick = viewModel::onConfirmArchive) { Text("Arsipkan") } },
            dismissButton = { TextButton(onClick = viewModel::onDismissArchive) { Text("Batal") } },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerMenusScreen(
    uiState: SellerMenusUiState,
    snackbarHostState: SnackbarHostState,
    onRetry: () -> Unit,
    onAddMenu: () -> Unit,
    onEditStock: (Menu) -> Unit,
    onStockInputChange: (String) -> Unit,
    onSaveStock: () -> Unit,
    onCancelEditStock: () -> Unit,
    onArchiveClick: (Menu) -> Unit,
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Menu & Stok") }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddMenu,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Tambah Menu") },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when {
            uiState.isLoading -> LoadingView(modifier)
            uiState.errorMessage != null && uiState.menus.isEmpty() -> ErrorView(uiState.errorMessage, onRetry, modifier)
            uiState.menus.isEmpty() -> EmptyView("Belum ada menu. Tambahkan menu pertamamu.", Icons.Filled.RestaurantMenu, modifier)
            else -> LazyColumn(
                modifier = modifier.fillMaxSize(),
                // Ruang bawah ekstra agar kartu terakhir tidak tertutup tombol Tambah Menu
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(uiState.menus, key = { it.id }) { menu ->
                    SellerMenuCard(
                        menu = menu,
                        isEditing = uiState.editingMenuId == menu.id,
                        stockInput = uiState.stockInput,
                        isStockInputInvalid = uiState.isStockInputInvalid,
                        isProcessing = uiState.isProcessing,
                        onEditStock = { onEditStock(menu) },
                        onStockInputChange = onStockInputChange,
                        onSaveStock = onSaveStock,
                        onCancelEditStock = onCancelEditStock,
                        onArchiveClick = { onArchiveClick(menu) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SellerMenuCard(
    menu: Menu,
    isEditing: Boolean,
    stockInput: String,
    isStockInputInvalid: Boolean,
    isProcessing: Boolean,
    onEditStock: () -> Unit,
    onStockInputChange: (String) -> Unit,
    onSaveStock: () -> Unit,
    onCancelEditStock: () -> Unit,
    onArchiveClick: () -> Unit,
) {
    val isArchived = menu.status == MenuStatus.ARCHIVED
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            FoodImage(menu.imageUrl, menu.category.imageKind(), Modifier.size(64.dp))
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(menu.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${Formatters.rupiah(menu.price)} · ${menu.category.label}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                MenuStatusChip(menu.status)
                if (isEditing) {
                    // Edit stok langsung di kartu
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = stockInput,
                            onValueChange = onStockInputChange,
                            label = { Text("Stok") },
                            isError = isStockInputInvalid,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(120.dp),
                        )
                        IconButton(onClick = onSaveStock, enabled = !isProcessing) {
                            Icon(Icons.Filled.Check, contentDescription = "Simpan stok", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onCancelEditStock) {
                            Icon(Icons.Filled.Close, contentDescription = "Batal")
                        }
                    }
                    if (isStockInputInvalid) {
                        Text("Stok harus bilangan bulat 0 atau lebih", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Stok: ${menu.stock}",
                            style = MaterialTheme.typography.titleSmall,
                            color = if (menu.stock == 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                        )
                        if (!isArchived) {
                            IconButton(onClick = onEditStock) {
                                Icon(Icons.Filled.Edit, contentDescription = "Ubah stok ${menu.name}", modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
            if (!isArchived) {
                IconButton(onClick = onArchiveClick) {
                    Icon(Icons.Filled.Archive, contentDescription = "Arsipkan ${menu.name}", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SellerMenusScreenPreview() {
    val nutrition = Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0)
    val menus = listOf(
        Menu(1, 1, "Dapur Gapura", "Mie Ayam Solo", null, MenuCategory.MAKANAN, 20000, 25, null, MenuStatus.APPROVED, nutrition, true),
        Menu(5, 1, "Dapur Gapura", "Nasi Ayam Geprek", null, MenuCategory.MAKANAN, 18000, 10, null, MenuStatus.PENDING, nutrition, false),
    )
    CanteenCareTheme {
        SellerMenusScreen(
            uiState = SellerMenusUiState(isLoading = false, menus = menus, editingMenuId = 5, stockInput = "10"),
            snackbarHostState = SnackbarHostState(),
            onRetry = {}, onAddMenu = {}, onEditStock = {}, onStockInputChange = {},
            onSaveStock = {}, onCancelEditStock = {}, onArchiveClick = {},
        )
    }
}
