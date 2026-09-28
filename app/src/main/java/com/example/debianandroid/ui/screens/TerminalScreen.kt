package com.example.debianandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
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
import com.example.debianandroid.model.LineType
import com.example.debianandroid.model.TerminalLine
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.AccentYellow
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.DarkSurfaceElevated
import com.example.debianandroid.theme.DarkSurfaceVariant
import com.example.debianandroid.theme.DebianRed
import com.example.debianandroid.theme.TerminalBlack
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.TextSecondary

@Composable
fun TerminalScreen(
    lines: List<TerminalLine>,
    onExecuteCommand: (String) -> Unit
) {
    var inputQuery by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(lines.size) {
        if (lines.isNotEmpty()) {
            listState.animateScrollToItem(lines.size - 1)
        }
    }

    val quickCommands = listOf(
        "neofetch",
        "apt update",
        "df -h",
        "free -m",
        "ps aux",
        "whoami",
        "uname -a",
        "cat /etc/os-release",
        "gimp",
        "help"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TerminalBlack)
    ) {
        // Terminal Window Header Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(DebianRed))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(AccentYellow))
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(AccentGreen))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "debian@localhost:~ [PRoot bash]",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                IconButton(
                    onClick = { onExecuteCommand("clear") },
                    modifier = Modifier.size(28.dp).testTag("clear_terminal_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "Clear",
                        tint = TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Terminal Log Console
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("terminal_output_list"),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(lines, key = { it.id }) { line ->
                val textColor = when (line.type) {
                    LineType.COMMAND -> AccentGreen
                    LineType.ERROR -> DebianRed
                    LineType.SUCCESS -> AccentGreen
                    LineType.INFO -> AccentCyan
                    LineType.SYSTEM -> AccentYellow
                    LineType.OUTPUT -> TextSecondary
                }
                val prefix = when (line.type) {
                    LineType.COMMAND -> ""
                    LineType.ERROR -> "[ERR] "
                    LineType.SUCCESS -> ""
                    LineType.INFO -> ""
                    LineType.SYSTEM -> ""
                    LineType.OUTPUT -> ""
                }
                Text(
                    text = prefix + line.text,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    color = textColor
                )
            }
        }

        // Quick Suggestion Chips Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceVariant)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickCommands.forEach { cmd ->
                AssistChip(
                    onClick = {
                        onExecuteCommand(cmd)
                    },
                    label = {
                        Text(
                            text = cmd,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TextPrimary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = DarkSurfaceElevated
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = BorderSubtle,
                        enabled = true
                    ),
                    modifier = Modifier.height(28.dp)
                )
            }
        }

        // Accessory Keys Bar (Bash keys)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkSurfaceElevated)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val keys = listOf("TAB", "ESC", "CTRL", "ALT", "|", "/", "-", "~", "$", ";")
            keys.forEach { k ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(DarkSurfaceVariant)
                        .border(1.dp, BorderSubtle, RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = k,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentCyan
                    )
                }
            }
        }

        // Terminal Input Field
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = AccentGreen,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(start = 4.dp, end = 8.dp)
                )
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = {
                        Text("Type bash command (e.g. neofetch, apt update)...", fontSize = 12.sp, color = TextMuted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("terminal_input_field"),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = TextPrimary
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentCyan,
                        unfocusedBorderColor = BorderSubtle,
                        focusedContainerColor = TerminalBlack,
                        unfocusedContainerColor = TerminalBlack
                    ),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputQuery.isNotBlank()) {
                                onExecuteCommand(inputQuery)
                                inputQuery = ""
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            onExecuteCommand(inputQuery)
                            inputQuery = ""
                        }
                    },
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(DebianRed)
                        .testTag("terminal_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Run",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
