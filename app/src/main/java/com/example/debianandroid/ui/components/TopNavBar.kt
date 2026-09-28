package com.example.debianandroid.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.ContainerStatus
import com.example.debianandroid.theme.AccentCyan
import com.example.debianandroid.theme.AccentGreen
import com.example.debianandroid.theme.BorderSubtle
import com.example.debianandroid.theme.DarkSurfaceElevated
import com.example.debianandroid.theme.DarkSurfaceVariant
import com.example.debianandroid.theme.DebianRed
import com.example.debianandroid.theme.TextMuted
import com.example.debianandroid.theme.TextPrimary
import com.example.debianandroid.theme.TextSecondary

@Composable
fun TopNavBar(
    status: ContainerStatus,
    distroName: String,
    archName: String,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = DarkSurfaceVariant,
        tonalElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand & Distro
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DebianRed),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "debian",
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        fontSize = 8.sp,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Debian Android",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusIndicatorPill(status = status)
                    }
                    Text(
                        text = "$distroName • $archName",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted
                    )
                }
            }

            // Power Action Button
            if (status == ContainerStatus.RUNNING) {
                ElevatedButton(
                    onClick = onStopClick,
                    modifier = Modifier.testTag("stop_container_button"),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = DarkSurfaceElevated,
                        contentColor = DebianRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Halt",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            } else {
                ElevatedButton(
                    onClick = onStartClick,
                    enabled = status == ContainerStatus.STOPPED,
                    modifier = Modifier.testTag("start_container_button"),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = DebianRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (status == ContainerStatus.STARTING) "Booting..." else "Launch",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
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
        ContainerStatus.STOPPING -> DebianRed.copy(alpha = 0.15f)
        ContainerStatus.STOPPED -> DarkSurfaceElevated
    }

    val dotColor = when (status) {
        ContainerStatus.RUNNING -> AccentGreen
        ContainerStatus.STARTING -> AccentCyan
        ContainerStatus.STOPPING -> DebianRed
        ContainerStatus.STOPPED -> TextMuted
    }

    val label = when (status) {
        ContainerStatus.RUNNING -> "Active"
        ContainerStatus.STARTING -> "Booting"
        ContainerStatus.STOPPING -> "Halting"
        ContainerStatus.STOPPED -> "Stopped"
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(pillBg)
            .border(0.5.dp, BorderSubtle, RoundedCornerShape(8.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = dotColor
        )
    }
}
