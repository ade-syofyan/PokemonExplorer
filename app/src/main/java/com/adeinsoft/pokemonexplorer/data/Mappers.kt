package com.adeinsoft.pokemonexplorer.data

import com.adeinsoft.pokemonexplorer.data.local.entity.EvolutionStageEntity
import com.adeinsoft.pokemonexplorer.data.local.entity.PokemonDetailEntity
import com.adeinsoft.pokemonexplorer.data.remote.dto.EvolutionChainDto
import com.adeinsoft.pokemonexplorer.data.remote.dto.PokemonDetailDto
import com.adeinsoft.pokemonexplorer.data.remote.dto.PokemonSpeciesDto
import com.adeinsoft.pokemonexplorer.domain.model.PokemonFull
import kotlin.math.round

fun extractIdFromUrl(url: String?): Int? =
    url?.trimEnd('/')?.substringAfterLast('/')?.toIntOrNull()

fun officialArtworkUrl(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"

fun spriteUrlFor(id: Int): String =
    "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/$id.png"

private fun cmToMeter(cm: Int): Double = round((cm / 10.0) * 100) / 100.0
private fun hgToKg(hg: Int): Double = round((hg / 10.0) * 100) / 100.0

fun mapToPokemonFull(
    d: PokemonDetailDto,
    s: PokemonSpeciesDto?,
    e: EvolutionChainDto?
): PokemonFull {
    val id = d.id
    val name = d.name
    val imageLarge = d.sprites?.other?.`official-artwork`?.front_default
        ?: officialArtworkUrl(id)

    val types = d.types.map { it.type.name }

    val abilities = d.abilities.map {
        PokemonFull.Ability(
            name = it.ability?.name ?: "",
            isHidden = it.is_hidden == true
        )
    }

    val stats = d.stats.map {
        PokemonFull.Stat(
            name = it.stat?.name ?: "",
            value = it.base_stat ?: 0
        )
    }

    val flavor = s?.flavor_text_entries
        ?.firstOrNull { it.language.name == "en" }
        ?.flavor_text
        ?.replace('\n', ' ')
        ?.replace('\u000c', ' ')
        ?.trim()

    val evoList: List<PokemonFull.EvolutionStage> =
        e?.let { flattenEvolutionChain(it) }.orEmpty()

    return PokemonFull(
        id = id,
        name = name,
        imageUrlLarge = imageLarge,
        types = types,
        heightM = cmToMeter(d.height),
        weightKg = hgToKg(d.weight),
        baseExp = d.base_experience,
        abilities = abilities,
        stats = stats,
        flavorText = flavor,
        evolution = evoList
    )
}

private fun flattenEvolutionChain(evo: EvolutionChainDto): List<PokemonFull.EvolutionStage> {
    val out = mutableListOf<PokemonFull.EvolutionStage>()
    fun walk(node: EvolutionChainDto.ChainLink) {
        val id = extractIdFromUrl(node.species.url) ?: return
        val img = spriteUrlFor(id)
        out += PokemonFull.EvolutionStage(id, node.species.name, img)
        node.evolves_to.forEach { walk(it) }
    }
    walk(evo.chain)
    return out
}

private fun List<String>.toCsvString(): String = joinToString(",")

private fun String.csvToList(): List<String> =
    if (isBlank()) emptyList() else split(',').map { it.trim() }

private fun List<PokemonFull.Ability>.toCsvAbility(): String =
    joinToString(",") { if (it.isHidden) "${it.name}(h)" else it.name }

private fun String.csvToAbilities(): List<PokemonFull.Ability> =
    csvToList().map {
        val hidden = it.endsWith("(h)")
        PokemonFull.Ability(name = it.removeSuffix("(h)"), isHidden = hidden)
    }

private fun List<PokemonFull.Stat>.toCsvStat(): String =
    joinToString(",") { "${it.name}:${it.value}" }

private fun String.csvToStats(): List<PokemonFull.Stat> =
    csvToList().mapNotNull {
        val parts = it.split(":")
        if (parts.size == 2) PokemonFull.Stat(parts[0], parts[1].toIntOrNull() ?: 0) else null
    }

/* ----- Domain -> Entities ----- */
fun PokemonFull.toDetailEntity(now: Long) = PokemonDetailEntity(
    id = id,
    name = name,
    imageUrlLarge = imageUrlLarge,
    typesCsv = types.toCsvString(),      // ← was toCsv()
    heightM = heightM,
    weightKg = weightKg,
    baseExp = baseExp,
    abilitiesCsv = abilities.toCsvAbility(), // ← was toCsv()
    statsCsv = stats.toCsvStat(),        // ← was toCsv()
    flavorText = flavorText,
    updatedAt = now
)

fun PokemonFull.toEvolutionEntities(): List<EvolutionStageEntity> =
    evolution.mapIndexed { index, e ->
        EvolutionStageEntity(
            parentId = id,
            orderIndex = index,
            stageId = e.id,
            stageName = e.name,
            imageUrl = e.imageUrl
        )
    }

/* ----- Entities -> Domain ----- */
fun PokemonDetailEntity.withEvolutionToDomain(
    evo: List<EvolutionStageEntity>
): PokemonFull = PokemonFull(
    id = id,
    name = name,
    imageUrlLarge = imageUrlLarge,
    types = typesCsv.csvToList(),
    heightM = heightM,
    weightKg = weightKg,
    baseExp = baseExp,
    abilities = abilitiesCsv.csvToAbilities(),
    stats = statsCsv.csvToStats(),
    flavorText = flavorText,
    evolution = evo.sortedBy { it.orderIndex }.map {
        PokemonFull.EvolutionStage(it.stageId, it.stageName, it.imageUrl)
    }
)

