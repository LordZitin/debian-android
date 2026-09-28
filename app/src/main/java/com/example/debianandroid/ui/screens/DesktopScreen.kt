package com.example.debianandroid.ui.screens

import android.view.HapticFeedbackConstants
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.model.DesktopWindow
import com.example.debianandroid.model.ProcessInfo
import com.example.debianandroid.model.WindowType
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.AccentYellow
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.DarkSurface
import com.example.debianandroid.theme.DarkSurfaceElevated
import com.example.debianandroid.theme.DarkSurfaceVariant
import com.example.debianandroid.theme.DebianRed
import com.example.debianandroid.theme.TerminalBlack
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.TextSecondary
import com.example.debianandroid.theme.WinlatorBlue
import com.example.debianandroid.theme.WinlatorCard
import com.example.debianandroid.theme.WinlatorCardElevated
import com.example.debianandroid.theme.WinlatorDarkBg
import com.example.debianandroid.theme.WinlatorSurface
import kotlinx.coroutines.delay
import kotlin.math.hypot

data class DrawPoint(val path: Path, val color: Color, val strokeWidth: Float)

enum class ClickEffectType { NONE, LEFT, DOUBLE, RIGHT }

@Composable
fun DesktopScreen(
    status: ContainerStatus,
    windows: List<DesktopWindow>,
    activeWindowId: String?,
    processes: List<ProcessInfo>,
    editorContent: String,
    lastFeedback: String?,
    onEditorContentChange: (String) -> Unit,
    onOpenWindow: (WindowType, String) -> Unit,
    onCloseWindow: (String) -> Unit,
    onStartContainer: () -> Unit,
    onKeybinding: (String) -> Unit,
    onMouseClick: (String) -> Unit,
    onOpenDrawer: () -> Unit = {},
    onExitToContainers: () -> Unit = {}
) {
    val view = LocalView.current
    var showAppMenu by remember { mutableStateOf(false) }
    var showHudGuides by remember { mutableStateOf(true) }
    var showContextMenu by remember { mutableStateOf(false) }
    var virtualKeyboardOpen by remember { mutableStateOf(false) }
    var keyboardInput by remember { mutableStateOf("") }

    // Mouse pointer coordinate
    var mousePos by remember { mutableStateOf(Offset(450f, 250f)) }
    var clickEffect by remember { mutableStateOf(ClickEffectType.NONE) }

    // Reset click effect after brief duration
    LaunchedEffect(clickEffect) {
        if (clickEffect != ClickEffectType.NONE) {
            delay(350)
            clickEffect = ClickEffectType.NONE
        }
    }

    // Joystick virtual positions on left half
    var joystickActive by remember { mutableStateOf(false) }
    var joystickCenter by remember { mutableStateOf(Offset(200f, 280f)) }
    var joystickThumb by remember { mutableStateOf(Offset(200f, 280f)) }
    var activeDirection by remember { mutableStateOf<String?>(null) }

    // Intercept Android Back button for ESC keybinding as requested
    BackHandler(enabled = true) {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        onKeybinding("ESC")
    }

    // Drawing paths for GIMP
    val drawingPaths = remember { mutableStateListOf<DrawPoint>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var selectedColor by remember { mutableStateOf(DebianRed) }
    var brushStrokeWidth by remember { mutableFloatStateOf(8f) }

    if (status != ContainerStatus.RUNNING) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WinlatorDarkBg)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("desktop_offline_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = WinlatorCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(WinlatorBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = "Desktop",
                            tint = WinlatorBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Winlator Container Desktop Offline",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Boot Container 1 to launch the X11 & Vulkan desktop with the built-in mouse enabled on the right screen half.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onStartContainer,
                        colors = ButtonDefaults.buttonColors(containerColor = WinlatorBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("launch_desktop_button")
                    ) {
                        Icon(Icons.Default.PlayArrow, null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Boot Container & Open Desktop", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
    ) {
        // Desktop Top Panel (Winlator XFCE Style Panel)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = WinlatorSurface,
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Drawer Hamburger button + Whisker Menu + Taskbar windows
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Hamburger button to open Winlator Drawer
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.size(32.dp).testTag("desktop_drawer_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Winlator Menu",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Applications Start Menu
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(WinlatorBlue)
                                .clickable { showAppMenu = true }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                .testTag("xfce_menu_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Applications",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showAppMenu,
                            onDismissRequest = { showAppMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("GIMP (Image Editor)") },
                                leadingIcon = { Icon(Icons.Default.Brush, null, tint = DebianRed) },
                                onClick = {
                                    showAppMenu = false
                                    onOpenWindow(WindowType.GIMP, "GIMP 2.8 - Image Manipulation")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("vkcube (Vulkan 3D Gear)") },
                                leadingIcon = { Icon(Icons.Default.ViewInAr, null, tint = AccentYellow) },
                                onClick = {
                                    showAppMenu = false
                                    onOpenWindow(WindowType.VULKAN_GEARS, "Vulkan Hardware Gears (Turnip)")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Leafpad (Text Editor)") },
                                leadingIcon = { Icon(Icons.Default.TextFields, null, tint = AccentCyan) },
                                onClick = {
                                    showAppMenu = false
                                    onOpenWindow(WindowType.EDITOR, "Leafpad - text editor")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Thunar (File Manager)") },
                                leadingIcon = { Icon(Icons.Default.Folder, null, tint = AccentGreen) },
                                onClick = {
                                    showAppMenu = false
                                    onOpenWindow(WindowType.FILES, "Thunar - /home/debian")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Task Manager (htop)") },
                                leadingIcon = { Icon(Icons.Default.Memory, null, tint = DebianRed) },
                                onClick = {
                                    showAppMenu = false
                                    onOpenWindow(WindowType.MONITOR, "Task Manager - Processes")
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Open Windows in Taskbar
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        windows.forEach { win ->
                            val isActive = win.id == activeWindowId
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isActive) WinlatorBlue else WinlatorCardElevated)
                                    .clickable { onOpenWindow(win.type, win.title) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = win.title.take(12) + "...",
                                    fontSize = 11.sp,
                                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Right: Controls HUD indicator, Virtual Keyboard toggle & Exit Desktop
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Built-in Touch Mouse Status Chip
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WinlatorCardElevated)
                            .border(0.5.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Touch Mouse Active",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGreen
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Controls HUD toggle
                    AssistChip(
                        onClick = { showHudGuides = !showHudGuides },
                        label = {
                            Text(
                                text = if (showHudGuides) "HUD: ON" else "HUD: OFF",
                                fontSize = 10.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Navigation,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp),
                                tint = AccentCyan
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = WinlatorCardElevated),
                        modifier = Modifier.height(26.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    // Virtual Keyboard toggle
                    IconButton(
                        onClick = { virtualKeyboardOpen = !virtualKeyboardOpen },
                        modifier = Modifier.size(28.dp).testTag("toggle_keyboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Keyboard,
                            contentDescription = "Keyboard",
                            tint = if (virtualKeyboardOpen) AccentYellow else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Exit Desktop button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WinlatorCardElevated)
                            .border(0.5.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .clickable { onExitToContainers() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("exit_to_containers_btn")
                    ) {
                        Text(
                            text = "Containers",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WinlatorBlue
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = ":0 X11 + Vulkan",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AccentGreen
                    )
                }
            }
        }

        // Live Input Feedback Pill
        AnimatedVisibility(
            visible = lastFeedback != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(WinlatorCardElevated.copy(alpha = 0.95f))
                    .padding(vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = lastFeedback ?: "",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AccentYellow,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Desktop Interactive Canvas with Split Touch Zones
        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0C1017))
        ) {
            val halfWidthPx = with(LocalDensity.current) { (maxWidth / 2).toPx() }
            val fullHeightPx = with(LocalDensity.current) { maxHeight.toPx() }
            val fullWidthPx = with(LocalDensity.current) { maxWidth.toPx() }

            // Desktop Shortcuts Grid
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                DesktopIcon(
                    title = "GIMP 2.8",
                    icon = Icons.Default.Brush,
                    color = DebianRed,
                    onClick = { onOpenWindow(WindowType.GIMP, "GIMP 2.8 - Image Manipulation") }
                )
                DesktopIcon(
                    title = "Vulkan Cube",
                    icon = Icons.Default.ViewInAr,
                    color = AccentYellow,
                    onClick = { onOpenWindow(WindowType.VULKAN_GEARS, "Vulkan Hardware Gears (Turnip)") }
                )
                DesktopIcon(
                    title = "File Manager",
                    icon = Icons.Default.Folder,
                    color = AccentCyan,
                    onClick = { onOpenWindow(WindowType.FILES, "Thunar - /home/debian") }
                )
                DesktopIcon(
                    title = "Text Editor",
                    icon = Icons.Default.TextFields,
                    color = AccentGreen,
                    onClick = { onOpenWindow(WindowType.EDITOR, "Leafpad - text editor") }
                )
            }

            // Desktop Wallpaper Watermark
            Box(modifier = Modifier.align(Alignment.Center)) {
                Text(
                    text = "winlator",
                    color = Color.White.copy(alpha = 0.035f),
                    fontSize = 84.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.SansSerif
                )
            }

            // Render Active Windows
            val currentActiveWin = windows.find { it.id == activeWindowId } ?: windows.firstOrNull()
            if (currentActiveWin != null && !currentActiveWin.isMinimized) {
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("active_window_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WinlatorCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Window Title Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(WinlatorSurface)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(AccentGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentActiveWin.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                            }
                            IconButton(
                                onClick = { onCloseWindow(currentActiveWin.id) },
                                modifier = Modifier.size(24.dp).testTag("close_window_button")
                            ) {
                                Icon(Icons.Default.Close, "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }

                        // Window Body
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ) {
                            when (currentActiveWin.type) {
                                WindowType.GIMP -> {
                                    GimpWindowContent(
                                        paths = drawingPaths,
                                        currentPath = currentPath,
                                        selectedColor = selectedColor,
                                        brushStrokeWidth = brushStrokeWidth,
                                        onColorChange = { col: Color -> selectedColor = col },
                                        onWidthChange = { w: Float -> brushStrokeWidth = w },
                                        onPathStart = { offset: Offset ->
                                            val p = Path().apply { moveTo(offset.x, offset.y) }
                                            currentPath = p
                                        },
                                        onPathMove = { offset: Offset ->
                                            currentPath?.lineTo(offset.x, offset.y)
                                        },
                                        onPathEnd = {
                                            currentPath?.let {
                                                drawingPaths.add(DrawPoint(it, selectedColor, brushStrokeWidth))
                                            }
                                            currentPath = null
                                        },
                                        onClear = { drawingPaths.clear() }
                                    )
                                }
                                WindowType.VULKAN_GEARS -> {
                                    VulkanGearsWindowContent()
                                }
                                WindowType.EDITOR -> {
                                    EditorWindowContent(
                                        content = editorContent,
                                        onContentChange = onEditorContentChange
                                    )
                                }
                                WindowType.FILES -> {
                                    FileManagerWindowContent()
                                }
                                WindowType.MONITOR -> {
                                    TaskManagerWindowContent(processes = processes)
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text("Debian Application Window", color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // TOUCH CONTROLLER OVERLAYS (SPLIT SCREEN AS REQUESTED BY USER)
            // Left Half: Invisible Joystick for Up/Down/Left/Right + Tap for ENTER
            // Right Half: Built-in Trackpad for Mouse Movement + Tap: L-Click, DblTap: Open, Hold: R-Click
            // =========================================================================

            Row(modifier = Modifier.fillMaxSize()) {
                // LEFT HALF: Joystick + Single Tap Enter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    onKeybinding("ENTER")
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragStart = { offset ->
                                    joystickActive = true
                                    joystickCenter = offset
                                    joystickThumb = offset
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val newThumb = Offset(
                                        joystickThumb.x + dragAmount.x,
                                        joystickThumb.y + dragAmount.y
                                    )
                                    val dx = newThumb.x - joystickCenter.x
                                    val dy = newThumb.y - joystickCenter.y
                                    val dist = hypot(dx, dy)
                                    val maxRadius = 75f
                                    joystickThumb = if (dist > maxRadius) {
                                        Offset(
                                            joystickCenter.x + (dx / dist) * maxRadius,
                                            joystickCenter.y + (dy / dist) * maxRadius
                                        )
                                    } else {
                                        newThumb
                                    }

                                    // Determine direction
                                    val dirThreshold = 22f
                                    if (dist > dirThreshold) {
                                        val newDir = when {
                                            kotlin.math.abs(dx) > kotlin.math.abs(dy) -> if (dx > 0) "RIGHT" else "LEFT"
                                            else -> if (dy > 0) "DOWN" else "UP"
                                        }
                                        if (newDir != activeDirection) {
                                            activeDirection = newDir
                                            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                            onKeybinding(newDir)
                                        }
                                    }
                                },
                                onDragEnd = {
                                    joystickActive = false
                                    activeDirection = null
                                },
                                onDragCancel = {
                                    joystickActive = false
                                    activeDirection = null
                                }
                            )
                        }
                )

                // RIGHT HALF: Mouse Movement + Single Tap L-Click + Double Tap + Hold R-Click
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                                    clickEffect = ClickEffectType.LEFT
                                    onMouseClick("LEFT")
                                },
                                onDoubleTap = {
                                    view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
                                    clickEffect = ClickEffectType.DOUBLE
                                    onMouseClick("DOUBLE")
                                },
                                onLongPress = {
                                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                    clickEffect = ClickEffectType.RIGHT
                                    showContextMenu = true
                                    onMouseClick("RIGHT")
                                }
                            )
                        }
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                mousePos = Offset(
                                    (mousePos.x + dragAmount.x * 1.25f).coerceIn(0f, fullWidthPx),
                                    (mousePos.y + dragAmount.y * 1.25f).coerceIn(0f, fullHeightPx)
                                )
                            }
                        }
                )
            }

            // Virtual Joystick Indicator on Left Half
            if (joystickActive) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Outer base ring
                    drawCircle(
                        color = AccentCyan.copy(alpha = 0.25f),
                        radius = 75f,
                        center = joystickCenter,
                        style = Stroke(width = 3.dp.toPx())
                    )
                    // Inner thumb stick
                    drawCircle(
                        color = WinlatorBlue.copy(alpha = 0.85f),
                        radius = 26f,
                        center = joystickThumb
                    )
                }
            }

            // Click ripple animation at mouse cursor position
            if (clickEffect != ClickEffectType.NONE) {
                val rippleColor = when (clickEffect) {
                    ClickEffectType.LEFT -> AccentGreen
                    ClickEffectType.DOUBLE -> AccentCyan
                    ClickEffectType.RIGHT -> AccentYellow
                    ClickEffectType.NONE -> Color.Transparent
                }
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawCircle(
                        color = rippleColor.copy(alpha = 0.45f),
                        radius = 22.dp.toPx(),
                        center = mousePos,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }
            }

            // OS Style Mouse Pointer Cursor on Screen
            Box(
                modifier = Modifier
                    .offset { IntOffset(mousePos.x.toInt(), mousePos.y.toInt()) }
                    .size(20.dp)
            ) {
                // Classic OS pointer arrow
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val arrowPath = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(16.dp.toPx(), 12.dp.toPx())
                        lineTo(9.dp.toPx(), 12.dp.toPx())
                        lineTo(13.dp.toPx(), 19.dp.toPx())
                        lineTo(10.dp.toPx(), 20.dp.toPx())
                        lineTo(6.dp.toPx(), 13.dp.toPx())
                        lineTo(0f, 17.dp.toPx())
                        close()
                    }
                    drawPath(arrowPath, Color(0xFF1E293B))
                    drawPath(
                        arrowPath,
                        Color.White,
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            }

            // Desktop Right-Click Context Menu
            if (showContextMenu) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                (mousePos.x.toInt() - 20).coerceIn(10, (fullWidthPx - 200).toInt()),
                                (mousePos.y.toInt() + 10).coerceIn(10, (fullHeightPx - 180).toInt())
                            )
                        }
                        .shadow(12.dp, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                        .background(WinlatorSurface)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                        .padding(6.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        ContextMenuItem(title = "Open Terminal", icon = Icons.Default.Navigation) {
                            showContextMenu = false
                            onOpenWindow(WindowType.TERMINAL, "Bash Terminal")
                        }
                        ContextMenuItem(title = "New Document (Leafpad)", icon = Icons.Default.TextFields) {
                            showContextMenu = false
                            onOpenWindow(WindowType.EDITOR, "Leafpad - text editor")
                        }
                        ContextMenuItem(title = "Task Manager (htop)", icon = Icons.Default.Memory) {
                            showContextMenu = false
                            onOpenWindow(WindowType.MONITOR, "Task Manager")
                        }
                        ContextMenuItem(title = "Vulkan 3D Gears", icon = Icons.Default.ViewInAr) {
                            showContextMenu = false
                            onOpenWindow(WindowType.VULKAN_GEARS, "Vulkan Hardware Gears")
                        }
                        ContextMenuItem(title = "Close Context Menu", icon = Icons.Default.Close) {
                            showContextMenu = false
                        }
                    }
                }
            }

            // Transparent Guidance HUD Banner
            if (showHudGuides) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = WinlatorSurface.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, BorderSubtle)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "🕹️ LEFT: D-Pad Joystick (Up/Down/L/R) • Tap: ⏎ Enter",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentCyan
                        )
                        Text(
                            text = "🖱️ RIGHT: Trackpad • Tap: L-Click • 2x: Dbl-Click • Hold: R-Click",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGreen
                        )
                        Text(
                            text = "⎋ BACK: Esc",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DebianRed
                        )
                    }
                }
            }

            // Virtual Keyboard Drawer Overlay
            if (virtualKeyboardOpen) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 36.dp, start = 40.dp, end = 40.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = WinlatorCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, WinlatorBlue)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = keyboardInput,
                            onValueChange = { keyboardInput = it },
                            placeholder = { Text("Type text to send into container...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = WinlatorSurface,
                                unfocusedContainerColor = WinlatorSurface,
                                focusedBorderColor = WinlatorBlue,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (keyboardInput.isNotEmpty()) {
                                    onKeybinding(keyboardInput)
                                    keyboardInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WinlatorBlue)
                        ) {
                            Icon(Icons.Default.Send, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Send")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ContextMenuItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(14.dp), tint = TextSecondary)
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 11.sp, color = TextPrimary)
    }
}

