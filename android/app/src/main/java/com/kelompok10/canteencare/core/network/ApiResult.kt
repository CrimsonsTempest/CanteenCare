package com.kelompok10.canteencare.core.network

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException

/** Hasil pemanggilan API: berhasil membawa data, atau gagal membawa pesan yang siap ditampilkan. */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>

    data class Error(
        val message: String,
        val code: String? = null,
        val httpStatus: Int? = null,
    ) : ApiResult<Nothing> {
        /** 401 berarti token tidak berlaku lagi, jadi user harus login ulang. */
        val isUnauthorized: Boolean get() = httpStatus == 401
    }
}

/**
 * Membungkus panggilan Retrofit agar exception berubah menjadi [ApiResult.Error].
 * CancellationException dilempar ulang supaya pembatalan coroutine tetap berjalan normal.
 */
suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> =
    try {
        ApiResult.Success(call())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        ErrorParser.parse(e.code(), e.response()?.errorBody()?.string())
    } catch (e: IOException) {
        ApiResult.Error(ErrorParser.NETWORK_ERROR_MESSAGE)
    }
