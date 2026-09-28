package com.example.debianandroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.AccentYellow
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.DebianAndroidTheme
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.TextSecondary
import com.example.debianandroid.theme.WinlatorBlue
import com.example.debianandroid.theme.WinlatorCard
import com.example.debianandroid.theme.WinlatorCardElevated
import com.example.debianandroid.theme.WinlatorDarkBg
import com.example.debianandroid.theme.WinlatorSurface
import com.example.debianandroid.ui.components.TopNavBar
import com.example.debianandroid.ui.screens.ContainersScreen
import com.example.debianandroid.ui.screens.DesktopScreen
import com.example.debianandroid.ui.screens.HardwareScreen
import com.example.debianandroid.ui.screens.InputControlsScreen
import com.example.debianandroid.ui.screens.PackagesScreen
import com.example.debianandroid.ui.screens.ServicesScreen
import com.example.debianandroid.ui.screens.SettingsScreen
import com.example.debianandroid.ui.screens.ShortcutsScreen
import com.example.debianandroid.ui.screens.TerminalScreen
import com.example.debianandroid.viewmodel.DebianViewModel
import kotlinx.coroutines.launch

enum class NavDestination(val label: String, val icon: ImageVector, val subtitle: String) {
    DESKTOP("Desktop", Icons.Default.Computer, "X11 & Vulkan Desktop with Touch Mouse"),
    CONTAINERS("Containers", Icons.Default.Widgets, "PRoot & Wine container management"),
    SHORTCUTS("Shortcuts", Icons.Default.Apps, "Application launcher shortcuts"),
    INPUT_CONTROLS("Input Controls", Icons.Default.Gamepad, "Built-in touch mouse & gesture profiles"),
    SERVICES("Services & Audio", Icons.Default.GraphicEq, "PulseAudio, Vulkan, SSH, Docker"),
    HARDWARE("Hardware & Ping", Icons.Default.Memory, "SoC telemetry & Termux ICMP ping"),
    TERMINAL("Terminal", Icons.Default.Terminal, "Debian PRoot rootless bash shell"),
    SETTINGS("Settings", Icons.Default.Settings, "Box64, Rootfs & Libraries Used")
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
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val status by viewModel.containerStatus.collectAsStateWithLifecycle()
    val containers by viewModel.containers.collectAsStateWithLifecycle()
    val shortcuts by viewModel.shortcuts.collectAsStateWithLifecycle()
    val terminalLines by viewModel.terminalLines.collectAsStateWithLifecycle()
    val desktopWindows by viewModel.desktopWindows.collectAsStateWithLifecycle()
    val activeWindowId by viewModel.activeWindowId.collectAsStateWithLifecycle()
    val processes by viewModel.processes.collectAsStateWithLifecycle()
    val editorContent by viewModel.editorContent.collectAsStateWithLifecycle()

    val pulseAudio by viewModel.pulseAudio.collectAsStateWithLifecycle()
    val vulkan by viewModel.vulkan.collectAsStateWithLifecycle()
    val sshServer by viewModel.sshServer.collectAsStateWithLifecycle()
    val docker by viewModel.docker.collectAsStateWithLifecycle()
    val hardwareInfo by viewModel.hardwareInfo.collectAsStateWithLifecycle()
    val networkInfo by viewModel.networkInfo.collectAsStateWithLifecycle()
    val pingSession by viewModel.pingSession.collectAsStateWithLifecycle()
    val lastInputFeedback by viewModel.lastInputFeedback.collectAsStateWithLifecycle()

