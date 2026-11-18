package com.kelompok10.canteencare.core.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.OffsetDateTime

class WibTimeTest {

    @Test
    fun `waktu UTC ditampilkan dalam WIB`() {
        val utc = OffsetDateTime.parse("2026-11-18T09:05:00Z")
        assertEquals("18 Nov 2026, 16.05 WIB", WibTime.formatDateTime(utc))
    }

    @Test
    fun `lewat tengah malam UTC tetap tanggal WIB yang benar`() {
        // 23.30 UTC tanggal 17 = 06.30 WIB tanggal 18
        val utc = OffsetDateTime.parse("2026-11-17T23:30:00+00:00")
        assertEquals("18 Nov 2026, 06.30 WIB", WibTime.formatDateTime(utc))
    }
}
