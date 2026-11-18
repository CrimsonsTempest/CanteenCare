package com.kelompok10.canteencare.domain.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun `login valid tidak menghasilkan error`() {
        assertNull(AuthValidator.validateLogin("daffa@student.ub.ac.id", "pembeli123"))
    }

    @Test
    fun `login dengan email kosong, format salah, atau password kosong ditolak`() {
        assertEquals("Email wajib diisi.", AuthValidator.validateLogin("  ", "x"))
        assertEquals("Format email tidak valid.", AuthValidator.validateLogin("daffa", "x"))
        assertEquals("Password wajib diisi.", AuthValidator.validateLogin("a@b.id", ""))
    }

    @Test
    fun `daftar valid tidak menghasilkan error`() {
        assertNull(AuthValidator.validateRegister("Budi", "budi@ub.ac.id", "rahasia", "rahasia"))
    }

    @Test
    fun `daftar mengecek nama, panjang password, dan konfirmasi`() {
        assertEquals("Nama lengkap wajib diisi.", AuthValidator.validateRegister("", "b@ub.ac.id", "rahasia", "rahasia"))
        assertEquals("Password minimal 6 karakter.", AuthValidator.validateRegister("Budi", "b@ub.ac.id", "12345", "12345"))
        assertEquals("Konfirmasi password tidak sama.", AuthValidator.validateRegister("Budi", "b@ub.ac.id", "rahasia", "rahasia1"))
    }
}
