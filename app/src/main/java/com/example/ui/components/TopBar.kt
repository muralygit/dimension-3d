package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.sensors.TiltState
import com.example.ui.theme.BackgroundDarker
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

@Composable
fun TopBar(
    currentPage: Int,
    totalPages: Int,
    onPageSelected: (Int) -> Unit,
    tiltState: TiltState,
    modifier: Modifier = Modifier
) {
    var showGyroDialog by remember { mutableStateOf(false) }

    val pageTitles = listOf("Main", "Media", "Tools")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title / Brand
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { showGyroDialog = true }
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(listOf(CyanNeon, PurpleNeon))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ScreenRotation,
                    contentDescription = null,
                    tint = BackgroundDarker,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "DIMENSION 3D",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = if (tiltState.isSimulatedMode) "3D Simulation Active" else "Physical Gyro Tilt",
                    color = if (tiltState.isSimulatedMode) CyanNeon else EmeraldNeon,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Center: Interactive 3-Page Tabs
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x331E293B))
                .border(1.dp, GlassBorder, RoundedCornerShape(20.dp))
                .padding(3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 0 until totalPages) {
                val isSelected = (i == currentPage)
                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) CyanNeon else Color.Transparent,
                    label = "tab_bg"
                )
                val textColor by animateColorAsState(
                    targetValue = if (isSelected) BackgroundDarker else TextSecondary,
                    label = "tab_text"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .clickable { onPageSelected(i) }
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = pageTitles.getOrElse(i) { "Page ${i + 1}" },
                        color = textColor,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        // Right: Gyro Sensor Controller Dialog Trigger
        IconButton(
            onClick = { showGyroDialog = true },
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0x221E293B))
                .border(1.dp, GlassBorder, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Gyroscope Controls",
                tint = CyanNeon,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (showGyroDialog) {
        GyroControlsDialog(
            tiltState = tiltState,
            onDismiss = { showGyroDialog = false }
        )
    }
}

/**
 * Interactive 3D Gyro Controls & Joystick Simulator Dialog
 */
@Composable
private fun GyroControlsDialog(
    tiltState: TiltState,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(24.dp, RoundedCornerShape(26.dp), spotColor = CyanNeon)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF1B233A), Color(0xFF0F1322))
                    )
                )
                .border(1.dp, GlassBorder, RoundedCornerShape(26.dp))
                .padding(22.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "3D Gyro & Tilt Studio",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (tiltState.isSensorAvailable) EmeraldNeon.copy(alpha = 0.2f)
                                else CyanNeon.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (tiltState.isSensorAvailable) "Hardware Sensor" else "Emulator / Touch",
                            color = if (tiltState.isSensorAvailable) EmeraldNeon else CyanNeon,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Tilt your physical device or drag inside the virtual trackpad below to explore the 3-layer parallax and 3D card tilts.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Virtual 3D Tilt Trackpad
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0B0E17))
                        .border(1.5.dp, CyanNeon.copy(alpha = 0.4f), CircleShape)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val newRoll = tiltState.roll + (dragAmount.x / 90f)
                                val newPitch = tiltState.pitch - (dragAmount.y / 90f)
                                tiltState.isSimulatedMode = false
                                tiltState.setManualTilt(newPitch, newRoll)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Center crosshair rings
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .border(1.dp, Color(0x3338BDF8), CircleShape)
                    )

                    // Joystick puck moving with roll & pitch
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .graphicsLayer {
                                translationX = (tiltState.roll * 60f).coerceIn(-65f, 65f)
                                translationY = (-tiltState.pitch * 60f).coerceIn(-65f, 65f)
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(listOf(CyanNeon, PurpleNeon))
                            )
                            .border(1.dp, Color.White, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Telemetry readouts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Pitch: ${(tiltState.pitch * 90f).roundToInt()}°",
                        color = CyanNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Roll: ${(tiltState.roll * 90f).roundToInt()}°",
                        color = PurpleNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Mode Toggle button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (tiltState.isSimulatedMode) CyanNeon else Color(0x221E293B))
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                tiltState.isSimulatedMode = !tiltState.isSimulatedMode
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tiltState.isSimulatedMode) "Auto-Wave: ON" else "Auto-Wave: OFF",
                            color = if (tiltState.isSimulatedMode) BackgroundDarker else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x221E293B))
                            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                            .clickable {
                                tiltState.resetTilt()
                                tiltState.isSimulatedMode = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Center Tilt",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Done",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
