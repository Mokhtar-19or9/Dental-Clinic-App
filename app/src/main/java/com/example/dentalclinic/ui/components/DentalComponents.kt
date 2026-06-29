package com.example.dentalclinic.ui.components

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.navigation.DentalRoute
import com.example.dentalclinic.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun RayImage(image: String?, modifier: Modifier = Modifier) {
    if (image.isNullOrBlank()) {
        ImageErrorPlaceholder(modifier)
        return
    }

    var loadError by remember(image) { mutableStateOf(false) }
    var base64Bitmap by remember(image) { mutableStateOf<android.graphics.Bitmap?>(null) }
    var decoding by remember(image) { mutableStateOf(false) }

    val isBase64Input = remember(image) {
        image != null && (image.startsWith("data:") || isBase64Chars(image))
    }

    val resolvedUrl = remember(image) {
        val url = if (isBase64Input) null else RetrofitClient.getImageUrl(image)
        Log.d("RayImage", "Original: $image -> Resolved: $url")
        url
    }

    LaunchedEffect(image) {
        if (isBase64Input) {
            decoding = true
            try {
                val rawB64 = extractRawBase64(image) ?: image
                base64Bitmap = withContext(Dispatchers.IO) { decodeBase64Bitmap(rawB64) }
                if (base64Bitmap == null) {
                    Log.e("RayImage", "Base64 decoding failed")
                    loadError = true
                }
            } catch (e: Exception) {
                loadError = true
            } finally {
                decoding = false
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp) // Force a minimum height so it doesn't collapse to 0
            .clip(RoundedCornerShape(12.dp))
            .background(DentalLine.copy(alpha = 0.3f)), // Light background to see the area
        contentAlignment = Alignment.Center
    ) {
        if (decoding) {
            CircularProgressIndicator(color = DentalTeal, modifier = Modifier.size(32.dp))
        } else if (base64Bitmap != null) {
            Image(
                bitmap = base64Bitmap!!.asImageBitmap(),
                contentDescription = "Dental Image",
                modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                contentScale = ContentScale.Fit
            )
        } else if (resolvedUrl != null) {
            if (loadError) {
                ImageErrorPlaceholder(Modifier.fillMaxSize())
            } else {
                AsyncImage(
                    model = resolvedUrl,
                    contentDescription = "Dental Image",
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(),
                    contentScale = ContentScale.Fit,
                    onState = { state ->
                        if (state is coil3.compose.AsyncImagePainter.State.Error) {
                            Log.e("RayImage", "Coil Error: ${state.result.throwable.message}")
                            loadError = true
                        }
                    }
                )
            }
        } else if (loadError) {
            ImageErrorPlaceholder(Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun ImageErrorPlaceholder(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DentalLine),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Filled.BrokenImage, contentDescription = null, tint = DentalMuted, modifier = Modifier.size(32.dp))
            Spacer(Modifier.height(4.dp))
            Text("Image unavailable", color = DentalMuted, fontSize = 11.sp)
        }
    }
}

private fun extractRawBase64(s: String): String? {
    val dataPrefix = "base64,"
    val idx = s.indexOf(dataPrefix)
    return if (idx >= 0) s.substring(idx + dataPrefix.length) else null
}

private fun isBase64Chars(s: String): Boolean {
    if (s.length < 20) return false
    return s.matches(Regex("^[A-Za-z0-9+/=_-]+$"))
}

private fun decodeBase64Bitmap(raw: String): android.graphics.Bitmap? {
    return try {
        // Clean the string from any whitespace or unexpected chars
        val cleaned = raw.replace(Regex("[^A-Za-z0-9+/=]"), "")
        val padded = when (cleaned.length % 4) {
            2 -> "${cleaned}=="
            3 -> "${cleaned}="
            else -> cleaned
        }
        val bytes = Base64.decode(padded, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) { null }
}

fun extractAnalysisImage(aiAnalysisJson: String?): String? {
    if (aiAnalysisJson.isNullOrBlank()) return null
    return try {
        val gson = com.google.gson.Gson()
        val obj = gson.fromJson(aiAnalysisJson, Map::class.java)
        for ((key, value) in obj) {
            val k = key.toString().lowercase()
            if (k in listOf("image", "analysisimage", "analysis_image", "overlay",
                    "heatmap", "mask", "resultimage", "result_image", "visualization",
                    "predictionimage", "scanimage", "processedimage", "image_url", "url", "file", "path")) {
                val v = value?.toString()
                if (!v.isNullOrBlank()) return v
            }
        }
        null
    } catch (_: Exception) {
        if (aiAnalysisJson.length > 50 && !aiAnalysisJson.contains("{")) {
            return aiAnalysisJson // Might be raw base64
        }
        null
    }
}

@Composable
fun DentalCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    accentColor: Color? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "scale")

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        if (accentColor != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(accentColor)
            )
        }
        Column(modifier = Modifier.padding(20.dp)) {
            content()
        }
    }
}

@Composable
fun DentalGradientCard(
    modifier: Modifier = Modifier,
    colors: List<Color> = listOf(DentalTeal, DentalTealDark),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.97f else 1f, label = "scale")

    Card(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = LocalIndication.current,
                        onClick = onClick
                    )
                } else Modifier
            ),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(colors))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title, 
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
        if (action != null && onAction != null) {
            Text(
                text = action,
                color = DentalTeal,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

@Composable
fun IconBubble(
    text: String,
    modifier: Modifier = Modifier,
    tint: Color = DentalTeal,
    background: Color = MaterialTheme.colorScheme.secondaryContainer,
    size: Int = 42
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = tint, fontWeight = FontWeight.Bold, fontSize = (size / 3).sp)
    }
}

@Composable
fun PrimaryDentalButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        enabled = enabled,
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DentalTeal,
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun SecondaryDentalButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DentalTeal
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, DentalTeal)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleSmall)
    }
}

@Composable
fun DentalHeader(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null
) {
    val initials = remember(title) {
        title.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .map { it.first().uppercase() }
            .joinToString("")
            .ifEmpty { "JD" }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(DentalTealDark, DentalTeal, DentalGradientEnd)
                )
            )
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(Modifier.width(4.dp))
        }
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(CircleShape)
                .background(DentalCyan),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                subtitle,
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        trailing?.invoke()
    }
}

@Composable
fun DentalBottomBar(
    routes: List<DentalRoute>,
    selectedRoute: DentalRoute,
    onNavigate: (DentalRoute) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        routes.forEach { item ->
            val selected = selectedRoute == item
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item) },
                icon = {
                    Text(
                        text = item.iconText,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) DentalTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                },
                label = {
                    Text(
                        if (item.labelRes != 0) stringResource(item.labelRes) else item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = DentalTeal,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                    indicatorColor = DentalTeal.copy(alpha = 0.12f)
                )
            )
        }
    }
}
