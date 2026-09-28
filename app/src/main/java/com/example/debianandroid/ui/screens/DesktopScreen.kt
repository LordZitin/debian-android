package com.example.debianandroid.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.OpenWith
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

data class DrawPoint(val path: Path, val color: Color, val strokeWidth: Float)

@Composable
fun DesktopScreen(
    status: ContainerStatus,
    windows: List<DesktopWindow>,
    activeWindowId: String?,
    processes: List<ProcessInfo>,
    editorContent: String,
    onEditorContentChange: (String) -> Unit,
    onOpenWindow: (WindowType, String) -> Unit,
    onCloseWindow: (String) -> Unit,
    onStartContainer: () -> Unit
) {
    var showAppMenu by remember { mutableStateOf(false) }
    var useTrackpadMode by remember { mutableStateOf(false) }
    var mousePos by remember { mutableStateOf(Offset(200f, 200f)) }

    // Canvas drawing paths for GIMP
    val drawingPaths = remember { mutableStateListOf<DrawPoint>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }
    var selectedColor by remember { mutableStateOf(DebianRed) }
    var brushStrokeWidth by remember { mutableFloatStateOf(8f) }

    if (status != ContainerStatus.RUNNING) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkSurface)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("desktop_offline_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
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
                            .background(DebianRed.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Widgets,
                            contentDescription = "Desktop",
                            tint = DebianRed,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "X11 / XSDL Server Offline",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "The Debian desktop graphical server (:0 display) is not active. Launch the Debian container to start the XFCE/LXDE desktop environment.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = onStartContainer,
                        colors = ButtonDefaults.buttonColors(containerColor = DebianRed),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("launch_desktop_button")
                    ) {
                        Text("Start Debian & Open Desktop", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
    ) {
        // Desktop Top Panel (XFCE Style Panel)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF14161F),
            tonalElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Whisker Menu Button
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurfaceElevated)
                                .clickable { showAppMenu = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("xfce_menu_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Applications",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentCyan
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
                                    onOpenWindow(WindowType.GIMP, "GIMP 2.8 - GNU Image Manipulation")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Leafpad (Text Editor)") },
                                leadingIcon = { Icon(Icons.Default.TextFields, null, tint = AccentYellow) },
                                onClick = {
                                    showAppMenu = false
                                    onOpenWindow(WindowType.EDITOR, "Leafpad - text editor")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Thunar (File Manager)") },
                                leadingIcon = { Icon(Icons.Default.Folder, null, tint = AccentCyan) },
                                onClick = {
                                    showAppMenu = false
                                    onOpenWindow(WindowType.FILES, "Thunar - /home/debian")
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Task Manager (htop)") },
                                leadingIcon = { Icon(Icons.Default.Memory, null, tint = AccentGreen) },
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
                                    .background(if (isActive) DebianRed else DarkSurfaceElevated)
                                    .clickable { onOpenWindow(win.type, win.title) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = win.title.take(12) + "...",
                                    fontSize = 11.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }

                // Tray Controls: Mouse mode toggle & clock
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssistChip(
                        onClick = { useTrackpadMode = !useTrackpadMode },
                        label = {
                            Text(
                                text = if (useTrackpadMode) "Mouse Mode" else "Direct Touch",
                                fontSize = 10.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (useTrackpadMode) Icons.Default.Mouse else Icons.Default.TouchApp,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = DarkSurfaceElevated),
                        modifier = Modifier.height(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = ":0 X11",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = AccentGreen
                    )
                }
            }
        }

        // Desktop Workspace Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF10121A))
                .pointerInput(Unit) {
                    if (useTrackpadMode) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            mousePos = Offset(
                                (mousePos.x + dragAmount.x).coerceIn(0f, size.width.toFloat()),
                                (mousePos.y + dragAmount.y).coerceIn(0f, size.height.toFloat())
                            )
                        }
                    }
                }
        ) {
            // Desktop Shortcuts Grid
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DesktopIcon(
                    title = "GIMP 2.8",
                    icon = Icons.Default.Brush,
                    color = DebianRed,
                    onClick = { onOpenWindow(WindowType.GIMP, "GIMP 2.8 - GNU Image Manipulation") }
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
                    color = AccentYellow,
                    onClick = { onOpenWindow(WindowType.EDITOR, "Leafpad - text editor") }
                )
                DesktopIcon(
                    title = "Processes",
                    icon = Icons.Default.Memory,
                    color = AccentGreen,
                    onClick = { onOpenWindow(WindowType.MONITOR, "Task Manager - Processes") }
                )
            }

            // Desktop Wallpaper Watermark
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text(
                    text = "debian",
                    color = Color.White.copy(alpha = 0.04f),
                    fontSize = 72.sp,
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
                        .padding(10.dp)
                        .testTag("active_window_card"),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Window Title Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(DarkSurfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = currentActiveWin.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onCloseWindow(currentActiveWin.id) },
                                    modifier = Modifier.size(24.dp).testTag("close_window_button")
                                ) {
                                    Icon(Icons.Default.Close, "Close", tint = TextMuted, modifier = Modifier.size(16.dp))
                                }
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
                                        onColorChange = { selectedColor = it },
                                        onWidthChange = { brushStrokeWidth = it },
                                        onPathStart = { offset ->
                                            val p = Path().apply { moveTo(offset.x, offset.y) }
                                            currentPath = p
                                        },
                                        onPathMove = { offset ->
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
                                        Text("Debian Utility Active", color = TextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Virtual Mouse Pointer in Trackpad Mode
            if (useTrackpadMode) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .offset(x = mousePos.x.dp, y = mousePos.y.dp)
                        .clip(CircleShape)
                        .background(AccentCyan.copy(alpha = 0.8f))
                        .border(1.dp, Color.White, CircleShape)
                )
            }
        }
    }
}

