package com.and04.naturealbum.utils.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NetworkManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _networkState = MutableStateFlow(NetworkState.DISCONNECTED)
    val networkState: StateFlow<Int> = _networkState

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: android.net.Network) {
            super.onAvailable(network)
            updateNetworkState()
        }

        override fun onLost(network: android.net.Network) {
            super.onLost(network)
            _networkState.value = NetworkState.DISCONNECTED
        }

        override fun onCapabilitiesChanged(
            network: android.net.Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            updateNetworkState()
        }
    }

    init {
        updateNetworkState() // 초기 상태 업데이트
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    private fun updateNetworkState() {
        val currentNetwork = connectivityManager.activeNetwork
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)

        _networkState.value = when {
            currentNetwork == null || caps == null -> {
                NetworkState.DISCONNECTED
            }

            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                NetworkState.CONNECTED_WIFI
            }

            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                NetworkState.CONNECTED_DATA
            }

            else -> {
                NetworkState.DISCONNECTED
            }
        }
    }
}
