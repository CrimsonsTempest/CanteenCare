package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.core.network.ApiService
import com.kelompok10.canteencare.core.network.safeApiCall
import com.kelompok10.canteencare.data.remote.dto.toDomain
import com.kelompok10.canteencare.data.remote.dto.toRequest
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuEdit
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Seller
import com.kelompok10.canteencare.domain.model.SellerDraft
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultAdminRepository @Inject constructor(
    private val api: ApiService,
) : AdminRepository {

    override suspend fun getMenus(status: MenuStatus?): ApiResult<List<Menu>> =
        safeApiCall { api.getAdminMenus(status?.name).map { it.toDomain() } }

    override suspend fun getMenu(menuId: Int): ApiResult<Menu> =
        safeApiCall { api.getMenu(menuId).toDomain() }

    override suspend fun updateMenu(menuId: Int, edit: MenuEdit): ApiResult<Menu> =
        safeApiCall { api.updateMenu(menuId, edit.toRequest()).toDomain() }

    override suspend fun verifyNutrition(menuId: Int, nutrition: Nutrition): ApiResult<Menu> =
        safeApiCall { api.verifyNutrition(menuId, nutrition.toRequest()).toDomain() }

    override suspend fun approveMenu(menuId: Int): ApiResult<Menu> =
        safeApiCall { api.approveMenu(menuId).toDomain() }

    override suspend fun rejectMenu(menuId: Int): ApiResult<Menu> =
        safeApiCall { api.rejectMenu(menuId).toDomain() }

    override suspend fun getSellers(): ApiResult<List<Seller>> =
        safeApiCall { api.getSellers().map { it.toDomain() } }

    override suspend fun createSeller(draft: SellerDraft): ApiResult<Seller> =
        safeApiCall { api.createSeller(draft.toRequest()).toDomain() }
}
