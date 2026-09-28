package com.example.debianandroid.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.WinlatorBlue
import com.example.debianandroid.theme.WinlatorCardElevated
import com.example.debianandroid.theme.WinlatorSurface

@Composable
fun TopNavBar(
    status: ContainerStatus,
    currentSection: String = "Containers",
    distroName: String,
    archName: String,
    builtInMouseActive: Boolean = true,
    onMenuClick: () -> Unit,
    onDesktopClick: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = WinlatorSurface,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Hamburger Menu Button + Winlator Brand + Current Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.size(38.dp).testTag("drawer_menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = TextPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(WinlatorBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "Winlator",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Winlator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = " • $currentSection",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentCyan
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusIndicatorPill(status = status)
                    }
                    Text(
                        text = "$distroName • $archName • Vulkan (Turnip)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Right: Touch Mouse indicator, Desktop Jump button & Power Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Built-in Touch Mouse Status Chip
                if (builtInMouseActive) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(WinlatorCardElevated)
                            .border(0.5.dp, BorderSubtle, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Touch Mouse",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGreen
                        )
                    }
                }

                // Quick Desktop Jump Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WinlatorBlue.copy(alpha = 0.2f))
                        .border(1.dp, WinlatorBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable { onDesktopClick() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                        .testTag("quick_desktop_button"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "Desktop",
                        tint = WinlatorBlue,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Desktop",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WinlatorBlue
                    )
                }

                // Power Action Button
                if (status == ContainerStatus.RUNNING) {
                    ElevatedButton(
                        onClick = onStopClick,
                        modifier = Modifier.testTag("stop_container_button"),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = WinlatorCardElevated,
                            contentColor = Color.Red
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Stop",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    ElevatedButton(
                        onClick = onStartClick,
                        enabled = status == ContainerStatus.STOPPED,
                        modifier = Modifier.testTag("start_container_button"),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = WinlatorBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (status == ContainerStatus.STARTING) "Booting..." else "Run",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicatorPill(status: ContainerStatus) {
    val pillBg = when (status) {
        ContainerStatus.RUNNING -> AccentGreen.copy(alpha = 0.15f)
        ContainerStatus.STARTING -> AccentCyan.copy(alpha = 0.15f)
        ContainerStatus.STOPPING -> Color.Red.copy(alpha = 0.15f)
        ContainerStatus.STOPPED -> WinlatorCardElevated
    }

    val dotColor = when (status) {
        ContainerStatus.RUNNING -> AccentGreen
        ContainerStatus.STARTING -> AccentCyan
        ContainerStatus.STOPPING -> Color.Red
        ContainerStatus.STOPPED -> TextMuted
    }

    val label = when (status) {
        ContainerStatus.RUNNING -> "Active"
        ContainerStatus.STARTING -> "Booting"
        ContainerStatus.STOPPING -> "Stopping"
        ContainerStatus.STOPPED -> "Stopped"
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(pillBg)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = dotColor
        )
    }
}
