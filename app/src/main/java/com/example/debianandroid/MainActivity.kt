package com.example.debianandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.debianandroid.model.WindowType
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.DarkSurface
import com.example.debianandroid.theme.DarkSurfaceElevated
import com.example.debianandroid.theme.DarkSurfaceVariant
import com.example.debianandroid.theme.DebianAndroidTheme
import com.example.debianandroid.theme.DebianRed
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.ui.components.TopNavBar
import com.example.debianandroid.ui.screens.DashboardScreen
import com.example.debianandroid.ui.screens.DesktopScreen
import com.example.debianandroid.ui.screens.PackagesScreen
import com.example.debianandroid.ui.screens.SettingsScreen
import com.example.debianandroid.ui.screens.TerminalScreen
import com.example.debianandroid.viewmodel.DebianViewModel

enum class NavDestination(val label: String) {
    DASHBOARD("Dashboard"),
    TERMINAL("Terminal"),
    DESKTOP("Desktop"),
    PACKAGES("Packages"),
    SETTINGS("Settings")
}

class MainActivity : ComponentActivity() {

    private val viewModel: DebianViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DebianAndroidTheme {
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppScreen(viewModel: DebianViewModel) {
    var currentTab by remember { mutableStateOf(NavDestination.DASHBOARD) }

    val status by viewModel.containerStatus.collectAsStateWithLifecycle()
    val metrics by viewModel.metrics.collectAsStateWithLifecycle()
    val terminalLines by viewModel.terminalLines.collectAsStateWithLifecycle()
    val packages by viewModel.packages.collectAsStateWithLifecycle()
    val desktopWindows by viewModel.desktopWindows.collectAsStateWithLifecycle()
    val activeWindowId by viewModel.activeWindowId.collectAsStateWithLifecycle()
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val processes by viewModel.processes.collectAsStateWithLifecycle()
    val editorContent by viewModel.editorContent.collectAsStateWithLifecycle()

    BackHandler(enabled = currentTab != NavDestination.DASHBOARD) {
        currentTab = NavDestination.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(DarkSurface),
        topBar = {
            TopNavBar(
                status = status,
                distroName = settings.distro.codeName,
                archName = settings.architecture.tag,
                onStartClick = { viewModel.startContainer() },
                onStopClick = { viewModel.stopContainer() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurfaceVariant,
                contentColor = TextPrimary
            ) {
                NavigationBarItem(
                    selected = currentTab == NavDestination.DASHBOARD,
                    onClick = { currentTab = NavDestination.DASHBOARD },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") },
                    modifier = Modifier.testTag("nav_tab_dashboard"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DebianRed,
                        selectedTextColor = DebianRed,
                        indicatorColor = DarkSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = currentTab == NavDestination.TERMINAL,
                    onClick = { currentTab = NavDestination.TERMINAL },
                    icon = { Icon(Icons.Default.Terminal, contentDescription = "Terminal") },
                    label = { Text("Terminal") },
                    modifier = Modifier.testTag("nav_tab_terminal"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentCyan,
                        selectedTextColor = AccentCyan,
                        indicatorColor = DarkSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = currentTab == NavDestination.DESKTOP,
                    onClick = { currentTab = NavDestination.DESKTOP },
                    icon = { Icon(Icons.Default.Computer, contentDescription = "Desktop") },
                    label = { Text("Desktop") },
                    modifier = Modifier.testTag("nav_tab_desktop"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DebianRed,
                        selectedTextColor = DebianRed,
                        indicatorColor = DarkSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = currentTab == NavDestination.PACKAGES,
                    onClick = { currentTab = NavDestination.PACKAGES },
                    icon = { Icon(Icons.Default.Widgets, contentDescription = "Packages") },
                    label = { Text("Packages") },
                    modifier = Modifier.testTag("nav_tab_packages"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DebianRed,
                        selectedTextColor = DebianRed,
                        indicatorColor = DarkSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
                NavigationBarItem(
                    selected = currentTab == NavDestination.SETTINGS,
                    onClick = { currentTab = NavDestination.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Settings") },
                    modifier = Modifier.testTag("nav_tab_settings"),
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = AccentCyan,
                        selectedTextColor = AccentCyan,
                        indicatorColor = DarkSurfaceElevated,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(DarkSurface)
        ) {
            when (currentTab) {
                NavDestination.DASHBOARD -> {
                    DashboardScreen(
                        status = status,
                        metrics = metrics,
                        settings = settings,
                        onStartClick = { viewModel.startContainer() },
                        onStopClick = { viewModel.stopContainer() },
                        onNavigateToTerminal = { currentTab = NavDestination.TERMINAL },
                        onNavigateToDesktop = { currentTab = NavDestination.DESKTOP },
                        onNavigateToPackages = { currentTab = NavDestination.PACKAGES },
                        onNavigateToSettings = { currentTab = NavDestination.SETTINGS }
                    )
                }
                NavDestination.TERMINAL -> {
                    TerminalScreen(
                        lines = terminalLines,
                        onExecuteCommand = { cmd -> viewModel.executeCommand(cmd) }
                    )
                }
                NavDestination.DESKTOP -> {
                    DesktopScreen(
                        status = status,
                        windows = desktopWindows,
                        activeWindowId = activeWindowId,
                        processes = processes,
                        editorContent = editorContent,
                        onEditorContentChange = { viewModel.updateEditorContent(it) },
                        onOpenWindow = { type, title -> viewModel.openWindow(type, title) },
                        onCloseWindow = { id -> viewModel.closeWindow(id) },
                        onStartContainer = { viewModel.startContainer() }
                    )
                }
                NavDestination.PACKAGES -> {
                    PackagesScreen(
                        packages = packages,
                        onInstallPackage = { id -> viewModel.installPackage(id) },
                        onUninstallPackage = { id -> viewModel.uninstallPackage(id) },
                        onRefreshRepositories = { viewModel.executeCommand("apt update") }
                    )
                }
                NavDestination.SETTINGS -> {
                    SettingsScreen(
                        settings = settings,
                        onSettingsChanged = { viewModel.updateSettings(it) }
                    )
                }
            }
        }
    }
}
