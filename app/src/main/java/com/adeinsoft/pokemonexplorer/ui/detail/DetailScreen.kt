package com.adeinsoft.pokemonexplorer.ui.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.adeinsoft.pokemonexplorer.domain.model.PokemonFull
import com.adeinsoft.pokemonexplorer.ui.components.AppHeaderBar
import com.adeinsoft.pokemonexplorer.ui.components.ShimmerAsyncImage
import com.adeinsoft.pokemonexplorer.ui.components.isOfflineNow
import com.adeinsoft.pokemonexplorer.ui.state.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.filter

@Composable
fun DetailScreen(
    idOrName: String,
    jump: String?,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
    vm: PokemonDetailViewModel = hiltViewModel(),
    autoScrollOnSwitch: Boolean = false,
) {
    LaunchedEffect(idOrName) { vm.load(idOrName) }

    val context = LocalContext.current
    val state by vm.uiState.collectAsState()

    var alertMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    var offlineNow = isOfflineNow(context)

    var jumpState by rememberSaveable { mutableStateOf(jump) }

    val switchInPlace: (String) -> Unit = { target ->
        scope.launch {
            val offline = isOfflineNow(context)
            if (offline) {
                if (vm.hasCachedDetail(target)) {
                    vm.load(target)
                    if (autoScrollOnSwitch) jumpState = "evo"
                } else {
                    alertMsg = "Kamu sedang offline dan data untuk \"$target\" belum tersimpan. Periksa koneksi internet lalu coba lagi."
                }
            } else {
                vm.load(target)
                if (autoScrollOnSwitch) jumpState = "evo"
            }
        }
    }

    Surface(Modifier.fillMaxSize()) {
        Column {
            AppHeaderBar(
                showBack = true,
                isDark = isDark,
                onToggleTheme = onToggleTheme,
                onBack = onBack
            )

            when (val s = state) {
                is UiState.Loading -> DetailShimmerPlaceholder()
                is UiState.Error -> DetailErrorCard(
                    message = s.message,
                    isOffline = offlineNow,
                    onRetry = { vm.retry(idOrName) },
                    onBack = onBack
                )
                is UiState.Success -> {
                    val offline by vm.serverIssue.collectAsState()
                    Column(Modifier.fillMaxSize()) {
                        androidx.compose.animation.AnimatedVisibility(visible = offline) {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                tonalElevation = 2.dp,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.CloudOff,
                                        contentDescription = null
                                    )
                                    Text(
                                        "Tidak dapat memuat dari jaringan. Menampilkan data tersimpan.",
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    TextButton(onClick = { vm.retry(idOrName) }) {
                                        Text("Coba lagi")
                                    }
                                }
                            }
                        }

                        DetailContent(
                            data = s.data,
                            jump = jumpState,
                            onSwitchTo = switchInPlace,
                            onJumpConsumed = { jumpState = null },
                            vm = vm
                        )
                    }
                }

            }
        }
    }

    if (alertMsg != null) {
        val title = if (offlineNow) "Mode Offline" else "Info Server"
        AlertDialog(
            onDismissRequest = { alertMsg = null },
            title = { Text(title) },
            text = { Text(alertMsg!!) },
            confirmButton = { TextButton(onClick = { alertMsg = null }) { Text("OK") } }
        )
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DetailContent(
    data: PokemonFull,
    jump: String?,
    onSwitchTo: (String) -> Unit,
    onJumpConsumed: () -> Unit,
    vm: PokemonDetailViewModel,
) {
    val evoBring = remember { BringIntoViewRequester() }
    val scroll = rememberScrollState()
    val isFav by vm.isFavorite.collectAsState()
    var pendingFavorite by remember { mutableStateOf<Boolean?>(null) }
    var autoScrollOnSwitch by rememberSaveable { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Box(Modifier.fillMaxWidth()) {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ShimmerAsyncImage(
                            model = data.imageUrlLarge,
                            contentDescription = data.name,
                            modifier = Modifier.size(180.dp),
                            shape = RoundedCornerShape(16.dp),
                            contentScale = ContentScale.Fit
                        )
                        Text(
                            text = data.name.replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            data.types.forEach { t -> AssistChip(onClick = {}, label = { Text(t) }) }
                        }
                    }
                }

                IconButton(
                    onClick = { pendingFavorite = !isFav },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFav) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFav) "Unfavorite" else "Add to favorites",
                        tint = if (isFav) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // About
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("About", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        InfoTile(title = "Height", value = "${data.heightM} m", modifier = Modifier.weight(1f))
                        InfoTile(title = "Weight", value = "${data.weightKg} kg", modifier = Modifier.weight(1f))
                        InfoTile(title = "Base EXP", value = data.baseExp?.toString() ?: "-", modifier = Modifier.weight(1f))
                    }
                    if (data.abilities.isNotEmpty()) {
                        Text("Abilities", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(
                            modifier = Modifier.horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            data.abilities.forEach { a ->
                                FilterChip(
                                    selected = a.isHidden,
                                    onClick = {},
                                    label = { Text(a.name) }
                                )
                            }
                        }
                    }
                }
            }

            // Stats
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Base Stats", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    data.stats.forEach { s -> StatRow(name = s.name, value = s.value) }
                }
            }

            // Flavor text
            if (!data.flavorText.isNullOrBlank()) {
                ElevatedCard(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Pokédex Entry", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(data.flavorText!!, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Evolution
            if (data.evolution.isNotEmpty()) {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .bringIntoViewRequester(evoBring)
                ) {
                    Column(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("Evolution", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            data.evolution.forEach { e ->
                                val isCurrent = (e.id == data.id) || e.name.equals(data.name, ignoreCase = true)
                                val itemModifier = Modifier
                                    .alpha(if (isCurrent) 0.5f else 1f)
                                    .then(if (!isCurrent) Modifier.clickable { onSwitchTo(e.name) } else Modifier)

                                Column(modifier = itemModifier, horizontalAlignment = Alignment.CenterHorizontally) {
                                    AsyncImage(
                                        model = e.imageUrl,
                                        contentDescription = e.name,
                                        modifier = Modifier.size(72.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Text(e.name.replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.labelLarge)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
        }
    }

    if (pendingFavorite != null) {
        val wantFav = pendingFavorite == true

        if (wantFav) {
            vm.toggleFavorite(data.id.toString(), true)
        } else {
            AlertDialog(
                onDismissRequest = { pendingFavorite = null },
                title = { Text("Hapus dari Favorit?") },
                text = {
                    Text(
                        "“${data.name.replaceFirstChar { it.uppercase() }}” akan dihapus dari daftar favorit."
                    )
                },
                confirmButton = {
                    TextButton(onClick = {
                        vm.toggleFavorite(data.id.toString(), false)
                        pendingFavorite = null
                    }) {
                        Text("Hapus")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingFavorite = null }) {
                        Text("Batal")
                    }
                }
            )
        }
    }

    LaunchedEffect(data.id) {
        scroll.animateScrollTo(0)
    }

    LaunchedEffect(data.id, jump) {
        if (jump == "evo" && autoScrollOnSwitch) {
            snapshotFlow { scroll.maxValue }.filter { it > 0 }.first()
            delay(120)
            evoBring.bringIntoView()
            onJumpConsumed()
        }
    }
}

@Composable
private fun InfoTile(title: String, value: String, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.heightIn(min = 80.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(title, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun StatRow(name: String, value: Int, max: Int = 200) {
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, style = MaterialTheme.typography.labelMedium)
            Text("$value", style = MaterialTheme.typography.labelMedium)
        }
        LinearProgressIndicator(
            progress = { value / max.toFloat() },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
