package com.kelompok10.canteencare

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Titik awal aplikasi. @HiltAndroidApp menyalakan dependency injection Hilt. */
@HiltAndroidApp
class CanteenCareApp : Application()
