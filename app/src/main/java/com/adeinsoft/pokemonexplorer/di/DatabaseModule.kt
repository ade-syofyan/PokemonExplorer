package com.adeinsoft.pokemonexplorer.di

import android.content.Context
import androidx.room.Room
import com.adeinsoft.pokemonexplorer.data.local.AppDatabase
import com.adeinsoft.pokemonexplorer.data.local.dao.PokemonDao
import com.adeinsoft.pokemonexplorer.data.local.dao.PokemonDetailDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides @Singleton
    fun provideDb(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "pokemon.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePokemonDao(db: AppDatabase): PokemonDao = db.pokemonDao()

    @Provides
    fun providePokemonDetailDao(db: AppDatabase): PokemonDetailDao = db.pokemonDetailDao()
}