@Composable
fun DesktopIcon(
    title: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(color.copy(alpha = 0.2f))
                .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            fontSize = 11.sp
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
    Column(modifier = Modifier.fillMaxSize()) {
        // GIMP Tool Palette & Palette Colors
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant)
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val colors = listOf(DebianRed, AccentCyan, AccentGreen, AccentYellow, Color.White, Color.Black)
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(c)
                            .border(if (selectedColor == c) 2.dp else 1.dp, if (selectedColor == c) Color.White else BorderSubtle, CircleShape)
                            .clickable { onColorChange(c) }
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Brush: ${brushStrokeWidth.toInt()}px",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(end = 6.dp)
                )
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(28.dp).testTag("clear_gimp_canvas")
                ) {
                    Icon(Icons.Default.Delete, "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                }
            }
        }

        // Real Interactive Drawing Canvas
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color.White)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset -> onPathStart(offset) },
                        onDrag = { change, _ ->
                            change.consume()
                            onPathMove(change.position)
                        },
                        onDragEnd = { onPathEnd() }
                    )
                }
        ) {
            paths.forEach { dp ->
                drawPath(
                    path = dp.path,
                    color = dp.color,
                    style = Stroke(
                        width = dp.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
            currentPath?.let { p ->
                drawPath(
                    path = p,
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
    Column(modifier = Modifier.fillMaxSize().background(TerminalBlack)) {
        OutlinedTextField(
            value = content,
            onValueChange = onContentChange,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .testTag("leafpad_editor_input"),
            textStyle = MaterialTheme.typography.bodySmall.copy(
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AccentCyan
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedContainerColor = TerminalBlack,
                unfocusedContainerColor = TerminalBlack
            )
        )
    }
}

@Composable
fun FileManagerWindowContent() {
    val folders = listOf(
        Pair("/bin", "Linux essential user command binaries"),
        Pair("/etc", "Debian host configuration and daemon files"),
        Pair("/home/debian", "User home workspace & scripts"),
        Pair("/root", "Root superuser home directory"),
        Pair("/usr", "Secondary hierarchy for shareable read-only data"),
        Pair("/var", "Variable files: apt caches, spool, logs"),
        Pair("/sdcard", "Mounted Android internal storage (/storage/emulated/0)")
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurfaceVariant)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(folders) { f ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Folder, null, tint = AccentCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(f.first, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                        Text(f.second, fontSize = 11.sp, color = TextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun TaskManagerWindowContent(processes: List<ProcessInfo>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurfaceVariant)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("PID / USER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                Text("CPU%  MEM%   COMMAND", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
            }
        }
        items(processes) { p ->
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                shape = RoundedCornerShape(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${p.pid} (${p.user})", fontSize = 12.sp, color = AccentYellow)
                    Text("${p.cpuPercent}%  ${p.memPercent}%  ${p.command}", fontSize = 11.sp, color = TextPrimary)
                }
            }
        }
    }
}
