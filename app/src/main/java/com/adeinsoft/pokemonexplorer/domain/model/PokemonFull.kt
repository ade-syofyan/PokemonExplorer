package com.adeinsoft.pokemonexplorer.domain.model

data class PokemonFull(
    val id: Int,
    val name: String,
    val imageUrlLarge: String?,
    val types: List<String>,
    val heightM: Double,
    val weightKg: Double,
    val baseExp: Int?,
    val abilities: List<Ability>,
    val stats: List<Stat>,
    val flavorText: String?,
    val evolution: List<EvolutionStage>
) {
    data class Ability(val name: String, val isHidden: Boolean)
    data class Stat(val name: String, val value: Int)
    data class EvolutionStage(val id: Int, val name: String, val imageUrl: String?)
}
