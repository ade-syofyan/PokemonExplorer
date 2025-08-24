package com.adeinsoft.pokemonexplorer.data.remote.dto

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val sprites: SpritesDto? = null,
    val types: List<TypeSlotDto> = emptyList(),
    val abilities: List<AbilitySlotDto> = emptyList(),
    val stats: List<StatDto> = emptyList(),
    val base_experience: Int? = null,
    val weight: Int = 0,
    val height: Int = 0,
    val species: NamedApiResourceDto? = null
)

data class SpritesDto(
    val front_default: String? = null,
    val other: OtherSpritesDto? = null
)

data class OtherSpritesDto(
    val `official-artwork`: OfficialArtworkDto? = null
)

data class OfficialArtworkDto(
    val front_default: String? = null
)

data class TypeSlotDto(
    val slot: Int,
    val type: NamedApiResourceDto
)

data class AbilitySlotDto(
    val ability: NamedApiResourceDto? = null,
    val is_hidden: Boolean? = null
)

data class StatDto(
    val base_stat: Int? = null,
    val stat: NamedApiResourceDto? = null
)

data class NamedApiResourceDto(
    val name: String,
    val url: String
)
