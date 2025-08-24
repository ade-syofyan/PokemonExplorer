package com.adeinsoft.pokemonexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imageUrlLarge: String?,
    val typesCsv: String,
    val heightM: Double,
    val weightKg: Double,
    val baseExp: Int?,
    val abilitiesCsv: String,
    val statsCsv: String,
    val flavorText: String?,
    val updatedAt: Long
)

@Entity(tableName = "evolution_stage")
data class EvolutionStageEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val parentId: Int,
    val orderIndex: Int,
    val stageId: Int,
    val stageName: String,
    val imageUrl: String?
)
