package com.kelompok10.canteencare.core.notification

import android.content.Intent

/**
 * Pesanan yang harus dibuka saat notifikasi diketuk.
 * Data ini dikirim backend di payload FCM: {"type": "ORDER_STATUS" | "NEW_ORDER", "order_id": "12"}.
 */
data class NotificationTarget(val type: String, val orderId: Int) {
    companion object {
        // Nama key sama dengan data payload dari backend
        const val EXTRA_TYPE = "type"
        const val EXTRA_ORDER_ID = "order_id"

        /**
         * Membaca target dari Intent yang membuka MainActivity.
         * Dipakai untuk dua kasus: notifikasi yang dibuat sistem saat aplikasi di background,
         * dan notifikasi yang kita buat sendiri saat aplikasi sedang dibuka.
         */
        fun fromIntent(intent: Intent?): NotificationTarget? {
            val extras = intent?.extras ?: return null
            val type = extras.getString(EXTRA_TYPE) ?: return null
            val orderId = extras.getString(EXTRA_ORDER_ID)?.toIntOrNull() ?: return null
            return NotificationTarget(type, orderId)
        }
    }
}
