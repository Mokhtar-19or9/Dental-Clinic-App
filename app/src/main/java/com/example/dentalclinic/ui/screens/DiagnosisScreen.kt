package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.components.*
import com.example.dentalclinic.ui.theme.*

@Composable
fun DiagnosisScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    data class DiagnosisItem(
        val name: String,
        val description: String,
        val date: String,
        val analysisJson: String?,
        val image: String?
    )

    val isAr = AppSettings.currentLanguage == "ar"
    val patientId = AppSettings.loggedInPatient?.id
    var diagnoses by remember { mutableStateOf<List<DiagnosisItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(patientId) {
        isLoading = true
        if (!patientId.isNullOrBlank()) {
            try {
                val response = RetrofitClient.service.getRaysByPatient(patientId)
                if (response.isSuccessful) {
                    val rays = response.body() ?: emptyList()
                    diagnoses = rays.mapNotNull { ray ->
                        DiagnosisItem(
                            name = ray.name ?: (if (isAr) "فحص" else "Examination"),
                            description = ray.description ?: (if (isAr) "لا يوجد وصف" else "No description"),
                            date = ray.createdAt?.substringBefore("T") ?: (if (isAr) "غير معروف" else "Unknown"),
                            analysisJson = ray.aiAnalysisJson,
                            image = ray.image
                        )
                    }
                }
            } catch (_: Exception) {
            }
        }
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        DentalHeader(
            title = if (isAr) "التشخيص" else stringResource(R.string.diagnosis),
            subtitle = if (isAr) "عرض خطة العلاج والنتائج" else stringResource(R.string.treatment_plan_desc),
            onBack = onBack
        )

        Column(modifier = Modifier.padding(20.dp)) {
            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DentalTeal)
                }
            } else if (diagnoses.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🩺", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (isAr) "لا توجد تقارير تشخيص بعد" else "No Diagnosis Reports Yet",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        if (isAr) "بمجرد إتمام فحصك الأول، ستظهر تقارير العلاج الخاصة بك هنا." else "Once you complete your first examination, your treatment reports will appear here.",
                        color = DentalMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                diagnoses.forEach { diagnosis ->
                    DentalCard(Modifier.fillMaxWidth()) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Filled.Description,
                                    contentDescription = null,
                                    tint = DentalTeal,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(diagnosis.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(diagnosis.date, color = DentalMuted, style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            HorizontalDivider(color = DentalLine)
                            Spacer(Modifier.height(12.dp))

                            Text(if (isAr) "التشخيص والنتائج" else "Diagnosis & Findings", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                diagnosis.description,
                                color = MaterialTheme.colorScheme.onSurface,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 22.sp
                            )

                            if (!diagnosis.image.isNullOrBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Text(if (isAr) "صورة الفحص" else "Examination Image", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                RayImage(diagnosis.image)
                            }

                            if (diagnosis.analysisJson != null) {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = DentalLine)
                                Spacer(Modifier.height(12.dp))
                                Text(if (isAr) "تقرير تحليل الذكاء الاصطناعي" else "AI Analysis Report", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 14.sp)
                                Spacer(Modifier.height(8.dp))

                                val analysisImg = extractAnalysisImage(diagnosis.analysisJson)
                                if (analysisImg != null) {
                                    RayImage(analysisImg)
                                    Spacer(Modifier.height(12.dp))
                                }

                                val textContent = try {
                                    val gson = com.google.gson.Gson()
                                    val obj = gson.fromJson(diagnosis.analysisJson, Map::class.java)
                                    val textParts = mutableListOf<String>()
                                    for ((key, value) in obj) {
                                        val k = key.toString().lowercase()
                                        if (k !in listOf("image", "analysisimage", "analysis_image", "overlay",
                                                "heatmap", "mask", "resultimage", "result_image", "visualization",
                                                "predictionimage", "scanimage", "processedimage", "image_url")) {
                                            textParts.add("${key}: ${value}")
                                        }
                                    }
                                    textParts.joinToString("\n")
                                } catch (_: Exception) {
                                    diagnosis.analysisJson
                                }

                                if (textContent.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DentalBlueSoft)
                                            .padding(12.dp)
                                    ) {
                                        Text(
                                            textContent,
                                            color = DentalText,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        )
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
}
