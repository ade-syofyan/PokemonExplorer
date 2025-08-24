package com.adeinsoft.pokemonexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.adeinsoft.pokemonexplorer.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {

    @Query("SELECT COUNT(*) FROM pokemon")
    suspend fun count(): Int
    @Query("DELETE FROM pokemon")
    suspend fun clearAll()
    @Query("SELECT * FROM pokemon ORDER BY id ASC")
    fun getAll(): Flow<List<PokemonEntity>>
    @Query("SELECT * FROM pokemon WHERE name LIKE '%' || :q || '%' ORDER BY id ASC")
    fun search(q: String): Flow<List<PokemonEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(list: List<PokemonEntity>)
    @Query("UPDATE pokemon SET isFavorite = :favorite WHERE id = :id ")
    suspend fun setFavorite(id: Int, favorite: Boolean)
    @Query("UPDATE pokemon SET isFavorite = :favorite WHERE LOWER(name) = LOWER(:name)")
    suspend fun setFavoriteByName(name: String, favorite: Boolean): Int

    data class SearchJoinRow(
        val id: Int,
        val name: String,
        val isFavorite: Int
    )

    // --- INDEX API ---
    @androidx.room.Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun upsertIndex(all: List<com.adeinsoft.pokemonexplorer.data.local.entity.PokemonIndexEntity>)

    @androidx.room.Query("SELECT COUNT(*) FROM pokemon_index")
    suspend fun indexCount(): Int

    @androidx.room.Query("""
        SELECT pi.id AS id,
               pi.name AS name,
               CASE WHEN p.isFavorite = 1 THEN 1 ELSE 0 END AS isFavorite
        FROM pokemon_index pi
        LEFT JOIN pokemon p ON p.id = pi.id
        WHERE pi.name LIKE '%' || :q || '%'
        ORDER BY pi.id
        LIMIT :limit
    """)
    fun searchIndex(q: String, limit: Int): Flow<List<SearchJoinRow>>

    @Query("SELECT COUNT(*) FROM pokemon WHERE id = :id")
    suspend fun existsById(id: Int): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertIgnore(entity: PokemonEntity)

    @Query("UPDATE pokemon SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavoriteById(id: Int, favorite: Boolean)

    @Transaction
    suspend fun ensureAndSetFavorite(
        id: Int,
        name: String,
        imageUrl: String?,
        favorite: Boolean
    ) {
        if (existsById(id) == 0) {
            insertIgnore(
                PokemonEntity(
                    id = id,
                    name = name,
                    imageUrl = imageUrl,
                    isFavorite = false
                )
            )
        }
        setFavoriteById(id, favorite)
    }

    @Query("SELECT isFavorite FROM pokemon WHERE id = :id LIMIT 1")
    fun observeFavoriteById(id: Int): Flow<Boolean?>

    @Query("SELECT isFavorite FROM pokemon WHERE LOWER(name) = LOWER(:name) LIMIT 1")
    fun observeFavoriteByName(name: String): Flow<Boolean?>

}
