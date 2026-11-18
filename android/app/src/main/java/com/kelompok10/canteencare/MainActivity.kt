package com.kelompok10.canteencare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelompok10.canteencare.ui.navigation.CanteenCareNavHost
import com.kelompok10.canteencare.ui.theme.CanteenCareTheme
import dagger.hilt.android.AndroidEntryPoint

/** Satu-satunya Activity. Semua layar adalah composable yang diatur oleh Navigation Compose. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CanteenCareTheme {
                CanteenCareNavHost()
            }
        }
    }
}
