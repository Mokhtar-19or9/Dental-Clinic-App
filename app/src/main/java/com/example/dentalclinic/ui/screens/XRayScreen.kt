package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.fake.FakeDentalData
import com.example.dentalclinic.ui.components.DentalCard
import com.example.dentalclinic.ui.components.IconBubble
import com.example.dentalclinic.ui.components.SectionHeader
import com.example.dentalclinic.ui.theme.*

@Composable
fun XRayScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DentalBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(stringResource(R.string.xray_records), fontWeight = FontWeight.Bold, color = DentalTeal, style = MaterialTheme.typography.headlineMedium)
        Text(stringResource(R.string.ai_assisted_analysis), color = DentalMuted)
        Spacer(Modifier.height(24.dp))

        Text(stringResource(R.string.latest_scan_preview), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 12.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF102A35), Color(0xFF08151A))))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                
                // Draw Grid lines
                for (i in 1..10) {
                    drawLine(Color.White.copy(alpha = 0.05f), Offset(w * i / 10f, 0f), Offset(w * i / 10f, h))
                    drawLine(Color.White.copy(alpha = 0.05f), Offset(0f, h * i / 10f), Offset(w, h * i / 10f))
                }

                // Draw Tooth silhouettes
                repeat(6) { index ->
                    val centerX = w * (index + 1) / 7f
                    val centerY = h / 2f
                    
                    val toothPath = Path().apply {
                        moveTo(centerX - 25f, centerY - 35f)
                        quadraticTo(centerX, centerY - 55f, centerX + 25f, centerY - 35f)
                        lineTo(centerX + 20f, centerY + 25f)
                        quadraticTo(centerX, centerY + 45f, centerX - 20f, centerY + 25f)
                        close()
                    }
                    
                    drawPath(
                        path = toothPath,
                        color = if (index == 2) Color(0xFFFFD166).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.4f)
                    )
                }

                // Scanning line
                drawLine(
                    color = DentalTeal,
                    start = Offset(0f, h * 0.4f),
                    end = Offset(w, h * 0.4f),
                    strokeWidth = 2.dp.toPx()
                )
                
                // Highlight Circle on a specific tooth
                drawCircle(
                    color = Color(0xFFEF4444).copy(alpha = 0.6f),
                    radius = 40f,
                    center = Offset(w * 3 / 7f, h / 2f),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            
            // Labels in X-ray
            Box(modifier = Modifier.padding(16.dp).align(Alignment.TopEnd)) {
                Text("AI ANALYZING...", color = DentalTeal, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            
            Box(modifier = Modifier.padding(16.dp).align(Alignment.BottomStart)) {
                Column {
                    Text("RESOLUTION: 4K", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                    Text("SCAN ID: #8821-X", color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        SectionHeader(stringResource(R.string.records))
        Spacer(Modifier.height(12.dp))

        FakeDentalData.xRays.forEach { record ->
            DentalCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconBubble("X", background = DentalBlueSoft)
                    Spacer(Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(record.title, fontWeight = FontWeight.Bold)
                        Text(record.date, color = DentalMuted, style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text(record.finding, color = DentalMuted, fontSize = 14.sp)
            }
            Spacer(Modifier.height(12.dp))
        }
    }
}
