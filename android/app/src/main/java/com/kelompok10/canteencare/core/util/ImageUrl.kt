package com.kelompok10.canteencare.core.util

import com.kelompok10.canteencare.BuildConfig
import java.net.URI

/**
 * Melengkapi alamat gambar dari server.
 *
 * Gambar hasil upload disimpan server sebagai path relatif, contoh `/uploads/menus/abc.png`,
 * karena alamat server berbeda antara emulator (10.0.2.2) dan HP (IP laptop).
 * Fungsi ini menggabungkannya dengan alamat server dari BASE_URL agar bisa dimuat Coil.
 */
object ImageUrl {

    fun resolve(url: String?, baseUrl: String = BuildConfig.BASE_URL): String? {
        val trimmed = url?.trim()
        if (trimmed.isNullOrEmpty()) return null
        if (!trimmed.startsWith("/")) return trimmed // sudah URL lengkap (http/https)
        val base = URI(baseUrl)
        val port = if (base.port == -1) "" else ":${base.port}"
        return "${base.scheme}://${base.host}$port$trimmed"
    }
}
