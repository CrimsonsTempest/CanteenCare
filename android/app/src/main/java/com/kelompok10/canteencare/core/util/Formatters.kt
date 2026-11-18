package com.kelompok10.canteencare.core.util

import kotlin.math.abs
import kotlin.math.roundToLong

/** Format angka untuk tampilan dengan gaya Indonesia: titik pemisah ribuan, koma desimal. */
object Formatters {

    /** 20000 → "Rp20.000" */
    fun rupiah(amount: Int): String {
        val sign = if (amount < 0) "-" else ""
        return "${sign}Rp${groupThousands(abs(amount.toLong()))}"
    }

    /** Angka gizi dengan maksimal satu desimal: 650.0 → "650", 63.75 → "63,8", 1800.0 → "1.800" */
    fun number(value: Double): String {
        val tenths = (abs(value) * 10).roundToLong()
        val whole = groupThousands(tenths / 10)
        val decimal = tenths % 10
        val sign = if (value < 0 && tenths != 0L) "-" else ""
        return if (decimal == 0L) "$sign$whole" else "$sign$whole,$decimal"
    }

    /** Angka gizi beserta satuannya: (650.0, "kkal") → "650 kkal" */
    fun nutrient(value: Double, unit: String): String = "${number(value)} $unit"

    private fun groupThousands(value: Long): String =
        value.toString().reversed().chunked(3).joinToString(".").reversed()
}
