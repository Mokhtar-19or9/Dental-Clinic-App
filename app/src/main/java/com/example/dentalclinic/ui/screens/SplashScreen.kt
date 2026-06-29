package com.example.dentalclinic.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.PatientParser
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.theme.DentalCyan
import com.example.dentalclinic.ui.theme.DentalMint
import com.example.dentalclinic.ui.theme.DentalTeal
import com.example.dentalclinic.ui.theme.DentalTealDark
import com.google.gson.Gson
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: (Boolean) -> Unit) {
    LaunchedEffect(Unit) {
        val hasToken = AppSettings.jwtToken != null
        val gson = Gson()

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
            AppSettings.loggedInPatient != null
        }

        delay(2000)
        onTimeout(isLoggedIn)
    }

    val infiniteTransition = rememberInfiniteTransition()
    val toothScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )
    val taglineAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(DentalTealDark, DentalTeal, DentalCyan),
                    start = Offset(0f, 0f),
                    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(id = R.drawable.image_logo),
                contentDescription = null,
                modifier = Modifier
                    .size(160.dp)
                    .scale(toothScale)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                stringResource(R.string.app_name),
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 3.sp
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Keep Smiling!",
                color = Color.White.copy(alpha = taglineAlpha),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.sp
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(y = (-40).dp)
                .size(200.dp)
                .alpha(0.08f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(DentalMint, Color.Transparent),
                        radius = 200f
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp)
                .size(250.dp)
                .alpha(0.06f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color.White, Color.Transparent),
                        radius = 250f
                    )
                )
        )
    }
}
