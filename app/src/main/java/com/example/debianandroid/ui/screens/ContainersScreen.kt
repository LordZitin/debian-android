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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.model.DebianDistro
import com.example.debianandroid.model.WinlatorContainer
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.AccentYellow
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.TextSecondary
import com.example.debianandroid.theme.WinlatorBlue
import com.example.debianandroid.theme.WinlatorCard
import com.example.debianandroid.theme.WinlatorCardElevated
import com.example.debianandroid.theme.WinlatorDarkBg
import com.example.debianandroid.theme.WinlatorSurface

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContainersScreen(
    containers: List<WinlatorContainer>,
    currentStatus: ContainerStatus,
    onRunContainer: (String) -> Unit,
    onStopContainer: () -> Unit,
    onAddContainer: (String, DebianDistro, String, String) -> Unit,
    onDeleteContainer: (String) -> Unit,
    onToggleOpenAtBoot: (String) -> Unit,
    onNavigateToDesktop: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var newContainerName by remember { mutableStateOf("Container ${containers.size + 1}") }
    var selectedDistro by remember { mutableStateOf(DebianDistro.BOOKWORM) }
    var selectedRes by remember { mutableStateOf("1280x720 (16:9)") }
    var selectedDriver by remember { mutableStateOf("Turnip (Adreno) + Zink") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Screen Header Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Containers",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Configure PRoot / Wine runtime containers & default boot container",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }

                    // Open at boot / Touch Mouse Tag
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WinlatorCardElevated)
                            .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mouse,
                            contentDescription = null,
                            tint = AccentGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Built-in Touch Mouse: Active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentGreen
                        )
                    }
                }
            }

            // Container Items List
            items(containers, key = { it.id }) { container ->
                WinlatorContainerCard(
                    container = container,
                    currentStatus = currentStatus,
                    onRun = {
                        onRunContainer(container.id)
                        onNavigateToDesktop()
                    },
                    onStop = onStopContainer,
                    onDelete = { onDeleteContainer(container.id) },
                    onToggleOpenAtBoot = { onToggleOpenAtBoot(container.id) }
                )
            }

            item { Spacer(modifier = Modifier.height(72.dp)) }
        }

        // Floating Add Container Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = WinlatorBlue,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_container_fab")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Container")
        }
    }

    // Add Container Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = {
                Text(
                    text = "New Container",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = newContainerName,
                        onValueChange = { newContainerName = it },
                        label = { Text("Container Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WinlatorCard,
                            unfocusedContainerColor = WinlatorCard,
                            focusedBorderColor = WinlatorBlue,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    OutlinedTextField(
                        value = selectedDistro.codeName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rootfs Distribution") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WinlatorCard,
                            unfocusedContainerColor = WinlatorCard,
                            focusedBorderColor = WinlatorBlue,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )

                    OutlinedTextField(
                        value = selectedDriver,
                        onValueChange = { selectedDriver = it },
                        label = { Text("Graphics Driver (Vulkan)") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = WinlatorCard,
                            unfocusedContainerColor = WinlatorCard,
                            focusedBorderColor = WinlatorBlue,
                            unfocusedBorderColor = BorderSubtle
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddContainer(newContainerName, selectedDistro, selectedRes, selectedDriver)
                        showAddDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WinlatorBlue)
                ) {
                    Text("Create", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showAddDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = TextMuted)
                ) {
                    Text("Cancel")
                }
            },
            containerColor = WinlatorSurface
        )
    }
}

@Composable
fun WinlatorContainerCard(
    container: WinlatorContainer,
    currentStatus: ContainerStatus,
    onRun: () -> Unit,
    onStop: () -> Unit,
    onDelete: () -> Unit,
    onToggleOpenAtBoot: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("container_card_${container.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = WinlatorCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Container Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(WinlatorBlue.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Computer,
                            contentDescription = null,
                            tint = WinlatorBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = container.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            // Interactive Open At Boot Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (container.openAtBoot) AccentGreen.copy(alpha = 0.2f) else WinlatorCardElevated)
                                    .border(0.5.dp, if (container.openAtBoot) AccentGreen else BorderSubtle, RoundedCornerShape(4.dp))
                                    .clickable { onToggleOpenAtBoot() }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PowerSettingsNew,
                                        contentDescription = null,
                                        tint = if (container.openAtBoot) AccentGreen else TextMuted,
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = if (container.openAtBoot) "Open at Boot: ON" else "Open at Boot: OFF",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (container.openAtBoot) AccentGreen else TextMuted
                                    )
                                }
                            }
                        }
                        Text(
                            text = container.rootfsDistro.codeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = AccentCyan
                        )
                    }
                }

                // Overflow Menu
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp).testTag("container_menu_${container.id}")
                    ) {
                        Icon(Icons.Default.MoreVert, "Options", tint = TextMuted)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (container.openAtBoot) "Disable Open at Boot" else "Set as Boot Container") },
                            leadingIcon = { Icon(Icons.Default.Check, null, tint = AccentGreen) },
                            onClick = {
                                menuExpanded = false
                                onToggleOpenAtBoot()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Storage Info (16.0 GB allocated)") },
                            leadingIcon = { Icon(Icons.Default.Info, null) },
                            onClick = { menuExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Container") },
                            leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Specs Badges Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpecBadge(label = "Screen", value = container.screenSize, modifier = Modifier.weight(1f))
                SpecBadge(label = "Graphics", value = container.graphicsDriver, modifier = Modifier.weight(1.3f))
                SpecBadge(label = "Audio", value = container.audioDriver, modifier = Modifier.weight(1f))
                SpecBadge(label = "DXVK", value = container.dxvkVersion, modifier = Modifier.weight(0.8f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Run / Stop Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                if (container.isRunning && currentStatus == ContainerStatus.RUNNING) {
                    Button(
                        onClick = onStop,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WinlatorCardElevated,
                            contentColor = Color.Red
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp).testTag("stop_container_${container.id}")
                    ) {
                        Icon(Icons.Default.Stop, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Stop", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = onRun,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WinlatorBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp).testTag("resume_desktop_${container.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Desktop", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                } else {
                    Button(
                        onClick = onRun,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WinlatorBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp).testTag("run_container_${container.id}")
                    ) {
                        Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Run Container", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SpecBadge(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(WinlatorCardElevated)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column {
            Text(text = label, fontSize = 9.sp, color = TextMuted)
            Text(
                text = value,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1
            )
        }
    }
}
