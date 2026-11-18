package com.kelompok10.canteencare.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme

/** Tombol − jumlah + untuk memilih jumlah porsi. Batasnya diatur oleh pemanggil. */
@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    canDecrease: Boolean = true,
    canIncrease: Boolean = true,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        FilledTonalIconButton(onClick = onDecrease, enabled = canDecrease) {
            Icon(Icons.Filled.Remove, contentDescription = "Kurangi")
        }
        Text(
            quantity.toString(),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 40.dp),
        )
        FilledTonalIconButton(onClick = onIncrease, enabled = canIncrease) {
            Icon(Icons.Filled.Add, contentDescription = "Tambah")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun QuantityStepperPreview() {
    CanteenCareTheme {
        QuantityStepper(quantity = 2, onDecrease = {}, onIncrease = {})
    }
}
