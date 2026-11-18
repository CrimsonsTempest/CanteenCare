package com.kelompok10.canteencare

import android.app.Application
import com.kelompok10.canteencare.core.notification.NotificationChannels
import dagger.hilt.android.HiltAndroidApp

/** Titik awal aplikasi. @HiltAndroidApp menyalakan dependency injection Hilt. */
@HiltAndroidApp
class CanteenCareApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NotificationChannels.create(this)
    }
}
