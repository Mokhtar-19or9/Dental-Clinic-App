package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.fake.FakeDentalData
import com.example.dentalclinic.data.model.NotificationItem
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.components.IconBubble
import com.example.dentalclinic.ui.theme.DentalBackground
import com.example.dentalclinic.ui.theme.DentalMuted
import com.example.dentalclinic.ui.theme.DentalTeal

@Composable
fun NotificationsScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val isAr = AppSettings.currentLanguage == "ar"
    val bookedApp = AppSettings.bookedAppointment
    
    // Create notifications based on current app state
    val notifications = remember(bookedApp) {
        val list = mutableStateListOf<NotificationItem>()
        
        // Add dynamic notification if an appointment is booked
        if (bookedApp != null) {
            val title = if (isAr) "تم تأكيد الموعد" else "Appointment Confirmed"
            val message = if (isAr) 
                "تم حجز موعدك لـ ${bookedApp.title} في ${bookedApp.date} الساعة ${bookedApp.time}."
            else 
                "Your appointment for ${bookedApp.title} on ${bookedApp.date} at ${bookedApp.time} has been confirmed."
            
            list.add(NotificationItem(title, message, if (isAr) "الآن" else "Just now", true))
        }
        
        // Add static fake data
        list.addAll(FakeDentalData.notifications)
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            stringResource(R.string.back), 
            color = DentalTeal, 
            fontWeight = FontWeight.Bold, 
            modifier = Modifier.clickable(onClick = onBack)
        )
        Spacer(Modifier.height(10.dp))
        Text(
            stringResource(R.string.notifications), 
            fontWeight = FontWeight.Bold, 
            color = DentalTeal,
            style = MaterialTheme.typography.headlineMedium
        )
        Text(stringResource(R.string.notifications_desc), color = DentalMuted)
        Spacer(Modifier.height(24.dp))
        
        notifications.forEachIndexed { index, notification ->
            DentalCard(
                Modifier.fillMaxWidth(),
                onClick = {
                    if (notification.unread) {
                        notifications[index] = notification.copy(unread = false)
                    }
                }
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble("N", background = if (notification.unread) DentalTeal.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp)
                    ) {
                        Text(
                            notification.title, 
                            fontWeight = if (notification.unread) FontWeight.Bold else FontWeight.Medium,
                            color = if (notification.unread) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                        Text(notification.message, color = DentalMuted, style = MaterialTheme.typography.bodySmall)
                        Text(notification.time, color = DentalTeal, style = MaterialTheme.typography.labelSmall)
                    }
                    if (notification.unread) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(DentalTeal)
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
