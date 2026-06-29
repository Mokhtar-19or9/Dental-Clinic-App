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
import androidx.compose.ui.graphics.Color
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(modifier: Modifier = Modifier) {
    val isAr = AppSettings.currentLanguage == "ar"
    var showBookSheet by remember { mutableStateOf(value = false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedDate by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()
    
    // Dynamic Date Logic
    val calendar = Calendar.getInstance()
    val locale = if (isAr) Locale("ar") else Locale.ENGLISH
    val todayFormatter = SimpleDateFormat("EEE, MMM d", locale)
    val dayNameFormatter = SimpleDateFormat("EEE", locale)
    val dayNumFormatter = SimpleDateFormat("d", locale)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
    ) {
        Text(if (isAr) "المواعيد" else stringResource(R.string.appointments), fontWeight = FontWeight.Bold, color = DentalTeal)
        Text(if (isAr) "عرض التقويم" else stringResource(R.string.calendar_view), color = DentalMuted)
        Spacer(Modifier.height(18.dp))

        DentalCard(Modifier.fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                // Generate next 5 days dynamically
                for (i in 0 until 5) {
                    val tempCal = calendar.clone() as Calendar
                    tempCal.add(Calendar.DAY_OF_YEAR, i)
                    val dayName = dayNameFormatter.format(tempCal.time)
                    val dayNum = dayNumFormatter.format(tempCal.time)
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(dayName, color = if (i == 0) DentalTeal else DentalMuted, fontWeight = if (i == 0) FontWeight.Bold else FontWeight.Normal)
                        AssistChip(
                            onClick = {}, 
                            label = { Text(dayNum) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = if (i == 0) DentalTeal.copy(alpha = 0.1f) else Color.Transparent
                            )
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(22.dp))
        SectionHeader(if (isAr) "القادمة" else stringResource(R.string.upcoming))
        Spacer(Modifier.height(10.dp))
        
        // Display current booked appointment if exists
        val currentBooking = AppSettings.bookedAppointment
        if (currentBooking != null) {
            AppointmentRow(currentBooking)
            Spacer(Modifier.height(12.dp))
        }

        FakeDentalData.appointments.forEach { appointment ->
            AppointmentRow(appointment)
            Spacer(Modifier.height(12.dp))
        }
        
        PrimaryDentalButton(
            text = if (isAr) "حجز موعد جديد" else stringResource(R.string.book_new_appointment),
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
                        if (isAr) "اختر الخدمة" else stringResource(R.string.select_service),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DentalTeal
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    ServiceOption(if (isAr) "فحص دوري" else stringResource(R.string.routine_checkup), "🦷") { selectedCategory = "routine" }
                    ServiceOption(if (isAr) "تبييض الأسنان" else stringResource(R.string.teeth_whitening), "✨") { selectedCategory = "whitening" }
                    ServiceOption(if (isAr) "حالة طارئة" else stringResource(R.string.emergency), "🚨") { selectedCategory = "emergency" }
                } else if ((selectedDate == null) && (selectedCategory != "emergency")) {
                    Text(
                        if (isAr) "اختر اليوم" else stringResource(R.string.select_day),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DentalTeal
                    )
                    Spacer(Modifier.height(16.dp))
                    
                    // Dynamic dates for selection
                    for (i in 0 until 5) {
                        val tempCal = calendar.clone() as Calendar
                        tempCal.add(Calendar.DAY_OF_YEAR, i)
                        val dateStr = todayFormatter.format(tempCal.time)
                        ServiceOption(dateStr, "📅") { selectedDate = dateStr }
                    }
                } else {
                    Text(
                        if (isAr) "اختر الوقت" else stringResource(R.string.select_time),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = DentalTeal
                    )
                    Spacer(Modifier.height(16.dp))

                    val times = if (isAr) listOf(
                        "09:00 ص", "10:00 ص", "11:00 ص",
                        "12:00 م", "01:00 م", "02:00 م",
                        "03:00 م", "04:00 م", "05:00 م"
                    ) else listOf(
                        "09:00 AM", "10:00 AM", "11:00 AM",
                        "12:00 PM", "01:00 PM", "02:00 PM",
                        "03:00 PM", "04:00 PM", "05:00 PM"
                    )
                    
                    val routineTitle = if (isAr) "فحص دوري" else stringResource(R.string.routine_checkup)
                    val whiteningTitle = if (isAr) "تبييض الأسنان" else stringResource(R.string.teeth_whitening)
                    val emergencyTitle = if (isAr) "حالة طارئة" else stringResource(R.string.emergency)
                    val serviceTitle = when(selectedCategory) {
                        "routine" -> routineTitle
                        "whitening" -> whiteningTitle
                        "emergency" -> emergencyTitle
                        else -> (if (isAr) "خدمة الأسنان" else "Dental Service")
                    }

                    if (selectedCategory == "emergency") {
                        val todayStr = todayFormatter.format(calendar.time)
                        Text(if (isAr) "هاتف الطبيب" else stringResource(R.string.doctor_phone), color = DentalTeal, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                        ServiceOption(if (isAr) "وقت الطوارئ" else stringResource(R.string.emergency_time), "🚑") { 
                            bookAppointment(emergencyTitle, todayStr, if (isAr) "في أسرع وقت" else "ASAP")
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
                                        bookAppointment(serviceTitle, selectedDate ?: (if (isAr) "اليوم" else "Today"), time)
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
                    Text(if (isAr) "إلغاء" else stringResource(R.string.cancel))
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
