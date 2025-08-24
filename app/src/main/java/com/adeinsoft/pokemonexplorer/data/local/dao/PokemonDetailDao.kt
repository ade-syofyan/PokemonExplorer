package com.adeinsoft.pokemonexplorer.data.local.dao

import androidx.room.*
import com.adeinsoft.pokemonexplorer.data.local.entity.EvolutionStageEntity
import com.adeinsoft.pokemonexplorer.data.local.entity.PokemonDetailEntity
import kotlinx.coroutines.flow.Flow

data class PokemonDetailWithEvolution(
    @Embedded val detail: PokemonDetailEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId",
        entity = EvolutionStageEntity::class
    )
    val evolution: List<EvolutionStageEntity>
)

@Dao
interface PokemonDetailDao {
    @Transaction
    @Query("SELECT * FROM pokemon_detail WHERE id = :id LIMIT 1")
    fun observeById(id: Int): Flow<PokemonDetailWithEvolution?>

    @Transaction
    @Query("SELECT * FROM pokemon_detail WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    fun observeByName(name: String): Flow<PokemonDetailWithEvolution?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDetail(entity: PokemonDetailEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEvolution(list: List<EvolutionStageEntity>)

    @Query("DELETE FROM evolution_stage WHERE parentId = :parentId")
    suspend fun deleteEvolutionFor(parentId: Int)

    @Query("SELECT * FROM pokemon_detail WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): PokemonDetailEntity?

    @Query("SELECT * FROM pokemon_detail WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    suspend fun getByName(name: String): PokemonDetailEntity?
}
