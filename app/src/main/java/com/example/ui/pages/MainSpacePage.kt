package com.example.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.DemoDataProvider
import com.example.model.WeatherInfo
import com.example.sensors.TiltState
import com.example.ui.components.TiltCard
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Main Space: Holographic 3D Clock & Weather Widget + 3D Tilt App Grid
 * Responsive for Phone, Tablet, Portrait, and Landscape.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainSpacePage(
    tiltState: TiltState,
    onAppClick: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    val weather = remember { WeatherInfo() }
    val density = LocalDensity.current.density

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        val screenWidth = maxWidth
        val isWideScreen = screenWidth > 640.dp

        Column(
            modifier = Modifier
                .widthIn(max = 840.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // ==========================================
            // 3D HOLOGRAPHIC CLOCK & WEATHER WIDGET
            // ==========================================
            val widgetRotX = (-tiltState.pitch * 9f).coerceIn(-15f, 15f)
            val widgetRotY = (tiltState.roll * 9f).coerceIn(-15f, 15f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationX = widgetRotX
                        rotationY = widgetRotY
                        cameraDistance = 16f * density
                    }
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = CyanNeon.copy(alpha = 0.25f)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0x331E293B),
                                Color(0x550F172A)
                            )
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                CyanNeon.copy(alpha = 0.6f),
                                GlassBorder,
                                PurpleNeon.copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .drawBehind {
                        // Dynamic light orb inside widget
                        val lightX = size.width * (0.6f + tiltState.roll * 0.3f)
                        val lightY = size.height * (0.4f - tiltState.pitch * 0.3f)
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    CyanNeon.copy(alpha = 0.18f),
                                    Color.Transparent
                                ),
                                center = Offset(lightX, lightY),
                                radius = size.width * 0.5f
                            ),
                            center = Offset(lightX, lightY),
                            radius = size.width * 0.5f
                        )
                    }
                    .padding(20.dp)
            ) {
                if (isWideScreen) {
                    // Landscape / Tablet layout for widget
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ClockSection(currentTime, currentDate)
                        WeatherSection(weather)
                    }
                } else {
                    // Portrait phone layout for widget
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ClockSection(currentTime, currentDate)
                        Spacer(modifier = Modifier.height(14.dp))
                        WeatherSection(weather)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DIMENSION APPS",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "3D TILT ACTIVE",
                    color = CyanNeon,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ==========================================
            // 3D FLOATING TILT APP CARDS GRID
            // ==========================================
            val columns = if (isWideScreen) 4 else 3
            val spacing = 12.dp
            val itemWidth = (screenWidth - (spacing * (columns + 1))) / columns

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(spacing),
                maxItemsInEachRow = columns
            ) {
                DemoDataProvider.mainApps.forEach { app ->
                    TiltCard(
                        app = app,
                        tiltState = tiltState,
                        onClick = { onAppClick(app) },
                        modifier = Modifier
                            .width(itemWidth.coerceIn(96.dp, 160.dp))
                            .height(118.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Padding for bottom 3D dock
        }
    }
}

@Composable
private fun ClockSection(time: String, date: String) {
    Column {
        Text(
            text = time.ifEmpty { "12:00" },
            color = TextPrimary,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-1).sp
        )
        Text(
            text = date.ifEmpty { "Dimension OS" },
            color = CyanNeon,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun WeatherSection(weather: WeatherInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Glowing weather sun / cosmic icon
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(CyanNeon.copy(alpha = 0.15f))
                .border(1.dp, CyanNeon.copy(alpha = 0.4f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = "Weather condition",
                tint = CyanNeon,
                modifier = Modifier.size(26.dp)
            )
        }

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = weather.temp,
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = weather.condition,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = weather.city,
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "•",
                    color = TextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = "H:${weather.high} L:${weather.low}",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}
