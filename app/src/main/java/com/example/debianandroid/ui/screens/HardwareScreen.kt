package com.example.debianandroid.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.HardwareInfo
import com.example.debianandroid.model.NetworkDiagnosticInfo
import com.example.debianandroid.model.PingSession
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
fun HardwareScreen(
    hardwareInfo: HardwareInfo?,
    networkInfo: NetworkDiagnosticInfo?,
    pingSession: PingSession,
    onRunPing: (String) -> Unit,
    onRefresh: () -> Unit
) {
    var pingTargetInput by remember { mutableStateOf(pingSession.targetHost) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hardware & Network Inspector",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Termux-like device telemetry & network diagnosis",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(10.dp))
                        .testTag("refresh_hw_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = AccentCyan)
                }
            }
        }

        // Network Diagnosis / Ping Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NetworkCheck, null, tint = AccentCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ICMP Network Ping Tool",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = pingTargetInput,
                            onValueChange = { pingTargetInput = it },
                            placeholder = { Text("e.g. 8.8.8.8 or google.com", fontSize = 12.sp, color = TextMuted) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("ping_host_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = DarkSurfaceElevated,
                                unfocusedContainerColor = DarkSurfaceElevated,
                                focusedBorderColor = AccentCyan,
                                unfocusedBorderColor = BorderSubtle
                            ),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(onGo = { onRunPing(pingTargetInput) })
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onRunPing(pingTargetInput) },
                            enabled = !pingSession.isRunning && pingTargetInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = DebianRed),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(52.dp).testTag("run_ping_button")
                        ) {
                            Icon(Icons.Default.PlayArrow, null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (pingSession.isRunning) "Pinging..." else "Ping")
                        }
                    }

                    // Ping Latency Stats
                    if (pingSession.packetsReceived > 0) {
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
                                    Text("Min RTT", fontSize = 10.sp, color = TextMuted)
                                    Text("${pingSession.minLatencyMs} ms", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentGreen)
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
                                    Text("Avg RTT", fontSize = 10.sp, color = TextMuted)
                                    Text("${pingSession.avgLatencyMs} ms", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentCyan)
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
                                    Text("Max RTT", fontSize = 10.sp, color = TextMuted)
                                    Text("${pingSession.maxLatencyMs} ms", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AccentYellow)
                                }
                            }
                        }
                    }

                    // Ping Log Console
                    if (pingSession.consoleLines.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp)),
                            color = TerminalBlack
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                pingSession.consoleLines.takeLast(6).forEach { line ->
                                    Text(
                                        text = line,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = if (line.contains("time=")) AccentGreen else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Active Network Interfaces & Wi-Fi Details
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Wifi, null, tint = AccentGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Network Interface Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    networkInfo?.let { net ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                InfoItem(label = "Local IP Address", value = net.localIp)
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "Subnet Mask", value = net.subnetMask)
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "Default Gateway", value = net.gateway)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                InfoItem(label = "Wi-Fi SSID", value = net.wifiSsid)
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "Link Speed", value = "${net.linkSpeedMbps} Mbps")
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "DNS Resolvers", value = net.dnsList.joinToString(", "))
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Active Physical & Virtual Interfaces:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        net.interfaces.forEach { intf ->
                            Text("• $intf", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = AccentCyan)
                        }
                    }
                }
            }
        }

        // Hardware Specifications & Telemetry
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Memory, null, tint = AccentYellow, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Device Hardware & Sensors (Termux Telemetry)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    hardwareInfo?.let { hw ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                InfoItem(label = "Device Model", value = hw.deviceModel)
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "SoC / Chipset", value = hw.socModel)
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "CPU Cores", value = "${hw.cpuCores} cores (${hw.cpuArch})")
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "Supported ABIs", value = hw.supportedAbis)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                InfoItem(label = "System RAM", value = "${hw.ramAvailMb} MB free / ${hw.ramTotalMb} MB")
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "Battery", value = "${hw.batteryPct}% (${hw.batteryTempC}°C, ${if (hw.isCharging) "Charging" else "Discharging"})")
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "Screen Resolution", value = "${hw.displayResolution} @ ${hw.refreshRateHz.toInt()}Hz")
                                Spacer(modifier = Modifier.height(6.dp))
                                InfoItem(label = "Hardware Sensors", value = "${hw.sensorsCount} detected")
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Sensors Inventory:", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        hw.sensorList.forEach { s ->
                            Text("• $s", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column {
        Text(text = label, fontSize = 10.sp, color = TextMuted)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    }
}
