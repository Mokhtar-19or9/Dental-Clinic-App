package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.layout.*
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
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.components.IconBubble
import com.example.dentalclinic.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    onNavigateToNotifications: () -> Unit
) {
    val isAr = AppSettings.currentLanguage == "ar"
    var showPrivacySheet by remember { mutableStateOf(false) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    var showSoonDialog by remember { mutableStateOf(false) }
    
    val soonMsg = stringResource(R.string.feature_soon)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            Text(
                if (isAr) "الإعدادات" else stringResource(R.string.settings),
                fontWeight = FontWeight.Bold,
                color = DentalTeal,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(18.dp))

            SettingRow(
                if (isAr) "الوضع الداكن" else stringResource(R.string.dark_mode),
                if (AppSettings.isDarkMode) (if (isAr) "مفعل" else stringResource(R.string.enabled)) else (if (isAr) "معطل" else stringResource(R.string.disabled)),
                "D",
                trailing = {
                    Switch(
                        checked = AppSettings.isDarkMode,
                        onCheckedChange = { AppSettings.isDarkMode = it }
                    )
                }
            )

            SettingRow(
                if (isAr) "اللغة" else stringResource(R.string.language),
                if (AppSettings.currentLanguage == "en") stringResource(R.string.english) else stringResource(R.string.arabic),
                "L",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { AppSettings.currentLanguage = "en" }) {
                            Text("EN", color = if (AppSettings.currentLanguage == "en") DentalTeal else MaterialTheme.colorScheme.outline)
                        }
                        TextButton(onClick = { AppSettings.currentLanguage = "ar" }) {
                            Text("AR", color = if (AppSettings.currentLanguage == "ar") DentalTeal else MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            )

            SettingRow(
                if (isAr) "التنبيهات" else stringResource(R.string.notifications), 
                if (isAr) "تنبيهات المواعيد" else stringResource(R.string.appointment_alerts), 
                "N",
                onClick = onNavigateToNotifications
            )
            
            SettingRow(
                if (isAr) "الخصوصية" else stringResource(R.string.privacy), 
                if (isAr) "إدارة بياناتك" else "Manage your data", 
                "P",
                onClick = { showPrivacySheet = true }
            )
            
            SettingRow(
                if (isAr) "طرق الدفع" else stringResource(R.string.payment_methods), 
                if (isAr) "البطاقات والمحافظ" else "Cards and Wallets", 
                "$",
                onClick = { showPaymentSheet = true }
            )
            
            SettingRow(
                if (isAr) "الدعم" else stringResource(R.string.support), 
                if (isAr) "مساعدة العيادة" else stringResource(R.string.clinic_help), 
                "?"
            )
        }
    }

    if (showPrivacySheet) {
        ModalBottomSheet(onDismissRequest = { showPrivacySheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp)) {
                Text(if (isAr) "إدارة بياناتك" else stringResource(R.string.manage_your_data), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { showPrivacySheet = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isAr) "طلب تصدير البيانات" else stringResource(R.string.request_data_export))
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { showPrivacySheet = false }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (isAr) "حذف الحساب" else stringResource(R.string.delete_account))
                }
            }
        }
    }

    if (showPaymentSheet) {
        ModalBottomSheet(onDismissRequest = { showPaymentSheet = false }) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 24.dp)) {
                Text(if (isAr) "اختر طريقة الدفع" else stringResource(R.string.select_payment_method), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
                PaymentOption(if (isAr) "بطاقة ائتمان" else stringResource(R.string.credit_card)) { 
                    showPaymentSheet = false
                    showSoonDialog = true
                }
                PaymentOption(if (isAr) "بايبال" else stringResource(R.string.paypal)) { 
                    showPaymentSheet = false
                    showSoonDialog = true
                }
                PaymentOption(if (isAr) "جوجل باي" else stringResource(R.string.google_pay)) { 
                    showPaymentSheet = false
                    showSoonDialog = true
                }
            }
        }
    }

    if (showSoonDialog) {
        AlertDialog(
            onDismissRequest = { showSoonDialog = false },
            confirmButton = {
                TextButton(onClick = { showSoonDialog = false }) {
                    Text(if (isAr) "حسناً" else "OK")
                }
            },
            title = { Text(if (isAr) "معلومات" else "Information") },
            text = { Text(soonMsg) },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = DentalTeal
        )
    }
}

@Composable
private fun PaymentOption(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Text(label, modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SettingRow(
    title: String,
    subtitle: String,
    icon: String,
    onClick: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    DentalCard(
        Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 4.dp)
        ) {
            IconBubble(icon)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp)
            ) {
                Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            trailing?.invoke()
        }
    }
    Spacer(Modifier.height(12.dp))
}
