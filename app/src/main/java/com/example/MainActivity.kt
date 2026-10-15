package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import com.example.model.AppItem
import com.example.model.DemoDataProvider
import com.example.model.DockApp
import com.example.sensors.rememberTiltState
import com.example.ui.components.AppDetailDialog
import com.example.ui.components.CarouselDock
import com.example.ui.components.CubePager
import com.example.ui.components.ParallaxBackground
import com.example.ui.components.TopBar
import com.example.ui.pages.MainSpacePage
import com.example.ui.pages.MediaHubPage
import com.example.ui.pages.ProductivityPage
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                DimensionHomeScreen()
            }
        }
    }
}

data class SelectedAppPreview(
    val title: String,
    val category: String,
    val description: String,
    val icon: ImageVector,
    val accentColor: Color
)

@Composable
fun DimensionHomeScreen() {
    val tiltState = rememberTiltState()
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { 3 })
    val coroutineScope = rememberCoroutineScope()

    var selectedApp by remember { mutableStateOf<SelectedAppPreview?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundDark,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // Allow interactive drag on background to tilt the 3D space directly
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        // Subtle manual tilt adjustment when dragging background
                        val deltaRoll = dragAmount.x / (size.width * 0.7f)
                        val deltaPitch = -dragAmount.y / (size.height * 0.7f)
                        tiltState.setManualTilt(
                            targetPitch = tiltState.pitch + deltaPitch,
                            targetRoll = tiltState.roll + deltaRoll
                        )
                    }
                }
        ) {
            // Feature 1: 3-Layer Parallax Background
            ParallaxBackground(
                tiltState = tiltState,
                modifier = Modifier.fillMaxSize()
            )

            // Main Content Layer with status bar insets
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                // Top Bar with 3D status & Page tabs
                TopBar(
                    currentPage = pagerState.currentPage,
                    totalPages = 3,
                    onPageSelected = { targetPage ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(targetPage)
                        }
                    },
                    tiltState = tiltState
                )

                // Feature 4: 3D Cube-Style Page Transitions between the 3 demo pages
                Box(modifier = Modifier.weight(1f)) {
                    CubePager(pagerState = pagerState) { page ->
                        when (page) {
                            0 -> MainSpacePage(
                                tiltState = tiltState,
                                onAppClick = { app ->
                                    selectedApp = SelectedAppPreview(
                                        title = app.name,
                                        category = app.category,
                                        description = app.description,
                                        icon = app.icon,
                                        accentColor = app.accentColor
                                    )
                                }
                            )
                            1 -> MediaHubPage(
                                tiltState = tiltState
                            )
                            2 -> ProductivityPage(
                                tiltState = tiltState,
                                onAppClick = { app ->
                                    selectedApp = SelectedAppPreview(
                                        title = app.name,
                                        category = app.category,
                                        description = app.description,
                                        icon = app.icon,
                                        accentColor = app.accentColor
                                    )
                                }
                            )
                        }
                    }
                }
            }

            // Feature 3: Rotating 3D Carousel Dock at bottom with navigation bar padding
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                CarouselDock(
                    dockApps = DemoDataProvider.dockApps,
                    onAppClick = { dockApp ->
                        selectedApp = SelectedAppPreview(
                            title = dockApp.name,
                            category = "Dock Orbit",
                            description = "Frequently accessed spatial quick action.",
                            icon = dockApp.icon,
                            accentColor = dockApp.accentColor
                        )
                    }
                )
            }

            // Interactive 3D App Preview Dialog
            selectedApp?.let { app ->
                AppDetailDialog(
                    title = app.title,
                    category = app.category,
                    description = app.description,
                    icon = app.icon,
                    accentColor = app.accentColor,
                    onDismiss = { selectedApp = null }
                )
            }
        }
    }
}
