package com.adeinsoft.pokemonexplorer.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.adeinsoft.pokemonexplorer.ui.detail.DetailScreen
import com.adeinsoft.pokemonexplorer.ui.detail.PokemonDetailViewModel
import com.adeinsoft.pokemonexplorer.ui.list.ListScreen
import com.adeinsoft.pokemonexplorer.ui.list.PokemonListViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDark: Boolean,
    onToggleTheme: () -> Unit
) {
    NavHost(navController = navController, startDestination = NavRoutes.LIST) {

        // LIST
        composable(NavRoutes.LIST) { backStackEntry ->
            val vm = hiltViewModel<PokemonListViewModel>(backStackEntry)
            ListScreen(
                onItemClick = { idOrName ->
                    navController.navigate(NavRoutes.detail(idOrName))
                },
                isDark = isDark,
                onToggleTheme = onToggleTheme,
                vm = vm
            )
        }

        // DETAIL/{idOrName}?jump={jump}
        composable(
            route = "detail/{idOrName}?jump={jump}",
            arguments = listOf(
                navArgument("idOrName") { type = NavType.StringType },
                navArgument("jump") { type = NavType.StringType; nullable = true }
            )
        ) { backStack ->
            var vm = hiltViewModel<PokemonDetailViewModel>(backStack)
            val idOrName = checkNotNull(backStack.arguments?.getString("idOrName"))
            val jump = backStack.arguments?.getString("jump")

            DetailScreen(
                idOrName = idOrName,
                jump = jump,
                isDark = isDark,
                onToggleTheme = onToggleTheme,
                onBack = { navController.popBackStack() },
                vm = vm
            )
        }
    }
}
