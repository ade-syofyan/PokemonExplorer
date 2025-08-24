package com.adeinsoft.pokemonexplorer.data.remote.dto

import com.adeinsoft.pokemonexplorer.data.local.entity.PokemonEntity
import com.adeinsoft.pokemonexplorer.domain.model.Pokemon
import com.adeinsoft.pokemonexplorer.domain.model.PokemonDetail

/* List endpoint -> Entities */
fun PokemonListResponseDto.toEntities(): List<PokemonEntity> =
    results.map { item ->
        val id = extractPokemonIdFromUrl(item.url)
        PokemonEntity(
            id = id,
            name = item.name,
            imageUrl = spriteUrl(id),
            isFavorite = false
        )
    }

/* Entity -> Domain (list item) */
fun PokemonEntity.toDomain(): Pokemon =
    Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl,
        isFavorite = isFavorite
    )

/* Detail DTO -> Domain detail */
fun PokemonDetailDto.toDomain(explicitId: Int? = null): PokemonDetail {
    val id = explicitId ?: 0
    val types = types.sortedBy { it.slot }.map { it.type.name }
    val artwork = sprites?.other?.`official-artwork`?.front_default ?: artworkUrl(id)
    return PokemonDetail(
        name = name,
        artworkUrl = artwork,
        types = types,
        weight = weight,
        height = height
    )
}
