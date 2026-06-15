package com.example.dentalclinic.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.navigation.DentalRoute
import com.example.dentalclinic.ui.theme.DentalBackground
import com.example.dentalclinic.ui.theme.DentalCyan
import com.example.dentalclinic.ui.theme.DentalLine
import com.example.dentalclinic.ui.theme.DentalMuted
import com.example.dentalclinic.ui.theme.DentalSurface
import com.example.dentalclinic.ui.theme.DentalTeal
import com.example.dentalclinic.ui.theme.DentalTealDark

@Composable
fun DentalCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            content()
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            title, 
            fontWeight = FontWeight.Bold, 
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        if (action != null && onAction != null) {
            Text(
                text = action,
                color = DentalTeal,
                fontWeight = FontWeight.SemiBold,
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
    background: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = tint, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun PrimaryDentalButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DentalTeal,
            contentColor = Color.White
        )
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun SecondaryDentalButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = DentalTeal
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, DentalTeal)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold)
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
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(Brush.verticalGradient(listOf(DentalTealDark, DentalTeal)))
            .padding(horizontal = 22.dp, vertical = 26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBack != null) {
            Text(
                "←",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick = onBack)
            )
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(DentalCyan),
            contentAlignment = Alignment.Center
        ) {
            Text(initials, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(subtitle, color = Color.White.copy(alpha = 0.8f), style = MaterialTheme.typography.bodySmall)
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
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
                        color = if (selected) DentalTeal else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                label = {
                    Text(
                        if (item.labelRes != 0) stringResource(item.labelRes) else item.label,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = DentalTeal,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    indicatorColor = DentalTeal.copy(alpha = 0.1f)
                )
            )
        }
    }
}
