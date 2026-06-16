package com.example.dentalclinic.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.ui.components.*
import com.example.dentalclinic.ui.theme.*

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
    var showContactSheet by remember { mutableStateOf(value = false) }
    val sheetState = rememberModalBottomSheetState()
    
    var visible by remember { mutableStateOf(value = false) }
    LaunchedEffect(Unit) { visible = true }

    // DYNAMIC DISPLAY NAME - Use API data first, NEVER fall back to FakeDentalData
    val displayName = remember(patient) {
        if (patient == null) return@remember "User"

        // Priority 1: FullName field
        if (!patient.fullName.isNullOrBlank()) return@remember patient.fullName

        // Priority 2: Combination of First and Last Name
        val concatName = "${patient.firstName ?: ""} ${patient.lastName ?: ""}".trim()
        if (concatName.isNotBlank()) return@remember concatName

        // Priority 3: Email prefix (only if we have literally nothing else)
        if (!patient.email.isNullOrBlank()) return@remember patient.email.split("@")[0]

        "User"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        DentalHeader(
            title = displayName,
            subtitle = stringResource(R.string.good_morning),
            trailing = {
                Text(
                    "N",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onOpenNotifications)
                )
            }
        )

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(durationMillis = 600)) + slideInVertically(initialOffsetY = { 40 })
        ) {
            Column {
                // Reminder Notification for booked appointment
                if (appointment != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DentalPink.copy(alpha = 0.2f))
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
                                    "${appointment.title} on ${appointment.date} at ${appointment.time}",
                                    color = Color.Black.copy(alpha = 0.7f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard("3", stringResource(R.string.appointments), Modifier.weight(1f), onClick = onOpenAppointments)
                    StatCard("12", stringResource(R.string.records), Modifier.weight(1f), onClick = onOpenHistory)
                    StatCard("98%", stringResource(R.string.health_score), Modifier.weight(1f), onClick = onOpenDiagnosis)
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    SectionHeader(
                        stringResource(R.string.upcoming_appointment),
                        stringResource(R.string.view_all),
                        onOpenAppointments
                    )
                    Spacer(Modifier.height(10.dp))
                    
                    if (appointment != null) {
                        DentalCard(modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconBubble("A", background = DentalBlueSoft)
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 14.dp)
                                ) {
                                    Text(appointment.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                    Text("${appointment.date} • ${appointment.time}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    Text(appointment.dentist, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                            }
                            Spacer(Modifier.height(14.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                SecondaryDentalButton(stringResource(R.string.reschedule), Modifier.weight(1f)) {}
                                PrimaryDentalButton(stringResource(R.string.contact), Modifier.weight(1f), onClick = { showContactSheet = true })
                            }
                        }
                    } else {
                        DentalCard(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
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

                    Spacer(Modifier.height(22.dp))
                    SectionHeader(stringResource(R.string.dental_health_summary))
                    Spacer(Modifier.height(10.dp))
                    DentalCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.overall_health), color = DentalMuted, style = MaterialTheme.typography.labelSmall)
                                Text(stringResource(R.string.excellent), fontWeight = FontWeight.Bold, color = DentalTeal)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.gum_condition), color = DentalMuted, style = MaterialTheme.typography.labelSmall)
                                Text(stringResource(R.string.healthy), fontWeight = FontWeight.Bold, color = DentalTeal)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(stringResource(R.string.hygiene_level), color = DentalMuted, style = MaterialTheme.typography.labelSmall)
                                Text(stringResource(R.string.good), fontWeight = FontWeight.Bold, color = DentalTeal)
                            }
                        }
                    }

                    Spacer(Modifier.height(22.dp))
                    SectionHeader(stringResource(R.string.quick_actions))
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        QuickAction(stringResource(R.string.xray), stringResource(R.string.view_dental_scans), "X", DentalBlueSoft, Modifier.weight(1f), onOpenXRay)
                        QuickAction(stringResource(R.string.diagnosis), stringResource(R.string.latest_reports), "D", DentalMint, Modifier.weight(1f), onOpenDiagnosis)
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        QuickAction(stringResource(R.string.medical_history), stringResource(R.string.procedures), "M", DentalCream, Modifier.weight(1f), onOpenHistory)
                        QuickAction(stringResource(R.string.chat), stringResource(R.string.message_clinic), "C", DentalPink, Modifier.weight(1f), onOpenChat)
                    }
                    Spacer(Modifier.height(28.dp))
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
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = DentalTeal
                )
                Spacer(Modifier.height(16.dp))
                
                ContactOption(stringResource(R.string.call_clinic), "📞") { showContactSheet = false }
                ContactOption(stringResource(R.string.send_email), "✉️") { showContactSheet = false }
                ContactOption(stringResource(R.string.live_chat), "💬") { 
                    showContactSheet = false
                    onOpenChat()
                }
                
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { showContactSheet = false },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.cancel), color = Color.Red)
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
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 20.sp)
            Spacer(Modifier.width(16.dp))
            Text(label, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    DentalCard(modifier = modifier, onClick = onClick) {
        IconBubble("✓")
        Spacer(Modifier.height(6.dp))
        Text(value, style = MaterialTheme.typography.titleLarge, color = DentalTeal)
        Text(label, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
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
        modifier = modifier.height(138.dp),
        onClick = onClick
    ) {
        IconBubble(icon, background = background)
        Spacer(Modifier.height(18.dp))
        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Text(subtitle, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall)
    }
}