    // Open at Boot Requirement:
    // If openAtBoot is enabled, start directly on Desktop with built-in mouse!
    var currentTab by remember {
        mutableStateOf(if (settings.openAtBoot) NavDestination.DESKTOP else NavDestination.CONTAINERS)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Ensure container is booting if on Desktop and open at boot is set
    LaunchedEffect(currentTab) {
        if (currentTab == NavDestination.DESKTOP && status == ContainerStatus.STOPPED) {
            viewModel.startContainer("container-1", openDesktopImmediately = true)
        }
    }

    // Android back button handling:
    // When drawer is open -> close drawer
    // When on Desktop screen -> sends ESC keybinding (handled inside DesktopScreen)
    // When on other screens -> return to Containers or Desktop
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }
    BackHandler(enabled = !drawerState.isOpen && currentTab != NavDestination.DESKTOP && currentTab != NavDestination.CONTAINERS) {
        currentTab = NavDestination.CONTAINERS
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = WinlatorSurface,
                drawerContentColor = TextPrimary,
                modifier = Modifier.width(320.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 16.dp)) {
                    // Drawer Header with Winlator Branding
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(WinlatorBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Computer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Winlator",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Debian PRoot Engine",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentCyan
                            )
                        }
                    }

                    // Status and Boot indicator row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(WinlatorCardElevated)
                            .border(0.5.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (status == ContainerStatus.RUNNING) AccentGreen else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (status == ContainerStatus.RUNNING) "Container 1 Active" else "Stopped",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (status == ContainerStatus.RUNNING) AccentGreen else TextMuted
                            )
                        }

                        if (settings.openAtBoot) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Mouse,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Touch Mouse",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AccentGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(10.dp))

                    // Drawer Navigation Items
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(NavDestination.values()) { dest ->
                            val isSelected = currentTab == dest
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        imageVector = dest.icon,
                                        contentDescription = dest.label,
                                        tint = if (isSelected) Color.White else TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Column {
                                        Text(
                                            text = dest.label,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = if (isSelected) Color.White else TextPrimary
                                        )
                                        Text(
                                            text = dest.subtitle,
                                            fontSize = 10.sp,
                                            color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextMuted,
                                            maxLines = 1
                                        )
                                    }
                                },
                                selected = isSelected,
                                onClick = {
                                    currentTab = dest
                                    scope.launch { drawerState.close() }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = WinlatorBlue,
                                    unselectedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.testTag("drawer_item_${dest.name.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    HorizontalDivider(color = BorderSubtle)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Drawer Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Winlator Cmod v7.1.0",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Debian 12 Bookworm",
                            fontSize = 10.sp,
                            color = AccentCyan
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize().background(WinlatorDarkBg),
            topBar = {
                // When on Desktop screen, DesktopScreen has its own integrated top panel
                if (currentTab != NavDestination.DESKTOP) {
                    TopNavBar(
                        status = status,
                        currentSection = currentTab.label,
                        distroName = settings.distro.codeName,
                        archName = settings.architecture.tag,
                        builtInMouseActive = settings.builtInMouseEnabled,
                        onMenuClick = { scope.launch { drawerState.open() } },
                        onDesktopClick = { currentTab = NavDestination.DESKTOP },
                        onStartClick = { viewModel.startContainer("container-1", openDesktopImmediately = false) },
                        onStopClick = { viewModel.stopContainer() }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(WinlatorDarkBg)
            ) {
                when (currentTab) {
                    NavDestination.CONTAINERS -> {
                        ContainersScreen(
                            containers = containers,
                            currentStatus = status,
                            onRunContainer = { id -> viewModel.startContainer(id, openDesktopImmediately = true) },
                            onStopContainer = { viewModel.stopContainer() },
                            onAddContainer = { name, distro, res, driver ->
                                viewModel.addContainer(name, distro, res, driver)
                            },
                            onDeleteContainer = { id -> viewModel.deleteContainer(id) },
                            onToggleOpenAtBoot = { id -> viewModel.toggleContainerOpenAtBoot(id) },
                            onNavigateToDesktop = { currentTab = NavDestination.DESKTOP }
                        )
                    }
                    NavDestination.SHORTCUTS -> {
                        ShortcutsScreen(
                            shortcuts = shortcuts,
                            onLaunchShortcut = { shortcut ->
                                viewModel.startContainer(shortcut.containerId, openDesktopImmediately = true)
                                viewModel.openWindow(shortcut.iconType, shortcut.title)
                                currentTab = NavDestination.DESKTOP
                            }
                        )
                    }
                    NavDestination.DESKTOP -> {
                        DesktopScreen(
                            status = status,
                            windows = desktopWindows,
                            activeWindowId = activeWindowId,
                            processes = processes,
                            editorContent = editorContent,
                            lastFeedback = lastInputFeedback,
                            onEditorContentChange = { viewModel.updateEditorContent(it) },
                            onOpenWindow = { type, title -> viewModel.openWindow(type, title) },
                            onCloseWindow = { id -> viewModel.closeWindow(id) },
                            onStartContainer = { viewModel.startContainer("container-1", openDesktopImmediately = true) },
                            onKeybinding = { key -> viewModel.sendKeybinding(key) },
                            onMouseClick = { clickType -> viewModel.sendMouseClick(clickType) },
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onExitToContainers = { currentTab = NavDestination.CONTAINERS }
                        )
                    }
                    NavDestination.INPUT_CONTROLS -> {
                        InputControlsScreen(
                            settings = settings,
                            onSettingsChanged = { viewModel.updateSettings(it) }
                        )
                    }
                    NavDestination.SERVICES -> {
                        ServicesScreen(
                            pulseAudio = pulseAudio,
                            vulkan = vulkan,
                            sshServer = sshServer,
                            docker = docker,
                            localIp = networkInfo?.localIp ?: "127.0.0.1",
                            onTogglePulseAudio = { viewModel.togglePulseAudio() },
                            onTestSound = { viewModel.playPulseAudioTestSound() },
                            onVolumeChange = { viewModel.setAudioVolume(it) },
                            onToggleMute = { viewModel.toggleAudioMute() },
                            onToggleVulkan = { viewModel.toggleVulkan(it) },
                            onToggleSsh = { viewModel.toggleSshServer() },
                            onToggleDocker = { viewModel.toggleDocker() },
                            onRunContainer = { img -> viewModel.runDockerContainer(img) }
                        )
                    }
                    NavDestination.HARDWARE -> {
                        HardwareScreen(
                            hardwareInfo = hardwareInfo,
                            networkInfo = networkInfo,
                            pingSession = pingSession,
                            onRunPing = { host -> viewModel.startPing(host) },
                            onRefresh = { viewModel.refreshHardwareAndNetwork() }
                        )
                    }
                    NavDestination.TERMINAL -> {
                        TerminalScreen(
                            lines = terminalLines,
                            onExecuteCommand = { cmd -> viewModel.executeCommand(cmd) }
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
}
