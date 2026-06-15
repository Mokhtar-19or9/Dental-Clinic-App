package com.example.dentalclinic

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.navigation.DentalApp
import com.example.dentalclinic.ui.theme.DentalClinicTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize AppSettings with context to enable persistence
        AppSettings.init(this)
        
        enableEdgeToEdge()
        setContent {
            val layoutDirection = if (AppSettings.currentLanguage == "ar") {
                LayoutDirection.Rtl
            } else {
                LayoutDirection.Ltr
            }

            // Update locale for string resources
            updateLocale(this, AppSettings.currentLanguage)

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                DentalClinicTheme(darkTheme = AppSettings.isDarkMode) {
                    DentalApp()
                }
            }
        }
    }

    private fun updateLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}
