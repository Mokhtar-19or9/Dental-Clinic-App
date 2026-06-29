package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.components.*
import com.example.dentalclinic.ui.theme.*

data class MedicalHistoryRecord(
    val type: String,
    val title: String,
    val date: String,
    val description: String,
    val image: String? = null,
    val analysisJson: String? = null
)

@Composable
fun MedicalHistoryScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val isAr = AppSettings.currentLanguage == "ar"
    val patientId = AppSettings.loggedInPatient?.id
    var records by remember { mutableStateOf<List<MedicalHistoryRecord>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(patientId) {
        isLoading = true
        if (!patientId.isNullOrBlank()) {
            try {
                val response = RetrofitClient.service.getRaysByPatient(patientId)
                if (response.isSuccessful) {
                    val rays = response.body() ?: emptyList()
                    records = rays.map { ray ->
                        MedicalHistoryRecord(
                            type = ray.name?.let {
                                val n = it.lowercase()
                                if (n.contains("diagnos") || n.contains("report") || n.contains("examination") || n.contains("تشخيص") || n.contains("تقرير") || n.contains("فحص"))
                                    "Diagnosis" else "X-Ray"
                            } ?: "X-Ray",
                            title = ray.name ?: (if (isAr) "سجل طبي" else "Medical Record"),
                            date = ray.createdAt?.substringBefore("T") ?: (if (isAr) "غير معروف" else "Unknown"),
                            description = ray.description ?: (if (isAr) "لا يوجد وصف" else "No description"),
                            image = ray.image,
                            analysisJson = ray.aiAnalysisJson
                        )
                    }.sortedByDescending { it.date }
                } else {
                    errorMsg = if (isAr) "فشل التحميل: ${response.code()}" else "Failed to load: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMsg = if (isAr) "خطأ: ${e.localizedMessage}" else "Error: ${e.localizedMessage}"
            }
        }
        isLoading = false
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DentalHeader(
            title = if (isAr) "التاريخ الطبي" else stringResource(R.string.medical_history),
            subtitle = if (isAr) "عرض جميع سجلاتك وإجراءاتك السابقة" else stringResource(R.string.medical_history_desc),
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            val patient = AppSettings.loggedInPatient
            if (patient != null) {
                DentalCard(Modifier.fillMaxWidth(), accentColor = DentalTeal) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.MedicalServices,
                            contentDescription = null,
                            tint = DentalTeal,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(if (isAr) "معلومات المريض" else "Patient Information", fontWeight = FontWeight.Bold, color = DentalTeal, fontSize = 14.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    if (!patient.fullName.isNullOrBlank()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (isAr) "الاسم" else "Name", color = DentalMuted, fontSize = 13.sp)
                            Text(patient.fullName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                    if (patient.age != null && patient.age != 0) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (isAr) "العمر" else "Age", color = DentalMuted, fontSize = 13.sp)
                            Text("${patient.age}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                    if (!patient.bloodType.isNullOrBlank()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (isAr) "فصيلة الدم" else "Blood Type", color = DentalMuted, fontSize = 13.sp)
                            Text(patient.bloodType, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                    if (!patient.chronicDiseases.isNullOrBlank()) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(if (isAr) "الحالات" else "Conditions", color = DentalMuted, fontSize = 13.sp)
                            Text(patient.chronicDiseases, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1)
                        }
                    }
                }
                Spacer(Modifier.height(20.dp))
            }

            if (isLoading) {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = DentalTeal)
                }
            } else if (errorMsg != null) {
                DentalCard(Modifier.fillMaxWidth()) {
                    Text(errorMsg ?: "", color = DentalError)
                }
            } else if (records.isEmpty()) {
                DentalCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🩻", fontSize = 36.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(if (isAr) "لا توجد سجلات طبية بعد" else "No medical records yet", fontWeight = FontWeight.Medium, color = DentalMuted)
                    }
                }
            } else {
                // Summary section
                val xrayCount = records.count { it.type == "X-Ray" }
                val diagnosisCount = records.count { it.type == "Diagnosis" }

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DentalCard(Modifier.weight(1f), accentColor = DentalBlueSoft) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🦷", fontSize = 28.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("$xrayCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DentalTeal)
                            Text(if (isAr) "أشعة" else "X-Rays", color = DentalMuted, fontSize = 11.sp)
                        }
                    }
                    DentalCard(Modifier.weight(1f), accentColor = DentalMint.copy(alpha = 0.3f)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📋", fontSize = 28.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("$diagnosisCount", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DentalTeal)
                            Text(if (isAr) "تشخيصات" else "Diagnoses", color = DentalMuted, fontSize = 11.sp)
                        }
                    }
                    DentalCard(Modifier.weight(1f), accentColor = DentalCardGreen) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("📅", fontSize = 28.sp)
                            Spacer(Modifier.height(4.dp))
                            Text("${records.size}", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = DentalTeal)
                            Text(if (isAr) "المجموع" else "Total", color = DentalMuted, fontSize = 11.sp)
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))

                Text(
                    if (isAr) "السجلات (${records.size})" else "Records (${records.size})",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                records.forEach { record ->
                    DentalCard(Modifier.fillMaxWidth()) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (record.type == "X-Ray") DentalBlueSoft
                                            else DentalMint.copy(alpha = 0.5f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        if (record.type == "X-Ray") "XR" else "DX",
                                        fontWeight = FontWeight.Bold,
                                        color = DentalTealDark,
                                        fontSize = 12.sp
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(record.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(record.date, color = DentalMuted, fontSize = 12.sp)
                                }
                            }

                            Spacer(Modifier.height(10.dp))
                            HorizontalDivider(color = DentalLine, thickness = 1.dp)
                            Spacer(Modifier.height(10.dp))

                            // Description / Findings
                            Text(
                                record.description,
                                color = DentalText,
                                fontSize = 13.sp,
                                lineHeight = 20.sp
                            )

                            // X-Ray image
                            if (!record.image.isNullOrBlank()) {
                                Spacer(Modifier.height(10.dp))
                                Text(if (isAr) "صورة الأشعة" else "Scan Image", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 12.sp)
                                Spacer(Modifier.height(6.dp))
                                RayImage(record.image)
                            }

                            // AI Analysis
                            if (record.analysisJson != null) {
                                Spacer(Modifier.height(10.dp))
                                HorizontalDivider(color = DentalLine, thickness = 1.dp)
                                Spacer(Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Filled.Analytics,
                                        contentDescription = null,
                                        tint = DentalTeal,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(6.dp))
                                    Text(if (isAr) "تحليل الذكاء الاصطناعي" else "AI Analysis", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 12.sp)
                                }
                                Spacer(Modifier.height(6.dp))

                                val analysisImg = extractAnalysisImage(record.analysisJson)
                                if (analysisImg != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                    ) {
                                        RayImage(analysisImg)
                                    }
                                } else {
                                    val analysisText = try {
                                        val gson = com.google.gson.Gson()
                                        val obj = gson.fromJson(record.analysisJson, Map::class.java)
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
                                        record.analysisJson
                                    }
                                    if (analysisText.isNotBlank()) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(DentalBlueSoft)
                                                .padding(12.dp)
                                        ) {
                                            Text(
                                                analysisText,
                                                color = DentalText,
                                                fontSize = 12.sp,
                                                lineHeight = 18.sp
                                            )
                                        }
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
