package com.adeinsoft.pokemonexplorer.di

import com.adeinsoft.pokemonexplorer.data.repo.PokemonRepositoryImpl
import com.adeinsoft.pokemonexplorer.domain.repo.PokemonRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPokemonRepository(
        impl: PokemonRepositoryImpl
    ): PokemonRepository
}

