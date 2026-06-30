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
import android.util.Log
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.components.*
import com.example.dentalclinic.ui.theme.*

data class DiagnosisItem(
    val name: String,
    val description: String,
    val date: String,
    val analysisJson: String?,
    val image: String?
)


@Composable
fun DiagnosisScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {

    val isAr = AppSettings.currentLanguage == "ar"
    val patientId = AppSettings.loggedInPatient?.id
    var diagnoses by remember { mutableStateOf<List<DiagnosisItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(patientId) {
        isLoading = true
        Log.d("DIAG_DBG", "patientId='$patientId'")
        val rays = mutableListOf<com.example.dentalclinic.data.api.RayResponse>()
        if (!patientId.isNullOrBlank()) {
            try {
                val response = RetrofitClient.service.getRaysByPatient(patientId)
                Log.d("DIAG_DBG", "status=${response.code()}")
                if (response.isSuccessful) {
                    rays.addAll(response.body() ?: emptyList())
                } else {
                    Log.d("DIAG_DBG", "errorBody=${response.errorBody()?.string()?.take(300)}")
                }
            } catch (e: Exception) {
                Log.d("DIAG_DBG", "exception=${e.message}")
            }
        }
        // Fallback: try getAllRays if patient endpoint returned nothing
        if (rays.isEmpty()) {
            try {
                val all = RetrofitClient.service.getAllRays()
                if (all.isSuccessful) {
                    rays.addAll(all.body() ?: emptyList())
                    Log.d("DIAG_DBG", "fallback getAllRays returned ${rays.size} items")
                }
            } catch (_: Exception) {}
        }
        rays.forEachIndexed { i, ray ->
            Log.d("DIAG_DBG", "ray#$i: name=${ray.name}, desc=${ray.description}, analysisJson=${ray.aiAnalysisJson?.take(200)}, image=${ray.image?.take(100)}")
        }
        diagnoses = rays.mapNotNull { ray ->
            DiagnosisItem(
                name = ray.name ?: (if (isAr) "فحص" else "Examination"),
                description = ray.description ?: "",
                date = ray.createdAt?.substringBefore("T") ?: (if (isAr) "غير معروف" else "Unknown"),
                analysisJson = ray.aiAnalysisJson,
                image = ray.image
            )
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

                            val displayText = formatDisplayText(diagnosis, isAr)

                            Text(if (isAr) "التشخيص والنتائج" else "Diagnosis & Findings", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 14.sp)
                            Spacer(Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DentalBlueSoft)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    displayText,
                                    color = DentalText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp
                                )
                            }

                            if (!diagnosis.image.isNullOrBlank()) {
                                Spacer(Modifier.height(12.dp))
                                Text(if (isAr) "صورة الفحص" else "Examination Image", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                RayImage(diagnosis.image)
                            }

                            val analysisImg = diagnosis.analysisJson?.let { extractAnalysisImage(it) }
                            if (analysisImg != null) {
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = DentalLine)
                                Spacer(Modifier.height(12.dp))
                                Text(if (isAr) "صورة تحليل الذكاء الاصطناعي" else "AI Analysis Image", fontWeight = FontWeight.SemiBold, color = DentalTeal, fontSize = 14.sp)
                                Spacer(Modifier.height(8.dp))
                                RayImage(analysisImg)
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }
            }
        }
    }
}

private fun formatDisplayText(d: DiagnosisItem, isAr: Boolean): String {
    // Try analysisJson as structured AI data
    val fromAnalysis = d.analysisJson?.let { formatAiAnalysis(it, isAr) }
    if (!fromAnalysis.isNullOrBlank()) return fromAnalysis

    // Try description as free-text diagnosis
    val desc = d.description?.trim()
    if (!desc.isNullOrBlank() && desc != (if (isAr) "لا يوجد وصف" else "No description")) {
        return desc
    }

    // Combine whatever fields have data
    val parts = mutableListOf<String>()
    if (d.name.isNotBlank()) parts.add(d.name)
    if (d.date.isNotBlank()) parts.add(d.date)
    if (parts.isNotEmpty()) return parts.joinToString(" — ")

    // Ultimate fallback
    return if (isAr) "لا توجد بيانات تشخيص متاحة لهذا الفحص" else "No diagnosis data available for this examination"
}

