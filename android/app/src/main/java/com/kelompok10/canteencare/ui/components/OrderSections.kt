package com.kelompok10.canteencare.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok10.canteencare.core.util.Formatters
import com.kelompok10.canteencare.domain.calculator.NutritionCalculator
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.OrderItem
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import com.kelompok10.canteencare.ui.theme.StatusRed

/**
 * Bagian tampilan pesanan yang dipakai bersama oleh layar detail pesanan pembeli dan penjual.
 */

/** Kartu daftar item pesanan beserta subtotal tiap baris dan total harga. */
@Composable
fun OrderItemsCard(items: List<OrderItem>, totalPrice: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items.forEach { OrderItemRow(it) }
            HorizontalDivider()
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total harga", style = MaterialTheme.typography.titleMedium)
                Text(
                    Formatters.rupiah(totalPrice),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Column(Modifier.weight(1f)) {
            Text(item.menuName, style = MaterialTheme.typography.bodyLarge)
            Text(
                "${item.quantity} × ${Formatters.rupiah(item.price)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            Formatters.rupiah(NutritionCalculator.calculateSubtotal(item.price, item.quantity)),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

/** Satu baris keterangan "Label: nilai". */
@Composable
fun InfoLine(label: String, value: String) {
    Row {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

/** Kotak pemberitahuan berwarna; merah jika [isError]. */
@Composable
fun NoticeCard(text: String, isError: Boolean = false) {
    val color = if (isError) StatusRed else MaterialTheme.colorScheme.primary
    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = color)
            Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderItemsCardPreview() {
    val items = listOf(
        OrderItem(1, "Mie Ayam Solo", 20000, 1, Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0)),
        OrderItem(4, "Es Teh Manis", 4000, 2, Nutrition(120.0, 30.0, 0.0, 0.0, 10.0, 30.0)),
    )
    CanteenCareTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NoticeCard("Menunggu konfirmasi penjual.")
            OrderItemsCard(items, NutritionCalculator.calculateTotalPrice(items))
        }
    }
}
