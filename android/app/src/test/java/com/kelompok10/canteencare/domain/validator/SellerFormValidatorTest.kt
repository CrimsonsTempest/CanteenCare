package com.kelompok10.canteencare.domain.validator

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SellerFormValidatorTest {

    @Test
    fun `isian lengkap dan benar tidak punya error`() {
        val invalid = SellerFormValidator.invalidFields("Pak Joko", "joko@canteencare.id", "penjual123", "Soto Pak Joko")
        assertTrue(invalid.isEmpty())
    }

    @Test
    fun `semua kolom salah terdeteksi sekaligus`() {
        val invalid = SellerFormValidator.invalidFields(" ", "joko", "123", "")
        assertEquals(SellerField.entries.toSet(), invalid)
    }

    @Test
    fun `password minimal enam karakter`() {
        assertEquals(
            setOf(SellerField.PASSWORD),
            SellerFormValidator.invalidFields("Pak Joko", "joko@canteencare.id", "12345", "Soto Pak Joko"),
        )
    }
}
