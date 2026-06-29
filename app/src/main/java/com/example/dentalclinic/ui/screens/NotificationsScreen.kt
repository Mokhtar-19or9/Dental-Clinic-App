package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.model.NotificationItem
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.theme.*

enum class NotificationFilter { All, Unread }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    val isAr = AppSettings.currentLanguage == "ar"
    val bookedApp = AppSettings.bookedAppointment

    val generatedNotifications = remember(bookedApp) {
        val list = mutableStateListOf<NotificationItem>()

        list.add(NotificationItem(
            title = if (isAr) "مرحباً بك في Smile Scan" else "Welcome to Smile Scan",
            message = if (isAr) "نحن سعداء بانضمامك! اكتشف ميزاتنا واحجز موعدك الأول." else "We're glad you joined! Explore our features and book your first appointment.",
            time = if (isAr) "قبل 3 أيام" else "3 days ago",
            unread = false
        ))

        list.add(NotificationItem(
            title = if (isAr) "تذكير بالفحص الدوري" else "Regular Checkup Reminder",
            message = if (isAr) "حان وقت الفحص الدوري! يُنصح بزيارة طبيب الأسنان كل 6 أشهر." else "Time for your regular checkup! It's recommended to visit the dentist every 6 months.",
            time = if (isAr) "قبل أسبوع" else "1 week ago",
            unread = true
        ))

        list.add(NotificationItem(
            title = if (isAr) "نصائح للعناية بالأسنان" else "Dental Care Tips",
            message = if (isAr) "تذكير: اغسل أسنانك مرتين يومياً واستخدم خيط الأسنان للحفاظ على صحة فمك." else "Reminder: Brush your teeth twice daily and floss to maintain oral health.",
            time = if (isAr) "قبل أسبوعين" else "2 weeks ago",
            unread = false
        ))

        if (bookedApp != null) {
            val title = if (isAr) "تم تأكيد الموعد" else "Appointment Confirmed"
            val message = if (isAr)
                "تم حجز موعدك لـ ${bookedApp.title} في ${bookedApp.date} الساعة ${bookedApp.time}."
            else
                "Your appointment for ${bookedApp.title} on ${bookedApp.date} at ${bookedApp.time} has been confirmed."

            list.add(0, NotificationItem(title, message, if (isAr) "الآن" else "Just now", true))
        }

        list.add(NotificationItem(
            title = if (isAr) "تحديث التطبيق" else "App Update",
            message = if (isAr) "تم تحديث التطبيق إلى الإصدار الأحدث. استمتع بالميزات الجديدة!" else "App updated to latest version. Enjoy the new features!",
            time = if (isAr) "قبل شهر" else "1 month ago",
            unread = false
        ))

        list
    }

    var notifications by remember { mutableStateOf(generatedNotifications.toList()) }
    var filter by remember { mutableStateOf(NotificationFilter.All) }

    fun markAllRead() {
        notifications = notifications.map { it.copy(unread = false) }.toMutableList()
    }

    fun clearAll() {
        notifications = mutableListOf()
    }

    fun toggleRead(index: Int) {
        if (index in notifications.indices) {
            val updated = notifications.toMutableList()
            updated[index] = updated[index].copy(unread = false)
            notifications = updated
        }
    }

    fun deleteNotification(index: Int) {
        if (index in notifications.indices) {
            val updated = notifications.toMutableList()
            updated.removeAt(index)
            notifications = updated
        }
    }

    val filteredNotifications = when (filter) {
        NotificationFilter.All -> notifications
        NotificationFilter.Unread -> notifications.filter { it.unread }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DentalTealDark, DentalTeal, DentalGradientEnd)
                    )
                )
                .padding(top = 12.dp, bottom = 16.dp, start = 8.dp, end = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        stringResource(R.string.notifications),
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(stringResource(R.string.notifications_desc), color = Color.White.copy(alpha = 0.7f))
                }
                if (notifications.isNotEmpty()) {
                    IconButton(onClick = { markAllRead() }) {
                        Icon(Icons.Filled.CheckCircle, contentDescription = "Mark all read", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = { clearAll() }) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Clear all", tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(22.dp))
                    }
                }
            }
        }

        if (notifications.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DentalBackground)
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == NotificationFilter.All,
                    onClick = { filter = NotificationFilter.All },
                    label = { Text(if (isAr) "الكل (${notifications.size})" else "All (${notifications.size})", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DentalTeal,
                        selectedLabelColor = Color.White
                    )
                )
                FilterChip(
                    selected = filter == NotificationFilter.Unread,
                    onClick = { filter = NotificationFilter.Unread },
                    label = { Text(if (isAr) "غير مقروء (${notifications.count { it.unread }})" else "Unread (${notifications.count { it.unread }})", fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DentalTeal,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            val unreadCount = notifications.count { it.unread }

            if (notifications.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🔔", fontSize = 48.sp)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            if (isAr) "لا توجد تنبيهات" else "No notifications",
                            color = DentalMuted,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Text(
                            if (isAr) "ستظهر التنبيهات هنا عند توفرها" else "Notifications will appear here when available",
                            color = DentalMuted.copy(alpha = 0.6f),
                            fontSize = 13.sp
                        )
                    }
                }
            } else if (filteredNotifications.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                    Text(
                        if (isAr) "لا توجد تنبيهات غير مقروءة" else "No unread notifications",
                        color = DentalMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                if (unreadCount > 0 && filter == NotificationFilter.All) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            if (isAr) "$unreadCount غير مقروءة" else "$unreadCount unread",
                            color = DentalTeal,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                        TextButton(onClick = { markAllRead() }) {
                            Text(if (isAr) "تحديد الكل كمقروء" else "Mark all read", color = DentalTeal, fontSize = 13.sp)
                        }
                    }
                }

                filteredNotifications.forEach { notification ->
                    val realIndex = notifications.indexOf(notification)
                    DentalCard(
                        Modifier.fillMaxWidth(),
                        onClick = { if (realIndex >= 0) toggleRead(realIndex) }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (notification.unread) Brush.linearGradient(
                                            colors = listOf(DentalTeal, DentalCyan)
                                        )
                                        else SolidColor(MaterialTheme.colorScheme.surfaceVariant)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    if (notification.unread) "!" else "i",
                                    fontWeight = FontWeight.Bold,
                                    color = if (notification.unread) Color.White else DentalMuted,
                                    fontSize = 16.sp
                                )
                            }
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
                                Text(
                                    notification.message,
                                    color = DentalMuted,
                                    style = MaterialTheme.typography.bodySmall,
                                    maxLines = 2
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    notification.time,
                                    color = DentalTeal.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (notification.unread) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(
                                                Brush.linearGradient(
                                                    colors = listOf(DentalTeal, DentalCyan)
                                                )
                                            )
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                IconButton(
                                    onClick = { if (realIndex >= 0) deleteNotification(realIndex) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Close,
                                        contentDescription = "Delete",
                                        tint = DentalMuted.copy(alpha = 0.5f),
                                        modifier = Modifier.size(16.dp)
                                    )
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
