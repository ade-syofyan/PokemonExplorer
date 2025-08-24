package com.adeinsoft.pokemonexplorer.ui.components

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.content.Context

fun isOfflineNow(ctx: Context): Boolean {
    val cm = ctx.getSystemService(ConnectivityManager::class.java) ?: return false
    val active = cm.activeNetwork ?: return true
    val caps = cm.getNetworkCapabilities(active) ?: return true
    return !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}
