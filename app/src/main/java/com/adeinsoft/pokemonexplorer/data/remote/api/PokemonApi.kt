package com.adeinsoft.pokemonexplorer.data.remote.api

import com.adeinsoft.pokemonexplorer.data.remote.dto.EvolutionChainDto
import com.adeinsoft.pokemonexplorer.data.remote.dto.PokemonDetailDto
import com.adeinsoft.pokemonexplorer.data.remote.dto.PokemonListResponseDto
import com.adeinsoft.pokemonexplorer.data.remote.dto.PokemonSpeciesDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface PokemonApi {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int = 10
    ): PokemonListResponseDto

    @GET("pokemon/{idOrName}")
    suspend fun getPokemonDetail(
        @Path("idOrName") idOrName: String
    ): PokemonDetailDto

    @GET("pokemon-species/{idOrName}")
    suspend fun getSpecies(@Path("idOrName") idOrName: String): PokemonSpeciesDto

    @GET
    suspend fun getEvolutionChain(@Url url: String): EvolutionChainDto

    @GET("pokemon")
    suspend fun getPokemonPage(
        @Query("offset") offset: Int,
        @Query("limit") limit: Int = 10
    ): PokemonListResponseDto
}
