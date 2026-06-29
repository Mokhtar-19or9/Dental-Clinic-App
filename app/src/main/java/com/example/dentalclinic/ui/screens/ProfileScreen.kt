package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.PatientResponse
import com.example.dentalclinic.data.api.PatientUpdateRequest
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.components.PrimaryDentalButton
import com.example.dentalclinic.ui.components.SectionHeader
import com.example.dentalclinic.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val isAr = AppSettings.currentLanguage == "ar"
    val patient by remember { derivedStateOf { AppSettings.loggedInPatient } }
    val patientData = patient
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val displayName: String = patientData?.let { p ->
        if (!p.fullName.isNullOrBlank()) p.fullName
        else if (!p.firstName.isNullOrBlank() || !p.lastName.isNullOrBlank()) {
            "${p.firstName ?: ""} ${p.lastName ?: ""}".trim()
        } else {
            p.email?.split("@")?.firstOrNull() ?: "User"
        }
    } ?: "User"

    val initials = displayName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
        .ifEmpty { "U" }

    val displayAge = patientData?.age?.let { if (it != 0) (if (isAr) "$it سنة" else "$it years") else null } ?: (if (isAr) "غير متوفر" else "N/A")
    val displayPhone = patientData?.phone?.takeIf { it.isNotBlank() } ?: (if (isAr) "غير متوفر" else "N/A")
    val displayEmail = patientData?.email?.takeIf { it.isNotBlank() } ?: (if (isAr) "غير متوفر" else "N/A")
    val displayBloodType = patientData?.bloodType?.takeIf { it.isNotBlank() } ?: (if (isAr) "غير متوفر" else "N/A")
    val displayConditions = patientData?.chronicDiseases?.takeIf { it.isNotBlank() } ?: (if (isAr) "لا يوجد" else "None")
    val displayMeds = patientData?.medicines?.takeIf { it.isNotBlank() } ?: (if (isAr) "لا يوجد" else "None")
    val displaySurgeries = patientData?.surgeries?.takeIf { it.isNotBlank() } ?: (if (isAr) "لا يوجد" else "None")
    val displayBirthday = patientData?.birthday?.substringBefore("T")?.takeIf { it.isNotBlank() } ?: (if (isAr) "غير متوفر" else "N/A")

    var isEditing by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var editName by remember(patient) { mutableStateOf(patient?.fullName ?: "") }
    var editPhone by remember(patient) { mutableStateOf(patient?.phone ?: "") }
    var editBloodType by remember(patient) { mutableStateOf(patient?.bloodType ?: "") }
    var editChronicDiseases by remember(patient) { mutableStateOf(patient?.chronicDiseases ?: "") }
    var editMedicines by remember(patient) { mutableStateOf(patient?.medicines ?: "") }
    var editSurgeries by remember(patient) { mutableStateOf(patient?.surgeries ?: "") }
    var editBirthday by remember(patient) { mutableStateOf(patient?.birthday ?: "") }

    LaunchedEffect(isEditing) {
        if (!isEditing) {
            editName = patient?.fullName ?: ""
            editPhone = patient?.phone ?: ""
            editBloodType = patient?.bloodType ?: ""
            editChronicDiseases = patient?.chronicDiseases ?: ""
            editMedicines = patient?.medicines ?: ""
            editSurgeries = patient?.surgeries ?: ""
            editBirthday = patient?.birthday ?: ""
        }
    }

    fun saveChanges() {
        val currentPatient = AppSettings.loggedInPatient ?: return
        val updated = PatientResponse(
            id = currentPatient.id,
            userId = currentPatient.userId,
            firstName = currentPatient.firstName,
            lastName = currentPatient.lastName,
            fullName = editName.ifBlank { currentPatient.fullName },
            email = currentPatient.email,
            age = currentPatient.age,
            phone = editPhone.ifBlank { currentPatient.phone },
            image = currentPatient.image,
            birthday = editBirthday.ifBlank { currentPatient.birthday },
            chronicDiseases = editChronicDiseases.ifBlank { null },
            medicines = editMedicines.ifBlank { null },
            surgeries = editSurgeries.ifBlank { null },
            bloodType = editBloodType.ifBlank { null }
        )
        AppSettings.savePatient(updated)
        isEditing = false
        scope.launch { snackbarHostState.showSnackbar(if (isAr) "تم تحديث الملف الشخصي بنجاح" else "Profile updated successfully") }

        val pid = currentPatient.id
        if (pid.isNotBlank()) {
            scope.launch {
                isSaving = true
                try {
                    val updateReq = PatientUpdateRequest(
                        patientId = pid,
                        fullName = editName.ifBlank { null },
                        phoneNumber = editPhone.ifBlank { null },
                        birthday = editBirthday.ifBlank { null },
                        chronicDiseases = editChronicDiseases.ifBlank { null },
                        medicines = editMedicines.ifBlank { null },
                        surgeries = editSurgeries.ifBlank { null },
                        bloodType = editBloodType.ifBlank { null }
                    )
                    val response = RetrofitClient.service.updatePatient(updateReq)
                    if (response.isSuccessful) {
                        val serverPatient = response.body()
                        if (serverPatient != null) {
                            AppSettings.savePatient(serverPatient)
                        }
                    }
                } catch (_: Exception) {}
                isSaving = false
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(DentalBackground)
                .verticalScroll(rememberScrollState())
                .padding(padding),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(DentalTealDark, DentalTeal, DentalGradientEnd)
                        )
                    )
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(DentalCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 28.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(displayName, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineMedium)
                    Text(if (isAr) "ملف المريض" else "Patient Profile", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodyMedium)
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SectionHeader(if (isAr) "معلومات شخصية" else "Personal Information")
                    if (!isEditing) {
                        FilledTonalIconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = DentalTeal)
                        }
                    }
                }
                Spacer(Modifier.height(10.dp))
                DentalCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = DentalTeal
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text(if (isAr) "الاسم الكامل" else "Full Name") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editPhone,
                            onValueChange = { editPhone = it },
                            label = { Text(if (isAr) "الهاتف" else "Phone") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editBirthday,
                            onValueChange = { editBirthday = it },
                            label = { Text(if (isAr) "تاريخ الميلاد (yyyy-MM-dd)" else "Birthday (yyyy-MM-dd)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    } else {
                        ProfileRow(if (isAr) "العمر" else "Age", displayAge)
                        HorizontalDivider(color = DentalLine, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow(if (isAr) "الهاتف" else "Phone", displayPhone)
                        HorizontalDivider(color = DentalLine, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow(if (isAr) "البريد الإلكتروني" else "Email", displayEmail)
                        HorizontalDivider(color = DentalLine, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow(if (isAr) "تاريخ الميلاد" else "Birthday", displayBirthday)
                    }
                }

                Spacer(Modifier.height(20.dp))
                SectionHeader(if (isAr) "معلومات طبية" else "Medical Information")
                Spacer(Modifier.height(10.dp))
                DentalCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = DentalSuccess
                ) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editBloodType,
                            onValueChange = { editBloodType = it },
                            label = { Text(if (isAr) "فصيلة الدم" else "Blood Type") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editChronicDiseases,
                            onValueChange = { editChronicDiseases = it },
                            label = { Text(if (isAr) "الأمراض المزمنة" else "Chronic Diseases") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editMedicines,
                            onValueChange = { editMedicines = it },
                            label = { Text(if (isAr) "الأدوية" else "Medicines") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editSurgeries,
                            onValueChange = { editSurgeries = it },
                            label = { Text(if (isAr) "العمليات الجراحية" else "Surgeries") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        ProfileRow(if (isAr) "فصيلة الدم" else "Blood Type", displayBloodType)
                        HorizontalDivider(color = DentalLine, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow(if (isAr) "الأمراض المزمنة" else "Chronic Diseases", displayConditions)
                        HorizontalDivider(color = DentalLine, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow(if (isAr) "الأدوية" else "Medicines", displayMeds)
                        HorizontalDivider(color = DentalLine, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
                        ProfileRow(if (isAr) "العمليات الجراحية" else "Surgeries", displaySurgeries)
                    }
                }

                Spacer(Modifier.height(24.dp))

                if (isEditing) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSaving
                        ) {
                            Icon(Icons.Filled.Close, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isAr) "إلغاء" else "Cancel")
                        }
                        PrimaryDentalButton(
                            text = if (isSaving) (if (isAr) "جاري الحفظ..." else "Saving...") else (if (isAr) "حفظ التغييرات" else "Save Changes"),
                            modifier = Modifier.weight(1f),
                            onClick = { saveChanges() },
                            enabled = !isSaving
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = {
                                AppSettings.logout()
                                onLogout()
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = DentalError)
                        ) {
                            Icon(Icons.Filled.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isAr) "تسجيل الخروج" else "Logout")
                        }
                        Button(
                            onClick = { isEditing = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DentalTeal)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(if (isAr) "تحديث الملف" else "Update Profile")
                        }
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = DentalMuted, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
    }
}
