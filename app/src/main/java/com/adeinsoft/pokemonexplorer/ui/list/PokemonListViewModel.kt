package com.adeinsoft.pokemonexplorer.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adeinsoft.pokemonexplorer.domain.model.Pokemon
import com.adeinsoft.pokemonexplorer.domain.repo.PokemonRepository
import com.adeinsoft.pokemonexplorer.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val PAGE_SIZE = 10


@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repo: PokemonRepository
) : ViewModel() {

    private val query = MutableStateFlow("")
    val queryState: StateFlow<String> = query.asStateFlow()

    private val favoritesOnly = MutableStateFlow(false)
    val favoritesOnlyState: StateFlow<Boolean> = favoritesOnly.asStateFlow()

    // paging flags
    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    private val _endReached = MutableStateFlow(false)
    val endReached: StateFlow<Boolean> = _endReached.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError

    fun clearError() { _lastError.value = null }

    fun setQuery(q: String) { query.value = q }
    fun setFavoritesOnly(enabled: Boolean) { favoritesOnly.value = enabled }

    fun onPullToRefresh(force: Boolean = true, limit: Int = PAGE_SIZE) {
        viewModelScope.launch {
            _isRefreshing.value = true
            _lastError.value = null
            val r = runCatching { repo.refreshPokemonList(limit = limit, force = force) }
            _isRefreshing.value = false
            _endReached.value = false
            r.onFailure { e -> _lastError.value = e.message ?: "Gagal memuat ulang" }
        }
    }

    fun refreshInitial() {
        viewModelScope.launch {
            _lastError.value = null
            runCatching { repo.refreshPokemonList(limit = PAGE_SIZE, force = false) }
                .onFailure { e -> _lastError.value = e.message ?: "Gagal memuat awal" }
        }
    }


    init {
        refreshInitial()
    }

    @OptIn(FlowPreview::class)
    private val baseStream: Flow<List<Pokemon>> =
        repo.getPokemonList()

    @OptIn(FlowPreview::class)
    private val searchStream: Flow<List<Pokemon>> =
        query.debounce(250)
            .flatMapLatest { q ->
                if (q.isBlank()) baseStream
                else repo.searchPokemon(q, limit = 50)
            }

    @OptIn(FlowPreview::class)
    val uiState: StateFlow<UiState<List<Pokemon>>> =
        combine(
            searchStream,
            favoritesOnly
        ) { list, favOnly ->
            val filtered = if (favOnly) list.filter { it.isFavorite } else list
            UiState.Success(filtered) as UiState<List<Pokemon>>
        }
            .onStart { emit(UiState.Loading) }
            .catch { e -> emit(UiState.Error(e.message ?: "Unknown error", e)) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Loading)
    fun toggleFavorite(idOrName: String, favorite: Boolean) {
        viewModelScope.launch { runCatching { repo.toggleFavorite(idOrName, favorite) } }
    }

    fun loadMore() {
        if (_isLoadingMore.value || _endReached.value) return
        viewModelScope.launch {
            _isLoadingMore.value = true
            val loaded = runCatching { repo.loadNextPage(limit = PAGE_SIZE) }.getOrDefault(false)
            if (!loaded) _endReached.value = true
            _isLoadingMore.value = false
        }
    }

    fun resetEndReached() { _endReached.value = false }

    fun toggleFavoriteById(id: Int, name: String, imageUrl: String?, favorite: Boolean) {
        viewModelScope.launch {
            runCatching { repo.toggleFavoriteEnsure(id, name, imageUrl, favorite) }
                .onFailure { android.util.Log.e("PokemonVM", "toggleFavoriteEnsure error", it) }
        }
    }
    suspend fun hasCachedDetail(idOrName: String): Boolean = repo.hasCachedDetail(idOrName)
}
