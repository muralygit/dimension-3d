package com.example.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.BlueElectric
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.PinkNeon
import com.example.ui.theme.PurpleNeon

data class AppItem(
    val id: String,
    val name: String,
    val category: String,
    val icon: ImageVector,
    val accentColor: Color,
    val glowColor: Color,
    val badgeCount: Int = 0,
    val description: String = ""
)

data class DockApp(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val accentColor: Color,
    val glowColor: Color,
    val badgeCount: Int = 0
)

data class WeatherInfo(
    val city: String = "Neo Tokyo",
    val temp: String = "24°",
    val condition: String = "Cosmic Clear",
    val high: String = "27°",
    val low: String = "18°",
    val humidity: String = "62%",
    val uvIndex: String = "3 (Low)",
    val wind: String = "12 km/h"
)

data class MediaTrack(
    val title: String = "Cyber Voyager 2099",
    val artist: String = "Synthetica Orchestra",
    val album: String = "Neon Horizon Vol. 3",
    val durationSeconds: Int = 248,
    val currentSeconds: Int = 94,
    val isPlaying: Boolean = true
)

data class SystemStat(
    val label: String,
    val value: String,
    val progress: Float,
    val icon: ImageVector,
    val color: Color
)

object DemoDataProvider {
    val mainApps = listOf(
        AppItem(
            id = "browser",
            name = "Orbit Web",
            category = "Network",
            icon = Icons.Default.Language,
            accentColor = CyanNeon,
            glowColor = CyanNeon.copy(alpha = 0.5f),
            badgeCount = 0,
            description = "Quantum-encrypted hyper-speed browsing engine."
        ),
        AppItem(
            id = "gallery",
            name = "Holo Gallery",
            category = "Media",
            icon = Icons.Default.PhotoLibrary,
            accentColor = PurpleNeon,
            glowColor = PurpleNeon.copy(alpha = 0.5f),
            badgeCount = 3,
            description = "Spatial stereoscopic photo and spatial capture viewer."
        ),
        AppItem(
            id = "camera",
            name = "Lens 3D",
            category = "Capture",
            icon = Icons.Default.CameraAlt,
            accentColor = BlueElectric,
            glowColor = BlueElectric.copy(alpha = 0.5f),
            badgeCount = 0,
            description = "LiDAR depth scanning and volumetric video recording."
        ),
        AppItem(
            id = "maps",
            name = "Geo Mesh",
            category = "Navigation",
            icon = Icons.Default.Map,
            accentColor = EmeraldNeon,
            glowColor = EmeraldNeon.copy(alpha = 0.5f),
            badgeCount = 0,
            description = "Full 3D holographic terrain map with live satellite telemetry."
        ),
        AppItem(
            id = "calc",
            name = "Matrix Calc",
            category = "Utility",
            icon = Icons.Default.Calculate,
            accentColor = AmberNeon,
            glowColor = AmberNeon.copy(alpha = 0.5f),
            badgeCount = 0,
            description = "High-precision tensor algebra and scientific computation engine."
        ),
        AppItem(
            id = "security",
            name = "Aegis Guard",
            category = "System",
            icon = Icons.Default.Security,
            accentColor = PinkNeon,
            glowColor = PinkNeon.copy(alpha = 0.5f),
            badgeCount = 1,
            description = "Real-time sandbox telemetry and cryptographic shield active."
        )
    )

    val dockApps = listOf(
        DockApp(
            id = "phone",
            name = "Comms",
            icon = Icons.Default.Phone,
            accentColor = EmeraldNeon,
            glowColor = EmeraldNeon.copy(alpha = 0.6f)
        ),
        DockApp(
            id = "messages",
            name = "Holochat",
            icon = Icons.AutoMirrored.Filled.Chat,
            accentColor = CyanNeon,
            glowColor = CyanNeon.copy(alpha = 0.6f),
            badgeCount = 5
        ),
        DockApp(
            id = "core",
            name = "Nexus Core",
            icon = Icons.Default.RocketLaunch,
            accentColor = PurpleNeon,
            glowColor = PurpleNeon.copy(alpha = 0.7f)
        ),
        DockApp(
            id = "music",
            name = "Audio Pulse",
            icon = Icons.Default.Headphones,
            accentColor = PinkNeon,
            glowColor = PinkNeon.copy(alpha = 0.6f)
        ),
        DockApp(
            id = "files",
            name = "Vault",
            icon = Icons.Default.Folder,
            accentColor = AmberNeon,
            glowColor = AmberNeon.copy(alpha = 0.6f)
        ),
        DockApp(
            id = "settings",
            name = "System",
            icon = Icons.Default.Settings,
            accentColor = BlueElectric,
            glowColor = BlueElectric.copy(alpha = 0.6f)
        )
    )

    val toolsApps = listOf(
        AppItem(
            id = "terminal",
            name = "Shell 3D",
            category = "Developer",
            icon = Icons.Default.Terminal,
            accentColor = EmeraldNeon,
            glowColor = EmeraldNeon.copy(alpha = 0.5f),
            description = "Local zsh terminal emulator with ANSI color highlighting."
        ),
        AppItem(
            id = "memory",
            name = "Silicon Vitals",
            category = "Diagnostics",
            icon = Icons.Default.Memory,
            accentColor = CyanNeon,
            glowColor = CyanNeon.copy(alpha = 0.5f),
            description = "GPU core clocks, NPU neural tensor ops, and thermal profiles."
        ),
        AppItem(
            id = "cloud",
            name = "Nebula Drive",
            category = "Storage",
            icon = Icons.Default.Cloud,
            accentColor = BlueElectric,
            glowColor = BlueElectric.copy(alpha = 0.5f),
            badgeCount = 2,
            description = "10 TB decentralized cold vault encrypted with zero-knowledge."
        ),
        AppItem(
            id = "video",
            name = "Holo Studio",
            category = "Creator",
            icon = Icons.Default.Videocam,
            accentColor = PinkNeon,
            glowColor = PinkNeon.copy(alpha = 0.5f),
            description = "Multi-track non-linear 4K HDR editor with stereoscopic rendering."
        )
    )

    val systemStats = listOf(
        SystemStat("Neural Core", "87% Load", 0.87f, Icons.Default.Memory, CyanNeon),
        SystemStat("Battery Cell", "92% Opt", 0.92f, Icons.Default.Speed, EmeraldNeon),
        SystemStat("RAM Buffer", "6.2 / 16 GB", 0.38f, Icons.Default.RocketLaunch, PurpleNeon),
        SystemStat("Quantum Link", "1.2 Gbps", 0.75f, Icons.Default.Navigation, AmberNeon)
    )
}
