package com.example.dentalclinic.ui.screens

import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.RayResponse
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.components.*
import com.example.dentalclinic.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun XRayScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}) {
    val patientId = AppSettings.loggedInPatient?.id
    val isAr = AppSettings.currentLanguage == "ar"
    var rays by remember { mutableStateOf<List<RayResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var selectedImage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(patientId) {
        isLoading = true
        try {
            if (!patientId.isNullOrBlank()) {
                val response = RetrofitClient.service.getRaysByPatient(patientId)
                if (response.isSuccessful) {
                    rays = response.body() ?: emptyList()
                } else {
                    errorMsg = if (isAr) "فشل في جلب البيانات: ${response.code()}" else "Failed to fetch: ${response.code()}"
                }
            } else {
                errorMsg = if (isAr) "لم يتم العثور على معرف المريض. يرجى تسجيل الخروج والدخول مجدداً." else "Patient ID not found. Please logout and login again."
            }
        } catch (_: Exception) {
            errorMsg = if (isAr) "خطأ في الاتصال بالسيرفر" else "Server connection error"
        } finally {
            isLoading = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(DentalTealDark)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        if (isAr) "سجلات الأشعة" else stringResource(R.string.xray_records),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(
                        if (isAr) "عرض صور الأشعة والتحليل الذكي" else "View your X-Ray scans and AI analysis",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Spacer(Modifier.height(8.dp))
            SectionHeader(if (isAr) "صور الأشعة" else "X-Ray Scans")

            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DentalTeal)
                }
            } else if (errorMsg != null) {
                Spacer(Modifier.height(20.dp))
                DentalCard(Modifier.fillMaxWidth()) {
                    Text(
                        errorMsg ?: "",
                        color = DentalError,
                        modifier = Modifier.padding(16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else if (rays.isEmpty()) {
                Spacer(Modifier.height(40.dp))
                DentalCard(Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Filled.Image,
                            contentDescription = null,
                            tint = DentalMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (isAr) "لا توجد صور أشعة متاحة حالياً" else "No X-Ray images available yet",
                            fontWeight = FontWeight.Medium,
                            color = DentalMuted
                        )
                        Text(
                            if (isAr) "ستظهر صورك هنا بعد موعدك الأول." else "Your scans will appear here after your first appointment.",
                            color = DentalMuted.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                rays.forEach { record ->
                    DentalCard(Modifier.fillMaxWidth()) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBubble("XR", background = DentalBlueSoft)
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(record.name ?: (if (isAr) "صورة أشعة" else "X-Ray Scan"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    Text(record.createdAt?.substringBefore("T") ?: "", color = DentalMuted, fontSize = 12.sp)
                                }
                            }

                            if (!record.image.isNullOrBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedImage = record.image }
                                ) {
                                    Log.d("RayImage", "image=${record.image?.take(200)} len=${record.image?.length}")
                                    RayImage(record.image)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(6.dp)
                                    ) {
                                        Icon(Icons.Filled.ZoomIn, contentDescription = "Zoom", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }

                            if (!record.description.isNullOrBlank()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "${if (isAr) "النتائج" else "Findings"}: ${record.description}",
                                    color = DentalMuted,
                                    fontSize = 13.sp
                                )
                            }

                            val analysisImg = extractAnalysisImage(record.aiAnalysisJson)
                            if (analysisImg != null) {
                                Spacer(Modifier.height(12.dp))
                                Text(if (isAr) "تحليل الذكاء الاصطناعي" else "AI Analysis", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectedImage = analysisImg }
                                ) {
                                    RayImage(analysisImg)
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(8.dp)
                                            .clip(RoundedCornerShape(50))
                                            .background(Color.Black.copy(alpha = 0.5f))
                                            .padding(6.dp)
                                    ) {
                                        Icon(Icons.Filled.ZoomIn, contentDescription = "Zoom", tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }

    if (selectedImage != null) {
        AlertDialog(
            onDismissRequest = { selectedImage = null },
            title = {},
            text = {
                RayImage(selectedImage)
            },
            confirmButton = {
                TextButton(onClick = { selectedImage = null }) {
                    Text(if (isAr) "إغلاق" else "Close", color = DentalTeal)
                }
            }
        )
    }
}
