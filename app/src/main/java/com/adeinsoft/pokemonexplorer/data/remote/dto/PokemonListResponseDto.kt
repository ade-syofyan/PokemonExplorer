package com.adeinsoft.pokemonexplorer.data.remote.dto

data class PokemonListResponseDto(
    val results: List<PokemonListItemDto> = emptyList()
)

data class PokemonListItemDto(
    val name: String,
    val url: String
)
