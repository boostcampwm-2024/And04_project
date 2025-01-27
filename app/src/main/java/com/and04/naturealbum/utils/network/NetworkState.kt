package com.and04.naturealbum.utils.network

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.and04.naturealbum.NatureAlbum

object NetworkState {
    const val CONNECTED_WIFI = 2
    const val CONNECTED_DATA = 1
    const val DISCONNECTED = 0
    private val connectivityManager =
        NatureAlbum.getInstance().getSystemService(ConnectivityManager::class.java)

    fun getNetWorkCode(): Int {
        val currentNetwork = connectivityManager.activeNetwork

        val caps = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return DISCONNECTED

        return when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> CONNECTED_WIFI
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> CONNECTED_DATA
            else -> DISCONNECTED
        }
    }
}
