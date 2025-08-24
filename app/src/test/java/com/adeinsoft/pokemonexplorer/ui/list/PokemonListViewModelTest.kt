package com.adeinsoft.pokemonexplorer.ui.list

import app.cash.turbine.test
import com.adeinsoft.pokemonexplorer.MainDispatcherRule
import com.adeinsoft.pokemonexplorer.domain.model.Pokemon
import com.adeinsoft.pokemonexplorer.ui.state.UiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PokemonListViewModelTest {

    @get:Rule val mainRule = MainDispatcherRule()
    private fun seed() = listOf(
        Pokemon(1, "bulbasaur", null, false),
        Pokemon(2, "ivysaur", null, false),
        Pokemon(3, "venusaur", null, false),
        Pokemon(4, "charmander", null, false),
        Pokemon(5, "charmeleon", null, false)
    )

    @Test
    fun initial_emits_loading_then_all_data() = runTest {
        val vm = PokemonListViewModel(FakePokemonRepository(seed()))
        vm.uiState.test {
            assert(awaitItem() is UiState.Loading)
            val success = awaitItem() as UiState.Success
            assertEquals(5, success.data.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun search_filters_by_name() = runTest {
        val vm = PokemonListViewModel(FakePokemonRepository(seed()))
        vm.uiState.test {
            awaitItem() // Loading
            awaitItem() // Initial success (5)

            vm.setQuery("char")
            advanceTimeBy(300) // debounce 250ms di VM

            val filtered = awaitItem() as UiState.Success
            assertEquals(listOf("charmander", "charmeleon"), filtered.data.map { it.name })
            cancelAndIgnoreRemainingEvents()
        }
    }
}
