package com.kelompok10.canteencare.core.network

/**
 * Daftar endpoint backend untuk Retrofit (base URL: BuildConfig.BASE_URL).
 * Setiap fungsi adalah `suspend fun` sehingga dipanggil dari coroutine.
 * Endpoint ditambahkan bertahap sesuai fase: auth di Phase 5, katalog di Phase 6, dan seterusnya.
 */
interface ApiService
