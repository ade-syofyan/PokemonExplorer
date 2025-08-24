package com.adeinsoft.pokemonexplorer.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument

object NavRoutes {
    const val LIST = "list"
    const val DETAIL_ROUTE = "detail/{idOrName}"

    fun detail(idOrName: String) = "detail/$idOrName"

}
