package com.example.debianandroid.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.DockerState
import com.example.debianandroid.model.PulseAudioState
import com.example.debianandroid.model.SshServerState
import com.example.debianandroid.model.VulkanState
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

@Composable
fun ServicesScreen(
    pulseAudio: PulseAudioState,
    vulkan: VulkanState,
    sshServer: SshServerState,
    docker: DockerState,
    localIp: String,
    onTogglePulseAudio: () -> Unit,
    onTestSound: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleVulkan: (Boolean) -> Unit,
    onToggleSsh: () -> Unit,
    onToggleDocker: () -> Unit,
    onRunContainer: (String) -> Unit
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Page Title
        item {
            Column {
                Text(
                    text = "System Services & Daemons",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "PulseAudio audio pipeline, Vulkan acceleration, OpenSSH server, and Docker",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // 1. PulseAudio Audio Support Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("pulseaudio_service_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.GraphicEq, null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PulseAudio Audio Server",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "TCP socket 127.0.0.1:${pulseAudio.port} • ${pulseAudio.sampleRate}Hz",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (pulseAudio.isRunning) AccentGreen else TextMuted
                                )
                            }
                        }
                        Switch(
                            checked = pulseAudio.isRunning,
                            onCheckedChange = { onTogglePulseAudio() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentCyan,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.testTag("toggle_pulseaudio_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PulseAudio bridges Debian sound applications to Android's AudioTrack via a local TCP socket sink, fixing previous audio dropouts and latency.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Volume & Sound Test Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier.size(36.dp).testTag("mute_audio_button")
                        ) {
                            Icon(
                                imageVector = if (pulseAudio.isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = "Mute",
                                tint = if (pulseAudio.isMuted) DebianRed else AccentCyan
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Slider(
                            value = if (pulseAudio.isMuted) 0f else pulseAudio.volume,
                            onValueChange = onVolumeChange,
                            valueRange = 0f..1f,
                            modifier = Modifier.weight(1f).testTag("volume_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = AccentCyan,
                                activeTrackColor = AccentCyan,
                                inactiveTrackColor = DarkSurfaceElevated
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = onTestSound,
                            enabled = pulseAudio.isRunning && !pulseAudio.isTestingSound,
                            colors = ButtonDefaults.buttonColors(containerColor = AccentCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(38.dp).testTag("test_sound_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (pulseAudio.isTestingSound) "Playing..." else "Test Sound",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Vulkan Acceleration Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("vulkan_service_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DebianRed.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ViewInAr, null, tint = DebianRed, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Vulkan Graphics Engine",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Vulkan v${vulkan.apiVersion} • ${vulkan.driver}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (vulkan.isEnabled) AccentGreen else TextMuted
                                )
                            }
                        }
                        Switch(
                            checked = vulkan.isEnabled,
                            onCheckedChange = { onToggleVulkan(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = DebianRed,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.testTag("toggle_vulkan_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Utilizes ${vulkan.wrapperName} (leegao / pipetto-crypto) and Winlator/Vortek graphics translation for direct hardware 3D rendering with DXVK and VKD3D-Proton.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Vulkan Extensions", fontSize = 10.sp, color = TextMuted)
                                Text("${vulkan.extensionsCount} available", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("Translation Layers", fontSize = 10.sp, color = TextMuted)
                                Text("${vulkan.dxvkVersion} / ${vulkan.vkd3dVersion}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AccentYellow)
                            }
                        }
                    }
                }
            }
        }

        // 3. OpenSSH Server Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("ssh_service_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.VpnKey, null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "OpenSSH Remote Server",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = if (sshServer.isRunning) "Running on port ${sshServer.port} (${sshServer.activeConnections} active)" else "Stopped",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (sshServer.isRunning) AccentGreen else TextMuted
                                )
                            }
                        }
                        Switch(
                            checked = sshServer.isRunning,
                            onCheckedChange = { onToggleSsh() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentGreen,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.testTag("toggle_ssh_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val sshCommand = "ssh -p ${sshServer.port} ${sshServer.username}@$localIp"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceElevated)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = sshCommand,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = AccentGreen
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(sshCommand))
                                Toast.makeText(context, "Copied SSH command to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, "Copy", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 4. Docker / Podman Support Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("docker_service_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AccentYellow.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Widgets, null, tint = AccentYellow, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Docker / Podman Virtualization",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${docker.engine} • ${docker.containers.size} containers",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (docker.isRunning) AccentGreen else TextMuted
                                )
                            }
                        }
                        Switch(
                            checked = docker.isRunning,
                            onCheckedChange = { onToggleDocker() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentYellow,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier.testTag("toggle_docker_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Rootless container engine inside PRoot using user namespaces and vfs storage driver to spin up lightweight Linux containers.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Docker Containers", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
                        Button(
                            onClick = { onRunContainer("python:3.11-alpine") },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = AccentYellow),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("run_docker_container_button")
                        ) {
                            Icon(Icons.Default.Add, null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Run Python", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    docker.containers.forEach { c ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurfaceElevated)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(c.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                                Text("${c.image} • ${c.ports}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextMuted)
                            }
                            Text(c.status, fontSize = 11.sp, color = AccentGreen)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
