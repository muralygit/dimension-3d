package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.abs

/**
 * 3D Cube-Style Pager Transition
 * Transforms page swipes into a realistic rotating 3D cube with
 * edge-pinned pivots, 90-degree angular perspective, and dynamic ambient shadow falloff.
 */
@Composable
fun CubePager(
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    pageContent: @Composable (page: Int) -> Unit
) {
    val density = LocalDensity.current.density

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        pageSpacing = 0.dp,
        beyondViewportPageCount = 1
    ) { page ->
        // Calculate continuous offset for current page
        val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Only apply 3D transformation when within visible transition range
                    if (pageOffset in -1f..1f) {
                        val angle = pageOffset * 90f // 90 degree cube corner
                        rotationY = angle
                        cameraDistance = 18f * density

                        // Anchor pivot to the shared joining edge
                        transformOrigin = if (pageOffset > 0f) {
                            // Page is exiting to the left: pivot on its right edge
                            TransformOrigin(1f, 0.5f)
                        } else {
                            // Page is entering from the right: pivot on its left edge
                            TransformOrigin(0f, 0.5f)
                        }
                    }
                }
        ) {
            // Render the page's actual UI content
            pageContent(page)

            // Dynamic ambient light falloff shadow on rotating cube face
            if (pageOffset in -1f..1f) {
                val shadeAlpha = (abs(pageOffset) * 0.70f).coerceIn(0f, 0.82f)
                if (shadeAlpha > 0.01f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = shadeAlpha))
                    )
                }
            }
        }
    }
}
