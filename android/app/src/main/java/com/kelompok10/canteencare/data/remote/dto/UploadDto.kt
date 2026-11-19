package com.kelompok10.canteencare.data.remote.dto

/** Balasan POST /uploads/menu-image: path gambar di server, contoh `/uploads/menus/abc.png`. */
data class ImageUploadDto(
    val imageUrl: String,
)