@Composable
fun VulkanGearsWindowContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBlack)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ViewInAr,
            contentDescription = "Vulkan Gears",
            tint = AccentYellow,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Vulkan Turnip 3D Graphics Engine",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Rendering via bionic-vulkan-wrapper (pipetto-crypto / leegao) & Winlator Vortek",
            style = MaterialTheme.typography.bodySmall,
            color = AccentCyan
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("FPS: 60.0", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = AccentGreen)
            Text("API: Vulkan 1.3", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextMuted)
            Text("Format: VK_FORMAT_B8G8R8A8_UNORM", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = TextMuted)
        }
    }
}

@Composable
fun DesktopIcon(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = Color.White
        )
    }
}

@Composable
fun GimpWindowContent(
    paths: List<DrawPoint>,
    currentPath: Path?,
    selectedColor: Color,
    brushStrokeWidth: Float,
    onColorChange: (Color) -> Unit,
    onWidthChange: (Float) -> Unit,
    onPathStart: (Offset) -> Unit,
    onPathMove: (Offset) -> Unit,
    onPathEnd: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
    ) {
        // GIMP Tool Palette
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = WinlatorSurface,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val palette = listOf(DebianRed, AccentGreen, AccentCyan, AccentYellow, Color.White, Color.Black)
                    palette.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (selectedColor == color) 2.dp else 1.dp,
                                    color = if (selectedColor == color) Color.White else BorderSubtle,
                                    shape = CircleShape
                                )
                                .clickable { onColorChange(color) }
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text("Stroke: ${brushStrokeWidth.toInt()}px", fontSize = 11.sp, color = TextMuted)
                    listOf(4f, 8f, 16f).forEach { size ->
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (brushStrokeWidth == size) WinlatorBlue else WinlatorCardElevated)
                                .clickable { onWidthChange(size) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "${size.toInt()}", fontSize = 9.sp, color = Color.White)
                        }
                    }
                }

                Button(
                    onClick = onClear,
                    colors = ButtonDefaults.buttonColors(containerColor = WinlatorCardElevated),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(26.dp)
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Canvas", fontSize = 10.sp)
                }
            }
        }

        // GIMP Canvas
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF262A36))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> onPathStart(offset) },
                        onDrag = { change, _ ->
                            change.consume()
                            onPathMove(change.position)
                        },
                        onDragEnd = { onPathEnd() },
                        onDragCancel = { onPathEnd() }
                    )
                }
        ) {
            paths.forEach { drawPoint ->
                drawPath(
                    path = drawPoint.path,
                    color = drawPoint.color,
                    style = Stroke(
                        width = drawPoint.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            currentPath?.let { path ->
                drawPath(
                    path = path,
                    color = selectedColor,
                    style = Stroke(
                        width = brushStrokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Composable
fun EditorWindowContent(
    content: String,
    onContentChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
            .padding(8.dp)
    ) {
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier.fillMaxSize(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                color = AccentCyan
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF0F1118),
                unfocusedContainerColor = Color(0xFF0F1118),
                focusedBorderColor = WinlatorBlue,
                unfocusedBorderColor = BorderSubtle
            )
        )
    }
}

@Composable
fun FileManagerWindowContent() {
    val items = listOf(
        Pair("Desktop", Icons.Default.Folder),
        Pair("Downloads", Icons.Default.Folder),
        Pair("Documents", Icons.Default.Folder),
        Pair(".wine", Icons.Default.Folder),
        Pair("startup.sh", Icons.Default.TextFields),
        Pair("gimp-sample.png", Icons.Default.Brush)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "Location: /home/debian",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                color = AccentGreen
            )
        }
        items(items) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WinlatorCard)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(item.second, null, tint = AccentCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(item.first, fontSize = 12.sp, color = TextPrimary)
            }
        }
    }
}

@Composable
fun TaskManagerWindowContent(processes: List<ProcessInfo>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PID", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("USER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("CPU%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("MEM%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("COMMAND", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            }
        }
        items(processes) { p ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(WinlatorCard)
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${p.pid}", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = AccentGreen)
                Text(p.user, fontSize = 11.sp, color = TextPrimary)
                Text("${p.cpuPercent}%", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = AccentYellow)
                Text("${p.memPercent}%", fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = AccentCyan)
                Text(p.command.take(18), fontSize = 11.sp, fontFamily = FontFamily.Monospace, color = TextPrimary)
            }
        }
    }
}
