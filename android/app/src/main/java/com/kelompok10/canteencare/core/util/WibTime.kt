package com.kelompok10.canteencare.core.util

import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Semua tanggal dan jam di aplikasi ditampilkan dalam WIB (Asia/Jakarta), apa pun zona waktu HP-nya. */
object WibTime {
    val ZONE: ZoneId = ZoneId.of("Asia/Jakarta")

    private val indonesian = Locale.forLanguageTag("id-ID")
    private val dateTimeFormat = DateTimeFormatter.ofPattern("d MMM yyyy, HH.mm", indonesian)
    private val longDateFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", indonesian)

    /** Tanggal hari ini menurut WIB. "Hari ini" untuk gizi harian selalu memakai ini. */
    fun today(): LocalDate = LocalDate.now(ZONE)

    /** 2026-11-18 → "Rabu, 18 November 2026" */
    fun formatLongDate(date: LocalDate): String = date.format(longDateFormat)

    /** 2026-11-18T09:05:00Z → "18 Nov 2026, 16.05 WIB" */
    fun formatDateTime(dateTime: OffsetDateTime): String =
        "${dateTime.atZoneSameInstant(ZONE).format(dateTimeFormat)} WIB"
}
