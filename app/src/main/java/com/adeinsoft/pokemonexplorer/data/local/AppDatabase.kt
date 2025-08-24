package com.adeinsoft.pokemonexplorer.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.adeinsoft.pokemonexplorer.data.local.dao.PokemonDao
import com.adeinsoft.pokemonexplorer.data.local.dao.PokemonDetailDao
import com.adeinsoft.pokemonexplorer.data.local.entity.EvolutionStageEntity
import com.adeinsoft.pokemonexplorer.data.local.entity.PokemonDetailEntity
import com.adeinsoft.pokemonexplorer.data.local.entity.PokemonEntity
import com.adeinsoft.pokemonexplorer.data.local.entity.PokemonIndexEntity

@Database(
    entities = [
        PokemonEntity::class,
        PokemonIndexEntity::class,
        PokemonDetailEntity::class,
        EvolutionStageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pokemonDao(): PokemonDao
    abstract fun pokemonDetailDao(): PokemonDetailDao
}
