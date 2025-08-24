package com.adeinsoft.pokemonexplorer.domain.repo

import com.adeinsoft.pokemonexplorer.domain.model.Pokemon
import com.adeinsoft.pokemonexplorer.domain.model.PokemonDetail
import com.adeinsoft.pokemonexplorer.domain.model.PokemonFull
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    fun getPokemonList(): Flow<List<Pokemon>>
    suspend fun refreshPokemonList(limit: Int = 50, force: Boolean = false)
    fun getPokemonDetail(idOrName: String): Flow<PokemonDetail>
    suspend fun toggleFavorite(idOrName: String, favorite: Boolean)
    suspend fun loadNextPage(limit: Int = 10): Boolean
    fun searchPokemon(query: String, limit: Int = 50): Flow<List<Pokemon>>
    suspend fun toggleFavoriteEnsure(id: Int, name: String, imageUrl: String?, favorite: Boolean)
    suspend fun getPokemonFull(idOrName: String): PokemonFull
    fun observePokemonFull(idOrName: String): Flow<PokemonFull?>
    suspend fun refreshPokemonFull(idOrName: String): Result<Unit>
    suspend fun hasCachedDetail(idOrName: String): Boolean
    fun observeFavorite(idOrName: String): Flow<Boolean>
}
