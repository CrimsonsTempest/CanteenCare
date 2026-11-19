package com.kelompok10.canteencare.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ImageUrlTest {

    private val emulatorBase = "http://10.0.2.2:8000/api/v1/"

    @Test
    fun `path hasil upload dilengkapi dengan alamat server`() {
        assertEquals(
            "http://10.0.2.2:8000/uploads/menus/abc.png",
            ImageUrl.resolve("/uploads/menus/abc.png", emulatorBase),
        )
        assertEquals(
            "http://192.168.1.5:8000/uploads/menus/abc.png",
            ImageUrl.resolve("/uploads/menus/abc.png", "http://192.168.1.5:8000/api/v1/"),
        )
    }

    @Test
    fun `server tanpa port tidak menambahkan port`() {
        assertEquals("https://contoh.id/uploads/a.png", ImageUrl.resolve("/uploads/a.png", "https://contoh.id/api/v1/"))
    }

    @Test
    fun `url lengkap dipakai apa adanya`() {
        assertEquals("https://contoh.id/mie.jpg", ImageUrl.resolve("https://contoh.id/mie.jpg", emulatorBase))
    }

    @Test
    fun `kosong atau null berarti tanpa gambar`() {
        assertNull(ImageUrl.resolve(null, emulatorBase))
        assertNull(ImageUrl.resolve("  ", emulatorBase))
    }
}
