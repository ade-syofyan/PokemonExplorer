@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.adeinsoft.pokemonexplorer.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.adeinsoft.pokemonexplorer.ui.components.PokemonCard
import com.adeinsoft.pokemonexplorer.ui.components.SearchBar
import com.adeinsoft.pokemonexplorer.ui.state.UiState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.adeinsoft.pokemonexplorer.ui.components.isOfflineNow
import com.adeinsoft.pokemonexplorer.ui.components.AppHeaderBar
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween

@Composable
fun ListScreen(
    onItemClick: (String) -> Unit,
    isDark: Boolean,
    onToggleTheme: () -> Unit,
    vm: PokemonListViewModel = hiltViewModel()
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var alertMsg by remember { mutableStateOf<String?>(null) }

    val state by vm.uiState.collectAsState()
    val query by vm.queryState.collectAsState()
    val favoritesOnly by vm.favoritesOnlyState.collectAsState()
    val isLoadingMore by vm.isLoadingMore.collectAsState()
    val endReached by vm.endReached.collectAsState()

    val listState = rememberLazyListState()
    var confirmUnfav by remember { mutableStateOf<String?>(null) }
    var offlineNow = isOfflineNow(ctx)

    val isRefreshing by vm.isRefreshing.collectAsState()
    val lastError by vm.lastError.collectAsState()

    if (confirmUnfav != null) {
        AlertDialog(
            onDismissRequest = { confirmUnfav = null },
            title = { Text("Hapus dari Favorit?") },
            text = { Text("“$confirmUnfav” akan di-unfavorite.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.toggleFavorite(confirmUnfav!!, false)
                    val target = (state as? UiState.Success)?.data?.firstOrNull { it.name == confirmUnfav }
                    if (target != null) {
                        vm.toggleFavoriteById(target.id, target.name, target.imageUrl, false)
                    }
                    confirmUnfav = null
                }) { Text("Unfavorite") }
            },
            dismissButton = {
                TextButton(onClick = { confirmUnfav = null }) { Text("Batal") }
            }
        )
    }

    val isScrolling by remember { derivedStateOf { listState.isScrollInProgress } }
    var collapsed by remember { mutableStateOf(false) }
    
    val currentListSize by remember(state) {
        derivedStateOf { (state as? UiState.Success)?.data?.size ?: 0 }
    }
    val LONG_LIST_THRESHOLD = 12
    val shouldAutoCollapse = remember(favoritesOnly, currentListSize) {
        !favoritesOnly && currentListSize >= LONG_LIST_THRESHOLD
    }

    LaunchedEffect(isScrolling, shouldAutoCollapse) {
        if (!shouldAutoCollapse) {
            collapsed = false
            return@LaunchedEffect
        }
        if (isScrolling) {
            collapsed = true
        } else {
            kotlinx.coroutines.delay(2_000)
            collapsed = false
        }
    }

    val onItemClickGated: (String) -> Unit = { name ->
        offlineNow = isOfflineNow(ctx)
        if (offlineNow) {
            scope.launch {
                val cached = vm.hasCachedDetail(name)
                if (cached) onItemClick(name)
                else {
                    alertMsg = "Kamu sedang offline dan data \"$name\" belum tersimpan.\n" +
                            "Periksa koneksi internet lalu coba lagi."
                }
            }
        } else onItemClick(name)
    }

    if (alertMsg != null) {
        AlertDialog(
            onDismissRequest = { alertMsg = null },
            title = { Text("Mode Offline") },
            text = { Text(alertMsg!!) },
            confirmButton = { TextButton(onClick = { alertMsg = null }) { Text("OK") } }
        )
    }

    // infinite scroll
    LaunchedEffect(listState, query, favoritesOnly, state) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .map { it ?: -1 }
            .distinctUntilChanged()
            .collectLatest { lastIndex ->
                val total = (state as? UiState.Success)?.data?.size ?: 0
                if (total == 0) return@collectLatest
                if (!favoritesOnly && query.isBlank() && lastIndex >= total - 3) {
                    vm.loadMore()
                }
            }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            AppHeaderBar(
                showBack = false,
                isDark = isDark,
                onToggleTheme = onToggleTheme
            )

            val isCompactCard = favoritesOnly || (shouldAutoCollapse && collapsed)

            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(180))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 12.dp,
                            vertical = if (isCompactCard) 8.dp else 12.dp
                        ),
                    verticalArrangement = Arrangement.spacedBy(if (isCompactCard) 6.dp else 10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Browse Pokémon",
                            style = if (isCompactCard)
                                MaterialTheme.typography.titleSmall
                            else
                                MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = {
                            vm.onPullToRefresh(force = true)
                            vm.resetEndReached()
                        }) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                        }
                    }

                    val showSearch = favoritesOnly || (!shouldAutoCollapse || !collapsed)
                    AnimatedVisibility(
                        visible = showSearch,
                        enter = expandVertically() + fadeIn(),
                        exit  = fadeOut() + shrinkVertically()
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            SearchBar(
                                query = query,
                                onQueryChange = {
                                    vm.setQuery(it)
                                    vm.resetEndReached()
                                }
                            )
                        }
                    }

                    FavoritesSwitchChips(
                        favoritesOnly = favoritesOnly,
                        onSelectAll = {
                            if (favoritesOnly) {
                                vm.setFavoritesOnly(false)
                                vm.resetEndReached()
                                scope.launch { listState.scrollToItem(0) }
                            }
                        },
                        onSelectFavorites = {
                            if (!favoritesOnly) {
                                vm.setFavoritesOnly(true)
                                vm.resetEndReached()
                                scope.launch { listState.scrollToItem(0) }
                            }
                        }
                    )
                }
            }

            val list = (state as? UiState.Success)?.data ?: emptyList()
            val isInitialLoading = state is UiState.Loading
            val isRefreshingEmpty = isRefreshing && list.isEmpty()
            val showInlineError = list.isNotEmpty() && (lastError != null)
            val showErrorState = list.isEmpty() && (lastError != null) && !isRefreshingEmpty && !isInitialLoading
            val showEmptyState = list.isEmpty() && !isInitialLoading && !isRefreshingEmpty && (lastError == null)

            AnimatedVisibility(
                visible = showInlineError,
                enter = expandVertically() + fadeIn(),
                exit  = fadeOut() + shrinkVertically()
            ) {
                ErrorInlineBanner(
                    message = lastError ?: "",
                    onRetry = { vm.onPullToRefresh(force = true) },
                    onDismiss = { vm.clearError() }
                )
            }

            // Content
            when (val s = state) {
                is UiState.Loading -> ListShimmerPlaceholder()
                is UiState.Error -> ErrorState(
                    message = s.message,
                    onRetry = { vm.refreshInitial(); vm.resetEndReached() }
                )
                is UiState.Success -> {
                    val data = s.data

                    when {
                        isInitialLoading -> {
                            ListShimmerPlaceholder()
                        }
                        isRefreshingEmpty -> {
                            ListShimmerPlaceholder()
                        }
                        showErrorState -> {
                            ErrorState(
                                message = lastError!!,
                                onRetry = { vm.onPullToRefresh(force = true); vm.resetEndReached() }
                            )
                        }
                        showEmptyState -> {
                            EmptyState(
                                showAllVisible = favoritesOnly,
                                clearSearchVisible = query.isNotBlank(),
                                onShowAll = {
                                    if (favoritesOnly) {
                                        vm.setFavoritesOnly(false)
                                        vm.resetEndReached()
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                },
                                onClearSearch = {
                                    if (query.isNotBlank()) {
                                        vm.setQuery("")
                                        vm.resetEndReached()
                                        scope.launch { listState.scrollToItem(0) }
                                    }
                                }
                            )
                        }
                        else -> {
                            LazyColumn(
                                state = listState,
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                contentPadding = PaddingValues(bottom = 24.dp)
                            ) {
                                items(
                                    items = data,
                                    key = { it.id },
                                    contentType = { "pokemon_item" }
                                ) { p ->
                                    PokemonCard(
                                        name = p.name,
                                        imageUrl = p.imageUrl,
                                        isFavorite = p.isFavorite,
                                        onClick = { onItemClickGated(p.name) },
                                        onToggleFavorite = {
                                            val wantFav = !p.isFavorite
                                            if (!wantFav) {
                                                confirmUnfav = p.name
                                            } else {
                                                vm.toggleFavoriteById(p.id, p.name, p.imageUrl, true)
                                            }
                                        }
                                    )
                                }
                                // Footer
                                item(key = "footer") {
                                    when {
                                        isLoadingMore -> Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        endReached -> Box(
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "Semua data sudah dimuat",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}

/* ---------- Reusable UI ---------- */

@Composable
private fun FavoritesSwitchChips(
    favoritesOnly: Boolean,
    onSelectAll: () -> Unit,
    onSelectFavorites: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilterChip(
            selected = !favoritesOnly,
            onClick = onSelectAll,
            label = { Text("All") }
        )
        FilterChip(
            selected = favoritesOnly,
            onClick = onSelectFavorites,
            label = { Text("Favorites") },
            leadingIcon = {
                Icon(
                    imageVector = if (favoritesOnly) Icons.Rounded.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null
                )
            }
        )
    }
}



