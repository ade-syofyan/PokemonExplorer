package com.adeinsoft.pokemonexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_index")
data class PokemonIndexEntity(
    @PrimaryKey val id: Int,
    val name: String
)
