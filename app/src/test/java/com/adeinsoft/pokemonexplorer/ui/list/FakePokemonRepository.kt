package com.adeinsoft.pokemonexplorer.ui.list

import com.adeinsoft.pokemonexplorer.domain.model.Pokemon
import com.adeinsoft.pokemonexplorer.domain.model.PokemonDetail
import com.adeinsoft.pokemonexplorer.domain.model.PokemonFull
import com.adeinsoft.pokemonexplorer.domain.repo.PokemonRepository
import kotlinx.coroutines.flow.*

class FakePokemonRepository(init: List<Pokemon> = emptyList()) : PokemonRepository {

    private val data = MutableStateFlow(init)

    override fun getPokemonList(): Flow<List<Pokemon>> = data

    override suspend fun refreshPokemonList(limit: Int, force: Boolean) {}

    override fun getPokemonDetail(idOrName: String): Flow<PokemonDetail> =
        flowOf(PokemonDetail(idOrName, null, emptyList(), 0, 0))

    override suspend fun toggleFavorite(idOrName: String, favorite: Boolean) {
        data.value = data.value.map {
            if (it.name.equals(idOrName, ignoreCase = true) || it.id.toString() == idOrName) {
                it.copy(isFavorite = favorite)
            } else it
        }
    }

    override suspend fun toggleFavoriteEnsure(
        id: Int,
        name: String,
        imageUrl: String?,
        favorite: Boolean
    ) {
        val current = data.value.toMutableList()
        val idx = current.indexOfFirst { it.id == id || it.name.equals(name, ignoreCase = true) }
        if (idx >= 0) {
            current[idx] = current[idx].copy(
                id = id,
                name = name,
                imageUrl = imageUrl ?: current[idx].imageUrl,
                isFavorite = favorite
            )
        } else {
            current += Pokemon(
                id = id,
                name = name,
                imageUrl = imageUrl,
                isFavorite = favorite
            )
        }
        data.value = current
    }

    override fun observeFavorite(idOrName: String): Flow<Boolean> {
        val asId = idOrName.toIntOrNull()
        return data.map { list ->
            val p = if (asId != null) {
                list.firstOrNull { it.id == asId }
            } else {
                list.firstOrNull { it.name.equals(idOrName, ignoreCase = true) }
            }
            p?.isFavorite ?: false
        }.distinctUntilChanged()
    }

    // ---- Paging ----
    override suspend fun loadNextPage(limit: Int): Boolean {
        return false
    }

    // ---- Search ----
    override fun searchPokemon(
        query: String,
        limit: Int
    ): Flow<List<Pokemon>> {
        val q = query.trim()
        if (q.isEmpty()) return data.map { it.take(limit) }

        return data.map { list ->
            list.asSequence()
                .filter { p ->
                    p.name.contains(q, ignoreCase = true) ||
                            p.id.toString() == q
                }
                .take(limit)
                .toList()
        }
    }

    override suspend fun getPokemonFull(idOrName: String): PokemonFull {
        throw UnsupportedOperationException("FakePokemonRepository.getPokemonFull() not supported")
    }

    override fun observePokemonFull(idOrName: String): Flow<PokemonFull?> =
        flowOf(null)

    override suspend fun refreshPokemonFull(idOrName: String): Result<Unit> =
        Result.success(Unit)

    override suspend fun hasCachedDetail(idOrName: String): Boolean = false
}
