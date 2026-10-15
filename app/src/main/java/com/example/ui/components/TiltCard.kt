package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppItem
import com.example.sensors.TiltState
import com.example.ui.theme.BackgroundDarker
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

/**
 * 3D Floating Tilt Card with dynamic depth tilt, dynamic specular sheen,
 * soft reactive shadow, and spring physics on touch.
 */
@Composable
fun TiltCard(
    app: AppItem,
    tiltState: TiltState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tiltMultiplier: Float = 14f
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Bouncy spring animation for press state
    val scaleAnim by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )

    val elevationAnim by animateFloatAsState(
        targetValue = if (isPressed) 2f else 10f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_elevation"
    )

    val density = LocalDensity.current.density
    val shape = RoundedCornerShape(22.dp)

    // Dynamic tilt angles derived from pitch & roll
    val rotX = (-tiltState.pitch * tiltMultiplier).coerceIn(-22f, 22f)
    val rotY = (tiltState.roll * tiltMultiplier).coerceIn(-22f, 22f)

    // Soft reactive shadow offset moves opposite to tilt
    val shadowOffsetX = (-tiltState.roll * 8f).dp
    val shadowOffsetY = (tiltState.pitch * 8f + 6f).dp

    Box(
        modifier = modifier
            .testTag("app_card_${app.id}")
            .graphicsLayer {
                rotationX = rotX
                rotationY = rotY
                cameraDistance = 14f * density
                scaleX = scaleAnim
                scaleY = scaleAnim
            }
            .offset {
                IntOffset(
                    x = (tiltState.roll * 4f).roundToInt(),
                    y = (tiltState.pitch * 4f).roundToInt()
                )
            }
            .shadow(
                elevation = elevationAnim.dp,
                shape = shape,
                ambientColor = Color.Black.copy(alpha = 0.5f),
                spotColor = app.glowColor.copy(alpha = 0.35f)
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF1B2238).copy(alpha = 0.88f),
                        Color(0xFF101424).copy(alpha = 0.95f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(180f, 260f)
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        app.accentColor.copy(alpha = 0.7f),
                        GlassBorder.copy(alpha = 0.2f),
                        Color.Transparent
                    )
                ),
                shape = shape
            )
            // Dynamic specular sheen across card face
            .drawBehind {
                val sheenX = size.width * (0.5f + tiltState.roll * 0.5f)
                val sheenY = size.height * (0.5f - tiltState.pitch * 0.5f)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.15f),
                            app.accentColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = Offset(sheenX, sheenY),
                        radius = size.width * 0.75f
                    ),
                    center = Offset(sheenX, sheenY),
                    radius = size.width * 0.75f
                )
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Floating 3D Icon container with radial glow
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .graphicsLayer {
                        // Inner icon floating slightly higher in 3D space with tilt
                        rotationX = rotX * 0.5f
                        rotationY = rotY * 0.5f
                    }
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = app.glowColor
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                app.accentColor.copy(alpha = 0.28f),
                                app.accentColor.copy(alpha = 0.08f)
                            )
                        )
                    )
                    .border(
                        width = 1.dp,
                        color = app.accentColor.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = app.icon,
                    contentDescription = app.name,
                    tint = app.accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Label
            Text(
                text = app.name,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )

            // Category tag
            Text(
                text = app.category,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }

        // Notification Badge if count > 0
        if (app.badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(app.accentColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${app.badgeCount}",
                    color = BackgroundDarker,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
