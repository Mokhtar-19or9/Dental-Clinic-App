package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.fake.FakeDentalData
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.components.DentalHeader
import com.example.dentalclinic.ui.components.IconBubble
import com.example.dentalclinic.ui.theme.DentalTeal
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DiagnosisScreen(modifier: Modifier = Modifier, onBack: () -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        DentalHeader(
            title = stringResource(R.string.diagnosis),
            subtitle = stringResource(R.string.treatment_plan_desc),
            onBack = onBack
        )

        Column(modifier = Modifier.padding(20.dp)) {
            DentalCard(Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.tooth_chart), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(12.dp))
                Canvas(Modifier.fillMaxWidth().height(180.dp)) {
                    val centerX = size.width / 2
                    val centerY = size.height / 2
                    repeat(16) { index ->
                        val angle = (index / 16f) * 6.28f
                        val x = centerX + cos(angle) * 120f
                        val y = centerY + sin(angle) * 58f
                        drawCircle(
                            color = if (index == 4 || index == 9) Color(0xFFFFD166) else Color.White,
                            radius = 18f,
                            center = androidx.compose.ui.geometry.Offset(x, y)
                        )
                        drawCircle(
                            color = DentalTeal.copy(alpha = 0.35f),
                            radius = 18f,
                            center = androidx.compose.ui.geometry.Offset(x, y),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            FakeDentalData.diagnoses.forEach { item ->
                DentalCard(Modifier.fillMaxWidth()) {
                    IconBubble("D")
                    Spacer(Modifier.height(10.dp))
                    Text("${item.tooth} • ${item.issue}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("${stringResource(R.string.severity)}: ${item.severity}", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    Text(item.treatmentPlan, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
