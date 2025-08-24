package com.adeinsoft.pokemonexplorer.domain.model

data class PokemonDetail(
    val name: String,
    val artworkUrl: String?,
    val types: List<String>,
    val weight: Int,
    val height: Int
)
