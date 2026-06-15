package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.fake.FakeDentalData
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.components.IconBubble
import com.example.dentalclinic.ui.theme.DentalBackground
import com.example.dentalclinic.ui.theme.DentalMuted
import com.example.dentalclinic.ui.theme.DentalTeal

@Composable
fun MedicalHistoryScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(stringResource(R.string.back), color = DentalTeal, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onBack))
        Spacer(Modifier.height(10.dp))
        Text(stringResource(R.string.medical_history), fontWeight = FontWeight.Bold, color = DentalTeal)
        Text(stringResource(R.string.medical_history_desc), color = DentalMuted)
        Spacer(Modifier.height(18.dp))
        FakeDentalData.history.forEach { item ->
            DentalCard(Modifier.fillMaxWidth()) {
                IconBubble("M")
                Spacer(Modifier.height(10.dp))
                Text(item.title, fontWeight = FontWeight.Bold)
                Text(item.date, color = DentalTeal)
                Text(item.description, color = DentalMuted)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
