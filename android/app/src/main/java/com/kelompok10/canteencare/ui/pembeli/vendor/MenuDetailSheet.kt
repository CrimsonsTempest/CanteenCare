package com.kelompok10.canteencare.ui.pembeli.vendor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok10.canteencare.core.util.Formatters
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.ui.components.FoodImage
import com.kelompok10.canteencare.ui.components.NutritionTable
import com.kelompok10.canteencare.ui.components.QuantityStepper
import com.kelompok10.canteencare.ui.components.VerifiedBadge
import com.kelompok10.canteencare.ui.components.imageKind
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme

/** Bottom sheet detail menu: gambar, harga, tabel gizi per porsi, pemilih jumlah, dan tombol tambah. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuDetailSheet(
    menu: Menu,
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAddToCart: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        MenuDetailContent(menu, quantity, onDecrease, onIncrease, onAddToCart)
    }
}

@Composable
private fun MenuDetailContent(
    menu: Menu,
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onAddToCart: () -> Unit,
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FoodImage(
            imageUrl = menu.imageUrl,
            kind = menu.category.imageKind(),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
        )
        Text(menu.name, style = MaterialTheme.typography.headlineSmall)
        Text(
            Formatters.rupiah(menu.price),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        menu.description?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text("Gizi per porsi", style = MaterialTheme.typography.titleMedium)
            VerifiedBadge(isVerified = menu.isNutritionVerified)
        }
        NutritionTable(menu.nutrition ?: Nutrition.ZERO)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text("Jumlah", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (menu.isAvailable) "Stok tersisa ${menu.stock}" else "Stok habis",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (menu.isAvailable) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                )
            }
            QuantityStepper(
                quantity = quantity,
                onDecrease = onDecrease,
                onIncrease = onIncrease,
                canDecrease = quantity > 1,
                canIncrease = quantity < menu.stock,
            )
        }

        Button(
            onClick = onAddToCart,
            enabled = menu.isAvailable,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(52.dp),
        ) {
            Icon(Icons.Filled.AddShoppingCart, contentDescription = null)
            Text(
                if (menu.isAvailable) "Tambah ke Keranjang" else "Stok Habis",
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MenuDetailContentPreview() {
    CanteenCareTheme {
        MenuDetailContent(
            menu = Menu(
                1, 1, "Dapur Gapura", "Mie Ayam Solo", "Mie ayam khas Solo dengan pangsit", MenuCategory.MAKANAN,
                20000, 25, null, "APPROVED", Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0), true,
            ),
            quantity = 2,
            onDecrease = {}, onIncrease = {}, onAddToCart = {},
        )
    }
}
