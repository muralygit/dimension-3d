package com.example.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.model.DemoDataProvider
import com.example.sensors.TiltState
import com.example.ui.components.TiltCard
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class TaskItem(val id: Int, val title: String, val tag: String, var isDone: Boolean)

/**
 * Page 3: Productivity & Labs Space
 * 3D Layered Task Deck, Decentralized Cloud Vault Storage telemetry,
 * and Productivity Apps.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductivityPage(
    tiltState: TiltState,
    onAppClick: (AppItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val tasks = remember {
        mutableStateListOf(
            TaskItem(1, "Deploy Quantum Hypercube v2.4", "CRITICAL", false),
            TaskItem(2, "Review LiDAR Mesh Spatial Scan", "3D MODEL", true),
            TaskItem(3, "Sync Zero-Knowledge Vault Keys", "SECURITY", false),
            TaskItem(4, "Calibrate Gyroscope Tilt Matrix", "HARDWARE", true)
        )
    }

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
            // 3D TASK STACK DECK
            // ==========================================
            val deckRotX = (-tiltState.pitch * 9f).coerceIn(-15f, 15f)
            val deckRotY = (tiltState.roll * 9f).coerceIn(-15f, 15f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationX = deckRotX
                        rotationY = deckRotY
                        cameraDistance = 16f * density
                    }
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = BlueElectric.copy(alpha = 0.25f)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF132038).copy(alpha = 0.9f),
                                Color(0xFF0F1526).copy(alpha = 0.95f)
                            )
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                BlueElectric.copy(alpha = 0.6f),
                                GlassBorder,
                                CyanNeon.copy(alpha = 0.4f)
                            )
                        ),
                        shape = RoundedCornerShape(26.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Layers,
                                contentDescription = null,
                                tint = BlueElectric,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ACTIVE SPRINT TASKS",
                                color = BlueElectric,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "${tasks.count { it.isDone }}/${tasks.size} Done",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    tasks.forEachIndexed { index, task ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x221E293B))
                                .clickable {
                                    tasks[index] = task.copy(isDone = !task.isDone)
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (task.isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (task.isDone) EmeraldNeon else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = task.title,
                                    color = if (task.isDone) TextMuted else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        when (task.tag) {
                                            "CRITICAL" -> AmberNeon.copy(alpha = 0.2f)
                                            "3D MODEL" -> PurpleNeon.copy(alpha = 0.2f)
                                            "SECURITY" -> CyanNeon.copy(alpha = 0.2f)
                                            else -> EmeraldNeon.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = task.tag,
                                    color = when (task.tag) {
                                        "CRITICAL" -> AmberNeon
                                        "3D MODEL" -> PurpleNeon
                                        "SECURITY" -> CyanNeon
                                        else -> EmeraldNeon
                                    },
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // STORAGE VAULT TELEMETRY
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationX = (-tiltState.pitch * 7f).coerceIn(-12f, 12f)
                        rotationY = (tiltState.roll * 7f).coerceIn(-12f, 12f)
                        cameraDistance = 14f * density
                    }
                    .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = CyanNeon.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(Color(0xFF131A2D))
                    .border(1.dp, GlassBorder, RoundedCornerShape(22.dp))
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = CyanNeon,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Cold Storage Vault",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "7.84 TB / 10.00 TB Used (78%)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CyanNeon.copy(alpha = 0.2f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "OPTIMIZED",
                            color = CyanNeon,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // PRODUCTIVITY TOOLS GRID
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LAB & SYSTEM UTILITIES",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val columns = if (isWideScreen) 4 else 2
            val spacing = 12.dp
            val itemWidth = (screenWidth - (spacing * (columns + 1))) / columns

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(spacing),
                maxItemsInEachRow = columns
            ) {
                DemoDataProvider.toolsApps.forEach { app ->
                    TiltCard(
                        app = app,
                        tiltState = tiltState,
                        onClick = { onAppClick(app) },
                        modifier = Modifier
                            .width(itemWidth.coerceIn(120.dp, 200.dp))
                            .height(118.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Padding for bottom 3D dock
        }
    }
}
