package com.kelompok10.canteencare.core.util

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Semua tanggal dan jam di aplikasi ditampilkan dalam WIB (Asia/Jakarta), apa pun zona waktu HP-nya. */
object WibTime {
    val ZONE: ZoneId = ZoneId.of("Asia/Jakarta")

    private val indonesian = Locale.forLanguageTag("id-ID")
    private val dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy, HH.mm", indonesian)

    /** 2026-11-18T09:05:00Z → "18 Nov 2026, 16.05 WIB" */
    fun formatDateTime(dateTime: OffsetDateTime): String =
        "${dateTime.atZoneSameInstant(ZONE).format(dateTimeFormat)} WIB"
}
