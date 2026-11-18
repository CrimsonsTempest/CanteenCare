package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.Vendor

/** Data stan dan menu dari server. Semua fungsi suspend karena memanggil jaringan sekali jalan. */
interface CatalogRepository {
    suspend fun getVendors(): ApiResult<List<Vendor>>

    suspend fun getVendor(vendorId: Int): ApiResult<Vendor>

    suspend fun getVendorMenus(vendorId: Int): ApiResult<List<Menu>>

    suspend fun searchMenus(query: String): ApiResult<List<Menu>>
}
