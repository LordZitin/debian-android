package com.example.debianandroid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.debianandroid.model.DebianPackage
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
fun PackagesScreen(
    packages: List<DebianPackage>,
    onInstallPackage: (String) -> Unit,
    onUninstallPackage: (String) -> Unit,
    onRefreshRepositories: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Development", "Graphics", "Utilities", "Networking", "Desktop")

    val filteredPackages = packages.filter { pkg ->
        val matchesCategory = selectedCategory == "All" || pkg.category.equals(selectedCategory, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                pkg.name.contains(searchQuery, ignoreCase = true) ||
                pkg.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    val installedCount = packages.count { it.isInstalled }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .padding(16.dp)
    ) {
        // Search & Refresh Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search Debian packages (e.g. gcc, gimp)...", fontSize = 13.sp, color = TextMuted) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("package_search_input"),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentCyan,
                    unfocusedBorderColor = BorderSubtle,
                    focusedContainerColor = DarkSurfaceVariant,
                    unfocusedContainerColor = DarkSurfaceVariant
                ),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = onRefreshRepositories,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceVariant)
                    .border(1.dp, BorderSubtle, RoundedCornerShape(12.dp))
                    .testTag("refresh_repos_button")
            ) {
                Icon(Icons.Default.Refresh, "apt update", tint = AccentCyan)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Categories Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isSelected = selectedCategory == cat
                AssistChip(
                    onClick = { selectedCategory = cat },
                    label = {
                        Text(
                            text = cat,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else TextSecondary
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (isSelected) DebianRed else DarkSurfaceElevated
                    ),
                    border = AssistChipDefaults.assistChipBorder(
                        borderColor = if (isSelected) DebianRed else BorderSubtle,
                        enabled = true
                    ),
                    modifier = Modifier.height(32.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Repository Summary Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${filteredPackages.size} packages available",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted
            )
            Text(
                text = "$installedCount installed",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
                color = AccentGreen
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Package Cards List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .testTag("package_items_list"),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPackages, key = { it.id }) { pkg ->
                PackageCard(
                    pkg = pkg,
                    onInstall = { onInstallPackage(pkg.id) },
                    onUninstall = { onUninstallPackage(pkg.id) }
                )
            }
        }
    }
}

@Composable
fun PackageCard(
    pkg: DebianPackage,
    onInstall: () -> Unit,
    onUninstall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderSubtle)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = pkg.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(DarkSurfaceElevated)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = pkg.category,
                                fontSize = 10.sp,
                                color = AccentCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Text(
                        text = "v${pkg.version} • ${pkg.sizeMb} MB",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                if (pkg.isInstalled) {
                    Button(
                        onClick = onUninstall,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceElevated,
                            contentColor = AccentGreen
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("uninstall_btn_${pkg.id}")
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Installed", fontSize = 11.sp)
                    }
                } else {
                    Button(
                        onClick = onInstall,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DebianRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp).testTag("install_btn_${pkg.id}")
                    ) {
                        Icon(Icons.Default.Download, null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Install", fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = pkg.description,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
        }
    }
}
