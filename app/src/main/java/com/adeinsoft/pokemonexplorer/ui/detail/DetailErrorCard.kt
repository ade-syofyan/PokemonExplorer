package com.adeinsoft.pokemonexplorer.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailErrorCard(
    message: String,
    isOffline: Boolean,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.elevatedCardColors()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon & Title
                Icon(
                    imageVector = if (isOffline) Icons.Outlined.CloudOff else Icons.Outlined.ErrorOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = if (isOffline) "Kamu sedang offline" else "Gagal memuat detail",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isOffline)
                        "Periksa koneksi internet kamu lalu coba lagi."
                    else
                        "Terjadi kesalahan saat memuat data. Coba lagi nanti.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Expandable technical message
                var showDetail by remember { mutableStateOf(false) }
                TextButton(onClick = { showDetail = !showDetail }) {
                    Text(if (showDetail) "Sembunyikan detail" else "Tampilkan detail")
                }
                AnimatedVisibility(visible = showDetail) {
                    Text(
                        text = message.ifBlank { "Tidak ada pesan rinci." },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) { Text("Kembali") }

                    Button(
                        onClick = onRetry,
                        modifier = Modifier.weight(1f)
                    ) { Text("Coba lagi") }
                }
            }
        }
    }
}
