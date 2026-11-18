package com.kelompok10.canteencare.domain.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TargetValidatorTest {

    @Test
    fun `angka bulat dan desimal diterima`() {
        assertEquals(2150.0, TargetValidator.parse("2150")!!, 0.0)
        assertEquals(62.5, TargetValidator.parse("62,5")!!, 0.0)
        assertEquals(62.5, TargetValidator.parse(" 62.5 ")!!, 0.0)
    }

    @Test
    fun `nol, negatif, kosong, dan huruf ditolak`() {
        assertNull(TargetValidator.parse("0"))
        assertNull(TargetValidator.parse("-10"))
        assertNull(TargetValidator.parse(""))
        assertNull(TargetValidator.parse("abc"))
        assertNull(TargetValidator.parse("1.2.3"))
    }

    @Test
    fun `nilai ditampilkan di form tanpa desimal jika bulat`() {
        assertEquals("2150", TargetValidator.toInput(2150.0))
        assertEquals("62.5", TargetValidator.toInput(62.5))
    }
}
