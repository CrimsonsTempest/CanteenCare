package com.kelompok10.canteencare.data.repository

import com.kelompok10.canteencare.core.network.ApiResult
import com.kelompok10.canteencare.domain.model.Menu
import com.kelompok10.canteencare.domain.model.MenuEdit
import com.kelompok10.canteencare.domain.model.MenuStatus
import com.kelompok10.canteencare.domain.model.Nutrition
import com.kelompok10.canteencare.domain.model.Seller
import com.kelompok10.canteencare.domain.model.SellerDraft

/** Fitur admin: verifikasi menu dan kelola akun penjual. */
interface AdminRepository {
    /** Semua menu; [status] null berarti semua status. */
    suspend fun getMenus(status: MenuStatus?): ApiResult<List<Menu>>

    suspend fun getMenu(menuId: Int): ApiResult<Menu>

    suspend fun updateMenu(menuId: Int, edit: MenuEdit): ApiResult<Menu>

    /** Simpan nilai gizi per porsi dan tandai sebagai terverifikasi admin. */
    suspend fun verifyNutrition(menuId: Int, nutrition: Nutrition): ApiResult<Menu>

    /** Setujui menu agar tampil untuk pembeli. Server menolak jika gizi belum diverifikasi. */
    suspend fun approveMenu(menuId: Int): ApiResult<Menu>

    suspend fun rejectMenu(menuId: Int): ApiResult<Menu>

    suspend fun getSellers(): ApiResult<List<Seller>>

    suspend fun createSeller(draft: SellerDraft): ApiResult<Seller>
}
