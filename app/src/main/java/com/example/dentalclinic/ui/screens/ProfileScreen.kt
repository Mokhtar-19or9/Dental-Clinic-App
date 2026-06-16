package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.fake.FakeDentalData
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.components.DentalHeader
import com.example.dentalclinic.ui.components.PrimaryDentalButton
import com.example.dentalclinic.ui.components.SectionHeader
import com.example.dentalclinic.ui.theme.DentalBackground
import com.example.dentalclinic.ui.theme.DentalMuted
import com.example.dentalclinic.ui.theme.DentalTeal

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogout: () -> Unit = {}
) {
    val apiPatient = AppSettings.loggedInPatient
    
    val name = if (apiPatient != null) {
        if (!apiPatient.fullName.isNullOrBlank()) apiPatient.fullName
        else "${apiPatient.firstName ?: ""} ${apiPatient.lastName ?: ""}".trim().ifBlank { 
            apiPatient.email?.split("@")?.firstOrNull() ?: "User"
        }
    } else {
        "User"
    }
    val age = if (apiPatient?.age != null && apiPatient.age != 0) "${apiPatient.age} years" else "N/A"
    val phone = if (!apiPatient?.phone.isNullOrBlank()) apiPatient?.phone!! else "N/A"
    val email = if (!apiPatient?.email.isNullOrBlank()) apiPatient?.email!! else "N/A"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
            .verticalScroll(rememberScrollState()),
    ) {
        DentalHeader(title = name, subtitle = stringResource(R.string.patient_profile))
        Column(modifier = Modifier.padding(20.dp)) {
            SectionHeader(stringResource(R.string.personal_information))
            Spacer(Modifier.height(10.dp))
            DentalCard(Modifier.fillMaxWidth()) {
                ProfileLine(stringResource(R.string.age), age)
                ProfileLine(stringResource(R.string.phone), phone)
                ProfileLine(stringResource(R.string.email), email)
                ProfileLine(stringResource(R.string.insurance), "N/A")
            }
            Spacer(Modifier.height(18.dp))
            SectionHeader(stringResource(R.string.medical_information))
            Spacer(Modifier.height(10.dp))
            DentalCard(Modifier.fillMaxWidth()) {
                ProfileLine(stringResource(R.string.blood_type), apiPatient?.bloodType ?: "O+")
                ProfileLine(stringResource(R.string.allergies), stringResource(R.string.penicillin))
                ProfileLine(stringResource(R.string.medical_conditions), apiPatient?.chronicDiseases ?: stringResource(R.string.asthma))
            }
            Spacer(Modifier.height(18.dp))
            SectionHeader(stringResource(R.string.medical_history_summary))
            Spacer(Modifier.height(10.dp))
            DentalCard(Modifier.fillMaxWidth()) {
                val latestHistory = FakeDentalData.history.firstOrNull()
                if (latestHistory != null) {
                    Text(latestHistory.title, fontWeight = FontWeight.Bold)
                    Text(latestHistory.date, color = DentalTeal, style = MaterialTheme.typography.labelSmall)
                    Text(latestHistory.description, color = DentalMuted, style = MaterialTheme.typography.bodySmall)
                } else {
                    Text(stringResource(R.string.none), color = DentalMuted)
                }
            }
            Spacer(Modifier.height(18.dp))
            SectionHeader(stringResource(R.string.emergency_contact))
            Spacer(Modifier.height(10.dp))
            DentalCard(Modifier.fillMaxWidth()) {
                Text("N/A", fontWeight = FontWeight.SemiBold, color = DentalTeal)
                Text(stringResource(R.string.available_for_urgent_updates), color = DentalMuted)
            }
            
            Spacer(Modifier.height(32.dp))
            PrimaryDentalButton(
                text = "Logout",
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    AppSettings.logout()
                    onLogout()
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ProfileLine(label: String, value: String) {
    Text(label, color = DentalMuted)
    Text(value, fontWeight = FontWeight.SemiBold)
    Spacer(Modifier.height(10.dp))
}
