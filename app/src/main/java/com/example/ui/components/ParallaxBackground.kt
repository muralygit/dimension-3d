package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.sensors.TiltState
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BackgroundDarker
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.PurpleNeon
import kotlin.math.cos
import kotlin.math.sin

/**
 * 3-Layer Parallax Background
 * Layer 1 (Far): Deep cosmic nebula & space grid (shift factor 18px)
 * Layer 2 (Mid): 3D Holographic geometric rings & horizon mesh (shift factor 45px)
 * Layer 3 (Near): Floating luminous particles & celestial glow motes (shift factor 85px)
 */
@Composable
fun ParallaxBackground(
    tiltState: TiltState,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "stars")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val spinAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(40000, easing = LinearEasing)
        ),
        label = "spin"
    )

    // Pre-calculate pseudo-random deterministic star coordinates for layer 1 & 3
    val starsLayer1 = remember {
        List(40) { index ->
            val u = ((index * 37) % 100) / 100f
            val v = ((index * 61) % 100) / 100f
            val size = 1f + (index % 3) * 0.8f
            Triple(u, v, size)
        }
    }

    val motesLayer3 = remember {
        List(20) { index ->
            val u = ((index * 73 + 19) % 100) / 100f
            val v = ((index * 43 + 31) % 100) / 100f
            val radius = 2.5f + (index % 4) * 1.5f
            val isCyan = index % 2 == 0
            Triple(u, v, Pair(radius, isCyan))
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        BackgroundDarker,
                        BackgroundDark,
                        Color(0xFF0F1426)
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Parallax displacements based on pitch and roll
            val farShiftX = tiltState.roll * 18f
            val farShiftY = tiltState.pitch * 18f

            val midShiftX = tiltState.roll * 45f
            val midShiftY = tiltState.pitch * 45f

            val nearShiftX = tiltState.roll * 85f
            val nearShiftY = tiltState.pitch * 85f

            // ==========================================
            // LAYER 1: Deep Cosmic Nebula & Deep Grid
            // ==========================================
            val nebula1Center = Offset(width * 0.3f + farShiftX, height * 0.25f + farShiftY)
            val nebula2Center = Offset(width * 0.75f + farShiftX, height * 0.7f + farShiftY)

            // Deep purple nebula
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        PurpleNeon.copy(alpha = 0.18f),
                        PurpleNeon.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    center = nebula1Center,
                    radius = width * 0.65f
                ),
                center = nebula1Center,
                radius = width * 0.65f
            )

            // Deep cyan nebula
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyanNeon.copy(alpha = 0.14f),
                        CyanNeon.copy(alpha = 0.03f),
                        Color.Transparent
                    ),
                    center = nebula2Center,
                    radius = width * 0.6f
                ),
                center = nebula2Center,
                radius = width * 0.6f
            )

            // Distant subtle stars (Layer 1)
            for (star in starsLayer1) {
                val sx = (star.first * width + farShiftX).mod(width)
                val sy = (star.second * height + farShiftY).mod(height)
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f * pulseAlpha),
                    radius = star.third,
                    center = Offset(sx, sy)
                )
            }

            // ==========================================
            // LAYER 2: 3D Holographic Rings & Geometric Mesh
            // ==========================================
            val midCenter = Offset(width * 0.5f + midShiftX, height * 0.45f + midShiftY)

            // Concentric 3D perspective ellipse rings
            val ringRadii = listOf(width * 0.28f, width * 0.44f, width * 0.62f)
            ringRadii.forEachIndexed { i, radius ->
                val ringColor = if (i % 2 == 0) CyanNeon else PurpleNeon
                val alpha = (0.08f - i * 0.02f).coerceAtLeast(0.02f)

                // Rotated oval simulating 3D inclined orbital plane
                drawOval(
                    color = ringColor.copy(alpha = alpha),
                    topLeft = Offset(midCenter.x - radius, midCenter.y - radius * 0.55f),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 1.1f),
                    style = Stroke(width = 1.2f)
                )
            }

            // Dynamic 3D Horizon Grid Lines (angled perspective)
            val horizonY = height * 0.68f + midShiftY
            val gridPath = Path()
            val stepCount = 8
            for (i in 0..stepCount) {
                val t = i.toFloat() / stepCount
                val startX = width * (0.5f + (t - 0.5f) * 0.3f) + midShiftX * 0.4f
                val endX = width * (0.5f + (t - 0.5f) * 1.8f) + midShiftX
                gridPath.moveTo(startX, horizonY)
                gridPath.lineTo(endX, height + 80f)
            }
            drawPath(
                path = gridPath,
                color = CyanNeon.copy(alpha = 0.07f),
                style = Stroke(width = 1f)
            )

            // ==========================================
            // LAYER 3: Near Floating Particles & Light Sheen
            // ==========================================
            for (mote in motesLayer3) {
                val mx = (mote.first * width + nearShiftX).mod(width)
                val my = (mote.second * height + nearShiftY).mod(height)
                val moteRadius = mote.third.first
                val isCyan = mote.third.second
                val moteColor = if (isCyan) CyanNeon else PurpleNeon

                // Particle outer glow
                drawCircle(
                    color = moteColor.copy(alpha = 0.25f * pulseAlpha),
                    radius = moteRadius * 2.2f,
                    center = Offset(mx, my)
                )
                // Particle bright core
                drawCircle(
                    color = Color.White.copy(alpha = 0.85f * pulseAlpha),
                    radius = moteRadius * 0.8f,
                    center = Offset(mx, my)
                )
            }

            // Interactive dynamic tilt sheen across the edge
            val sheenCenterX = width * (0.5f + tiltState.roll * 0.4f)
            val sheenCenterY = height * (0.15f - tiltState.pitch * 0.3f)
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.12f),
                        CyanNeon.copy(alpha = 0.04f),
                        Color.Transparent
                    ),
                    center = Offset(sheenCenterX, sheenCenterY),
                    radius = width * 0.45f
                ),
                center = Offset(sheenCenterX, sheenCenterY),
                radius = width * 0.45f
            )
        }
    }
}
