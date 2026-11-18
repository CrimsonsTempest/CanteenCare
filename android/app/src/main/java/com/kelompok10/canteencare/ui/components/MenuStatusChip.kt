package com.kelompok10.canteencare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.ui.theme.StatusAmber
import com.kelompok10.canteencare.ui.theme.StatusGreen
import com.kelompok10.canteencare.ui.theme.StatusRed

/** Label status menu: hijau tampil, kuning menunggu verifikasi, merah ditolak, abu-abu diarsipkan. */
@Composable
fun MenuStatusChip(status: MenuStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        MenuStatus.APPROVED -> StatusGreen
        MenuStatus.PENDING -> StatusAmber
        MenuStatus.REJECTED -> StatusRed
        MenuStatus.ARCHIVED -> Color.Gray
    }
    Text(
        status.label,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        modifier = modifier
            .background(color.copy(alpha = 0.14f), CircleShape)
            .padding(horizontal = 10.dp, vertical = 4.dp),
    )
}
