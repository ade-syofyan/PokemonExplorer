package com.adeinsoft.pokemonexplorer.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adeinsoft.pokemonexplorer.domain.model.PokemonFull
import com.adeinsoft.pokemonexplorer.domain.repo.PokemonRepository
import com.adeinsoft.pokemonexplorer.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repo: PokemonRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<PokemonFull>>(UiState.Loading)
    val uiState: StateFlow<UiState<PokemonFull>> = _uiState

    private val _serverIssue = MutableStateFlow(false)
    val serverIssue: StateFlow<Boolean> = _serverIssue

    private var observeJob: Job? = null

    private val favKey = MutableStateFlow<String?>(null)

    val isFavorite: StateFlow<Boolean> =
        favKey.filterNotNull()
            .flatMapLatest { key -> repo.observeFavorite(key) }
            .stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun load(idOrName: String) {

        viewModelScope.launch {
            if (!repo.hasCachedDetail(idOrName)) {
                _uiState.value = UiState.Loading
            }
        }

        _uiState.value = UiState.Loading
        favKey.value = idOrName

        observeJob?.cancel()
        observeJob = viewModelScope.launch {
            repo.observePokemonFull(idOrName).collectLatest { cached ->
                if (cached != null) {
                    _uiState.value = UiState.Success(cached)
                    favKey.value = cached.id.toString()
                } else {
                    _uiState.value = UiState.Loading
                }
            }
        }
        viewModelScope.launch {
            val result = repo.refreshPokemonFull(idOrName)
            result.onSuccess { _serverIssue.value = false }
            result.onFailure {
                _serverIssue.value = true
                if (_uiState.value is UiState.Loading) {
                    _uiState.value = UiState.Error(it.message ?: "Failed to load", it)
                }
            }
        }
    }

    fun retry(idOrName: String) = load(idOrName)

    suspend fun hasCachedDetail(idOrName: String): Boolean =
        repo.hasCachedDetail(idOrName)

    fun toggleFavorite(nameOrId: String, set: Boolean) = viewModelScope.launch {
        repo.toggleFavorite(nameOrId, set)
    }
}
