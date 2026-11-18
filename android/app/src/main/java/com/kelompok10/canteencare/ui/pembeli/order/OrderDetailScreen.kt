package com.kelompok10.canteencare.ui.pembeli.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelompok10.canteencare.core.util.Formatters
import com.kelompok10.canteencare.core.util.WibTime
import com.kelompok10.canteencare.domain.calculator.NutritionCalculator
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Order
import com.kelompok10.canteencare.domain.model.OrderItem
import com.kelompok10.canteencare.domain.model.OrderStatus
import com.kelompok10.canteencare.domain.model.PaymentMethod
import com.kelompok10.canteencare.ui.components.ErrorView
import com.kelompok10.canteencare.ui.components.LoadingView
import com.kelompok10.canteencare.ui.components.NutritionTable
import com.kelompok10.canteencare.ui.components.StatusChip
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import com.kelompok10.canteencare.ui.theme.StatusRed
import java.time.OffsetDateTime

@Composable
fun OrderDetailRoute(
    onBack: () -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    OrderDetailScreen(uiState = uiState, onBack = onBack, onRetry = viewModel::load)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(uiState: OrderDetailUiState, onBack: () -> Unit, onRetry: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Pesanan") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        val order = uiState.order
        when {
            uiState.isLoading && order == null -> LoadingView(modifier)
            order == null -> ErrorView(uiState.errorMessage ?: "Pesanan tidak ditemukan.", onRetry, modifier)
            else -> OrderDetailContent(order, uiState.totalPrice, uiState.totalNutrition, modifier)
        }
    }
}

@Composable
private fun OrderDetailContent(order: Order, totalPrice: Int, totalNutrition: Nutrition, modifier: Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Ringkasan: status, nomor, stan, waktu
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            StatusChip(order.status)
            Text(order.orderNumber, style = MaterialTheme.typography.headlineSmall)
            Text(order.vendorName, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            InfoLine("Dipesan", WibTime.formatDateTime(order.createdAt))
            order.resolvedAt?.let {
                InfoLine(if (order.status == OrderStatus.DITOLAK) "Ditolak" else "Dikonfirmasi", WibTime.formatDateTime(it))
            }
            InfoLine("Pembayaran", order.paymentMethod.label)
            order.note?.let { InfoLine("Catatan", it) }
        }

        when (order.status) {
            OrderStatus.MENUNGGU -> Notice("Menunggu konfirmasi penjual. Kamu akan mendapat notifikasi saat pesanan diproses.")
            OrderStatus.DITOLAK -> Notice("Alasan ditolak: ${order.rejectionReason ?: "-"}", isError = true)
            OrderStatus.DIKONFIRMASI -> Notice("Pesanan dikonfirmasi. Silakan ambil di ${order.vendorName}.")
        }

        // Daftar item dan total harga
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                order.items.forEach { OrderItemRow(it) }
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

        Text("Total Gizi Pesanan", style = MaterialTheme.typography.titleMedium)
        NutritionTable(totalNutrition)
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

@Composable
private fun InfoLine(label: String, value: String) {
    Row {
        Text("$label: ", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun Notice(text: String, isError: Boolean = false) {
    val color = if (isError) StatusRed else MaterialTheme.colorScheme.primary
    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.10f))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Info, contentDescription = null, tint = color)
            Text(text, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Preview(showBackground = true, heightDp = 1000)
@Composable
private fun OrderDetailScreenPreview() {
    val items = listOf(
        OrderItem(1, "Mie Ayam Solo", 20000, 1, Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0)),
        OrderItem(4, "Es Teh Manis", 4000, 2, Nutrition(120.0, 30.0, 0.0, 0.0, 10.0, 30.0)),
    )
    val order = Order(
        1, "CC-20261118-A3F9", "Daffa", 1, "Dapur Gapura", OrderStatus.MENUNGGU, PaymentMethod.TUNAI,
        "Sambal dipisah", null, OffsetDateTime.parse("2026-11-18T09:05:00Z"), null, items,
    )
    CanteenCareTheme {
        OrderDetailScreen(
            uiState = OrderDetailUiState(
                isLoading = false,
                order = order,
                totalPrice = NutritionCalculator.calculateTotalPrice(items),
                totalNutrition = NutritionCalculator.calculateOrderNutrition(items),
            ),
            onBack = {}, onRetry = {},
        )
    }
}
