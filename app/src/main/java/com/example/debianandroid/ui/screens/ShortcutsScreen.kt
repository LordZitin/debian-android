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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.WindowType
import com.example.debianandroid.model.WinlatorShortcut
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.AccentYellow
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.DebianRed
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.WinlatorBlue
import com.example.debianandroid.theme.WinlatorCard
import com.example.debianandroid.theme.WinlatorDarkBg

@Composable
fun ShortcutsScreen(
    shortcuts: List<WinlatorShortcut>,
    onLaunchShortcut: (WinlatorShortcut) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WinlatorDarkBg)
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Shortcuts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Tap any shortcut to boot directly into Desktop with Touch Mouse",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 140.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize().testTag("shortcuts_grid")
        ) {
            items(shortcuts, key = { it.id }) { shortcut ->
                val (icon, color) = when (shortcut.iconType) {
                    WindowType.GIMP -> Pair(Icons.Default.Brush, DebianRed)
                    WindowType.VULKAN_GEARS -> Pair(Icons.Default.ViewInAr, AccentYellow)
                    WindowType.TERMINAL -> Pair(Icons.Default.Terminal, AccentGreen)
                    WindowType.EDITOR -> Pair(Icons.Default.TextFields, AccentCyan)
                    WindowType.MONITOR -> Pair(Icons.Default.Memory, AccentYellow)
                    WindowType.FILES -> Pair(Icons.Default.Folder, WinlatorBlue)
                    else -> Pair(Icons.Default.PlayArrow, WinlatorBlue)
                }

                Card(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onLaunchShortcut(shortcut) }
                        .testTag("shortcut_card_${shortcut.id}"),
                    colors = CardDefaults.cardColors(containerColor = WinlatorCard),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(color.copy(alpha = 0.2f))
                                .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = shortcut.title,
                                tint = color,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = shortcut.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = shortcut.subtitle,
                            fontSize = 10.sp,
                            color = TextMuted,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
