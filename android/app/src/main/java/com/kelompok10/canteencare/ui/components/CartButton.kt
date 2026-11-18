package com.kelompok10.canteencare.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/** Ikon keranjang dengan badge jumlah item. Badge disembunyikan jika keranjang kosong. */
@Composable
fun CartButton(itemCount: Int, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (itemCount > 0) Badge { Text(if (itemCount > 99) "99+" else itemCount.toString()) }
            },
        ) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = "Keranjang, $itemCount item")
        }
    }
}
