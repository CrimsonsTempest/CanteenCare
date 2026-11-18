package com.kelompok10.canteencare

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kelompok10.canteencare.core.notification.NotificationTarget
import com.kelompok10.canteencare.ui.navigation.CanteenCareNavHost
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow

/** Satu-satunya Activity. Semua layar adalah composable yang diatur oleh Navigation Compose. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    /** Pesanan dari notifikasi yang diketuk dan belum dibuka. */
    private val notificationTarget = MutableStateFlow<NotificationTarget?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Hanya saat pertama dibuat, bukan saat diputar, supaya pesanan tidak dibuka dua kali
        if (savedInstanceState == null) notificationTarget.value = NotificationTarget.fromIntent(intent)
        enableEdgeToEdge()
        setContent {
            CanteenCareTheme {
                val target by notificationTarget.collectAsStateWithLifecycle()
                CanteenCareNavHost(
                    notificationTarget = target,
                    onNotificationTargetHandled = { notificationTarget.value = null },
                )
            }
        }
    }

    /** Notifikasi diketuk saat aplikasi sudah terbuka (launchMode singleTop). */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        NotificationTarget.fromIntent(intent)?.let { notificationTarget.value = it }
    }
}
