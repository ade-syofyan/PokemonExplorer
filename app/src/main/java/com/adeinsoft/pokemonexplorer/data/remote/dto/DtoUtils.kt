package com.adeinsoft.pokemonexplorer.data.remote.dto

internal fun extractPokemonIdFromUrl(url: String): Int =
    url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: 0

internal fun spriteUrl(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

internal fun artworkUrl(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
