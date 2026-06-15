package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.fake.FakeDentalData
import com.example.dentalclinic.data.model.Appointment
import com.example.dentalclinic.ui.components.*
import com.example.dentalclinic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(modifier: Modifier = Modifier) {
    var showBookSheet by remember { mutableStateOf(value = false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(stringResource(R.string.appointments), fontWeight = FontWeight.Bold, color = DentalTeal)
        Text(stringResource(R.string.calendar_view), color = DentalMuted)
        Spacer(Modifier.height(18.dp))

        DentalCard(Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                listOf(
                    stringResource(R.string.mon),
                    stringResource(R.string.tue),
                    stringResource(R.string.wed),
                    stringResource(R.string.thu),
                    stringResource(R.string.fri),
                ).forEachIndexed { index, day ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(day, color = DentalMuted)
                        AssistChip(onClick = {}, label = { Text((24 + index).toString()) })
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        SectionHeader(stringResource(R.string.upcoming))
        Spacer(Modifier.height(10.dp))
        FakeDentalData.appointments.forEach { appointment ->
            AppointmentRow(appointment)
            Spacer(Modifier.height(12.dp))
        }
        PrimaryDentalButton(
            text = stringResource(R.string.book_new_appointment),
            modifier = Modifier.fillMaxWidth()
        ) { 
            selectedCategory = null
            selectedDate = null
            showBookSheet = true 
        }
    }

    if (showBookSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBookSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp, start = 24.dp, end = 24.dp)
            ) {
                if (selectedCategory == null) {
                    Text(
                        stringResource(R.string.select_service),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DentalTeal
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    ServiceOption(stringResource(R.string.routine_checkup), "🦷") { selectedCategory = "routine" }
                    ServiceOption(stringResource(R.string.teeth_whitening), "✨") { selectedCategory = "whitening" }
                    ServiceOption(stringResource(R.string.emergency), "🚨") { selectedCategory = "emergency" }
                } else if ((selectedDate == null) && (selectedCategory != "emergency")) {
                    Text(
                        stringResource(R.string.select_day),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DentalTeal
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    val days = listOf(
                        "${stringResource(R.string.mon)}, May 25",
                        "${stringResource(R.string.tue)}, May 26",
                        "${stringResource(R.string.wed)}, May 27",
                        "${stringResource(R.string.thu)}, May 28",
                        "${stringResource(R.string.fri)}, May 29"
                    )
                    
                    days.forEach { day ->
                        ServiceOption(day, "📅") { selectedDate = day }
                    }
                } else {
                    Text(
                        stringResource(R.string.select_time),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DentalTeal
                    )
                    Spacer(Modifier.height(16.dp))

                    val times = listOf(
                        "09:00 AM", "10:00 AM", "11:00 AM",
                        "12:00 PM", "01:00 PM", "02:00 PM",
                        "03:00 PM", "04:00 PM", "05:00 PM"
                    )
                    
                    val routineTitle = stringResource(R.string.routine_checkup)
                    val whiteningTitle = stringResource(R.string.teeth_whitening)
                    val emergencyTitle = stringResource(R.string.emergency)
                    val serviceTitle = when(selectedCategory) {
                        "routine" -> routineTitle
                        "whitening" -> whiteningTitle
                        "emergency" -> emergencyTitle
                        else -> "Dental Service"
                    }

                    if (selectedCategory == "emergency") {
                        Text(stringResource(R.string.doctor_phone), color = DentalTeal, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        ServiceOption(stringResource(R.string.emergency_time), "🚑") { 
                            bookAppointment(emergencyTitle, "Today", "ASAP")
                            showBookSheet = false 
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.height(200.dp)
                        ) {
                            items(times) { time ->
                                FilterChip(
                                    selected = false,
                                    onClick = {
                                        bookAppointment(serviceTitle, selectedDate ?: "Today", time)
                                        showBookSheet = false
                                    },
                                    label = { Text(time, fontSize = 12.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        containerColor = DentalTeal.copy(alpha = 0.1f),
                                        labelColor = DentalTeal
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(borderColor = DentalTeal, enabled = true, selected = false)
                                )
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { showBookSheet = false },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

private fun bookAppointment(title: String, date: String, time: String) {
    AppSettings.bookedAppointment = Appointment(
        title = title,
        date = date,
        time = time,
        dentist = "Dr. Sarah Johnson",
        status = "Booked"
    )
}

@Composable
private fun ServiceOption(label: String, icon: String, onClick: () -> Unit) {
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
private fun AppointmentRow(appointment: Appointment) {
    DentalCard(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconBubble("A", background = DentalBlueSoft)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(appointment.title, fontWeight = FontWeight.Bold)
                Text("${appointment.date} • ${appointment.time}", color = DentalMuted)
                Text(appointment.dentist, color = DentalMuted)
            }
            Text(appointment.status, color = DentalTeal, fontWeight = FontWeight.SemiBold)
        }
    }
}
