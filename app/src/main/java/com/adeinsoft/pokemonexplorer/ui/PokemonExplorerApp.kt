// ui/PokemonExplorerApp.kt
package com.adeinsoft.pokemonexplorer.ui

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.adeinsoft.pokemonexplorer.ui.theme.PokemonExplorerTheme
import com.adeinsoft.pokemonexplorer.navigation.AppNavGraph

@Composable
fun PokemonExplorerApp() {
    var darkTheme by rememberSaveable { mutableStateOf(false) }
    val navController = rememberNavController()

    PokemonExplorerTheme(darkTheme = darkTheme) {
        AppNavGraph(
            navController = navController,
            isDark = darkTheme,
            onToggleTheme = { darkTheme = !darkTheme }
        )
    }
}
