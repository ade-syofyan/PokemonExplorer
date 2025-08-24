package com.adeinsoft.pokemonexplorer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppHeaderBar(
    showBack: Boolean,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: (() -> Unit)? = null,
    title: String = "Pokemon Explorer",
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 56.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                IconButton(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    onClick = { onBack?.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Spacer(Modifier.width(4.dp))
            } else {
                Spacer(Modifier.width(4.dp))
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                onClick = onToggleTheme
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = "Toggle theme"
                )
            }
        }
    }
}
