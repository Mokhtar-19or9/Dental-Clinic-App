package com.example.dentalclinic.data

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.dentalclinic.data.api.PatientResponse
import com.example.dentalclinic.data.api.RayResponse
import com.example.dentalclinic.data.api.RetrofitClient
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ReportGenerator {

    fun generateReport(context: Context, patient: PatientResponse?, rays: List<RayResponse> = emptyList()): File {
        val html = buildHtml(patient, rays)
        val fileName = "medical_report_${System.currentTimeMillis()}.htm"
        val file = File(context.cacheDir, fileName)
        file.writeBytes(html.toByteArray(Charsets.UTF_8))
        return file
    }

    fun shareReport(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/html"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Share Report"))
    }

    private fun buildHtml(patient: PatientResponse?, rays: List<RayResponse>): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.US)
        val now = dateFormat.format(Date())
        val name = patient?.fullName ?: patient?.let { "${it.firstName ?: ""} ${it.lastName ?: ""}".trim() } ?: "Unknown"
        val age = patient?.age?.toString() ?: "—"
        val blood = patient?.bloodType ?: "—"
        val phone = patient?.phone ?: "—"
        val email = patient?.email ?: "—"
        val birthday = patient?.birthday ?: "—"
        val chronic = patient?.chronicDiseases ?: "—"
        val medicines = patient?.medicines ?: "—"
        val surgeries = patient?.surgeries ?: "—"

        val raysHtml = if (rays.isEmpty()) {
            "<p style='color: #999; font-size: 13px;'>No X-ray records found.</p>"
        } else {
            rays.joinToString("") { ray ->
                val imageUrl = RetrofitClient.getImageUrl(ray.image) ?: ""
                """
                <div class="ray-card">
                    <div class="ray-header">
                        <strong>${ray.name ?: "X-Ray Scan"}</strong>
                        <span style="float: right; color: #666;">${ray.createdAt?.substringBefore("T") ?: ""}</span>
                    </div>
                    <div style="margin-top: 10px;">
                        <img src="$imageUrl" alt="X-Ray Image" style="max-width: 100%; border-radius: 8px; border: 1px solid #ddd;">
                    </div>
                    <div class="diagnosis">
                        <strong>Diagnosis & Findings:</strong>
                        <p>${ray.description ?: "No description provided."}</p>
                    </div>
                </div>
                """
            }
        }

        return """
<html>
<head>
<meta charset="UTF-8">
<style>
    body { font-family: 'Segoe UI', Arial, sans-serif; color: #333; margin: 40px; }
    .header { text-align: center; border-bottom: 3px solid #0d9488; padding-bottom: 20px; margin-bottom: 30px; }
    .header h1 { color: #0d9488; margin: 0; font-size: 26px; }
    .header p { color: #666; margin: 5px 0 0 0; font-size: 13px; }
    h2 { color: #0d9488; border-left: 4px solid #0d9488; padding-left: 10px; margin-top: 25px; font-size: 18px; }
    table { width: 100%; border-collapse: collapse; margin: 10px 0; }
    td { padding: 8px 12px; border: 1px solid #ddd; font-size: 13px; }
    td.label { font-weight: bold; background: #f0fdfa; width: 35%; color: #333; }
    td.value { color: #555; }
    .ray-card { border: 1px solid #eee; border-radius: 12px; padding: 15px; margin-bottom: 20px; background: #fafafa; }
    .ray-header { border-bottom: 1px solid #eee; padding-bottom: 8px; margin-bottom: 10px; font-size: 14px; }
    .diagnosis { margin-top: 12px; font-size: 13px; padding: 10px; background: #fff; border-radius: 6px; border-left: 3px solid #0d9488; }
    .diagnosis p { margin: 5px 0 0 0; color: #444; line-height: 1.5; }
    .footer { margin-top: 40px; padding-top: 15px; border-top: 1px solid #ddd; font-size: 11px; color: #999; text-align: center; }
</style>
</head>
<body>
<div class="header">
    <h1>SmartCare Dental Clinic</h1>
    <p>Medical Report &bull; Generated: $now</p>
</div>

<h2>Patient Information</h2>
<table>
    <tr><td class="label">Full Name</td><td class="value">$name</td></tr>
    <tr><td class="label">Age</td><td class="value">$age</td></tr>
    <tr><td class="label">Blood Type</td><td class="value">$blood</td></tr>
    <tr><td class="label">Date of Birth</td><td class="value">$birthday</td></tr>
    <tr><td class="label">Phone</td><td class="value">$phone</td></tr>
    <tr><td class="label">Email</td><td class="value">$email</td></tr>
</table>

<h2>Medical History</h2>
<table>
    <tr><td class="label">Chronic Diseases</td><td class="value">$chronic</td></tr>
    <tr><td class="label">Medications</td><td class="value">$medicines</td></tr>
    <tr><td class="label">Surgeries</td><td class="value">$surgeries</td></tr>
</table>

<h2>X-Ray & Diagnosis Records</h2>
$raysHtml

<div class="footer">
    SmartCare Dental Clinic &bull; This report was generated automatically on $now &bull; For medical inquiries, please contact your dentist.
</div>
</body>
</html>
""".trimIndent()
    }
}
