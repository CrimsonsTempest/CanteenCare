package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.core.network.ApiService
import com.kelompok10.canteencare.core.network.safeApiCall
import com.kelompok10.canteencare.data.remote.dto.toDomain
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.Vendor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultCatalogRepository @Inject constructor(
    private val api: ApiService,
) : CatalogRepository {

    override suspend fun getVendors(): ApiResult<List<Vendor>> =
        safeApiCall { api.getVendors().map { it.toDomain() } }

    override suspend fun getVendor(vendorId: Int): ApiResult<Vendor> =
        safeApiCall { api.getVendor(vendorId).toDomain() }

    override suspend fun getVendorMenus(vendorId: Int): ApiResult<List<Menu>> =
        safeApiCall { api.getVendorMenus(vendorId).map { it.toDomain() } }

    override suspend fun searchMenus(query: String): ApiResult<List<Menu>> =
        safeApiCall { api.searchMenus(query).map { it.toDomain() } }
}
