package com.adeinsoft.pokemonexplorer.data.remote.dto

data class EvolutionChainDto(
    val id: Int,
    val chain: ChainLink
) {
    data class ChainLink(
        val species: NamedResource,
        val evolves_to: List<ChainLink> = emptyList(),
        val evolution_details: List<EvolutionDetail> = emptyList()
    )
    data class NamedResource(val name: String, val url: String)
    data class EvolutionDetail(
        val min_level: Int? = null,
        val trigger: NamedResource? = null,
        val item: NamedResource? = null
    )
}
