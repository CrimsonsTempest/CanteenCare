package com.kelompok10.canteencare.data.repository

import android.net.Uri
import com.kelompok10.canteencare.core.network.ApiResult

/** Upload gambar menu dari galeri HP ke server. Dipakai penjual (menu baru) dan admin (edit menu). */
interface ImageRepository {
    /** Mengembalikan path gambar di server, siap disimpan sebagai `imageUrl` menu. */
    suspend fun uploadMenuImage(uri: Uri): ApiResult<String>
}
