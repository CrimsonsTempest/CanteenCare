package com.kelompok10.canteencare.core.notification

import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kelompok10.canteencare.MainActivity
import com.kelompok10.canteencare.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Menerima pesan FCM dari backend.
 *
 * - Aplikasi di background/tertutup: Android menampilkan notifikasi sendiri, dan saat diketuk
 *   MainActivity terbuka dengan data "type" dan "order_id" di intent extras.
 * - Aplikasi sedang dibuka: Android TIDAK menampilkan notifikasi otomatis, jadi kita buat sendiri
 *   di [onMessageReceived] dengan data yang sama.
 */
@AndroidEntryPoint
class CanteenCareMessagingService : FirebaseMessagingService() {

    @Inject lateinit var fcmTokenManager: FcmTokenManager

    /**
     * Token FCM perangkat berganti, jadi kirim token baru ke backend.
     * Ditandai usang di Firebase Messaging 26 (alasannya sama dengan di FcmTokenManager),
     * tapi tetap dipakai karena backend mengirim notifikasi berdasarkan token.
     */
    @Suppress("OVERRIDE_DEPRECATION")
    override fun onNewToken(token: String) {
        fcmTokenManager.syncToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: return
        val body = message.notification?.body.orEmpty()
        val type = message.data[NotificationTarget.EXTRA_TYPE]
        val orderId = message.data[NotificationTarget.EXTRA_ORDER_ID]

        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationTarget.EXTRA_TYPE, type)
            putExtra(NotificationTarget.EXTRA_ORDER_ID, orderId)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            orderId?.toIntOrNull() ?: 0, // request code berbeda per pesanan agar extras tidak tertimpa
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(this, NotificationChannels.ORDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        // Cek apakah notifikasi diizinkan user. Fungsi ini benar untuk semua versi Android:
        // di Android 13+ ia mengikuti izin POST_NOTIFICATIONS, di versi lama mengikuti pengaturan aplikasi.
        val notificationManager = NotificationManagerCompat.from(this)
        if (notificationManager.areNotificationsEnabled()) {
            try {
                notificationManager.notify(orderId?.toIntOrNull() ?: 0, notification)
            } catch (e: SecurityException) {
                // Izin dicabut tepat sebelum notifikasi tampil; cukup diabaikan
            }
        }
    }
}
