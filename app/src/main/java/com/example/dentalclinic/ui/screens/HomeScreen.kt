package com.example.dentalclinic.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.ReportGenerator
import com.example.dentalclinic.data.api.RayResponse
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.components.*
import com.example.dentalclinic.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onOpenAppointments: () -> Unit,
    onOpenXRay: () -> Unit,
    onOpenDiagnosis: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenChat: () -> Unit,
    onOpenNotifications: () -> Unit,
) {
    val appointment = AppSettings.bookedAppointment
    val patient = AppSettings.loggedInPatient
    val context = LocalContext.current
    var showContactSheet by remember { mutableStateOf(value = false) }
    var generatingReport by remember { mutableStateOf(false) }
    var reportError by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    
    var visible by remember { mutableStateOf(value = false) }
    LaunchedEffect(Unit) { visible = true }

    val displayName = remember(patient) {
        if (patient == null) return@remember "User"
        if (!patient.fullName.isNullOrBlank()) return@remember patient.fullName
        val concatName = "${patient.firstName ?: ""} ${patient.lastName ?: ""}".trim()
        if (concatName.isNotBlank()) return@remember concatName
        if (!patient.email.isNullOrBlank()) return@remember patient.email.split("@")[0]
        "User"
    }

    val isAr = AppSettings.currentLanguage == "ar"

    val greeting = remember(isAr) {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        if (isAr) {
            when {
                hour < 12 -> "صباح الخير"
                hour < 17 -> "مساء الخير"
                else -> "مساء الخير"
            }
        } else {
            when {
                hour < 12 -> "Good Morning"
                hour < 17 -> "Good Afternoon"
                else -> "Good Evening"
            }
        }
    }

    val initials = remember(displayName) {
        displayName.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifEmpty { "U" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        // Gradient Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DentalTealDark, DentalTeal, DentalGradientEnd)
                    )
                )
                .padding(top = 16.dp, bottom = 28.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(DentalCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(
                                greeting,
                                color = Color.White.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall
                            )
                            Text(
                                displayName,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onOpenNotifications() }
                    ) {
                        IconBubble(
                            text = "🔔",
                            background = Color.White.copy(alpha = 0.2f),
                            tint = Color.White,
                            size = 44
                        )
                    }
                }
                Spacer(Modifier.height(16.dp))
                DentalCard(
                    modifier = Modifier.fillMaxWidth(),
                    accentColor = DentalWarning
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🦷", fontSize = 24.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                if (isAr) "الزيارة القادمة" else "Next Visit",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleSmall,
                                color = DentalMuted
                            )
                            Text(
                                if (appointment != null) {
                                    if (isAr) "${appointment.date} في ${appointment.time}" 
                                    else "${appointment.date} at ${appointment.time}"
                                } else (if (isAr) "لا توجد زيارات قادمة" else "No upcoming visits"),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp)
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(durationMillis = 600)) + slideInVertically(initialOffsetY = { 40 })
            ) {
                Column {
                    // Appointment Reminder
                    if (appointment != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(DentalPink.copy(alpha = 0.3f))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🔔", fontSize = 24.sp)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        stringResource(R.string.appointment_booked_reminder),
                                        fontWeight = FontWeight.Bold,
                                        color = DentalTealDark,
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        if (isAr) "${appointment.title} في ${appointment.date} الساعة ${appointment.time}" else "${appointment.title} on ${appointment.date} at ${appointment.time}",
                                        color = Color.Black.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        // Appointment Section
                        SectionHeader(
                            stringResource(R.string.upcoming_appointment),
                            stringResource(R.string.view_all),
                            onOpenAppointments
                        )
                        Spacer(Modifier.height(10.dp))
                        
                        if (appointment != null) {
                            DentalGradientCard(
                                modifier = Modifier.fillMaxWidth(),
                                colors = listOf(DentalTealDark, DentalTeal)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(Color.White.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("📅", fontSize = 22.sp)
                                    }
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 14.dp)
                                    ) {
                                        Text(appointment.title, fontWeight = FontWeight.Bold, color = Color.White, style = MaterialTheme.typography.titleMedium)
                                        Text("${appointment.date} • ${appointment.time}", color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
                                        Text(appointment.dentist, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                Spacer(Modifier.height(14.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    OutlinedButton(
                                        onClick = {},
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(22.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
                                    ) {
                                        Text(stringResource(R.string.reschedule), fontWeight = FontWeight.SemiBold)
                                    }
                                    Button(
                                        onClick = { showContactSheet = true },
                                        modifier = Modifier.weight(1f).height(44.dp),
                                        shape = RoundedCornerShape(22.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = DentalTeal)
                                    ) {
                                        Text(stringResource(R.string.contact), fontWeight = FontWeight.SemiBold)
                                    }
                                }
                            }
                        } else {
                            DentalCard(modifier = Modifier.fillMaxWidth()) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🕐", fontSize = 32.sp)
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        stringResource(R.string.no_upcoming_appointments),
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        SecondaryDentalButton(
                                            text = stringResource(R.string.chat),
                                            modifier = Modifier.weight(1f),
                                            onClick = onOpenChat
                                        )
                                        PrimaryDentalButton(
                                            text = stringResource(R.string.book_new_appointment),
                                            modifier = Modifier.weight(1f),
                                            onClick = onOpenAppointments
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        SectionHeader(stringResource(R.string.dental_health_summary))
                        Spacer(Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HealthBadge(stringResource(R.string.overall_health), stringResource(R.string.excellent), DentalSuccess, DentalCardGreen, Modifier.weight(1f))
                            HealthBadge(stringResource(R.string.gum_condition), stringResource(R.string.healthy), DentalTeal, DentalBlueSoft, Modifier.weight(1f))
                            HealthBadge(stringResource(R.string.hygiene_level), stringResource(R.string.good), DentalWarning, DentalCardOrange, Modifier.weight(1f))
                        }

                        Spacer(Modifier.height(24.dp))
                        SectionHeader("Generate Report")
                        Spacer(Modifier.height(10.dp))
                        DentalCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                if (!generatingReport) {
                                        scope.launch {
                                        generatingReport = true
                                        reportError = false
                                        try {
                                            val rays = if (!patient?.id.isNullOrBlank()) {
                                                val response = RetrofitClient.service.getRaysByPatient(patient!!.id)
                                                if (response.isSuccessful) response.body() ?: emptyList<RayResponse>() else emptyList<RayResponse>()
                                            } else emptyList<RayResponse>()

                                            val file = withContext(Dispatchers.IO) {
                                                ReportGenerator.generateReport(context, patient, rays)
                                            }
                                            ReportGenerator.shareReport(context, file)
                                        } catch (_: Exception) {
                                            reportError = true
                                        } finally {
                                            generatingReport = false
                                        }
                                    }
                                }
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (generatingReport) DentalLine else DentalCyan),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(if (generatingReport) "⏳" else "📄", fontSize = 22.sp)
                                }
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        if (generatingReport) "Generating..." else (if (isAr) "تقرير طبي" else "Medical Report"),
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        if (reportError) (if (isAr) "فشل في إنشاء التقرير" else "Failed to generate")
                                        else if (generatingReport) (if (isAr) "جارٍ الإنشاء..." else "Please wait...")
                                        else (if (isAr) "إنشاء تقرير Word ببياناتك الطبية" else "Generate a Word report with your medical data"),
                                        color = if (reportError) DentalError else DentalMuted,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                                if (!generatingReport) {
                                    Text("→", color = DentalTeal, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))
                        SectionHeader(stringResource(R.string.quick_actions))
                        Spacer(Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            QuickAction(stringResource(R.string.xray), stringResource(R.string.view_dental_scans), "🦷", DentalBlueSoft, Modifier.weight(1f), onOpenXRay)
                            QuickAction(stringResource(R.string.diagnosis), stringResource(R.string.latest_reports), "📋", DentalMint, Modifier.weight(1f), onOpenDiagnosis)
                        }
                        Spacer(Modifier.height(14.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                            QuickAction(stringResource(R.string.medical_history), stringResource(R.string.procedures), "📁", DentalCream, Modifier.weight(1f), onOpenHistory)
                            QuickAction(stringResource(R.string.chat), stringResource(R.string.message_clinic), "💬", DentalPink, Modifier.weight(1f), onOpenChat)
                        }
                        Spacer(Modifier.height(28.dp))
                    }
                }
            }
        }
    }

    if (showContactSheet) {
        ModalBottomSheet(
            onDismissRequest = { showContactSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                Text(
                    stringResource(R.string.contact_doctor),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DentalTeal
                )
                Spacer(Modifier.height(16.dp))
                ContactOption(stringResource(R.string.live_chat), "💬") { 
                    showContactSheet = false
                    onOpenChat()
                }
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { showContactSheet = false },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.cancel), color = DentalError)
                }
            }
        }
    }
}

@Composable
private fun ContactOption(label: String, icon: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 22.sp)
            Spacer(Modifier.width(16.dp))
            Text(label, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun HealthBadge(label: String, value: String, dotColor: Color, bgColor: Color, modifier: Modifier = Modifier) {
    DentalCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = dotColor)
                Text(label, color = DentalMuted, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun QuickAction(
    title: String,
    subtitle: String,
    icon: String,
    background: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    DentalCard(
        modifier = modifier.height(130.dp),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(background),
                contentAlignment = Alignment.Center
            ) {
                Text(icon, fontSize = 22.sp)
            }
            Spacer(Modifier.height(16.dp))
            Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, color = DentalMuted, style = MaterialTheme.typography.bodySmall)
        }
    }
}
