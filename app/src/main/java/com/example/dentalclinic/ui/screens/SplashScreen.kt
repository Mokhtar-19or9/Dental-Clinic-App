package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.PatientParser
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.components.FunnyToothMascot
import com.example.dentalclinic.ui.theme.DentalTeal
import com.example.dentalclinic.ui.theme.DentalTealDark
import com.google.gson.Gson
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        val hasToken = AppSettings.jwtToken != null
        val gson = Gson()

        // Try to fetch current patient during splash (only if we have a token)
        val response = if (hasToken) {
            try {
                RetrofitClient.service.getCurrentPatient()
            } catch (e: Exception) {
                null
            }
        } else null

        val isLoggedIn = if (response?.isSuccessful == true) {
            val rawBody = response.body()?.string()
            if (!rawBody.isNullOrBlank()) {
                val p = PatientParser.parsePatient(rawBody, gson)
                if (p != null) AppSettings.savePatient(p)
            }
            AppSettings.loggedInPatient != null
        } else {
            // Check if we have a persisted patient even if API fails (offline mode)
            AppSettings.loggedInPatient != null
        }

        delay(2000) 
        onTimeout(isLoggedIn)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DentalTealDark, DentalTeal))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            FunnyToothMascot(
                modifier = Modifier.size(150.dp),
                style = AppSettings.mascotStyle
            )
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            
            Text(
                "Keep Smiling!",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
