package com.kelompok10.canteencare.ui.penjual.order

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelompok10.canteencare.core.util.WibTime
import com.kelompok10.canteencare.domain.calculator.NutritionCalculator
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Order
import com.kelompok10.canteencare.domain.model.OrderItem
import com.kelompok10.canteencare.domain.model.OrderStatus
import com.kelompok10.canteencare.domain.model.PaymentMethod
import com.kelompok10.canteencare.ui.components.ErrorView
import com.kelompok10.canteencare.ui.components.InfoLine
import com.kelompok10.canteencare.ui.components.LoadingView
import com.kelompok10.canteencare.ui.components.NoticeCard
import com.kelompok10.canteencare.ui.components.OrderItemsCard
import com.kelompok10.canteencare.ui.components.StatusChip
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

@Composable
fun SellerOrderDetailRoute(
    onBack: () -> Unit,
    viewModel: SellerOrderDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.message) {
        uiState.message?.let { message ->
            viewModel.onMessageShown()
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    SellerOrderDetailScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onRetry = viewModel::load,
        onConfirm = viewModel::confirm,
        onRejectClick = viewModel::onRejectClick,
    )

    if (uiState.showRejectDialog) {
        RejectDialog(
            reason = uiState.rejectReason,
            onReasonChange = viewModel::onRejectReasonChange,
            onConfirm = viewModel::reject,
            onDismiss = viewModel::onDismissRejectDialog,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrderDetailScreen(
    uiState: SellerOrderDetailUiState,
    snackbarHostState: SnackbarHostState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onConfirm: () -> Unit,
    onRejectClick: () -> Unit,
) {
    val order = uiState.order
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
        bottomBar = {
            // Tombol aksi hanya untuk pesanan yang masih menunggu
            if (order?.status == OrderStatus.MENUNGGU) {
                ActionBar(uiState.isProcessing, onConfirm, onRejectClick)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        val modifier = Modifier.padding(padding)
        when {
            uiState.isLoading && order == null -> LoadingView(modifier)
            order == null -> ErrorView(uiState.errorMessage ?: "Pesanan tidak ditemukan.", onRetry, modifier)
            else -> Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusChip(order.status)
                    Text(order.orderNumber, style = MaterialTheme.typography.headlineSmall)
                    InfoLine("Pembeli", order.buyerName)
                    InfoLine("Dipesan", WibTime.formatDateTime(order.createdAt))
                    InfoLine("Pembayaran", order.paymentMethod.label)
                }
                order.note?.let { NoticeCard("Catatan pembeli: $it") }
                if (order.status == OrderStatus.DITOLAK) {
                    NoticeCard("Ditolak: ${order.rejectionReason ?: "-"}", isError = true)
                }
                OrderItemsCard(order.items, uiState.totalPrice)
            }
        }
    }
}

@Composable
private fun ActionBar(isProcessing: Boolean, onConfirm: () -> Unit, onReject: () -> Unit) {
    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(
                onClick = onReject,
                enabled = !isProcessing,
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) { Text("Tolak") }
            Button(
                onClick = onConfirm,
                enabled = !isProcessing,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Konfirmasi")
                }
            }
        }
    }
}

@Composable
private fun RejectDialog(
    reason: String,
    onReasonChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tolak pesanan?") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Alasan akan dikirim ke pembeli lewat notifikasi. Stok tidak berubah.")
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    label = { Text("Alasan") },
                    placeholder = { Text("Contoh: Bahan habis") },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = reason.isNotBlank()) { Text("Tolak Pesanan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } },
    )
}

@Preview(showBackground = true)
@Composable
private fun SellerOrderDetailScreenPreview() {
    val items = listOf(OrderItem(1, "Mie Ayam Solo", 20000, 2, Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0)))
    val order = Order(
        3, "CC-20261118-9308", "Daffa", 1, "Dapur Gapura", OrderStatus.MENUNGGU, PaymentMethod.QRIS,
        "Sambal dipisah", null, OffsetDateTime.parse("2026-11-18T09:56:00Z"), null, items,
    )
    CanteenCareTheme {
        SellerOrderDetailScreen(
            uiState = SellerOrderDetailUiState(
                isLoading = false, order = order, totalPrice = NutritionCalculator.calculateTotalPrice(items),
            ),
            snackbarHostState = SnackbarHostState(),
            onBack = {}, onRetry = {}, onConfirm = {}, onRejectClick = {},
        )
    }
}
