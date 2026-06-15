package com.example.dentalclinic.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

enum class MascotStyle {
    CASUAL, COOL, SUPERHERO
}

@Composable
fun FunnyToothMascot(
    modifier: Modifier = Modifier,
    style: MascotStyle = MascotStyle.CASUAL
) {
    Canvas(modifier = modifier.size(120.dp)) {
        val w = size.width
        val h = size.height
        
        // Base Tooth Path
        val toothPath = Path().apply {
            moveTo(w * 0.25f, h * 0.25f)
            cubicTo(w * 0.25f, h * 0.05f, w * 0.75f, h * 0.05f, w * 0.75f, h * 0.25f)
            quadraticTo(w * 0.82f, h * 0.5f, w * 0.78f, h * 0.75f)
            cubicTo(w * 0.78f, h * 0.95f, w * 0.55f, h * 0.95f, w * 0.55f, h * 0.75f)
            quadraticTo(w * 0.5f, h * 0.65f, w * 0.45f, h * 0.75f)
            cubicTo(w * 0.45f, h * 0.95f, w * 0.22f, h * 0.95f, w * 0.22f, h * 0.75f)
            quadraticTo(w * 0.18f, h * 0.5f, w * 0.25f, h * 0.25f)
            close()
        }

        when (style) {
            MascotStyle.CASUAL -> {
                drawPath(path = toothPath, color = Color.White, style = Fill)
                drawPath(path = toothPath, color = Color(0xFFCBD5E1), style = Stroke(width = 4f))

                // Eyes
                drawCircle(color = Color.Black, radius = w * 0.07f, center = Offset(w * 0.38f, h * 0.35f))
                drawCircle(color = Color.Black, radius = w * 0.07f, center = Offset(w * 0.62f, h * 0.35f))
                drawCircle(color = Color.White, radius = w * 0.02f, center = Offset(w * 0.36f, h * 0.33f))
                drawCircle(color = Color.White, radius = w * 0.02f, center = Offset(w * 0.60f, h * 0.33f))

                // Smile
                val smilePath = Path().apply {
                    moveTo(w * 0.35f, h * 0.55f)
                    quadraticTo(w * 0.5f, h * 0.72f, w * 0.65f, h * 0.55f)
                }
                drawPath(path = smilePath, color = Color(0xFFF43F5E), style = Stroke(width = 6f))
                
                // Rosy Cheeks
                drawCircle(color = Color(0xFFFFB7B7), radius = w * 0.05f, center = Offset(w * 0.28f, h * 0.52f), alpha = 0.6f)
                drawCircle(color = Color(0xFFFFB7B7), radius = w * 0.05f, center = Offset(w * 0.72f, h * 0.52f), alpha = 0.6f)
            }
            MascotStyle.COOL -> {
                // Shiny Golden Body
                drawPath(path = toothPath, color = Color(0xFFFFD700), style = Fill)
                drawPath(path = toothPath, color = Color(0xFFB8860B), style = Stroke(width = 6f))

                // Sunglasses
                drawRect(color = Color.Black, topLeft = Offset(w * 0.25f, h * 0.3f), size = Size(w * 0.5f, h * 0.15f))
                drawRect(color = Color.Black, topLeft = Offset(w * 0.45f, h * 0.33f), size = Size(w * 0.1f, h * 0.05f))
                
                // Cool Smile
                val smilePath = Path().apply {
                    moveTo(w * 0.35f, h * 0.55f)
                    quadraticTo(w * 0.5f, h * 0.7f, w * 0.65f, h * 0.55f)
                }
                drawPath(path = smilePath, color = Color.White, style = Stroke(width = 8f))
            }
            MascotStyle.SUPERHERO -> {
                // Cape
                val capePath = Path().apply {
                    moveTo(w * 0.2f, h * 0.3f)
                    lineTo(w * 0.05f, h * 0.8f)
                    lineTo(w * 0.95f, h * 0.8f)
                    lineTo(w * 0.8f, h * 0.3f)
                    close()
                }
                drawPath(path = capePath, color = Color(0xFFEF4444), style = Fill)

                drawPath(path = toothPath, color = Color.White, style = Fill)
                drawPath(path = toothPath, color = Color(0xFFCBD5E1), style = Stroke(width = 4f))

                // Mask
                drawRect(color = Color(0xFF3B82F6), topLeft = Offset(w * 0.22f, h * 0.3f), size = Size(w * 0.56f, h * 0.12f))

                // Heroic Smile
                val smilePath = Path().apply {
                    moveTo(w * 0.35f, h * 0.55f)
                    quadraticTo(w * 0.5f, h * 0.68f, w * 0.65f, h * 0.55f)
                }
                drawPath(path = smilePath, color = Color.Black, style = Stroke(width = 5f))
            }
        }
    }
}
