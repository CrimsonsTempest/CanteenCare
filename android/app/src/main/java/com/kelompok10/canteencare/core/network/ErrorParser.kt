package com.kelompok10.canteencare.core.network

import com.google.gson.Gson
import com.google.gson.JsonParseException

/**
 * Membaca body error dari backend yang selalu berbentuk:
 * {"detail": {"code": "INSUFFICIENT_STOCK", "message": "Stok ... tersisa 2."}}
 * Pesan dari server sudah berbahasa Indonesia, jadi bisa langsung ditampilkan.
 */
object ErrorParser {
    const val NETWORK_ERROR_MESSAGE = "Tidak dapat terhubung ke server. Periksa koneksi lalu coba lagi."
    const val UNKNOWN_ERROR_MESSAGE = "Terjadi kesalahan pada server. Silakan coba lagi."

    private data class ErrorDetail(val code: String?, val message: String?)
    private data class ErrorBody(val detail: ErrorDetail?)

    private val gson = Gson()

    fun parse(httpStatus: Int, body: String?): ApiResult.Error {
        val detail = try {
            body?.let { gson.fromJson(it, ErrorBody::class.java)?.detail }
        } catch (e: JsonParseException) {
            null // body bukan format error standar, misalnya halaman HTML
        }
        return ApiResult.Error(
            message = detail?.message ?: UNKNOWN_ERROR_MESSAGE,
            code = detail?.code,
            httpStatus = httpStatus,
        )
    }
}
