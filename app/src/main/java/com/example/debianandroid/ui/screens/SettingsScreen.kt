package com.example.debianandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Monitor
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.Architecture
import com.example.debianandroid.model.DebianDistro
import com.example.debianandroid.model.DebianSettings
import com.example.debianandroid.model.DesktopEnv
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.DarkSurface
import com.example.debianandroid.theme.DarkSurfaceElevated
import com.example.debianandroid.theme.DarkSurfaceVariant
import com.example.debianandroid.theme.DebianRed
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: DebianSettings,
    onSettingsChanged: (DebianSettings) -> Unit
) {
    var distroExpanded by remember { mutableStateOf(false) }
    var archExpanded by remember { mutableStateOf(false) }
    var desktopExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Distribution & Architecture Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Distribution & Architecture",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Distro Dropdown
                    ExposedDropdownMenuBox(
                        expanded = distroExpanded,
                        onExpandedChange = { distroExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = settings.distro.codeName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Debian Distro Release") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = distroExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("distro_dropdown"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = distroExpanded,
                            onDismissRequest = { distroExpanded = false }
                        ) {
                            DebianDistro.values().forEach { d ->
                                DropdownMenuItem(
                                    text = { Text(d.codeName) },
                                    onClick = {
                                        onSettingsChanged(settings.copy(distro = d))
                                        distroExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Arch Dropdown
                    ExposedDropdownMenuBox(
                        expanded = archExpanded,
                        onExpandedChange = { archExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${settings.architecture.tag} (${settings.architecture.label})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Hardware Target Architecture") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = archExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("arch_dropdown"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = archExpanded,
                            onDismissRequest = { archExpanded = false }
                        ) {
                            Architecture.values().forEach { a ->
                                DropdownMenuItem(
                                    text = { Text("${a.tag} (${a.label})") },
                                    onClick = {
                                        onSettingsChanged(settings.copy(architecture = a))
                                        archExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Desktop Env Dropdown
                    ExposedDropdownMenuBox(
                        expanded = desktopExpanded,
                        onExpandedChange = { desktopExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = "${settings.desktopEnv.displayName} - ${settings.desktopEnv.memoryFootprint}",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Desktop GUI Environment") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = desktopExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("desktop_env_dropdown"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = desktopExpanded,
                            onDismissRequest = { desktopExpanded = false }
                        ) {
                            DesktopEnv.values().forEach { env ->
                                DropdownMenuItem(
                                    text = { Text("${env.displayName} - ${env.memoryFootprint}") },
                                    onClick = {
                                        onSettingsChanged(settings.copy(desktopEnv = env))
                                        desktopExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // PRoot Virtualization Engine Settings
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PRoot Virtualization Engine",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    SettingToggleRow(
                        title = "SELinux Acceleration Hook",
                        subtitle = "Load libandroid-shmem-disableselinux.so for fast X11 rendering",
                        isChecked = settings.enableSELinuxBypass,
                        onCheckedChange = { onSettingsChanged(settings.copy(enableSELinuxBypass = it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingToggleRow(
                        title = "Fake Root Execution (-0)",
                        subtitle = "Pretend to be root user for package managers & system tools",
                        isChecked = settings.enableFakeRoot,
                        onCheckedChange = { onSettingsChanged(settings.copy(enableFakeRoot = it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingToggleRow(
                        title = "Mount Android Internal Storage",
                        subtitle = "Bind /storage/emulated/0 to /sdcard inside Debian",
                        isChecked = settings.bindSdCard,
                        onCheckedChange = { onSettingsChanged(settings.copy(bindSdCard = it)) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    SettingToggleRow(
                        title = "Enable link2symlink",
                        subtitle = "Translate hard links to symlinks on FAT/exFAT filesystems",
                        isChecked = settings.enableLink2Symlink,
                        onCheckedChange = { onSettingsChanged(settings.copy(enableLink2Symlink = it)) }
                    )
                }
            }
        }

        // c-ares DNS Resolver & Networking
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "c-ares Asynchronous DNS Resolver",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Uses c-ares asynchronous DNS library to resolve names without root network privileges.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = settings.dnsServer,
                        onValueChange = { onSettingsChanged(settings.copy(dnsServer = it)) },
                        label = { Text("Primary DNS Nameserver") },
                        modifier = Modifier.fillMaxWidth().testTag("primary_dns_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated,
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = settings.secondaryDns,
                        onValueChange = { onSettingsChanged(settings.copy(secondaryDns = it)) },
                        label = { Text("Fallback DNS Nameserver") },
                        modifier = Modifier.fillMaxWidth().testTag("secondary_dns_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = DarkSurfaceElevated,
                            unfocusedContainerColor = DarkSurfaceElevated,
                            focusedBorderColor = AccentCyan,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                }
            }
        }

        // About & License
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "About Debian on Android",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Rewrite of LordZitin/debian-android for modern Android with Jetpack Compose. Includes PRoot virtualization, c-ares DNS resolver, SELinux shmem accelerator hook, and XSDL desktop integration.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun SettingToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = DebianRed,
                uncheckedTrackColor = DarkSurfaceElevated
            )
        )
    }
}
