package com.kelompok10.canteencare.data.repository

import android.content.Context
import android.net.Uri
import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.core.network.ApiService
import com.kelompok10.canteencare.core.network.safeApiCall
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultImageRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: ApiService,
) : ImageRepository {

    override suspend fun uploadMenuImage(uri: Uri): ApiResult<String> {
        val resolver = context.contentResolver
        // Baca isi file dari galeri di thread IO, karena membaca file tidak boleh di main thread
        val bytes = try {
            withContext(Dispatchers.IO) {
                resolver.openInputStream(uri)?.use { it.readBytes() }
            }
        } catch (e: IOException) {
            null
        } ?: return ApiResult.Error("Gambar tidak dapat dibaca. Coba pilih gambar lain.")

        // Cek ukuran di HP dulu supaya tidak membuang kuota untuk file yang pasti ditolak server
        if (bytes.size > MAX_IMAGE_BYTES) return ApiResult.Error("Ukuran gambar maksimal 2 MB.")

        val mimeType = resolver.getType(uri) ?: "image/*"
        val part = MultipartBody.Part.createFormData(
            name = "file",
            filename = "menu-image",
            body = bytes.toRequestBody(mimeType.toMediaTypeOrNull()),
        )
        return safeApiCall { api.uploadMenuImage(part).imageUrl }
    }

    private companion object {
        const val MAX_IMAGE_BYTES = 2 * 1024 * 1024 // sama dengan batas di server
    }
}
