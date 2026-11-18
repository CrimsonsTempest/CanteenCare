package com.kelompok10.canteencare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kelompok10.canteencare.core.util.Formatters
import com.kelompok10.canteencare.domain.calculator.NutritionCalculator
import com.kelompok10.canteencare.domain.model.Nutrient
import com.kelompok10.canteencare.domain.model.NutrientKind
import com.kelompok10.canteencare.domain.model.NutrientProgress
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.NutritionTarget
import com.kelompok10.canteencare.domain.model.StatusTone
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import com.kelompok10.canteencare.ui.theme.StatusAmber
import com.kelompok10.canteencare.ui.theme.StatusGreen
import com.kelompok10.canteencare.ui.theme.StatusRed

/** Warna status: hijau baik, kuning perhatian, merah berlebih/melewati batas. */
fun StatusTone.color(): Color = when (this) {
    StatusTone.GOOD -> StatusGreen
    StatusTone.WARNING -> StatusAmber
    StatusTone.DANGER -> StatusRed
}

/**
 * Satu baris progres gizi harian: nama gizi, konsumsi/target, bar berwarna sesuai status,
 * persentase, dan keterangan sisa atau kelebihan.
 */
@Composable
fun NutrientProgressBar(progress: NutrientProgress, modifier: Modifier = Modifier) {
    val color = progress.status.tone.color()
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(progress.nutrient.label, style = MaterialTheme.typography.titleSmall)
                Text(
                    "${Formatters.number(progress.consumed)} / ${Formatters.nutrient(progress.target, progress.unit)}" +
                        if (progress.kind == NutrientKind.LIMIT) " (batas)" else " (target)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                progress.status.label,
                style = MaterialTheme.typography.labelMedium,
                color = color,
                modifier = Modifier
                    .background(color.copy(alpha = 0.14f), CircleShape)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )
        }
        LinearProgressIndicator(
            // Bar penuh maksimal 100%, kelebihan ditunjukkan lewat warna merah dan teks
            progress = { (progress.percentage / 100).toFloat().coerceIn(0f, 1f) },
            color = color,
            trackColor = color.copy(alpha = 0.18f),
            strokeCap = StrokeCap.Round,
            drawStopIndicator = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(CircleShape),
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                remainingText(progress),
                style = MaterialTheme.typography.bodySmall,
                color = if (progress.excess > 0) color else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                "${Formatters.number(progress.percentage)}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = color,
            )
        }
    }
}

/** "Sisa 780 kkal", "Melebihi batas 26 g", atau "Lebih 120 kkal dari target". */
private fun remainingText(progress: NutrientProgress): String {
    val unit = progress.unit
    return when {
        progress.excess <= 0 -> "Sisa ${Formatters.nutrient(progress.remaining, unit)}"
        progress.kind == NutrientKind.LIMIT -> "Melebihi batas ${Formatters.nutrient(progress.excess, unit)}"
        else -> "Lebih ${Formatters.nutrient(progress.excess, unit)} dari target"
    }
}

@Preview(showBackground = true)
@Composable
private fun NutrientProgressBarPreview() {
    val consumed = Nutrition(1370.0, 200.0, 55.0, 45.0, 2300.0, 76.0)
    val list = NutritionCalculator.calculateProgress(consumed, NutritionTarget.DEFAULT)
    CanteenCareTheme {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            list.filter { it.nutrient in setOf(Nutrient.CALORIES, Nutrient.PROTEIN, Nutrient.SUGAR) }
                .forEach { NutrientProgressBar(it) }
        }
    }
}
