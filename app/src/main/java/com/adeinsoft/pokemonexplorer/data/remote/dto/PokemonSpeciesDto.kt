package com.adeinsoft.pokemonexplorer.data.remote.dto

data class PokemonSpeciesDto(
    val flavor_text_entries: List<FlavorTextEntry> = emptyList(),
    val evolution_chain: ApiResourceUrl? = null
) {
    data class FlavorTextEntry(
        val flavor_text: String,
        val language: NamedResource
    )
    data class NamedResource(
        val name: String,
        val url: String
    )
    data class ApiResourceUrl(val url: String)
}
