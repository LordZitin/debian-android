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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.DebianSettings
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

@Composable
fun InputControlsScreen(
    settings: DebianSettings,
    onSettingsChanged: (DebianSettings) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Input Controls",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Configure built-in touch mouse, invisible joystick, and keybinding gestures",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }

        // Active Input Profile Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("active_input_profile_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WinlatorCard),
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(WinlatorBlue.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Gamepad, null, tint = WinlatorBlue, modifier = Modifier.size(22.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Active Profile: Split Touch & Mouse",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "Winlator RTS / Hybrid Touch Navigation",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AccentCyan
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Left Screen Description
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WinlatorCardElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Navigation, null, tint = AccentCyan, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Left Screen Half", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Invisible D-pad Joystick for Up / Down / Left / Right", fontSize = 10.sp, color = TextSecondary)
                                Text("• Single Tap: ⏎ Enter keybinding", fontSize = 10.sp, color = AccentGreen, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        // Right Screen Description
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(WinlatorCardElevated)
                                .border(1.dp, BorderSubtle, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Mouse, null, tint = AccentGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Right Screen Half", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("• Drag to move mouse cursor", fontSize = 10.sp, color = TextSecondary)
                                Text("• Single Tap: Left-Click", fontSize = 10.sp, color = AccentCyan)
                                Text("• Double Tap: Double-Click", fontSize = 10.sp, color = AccentCyan)
                                Text("• Hold: Right-Click (with haptics)", fontSize = 10.sp, color = AccentYellow, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }
        }

        // Settings / Customization
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WinlatorCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Touch Mouse Configuration",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Open at Boot", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text("Automatically launch container desktop with built-in mouse on app startup", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = settings.openAtBoot,
                            onCheckedChange = { onSettingsChanged(settings.copy(openAtBoot = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = WinlatorBlue,
                                uncheckedTrackColor = WinlatorCardElevated
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Built-in Touch Mouse", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                            Text("Enable right screen trackpad with tap/double-tap/hold clicks", fontSize = 11.sp, color = TextMuted)
                        }
                        Switch(
                            checked = settings.builtInMouseEnabled,
                            onCheckedChange = { onSettingsChanged(settings.copy(builtInMouseEnabled = it)) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AccentGreen,
                                uncheckedTrackColor = WinlatorCardElevated
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Cursor Sensitivity: ${String.format("%.1f", settings.mouseSensitivity)}x", fontSize = 12.sp, color = TextSecondary)
                    Slider(
                        value = settings.mouseSensitivity,
                        onValueChange = { onSettingsChanged(settings.copy(mouseSensitivity = it)) },
                        valueRange = 0.5f..2.5f,
                        colors = SliderDefaults.colors(
                            thumbColor = WinlatorBlue,
                            activeTrackColor = WinlatorBlue,
                            inactiveTrackColor = WinlatorCardElevated
                        )
                    )
                }
            }
        }

        // Back Keybinding Note
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = WinlatorCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AccentYellow.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⎋", fontSize = 18.sp, color = AccentYellow, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Android Back Button -> ESC Keybinding",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pressing the device hardware or gesture Back button inside Desktop sends the ESC key to applications.",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}
