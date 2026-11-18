package com.kelompok10.canteencare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok10.canteencare.core.util.Formatters
import com.kelompok10.canteencare.domain.model.Nutrient
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import com.kelompok10.canteencare.ui.theme.StatusAmber
import com.kelompok10.canteencare.ui.theme.StatusGreen

/** Tabel enam nilai gizi. Dipakai untuk gizi per porsi maupun total pesanan. */
@Composable
fun NutritionTable(nutrition: Nutrition, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Nutrient.entries.forEachIndexed { index, nutrient ->
                if (index > 0) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(nutrient.label, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        Formatters.nutrient(nutrition.valueOf(nutrient), nutrient.unit),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

/** Lencana status verifikasi gizi oleh admin. */
@Composable
fun VerifiedBadge(isVerified: Boolean, modifier: Modifier = Modifier) {
    val color = if (isVerified) StatusGreen else StatusAmber
    Row(
        modifier = modifier
            .background(color.copy(alpha = 0.12f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            if (isVerified) Icons.Filled.Verified else Icons.Filled.HourglassTop,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp),
        )
        Text(
            if (isVerified) "Gizi terverifikasi admin" else "Gizi belum diverifikasi",
            style = MaterialTheme.typography.labelMedium,
            color = color,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NutritionTablePreview() {
    CanteenCareTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            VerifiedBadge(isVerified = true)
            NutritionTable(Nutrition(650.0, 85.0, 30.0, 25.0, 1800.0, 6.0))
        }
    }
}
