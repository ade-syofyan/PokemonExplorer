package com.adeinsoft.pokemonexplorer.data.repo

import com.adeinsoft.pokemonexplorer.data.local.dao.PokemonDao
import com.adeinsoft.pokemonexplorer.data.local.dao.PokemonDetailDao
import com.adeinsoft.pokemonexplorer.data.mapToPokemonFull
import com.adeinsoft.pokemonexplorer.data.remote.api.PokemonApi
import com.adeinsoft.pokemonexplorer.data.remote.dto.extractPokemonIdFromUrl
import com.adeinsoft.pokemonexplorer.data.remote.dto.toDomain
import com.adeinsoft.pokemonexplorer.data.remote.dto.toEntities
import com.adeinsoft.pokemonexplorer.data.toDetailEntity
import com.adeinsoft.pokemonexplorer.data.toEvolutionEntities
import com.adeinsoft.pokemonexplorer.data.withEvolutionToDomain
import com.adeinsoft.pokemonexplorer.domain.model.Pokemon
import com.adeinsoft.pokemonexplorer.domain.model.PokemonDetail
import com.adeinsoft.pokemonexplorer.domain.model.PokemonFull
import com.adeinsoft.pokemonexplorer.domain.repo.PokemonRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PokemonRepositoryImpl @Inject constructor(
    private val api: PokemonApi,
    private val dao: PokemonDao,
    private val detailDao: PokemonDetailDao,
) : PokemonRepository {

    override fun getPokemonList(): Flow<List<Pokemon>> =
        dao.getAll().map { list -> list.map { it.toDomain() } }

    override suspend fun refreshPokemonList(limit: Int, force: Boolean) {
        try {
            val localCount = dao.count()
            if (!force && localCount > 0) return

            if (force && localCount > 0) {
                dao.clearAll()
            }

            val remote = api.getPokemonList(limit)
            val entities = remote.toEntities()
            if (entities.isNotEmpty()) {
                dao.upsertAll(entities)
            }
            android.util.Log.d("PokemonRepo", "Refreshed ${entities.size} items")
        } catch (t: Throwable) {
            android.util.Log.e("PokemonRepo", "refresh failed", t)
        }
    }

    override fun getPokemonDetail(idOrName: String): Flow<PokemonDetail> = flow {
        val detail = api.getPokemonDetail(idOrName)
        val id = idOrName.toIntOrNull()
            ?: runCatching { extractPokemonIdFromUrl(detail.sprites?.front_default ?: "") }.getOrDefault(0)
        emit(detail.toDomain(explicitId = id))
    }

    override suspend fun loadNextPage(limit: Int): Boolean {
        val offset = dao.count()
        val remote = api.getPokemonPage(offset = offset, limit = limit)
        val entities = remote.toEntities()
        if (entities.isEmpty()) return false
        dao.upsertAll(entities)
        return true
    }

    override fun searchPokemon(query: String, limit: Int): Flow<List<Pokemon>> = flow {

        if (dao.indexCount() == 0) {
            val page = api.getPokemonPage(offset = 0, limit = 2000)
            val idx = page.results.mapNotNull { item ->
                val id = item.url
                    .trimEnd('/')
                    .substringAfterLast("pokemon/")
                    .toIntOrNull()
                id?.let { com.adeinsoft.pokemonexplorer.data.local.entity.PokemonIndexEntity(it, item.name) }
            }
            if (idx.isNotEmpty()) dao.upsertIndex(idx)
        }

        emitAll(
            dao.searchIndex(query, limit).map { rows ->
                rows.map { r ->
                    Pokemon(
                        id = r.id,
                        name = r.name,
                        imageUrl = com.adeinsoft.pokemonexplorer.data.spriteUrlFor(r.id),
                        isFavorite = r.isFavorite == 1
                    )
                }
            }
        )
    }

    override suspend fun toggleFavoriteEnsure(
        id: Int,
        name: String,
        imageUrl: String?,
        favorite: Boolean
    ) {
        dao.ensureAndSetFavorite(id, name, imageUrl, favorite)
    }

    override suspend fun getPokemonFull(idOrName: String): PokemonFull = coroutineScope {
        val d = async { api.getPokemonDetail(idOrName) }
        val s = async { api.getSpecies(idOrName) }
        val detail = d.await()
        val species = s.await()
        val evo = species.evolution_chain?.url?.let { api.getEvolutionChain(it) }
        mapToPokemonFull(detail, species, evo)
    }

    override fun observePokemonFull(idOrName: String): Flow<PokemonFull?> {
        val id = idOrName.toIntOrNull()

        return if (id != null) {
            detailDao.observeById(id).map { it?.let { r -> r.detail.withEvolutionToDomain(r.evolution) } }
        } else {
            detailDao.observeByName(idOrName).map { it?.let { r -> r.detail.withEvolutionToDomain(r.evolution) } }
        }
    }

    override suspend fun refreshPokemonFull(idOrName: String): Result<Unit> = withContext(
        Dispatchers.IO) {
        runCatching {
            val detail = api.getPokemonDetail(idOrName)
            val speciesIdFromUrl = detail.species?.url?.let { extractPokemonIdFromUrl(it) }
            val species = when {
                speciesIdFromUrl != null && speciesIdFromUrl > 0 ->
                    api.getSpecies(speciesIdFromUrl.toString())
                detail.species?.name != null ->
                    api.getSpecies(detail.species.name)
                else ->
                    api.getSpecies(detail.name) // fallback terakhir (jarang kepakai)
            }
            val evo = species.evolution_chain?.url?.let { api.getEvolutionChain(it) }

            val full = mapToPokemonFull(detail, species, evo)
            val now = System.currentTimeMillis()

            detailDao.upsertDetail(full.toDetailEntity(now))
            detailDao.deleteEvolutionFor(full.id)
            detailDao.upsertEvolution(full.toEvolutionEntities())
        }
    }

    override suspend fun hasCachedDetail(idOrName: String): Boolean {
        val id = idOrName.toIntOrNull()
        return if (id != null) {
            detailDao.getById(id) != null
        } else {
            detailDao.getByName(idOrName) != null
        }
    }

    override fun observeFavorite(idOrName: String): Flow<Boolean> {
        val id = idOrName.toIntOrNull()
        return if (id != null) {
            dao.observeFavoriteById(id)
        } else {
            dao.observeFavoriteByName(idOrName)
        }.map { it ?: false }
    }

    override suspend fun toggleFavorite(idOrName: String, favorite: Boolean) {
        val id = idOrName.toIntOrNull()
        if (id != null) {
            dao.setFavoriteById(id, favorite)
        } else {
            dao.setFavoriteByName(idOrName, favorite)
        }
    }

}
