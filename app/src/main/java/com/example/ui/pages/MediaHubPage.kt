package com.example.ui.pages

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DemoDataProvider
import com.example.model.MediaTrack
import com.example.sensors.TiltState
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Page 2: Media & Hub Space
 * Interactive 3D Spinning Vinyl Audio Deck, Real-time Sound Spectrum,
 * and 3D System Telemetry Vitals.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaHubPage(
    tiltState: TiltState,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var playbackProgress by remember { mutableFloatStateOf(0.38f) }
    var volume by remember { mutableFloatStateOf(0.75f) }
    val track = remember { MediaTrack() }
    val density = LocalDensity.current.density

    // Disc spin animation
    val infiniteTransition = rememberInfiniteTransition(label = "vinyl")
    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "disc_spin"
    )

    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sound_wave"
    )

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
            // 3D SPATIAL AUDIO PLAYER DECK
            // ==========================================
            val deckRotX = (-tiltState.pitch * 10f).coerceIn(-18f, 18f)
            val deckRotY = (tiltState.roll * 10f).coerceIn(-18f, 18f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        rotationX = deckRotX
                        rotationY = deckRotY
                        cameraDistance = 16f * density
                    }
                    .shadow(
                        elevation = 14.dp,
                        shape = RoundedCornerShape(26.dp),
                        spotColor = PinkNeon.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(26.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF231936).copy(alpha = 0.9f),
                                Color(0xFF131526).copy(alpha = 0.95f)
                            )
                        )
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                PinkNeon.copy(alpha = 0.6f),
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
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.GraphicEq,
                                contentDescription = null,
                                tint = PinkNeon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SPATIAL AUDIO 3D",
                                color = PinkNeon,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "FLAC • 96kHz / 24bit",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        // 3D Spinning Vinyl Disc
                        Box(
                            modifier = Modifier
                                .size(92.dp)
                                .shadow(8.dp, CircleShape, spotColor = PinkNeon)
                                .clip(CircleShape)
                                .background(Color(0xFF0F0E17))
                                .border(2.dp, Color(0xFF2D2A45), CircleShape)
                                .graphicsLayer {
                                    rotationZ = if (isPlaying) spinAngle else 0f
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Grooves on vinyl
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val center = Offset(size.width / 2f, size.height / 2f)
                                listOf(0.35f, 0.55f, 0.75f, 0.9f).forEach { r ->
                                    drawCircle(
                                        color = Color(0x334A4566),
                                        radius = (size.width / 2f) * r,
                                        center = center,
                                        style = Stroke(width = 1f)
                                    )
                                }
                            }
                            // Center Label
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(PinkNeon),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Track Info & Audio Spectrum
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                color = TextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${track.artist} • ${track.album}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Animated Audio Spectrum Bars
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(26.dp)
                            ) {
                                val barCount = 18
                                val barWidth = (size.width / barCount) * 0.65f
                                val spacing = (size.width / barCount)
                                for (i in 0 until barCount) {
                                    val factor = ((i * 17 + (waveOffset * 30).toInt()) % 10) / 10f
                                    val barHeight = if (isPlaying) {
                                        (size.height * (0.25f + factor * 0.75f)).coerceAtLeast(4f)
                                    } else {
                                        4f
                                    }
                                    val x = i * spacing
                                    val y = size.height - barHeight
                                    drawRoundRect(
                                        brush = Brush.verticalGradient(
                                            colors = listOf(PinkNeon, CyanNeon)
                                        ),
                                        topLeft = Offset(x, y),
                                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Slider
                    Slider(
                        value = playbackProgress,
                        onValueChange = { playbackProgress = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = PinkNeon,
                            activeTrackColor = PinkNeon,
                            inactiveTrackColor = Color(0x33FFFFFF)
                        )
                    )

                    // Playback Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "1:34", color = TextMuted, fontSize = 11.sp)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(onClick = { playbackProgress = (playbackProgress - 0.1f).coerceAtLeast(0f) }) {
                                Icon(Icons.Default.FastRewind, contentDescription = "Rewind", tint = TextPrimary)
                            }
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(PinkNeon)
                                    .clickable { isPlaying = !isPlaying },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = Color.Black,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            IconButton(onClick = { playbackProgress = (playbackProgress + 0.1f).coerceAtMost(1f) }) {
                                Icon(Icons.Default.FastForward, contentDescription = "Forward", tint = TextPrimary)
                            }
                        }

                        Text(text = "4:08", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ==========================================
            // 3D SYSTEM VITALS TELEMETRY
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "NEURAL TELEMETRY",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Text(
                    text = "ALL CORES NOMINAL",
                    color = EmeraldNeon,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            val vitalsColumns = if (isWideScreen) 4 else 2
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                maxItemsInEachRow = vitalsColumns
            ) {
                DemoDataProvider.systemStats.forEach { stat ->
                    val statWidth = (screenWidth - (12.dp * (vitalsColumns + 1))) / vitalsColumns
                    Box(
                        modifier = Modifier
                            .width(statWidth.coerceIn(140.dp, 260.dp))
                            .graphicsLayer {
                                rotationX = (-tiltState.pitch * 8f).coerceIn(-14f, 14f)
                                rotationY = (tiltState.roll * 8f).coerceIn(-14f, 14f)
                                cameraDistance = 14f * density
                            }
                            .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = stat.color.copy(alpha = 0.25f))
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E2438), Color(0xFF111424))
                                )
                            )
                            .border(
                                1.dp,
                                stat.color.copy(alpha = 0.4f),
                                RoundedCornerShape(18.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = stat.icon,
                                    contentDescription = null,
                                    tint = stat.color,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = stat.value,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = stat.label,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Progress bar
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0x33FFFFFF))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(stat.progress)
                                        .height(6.dp)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(stat.color)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(120.dp)) // Padding for bottom 3D dock
        }
    }
}
