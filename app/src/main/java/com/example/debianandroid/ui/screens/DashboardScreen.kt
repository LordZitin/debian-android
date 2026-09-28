package com.example.debianandroid.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.model.DebianSettings
import com.example.debianandroid.model.SystemMetrics
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.AccentYellow
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.DarkSurface
import com.example.debianandroid.theme.DarkSurfaceElevated
import com.example.debianandroid.theme.DarkSurfaceVariant
import com.example.debianandroid.theme.DebianRed
import com.example.debianandroid.theme.DebianRedLight
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.TextSecondary

@Composable
fun DashboardScreen(
    status: ContainerStatus,
    metrics: SystemMetrics,
    settings: DebianSettings,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    onNavigateToTerminal: () -> Unit,
    onNavigateToDesktop: () -> Unit,
    onNavigateToPackages: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(4.dp)) }

        // Hero Banner Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkSurfaceVariant
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Debian Linux Environment",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "PRoot virtualization • No root required",
                                style = MaterialTheme.typography.bodySmall,
                                color = DebianRedLight
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(DebianRed.copy(alpha = 0.2f))
                                .border(1.dp, DebianRed, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "@",
                                color = DebianRed,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Run desktop applications like GIMP, development toolchains (gcc, python, rust), and server utilities directly on your device without modifying Android system files.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Launch / Stop Button
                    if (status == ContainerStatus.RUNNING) {
                        Button(
                            onClick = onStopClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("banner_stop_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceElevated,
                                contentColor = DebianRed
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Halt")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop Debian Container", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onStartClick,
                            enabled = status == ContainerStatus.STOPPED,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("banner_start_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DebianRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Launch")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (status == ContainerStatus.STARTING) "Booting Container..." else "Launch Debian Environment",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Live Resource Metrics
        item {
            Text(
                text = "Live Container Metrics",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "CPU",
                    value = "${if (status == ContainerStatus.RUNNING) metrics.cpuUsagePercent else 0}%",
                    progress = if (status == ContainerStatus.RUNNING) metrics.cpuUsagePercent / 100f else 0f,
                    icon = Icons.Default.Speed,
                    color = AccentCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "RAM",
                    value = "${if (status == ContainerStatus.RUNNING) metrics.ramUsedMb else 0} MB",
                    progress = if (status == ContainerStatus.RUNNING) metrics.ramUsedMb.toFloat() / metrics.ramTotalMb else 0f,
                    icon = Icons.Default.Memory,
                    color = AccentGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Rootfs",
                    value = "${metrics.diskUsedGb} GB",
                    progress = metrics.diskUsedGb / metrics.diskTotalGb,
                    icon = Icons.Default.Storage,
                    color = AccentYellow,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Navigation Tiles
        item {
            Text(
                text = "Subsystems & Utilities",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UtilityTile(
                        title = "Bash Terminal",
                        subtitle = "Interactive shell with APT",
                        icon = Icons.Default.Terminal,
                        accentColor = AccentGreen,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_tile_terminal"),
                        onClick = onNavigateToTerminal
                    )
                    UtilityTile(
                        title = "X11 Desktop",
                        subtitle = "${settings.desktopEnv.displayName} GUI",
                        icon = Icons.Default.Computer,
                        accentColor = AccentCyan,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_tile_desktop"),
                        onClick = onNavigateToDesktop
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    UtilityTile(
                        title = "Package Manager",
                        subtitle = "APT Software Repository",
                        icon = Icons.Default.Widgets,
                        accentColor = DebianRed,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_tile_packages"),
                        onClick = onNavigateToPackages
                    )
                    UtilityTile(
                        title = "PRoot Settings",
                        subtitle = "SELinux & DNS config",
                        icon = Icons.Default.Settings,
                        accentColor = AccentYellow,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("nav_tile_settings"),
                        onClick = onNavigateToSettings
                    )
                }
            }
        }

        // Virtualization Subsystems Status
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "System Compatibility & Overlays",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    StatusItem(
                        icon = Icons.Default.Security,
                        title = "SELinux Bypass Layer",
                        detail = if (settings.enableSELinuxBypass) "libandroid-shmem-disableselinux.so active" else "Disabled"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusItem(
                        icon = Icons.Default.Dns,
                        title = "c-ares Asynchronous DNS",
                        detail = "Nameserver: ${settings.dnsServer} (no-root socket resolution)"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    StatusItem(
                        icon = Icons.Default.Folder,
                        title = "Android Storage Integration",
                        detail = if (settings.bindSdCard) "/storage/emulated/0 mounted to /sdcard" else "Isolated rootfs only"
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    progress: Float,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = color,
                trackColor = DarkSurfaceElevated
            )
        }
    }
}

@Composable
fun UtilityTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun StatusItem(
    icon: ImageVector,
    title: String,
    detail: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AccentGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TextPrimary
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
    }
}
