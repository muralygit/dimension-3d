package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.model.DockApp
import com.example.ui.theme.BackgroundDarker
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.PurpleNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Rotating 3D Carousel Dock
 * Swiping spins the icons smoothly in a 3D circle with cylindrical perspective,
 * dynamic z-sorting, inward face tilt, scale, and spring snap physics.
 */
@Composable
fun CarouselDock(
    dockApps: List<DockApp>,
    onAppClick: (DockApp) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val rotationAnim = remember { Animatable(0f) }
    val density = LocalDensity.current.density

    val count = dockApps.size
    val anglePerItem = (2.0 * PI / count).toFloat()

    // Active item closest to the front (angle ~ 0 mod 2*PI)
    val activeIndex by remember {
        derivedStateOf {
            val normalized = ((-rotationAnim.value % (2 * PI) + (2 * PI)) % (2 * PI)).toFloat()
            val index = ((normalized + anglePerItem / 2f) / anglePerItem).toInt() % count
            index
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .testTag("carousel_dock")
            .pointerInput(count) {
                detectDragGestures(
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val deltaAngle = (dragAmount.x / (size.width * 0.45f)) * 1.3f
                        coroutineScope.launch {
                            rotationAnim.snapTo(rotationAnim.value + deltaAngle)
                        }
                    },
                    onDragEnd = {
                        // Snap smoothly to nearest item with spring physics
                        val targetItem = Math.round(-rotationAnim.value / anglePerItem).toInt()
                        val targetAngle = -targetItem * anglePerItem
                        coroutineScope.launch {
                            rotationAnim.animateTo(
                                targetValue = targetAngle,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                        }
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        val availableWidth = maxWidth
        // Adapt radius based on available width (phone vs tablet)
        val radiusX = (availableWidth.value * 0.36f).coerceIn(110f, 260f)

        // Glassmorphic Pedestal Platform below dock
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp)
                .width((availableWidth.value * 0.88f).coerceIn(240f, 540f).dp)
                .height(46.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x331E293B),
                            Color(0x660F172A)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            GlassBorder,
                            CyanNeon.copy(alpha = 0.4f),
                            GlassBorder,
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .drawBehind {
                    // Orbital glow disc
                    drawOval(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                CyanNeon.copy(alpha = 0.25f),
                                PurpleNeon.copy(alpha = 0.10f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height / 2f),
                            radius = size.width * 0.45f
                        )
                    )
                }
        )

        // Render each 3D carousel item with z-depth sorting
        dockApps.forEachIndexed { index, app ->
            val itemAngle = rotationAnim.value + index * anglePerItem
            // z: +1 is closest to user (front), -1 is farthest (back)
            val z = cos(itemAngle.toDouble()).toFloat()
            val x = (sin(itemAngle.toDouble()).toFloat()) * radiusX

            // Scale between 0.68f and 1.15f
            val normZ = (z + 1f) / 2f // 0f .. 1f
            val itemScale = 0.68f + normZ * 0.47f
            val itemAlpha = 0.42f + normZ * 0.58f

            // Inward cylinder tilt (rotationY)
            val rotY = (-sin(itemAngle.toDouble()).toFloat()) * 32f

            Box(
                modifier = Modifier
                    .zIndex(normZ * 100f)
                    .offset {
                        IntOffset(
                            x = (x * density).roundToInt(),
                            y = ((-z * 10f) * density).roundToInt() - (12 * density).roundToInt()
                        )
                    }
                    .graphicsLayer {
                        scaleX = itemScale
                        scaleY = itemScale
                        alpha = itemAlpha
                        rotationY = rotY
                        cameraDistance = 14f * density
                    }
                    .clickable {
                        // If tapped, animate carousel to bring this app front and center!
                        val targetItem = index
                        val currentNorm = ((-rotationAnim.value % (2 * PI) + 2 * PI) % (2 * PI))
                        val targetAngle = -index * anglePerItem
                        // Find closest rotational direction
                        var diff = (targetAngle - rotationAnim.value) % (2 * PI).toFloat()
                        if (diff > PI) diff -= (2 * PI).toFloat()
                        if (diff < -PI) diff += (2 * PI).toFloat()

                        coroutineScope.launch {
                            rotationAnim.animateTo(
                                targetValue = rotationAnim.value + diff,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                            onAppClick(app)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                // Dock Icon 3D Sphere / Glass Disc
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .shadow(
                            elevation = (4f + normZ * 10f).dp,
                            shape = CircleShape,
                            spotColor = app.glowColor
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF252D48),
                                    Color(0xFF111626)
                                )
                            )
                        )
                        .border(
                            width = (1f + normZ * 1f).dp,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    app.accentColor.copy(alpha = 0.85f),
                                    app.accentColor.copy(alpha = 0.2f)
                                )
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = app.icon,
                        contentDescription = app.name,
                        tint = app.accentColor,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Badge if present
                if (app.badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(app.accentColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${app.badgeCount}",
                            color = BackgroundDarker,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Active App Name display below carousel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedContent(
                targetState = dockApps.getOrNull(activeIndex)?.name ?: "",
                transitionSpec = {
                    (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMedium)))
                        .togetherWith(fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMedium)))
                },
                label = "dock_name"
            ) { name ->
                Text(
                    text = name,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
