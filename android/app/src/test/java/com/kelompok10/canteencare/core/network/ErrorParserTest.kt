package com.kelompok10.canteencare.core.network

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ErrorParserTest {

    @Test
    fun `membaca pesan dan kode dari format error standar backend`() {
        val body = """{"detail":{"code":"INSUFFICIENT_STOCK","message":"Stok Mie Ayam Solo tidak mencukupi, tersisa 2."}}"""

        val error = ErrorParser.parse(409, body)

        assertEquals("Stok Mie Ayam Solo tidak mencukupi, tersisa 2.", error.message)
        assertEquals("INSUFFICIENT_STOCK", error.code)
        assertEquals(409, error.httpStatus)
        assertFalse(error.isUnauthorized)
    }

    @Test
    fun `status 401 ditandai sebagai sesi tidak berlaku`() {
        val body = """{"detail":{"code":"UNAUTHORIZED","message":"Sesi tidak valid, silakan login lagi."}}"""

        assertTrue(ErrorParser.parse(401, body).isUnauthorized)
    }

    @Test
    fun `body bukan JSON memakai pesan umum`() {
        val error = ErrorParser.parse(502, "<html>Bad Gateway</html>")

        assertEquals(ErrorParser.UNKNOWN_ERROR_MESSAGE, error.message)
        assertNull(error.code)
    }

    @Test
    fun `body kosong memakai pesan umum`() {
        assertEquals(ErrorParser.UNKNOWN_ERROR_MESSAGE, ErrorParser.parse(500, null).message)
    }
}
