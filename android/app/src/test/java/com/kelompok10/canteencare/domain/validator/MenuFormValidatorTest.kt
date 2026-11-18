package com.kelompok10.canteencare.domain.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuFormValidatorTest {

    @Test
    fun `harga dan stok harus bilangan bulat nol atau lebih`() {
        assertEquals(20000, MenuFormValidator.parseNonNegativeInt("20000"))
        assertEquals(0, MenuFormValidator.parseNonNegativeInt(" 0 "))
        assertNull(MenuFormValidator.parseNonNegativeInt("-1"))
        assertNull(MenuFormValidator.parseNonNegativeInt("12.5"))
        assertNull(MenuFormValidator.parseNonNegativeInt(""))
        assertNull(MenuFormValidator.parseNonNegativeInt("9999999999")) // terlalu besar
    }

    @Test
    fun `gizi boleh desimal dan boleh nol`() {
        assertEquals(12.5, MenuFormValidator.parseNonNegativeDecimal("12,5")!!, 0.0)
        assertEquals(0.0, MenuFormValidator.parseNonNegativeDecimal("0")!!, 0.0)
        assertNull(MenuFormValidator.parseNonNegativeDecimal("-3"))
        assertNull(MenuFormValidator.parseNonNegativeDecimal("abc"))
    }

    @Test
    fun `url gambar opsional tapi harus http atau https`() {
        assertTrue(MenuFormValidator.isValidImageUrl(""))
        assertTrue(MenuFormValidator.isValidImageUrl("https://contoh.id/mie.jpg"))
        assertFalse(MenuFormValidator.isValidImageUrl("mie.jpg"))
    }
}
