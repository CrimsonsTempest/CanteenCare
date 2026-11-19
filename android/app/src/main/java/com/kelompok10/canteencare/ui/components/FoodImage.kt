package com.kelompok10.canteencare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import coil3.compose.SubcomposeAsyncImage
import com.kelompok10.canteencare.core.util.ImageUrl
import com.kelompok10.canteencare.domain.model.MenuCategory
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme

/** Ikon pengganti gambar sesuai jenisnya. */
enum class ImageKind(val icon: ImageVector) {
    FOOD(Icons.Filled.Restaurant),
    DRINK(Icons.Filled.LocalDrink),
    STALL(Icons.Filled.Storefront),
}

fun MenuCategory.imageKind(): ImageKind = if (this == MenuCategory.MINUMAN) ImageKind.DRINK else ImageKind.FOOD

/**
 * Gambar menu atau stan dari URL memakai Coil. Path hasil upload dilengkapi dulu oleh [ImageUrl].
 * Jika URL kosong atau gagal dimuat, tampil kotak hijau lembut dengan ikon makanan/minuman/stan.
 */
@Composable
fun FoodImage(imageUrl: String?, kind: ImageKind, modifier: Modifier = Modifier) {
    val shapedModifier = modifier.clip(MaterialTheme.shapes.medium)
    if (imageUrl.isNullOrBlank()) {
        ImagePlaceholder(kind, shapedModifier)
    } else {
        SubcomposeAsyncImage(
            model = ImageUrl.resolve(imageUrl),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = { ImagePlaceholder(kind, Modifier.fillMaxSize()) },
            error = { ImagePlaceholder(kind, Modifier.fillMaxSize()) },
            modifier = shapedModifier,
        )
    }
}

@Composable
private fun ImagePlaceholder(kind: ImageKind, modifier: Modifier) {
    Box(
        modifier = modifier.background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            kind.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
            modifier = Modifier.size(40.dp),
        )
    }
}

@Preview
@Composable
private fun FoodImagePreview() {
    CanteenCareTheme {
        FoodImage(imageUrl = null, kind = ImageKind.FOOD, modifier = Modifier.fillMaxWidth().size(120.dp))
    }
}