private fun formatAiAnalysis(json: String?, isAr: Boolean): String {
    if (json.isNullOrBlank()) return ""

    val imageKeys = setOf("image", "analysisimage", "analysis_image", "overlay", "heatmap",
        "mask", "resultimage", "result_image", "visualization", "predictionimage",
        "scanimage", "processedimage", "image_url", "url", "file", "path", "imagepath",
        "originalimage", "croppedimage", "base64", "jpeg", "png")

    fun isEncodedToken(s: String): Boolean =
        s.length > 60 && !s.contains(" ") && s.all { it.isLetterOrDigit() || it == '/' || it == '+' || it == '=' || it == '-' || it == '_' }

    // Collect values from flattened JSON, grouped by key
    fun extractPairs(obj: Any?, depth: Int = 0): List<Pair<String, String>> {
        if (depth > 5) return emptyList()
        val result = mutableListOf<Pair<String, String>>()
        when (obj) {
            is Map<*, *> -> {
                for ((key, value) in obj) {
                    val k = key?.toString() ?: continue
                    if (k.lowercase() in imageKeys) continue
                    if (value is Map<*, *> || value is List<*>) {
                        result.addAll(extractPairs(value, depth + 1))
                    } else {
                        val v = value?.toString()?.trim() ?: continue
                        if (v.isBlank()) continue
                        if (v.startsWith("/") || v.startsWith("http") || v.startsWith("data:")) continue
                        if (isEncodedToken(v)) continue
                        result.add(k to v)
                    }
                }
            }
            is List<*> -> {
                for (item in obj) {
                    result.addAll(extractPairs(item, depth + 1))
                }
            }
            else -> {
                val v = obj?.toString()?.trim() ?: return result
                if (!isEncodedToken(v) && v.length > 3) result.add("" to v)
            }
        }
        return result
    }

    return try {
        val gson = com.google.gson.Gson()
        val parsed = gson.fromJson(json, Any::class.java)
        if (parsed is String) return parsed.trim().ifBlank { "" }

        val pairs = extractPairs(parsed)

        // Separate long descriptive text from short values
        val longTexts = pairs
            .filter { it.second.length > 30 && it.second.contains(" ") }
            .map { it.second }
        val shortPairs = pairs
            .filter { it.second.length <= 30 }

        buildString {
            // Descriptive text first
            if (longTexts.isNotEmpty()) {
                append(longTexts.joinToString("\n\n"))
                if (shortPairs.isNotEmpty()) append("\n\n")
            }

            // Short values → formatted bullet list
            if (shortPairs.isNotEmpty()) {
                val diagnosisVal = shortPairs.firstOrNull { p ->
                    p.first.lowercase() in setOf("predicted_class", "predictedclass", "class", "label", "diagnosis", "finding", "findings", "condition", "result")
                }
                val confidenceVal = shortPairs.firstOrNull { p ->
                    p.first.lowercase() in setOf("confidence", "score", "accuracy", "prob", "probability")
                }
                val otherVals = shortPairs.filter { p ->
                    p.first.isNotBlank() && p != diagnosisVal && p != confidenceVal &&
                    p.first.lowercase() !in imageKeys
                }

                if (isAr) append("نتيجة تحليل الأشعة:") else append("X-Ray Analysis Result:")
                if (diagnosisVal != null) {
                    append("\n• ${if (isAr) "التشخيص" else "Diagnosis"}: ${diagnosisVal.second}")
                } else if (shortPairs.size == 1) {
                    append("\n• ${shortPairs[0].second}")
                }
                if (confidenceVal != null) {
                    append("\n• ${if (isAr) "نسبة الثقة" else "Confidence"}: ${confidenceVal.second}")
                }
                for ((k, v) in otherVals) {
                    if (v.length > 2) append("\n• ${k.replace("_", " ").replaceFirstChar { it.uppercase() }}: $v")
                }
            }
        }.ifBlank {
            pairs.firstOrNull { it.second.contains(" ") && it.second.length > 5 }?.second ?: ""
        }
    } catch (_: Exception) {
        json.trim().removeSurrounding("\"").trim().ifBlank { "" }
    }
}
