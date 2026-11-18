package com.kelompok10.canteencare.core.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun `rupiah memakai titik sebagai pemisah ribuan`() {
        assertEquals("Rp0", Formatters.rupiah(0))
        assertEquals("Rp4.000", Formatters.rupiah(4000))
        assertEquals("Rp20.000", Formatters.rupiah(20000))
        assertEquals("Rp1.250.000", Formatters.rupiah(1_250_000))
    }

    @Test
    fun `angka gizi tanpa desimal jika bulat`() {
        assertEquals("650", Formatters.number(650.0))
        assertEquals("1.800", Formatters.number(1800.0))
        assertEquals("0", Formatters.number(0.0))
    }

    @Test
    fun `angka gizi dibulatkan satu desimal dengan koma`() {
        assertEquals("63,7", Formatters.number(63.72))
        assertEquals("12,5", Formatters.number(12.5))
        assertEquals("1.370,4", Formatters.number(1370.36))
    }

    @Test
    fun `angka gizi dengan satuan`() {
        assertEquals("650 kkal", Formatters.nutrient(650.0, "kkal"))
        assertEquals("1.800 mg", Formatters.nutrient(1800.0, "mg"))
    }
}
