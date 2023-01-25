package com.example.rastreioonibus.presentation.util

import android.net.ConnectivityManager
import android.net.Network
import android.os.Build

class ConnectivityState(private val connectivityManager: ConnectivityManager) {

    fun haveInternetOnInitApp(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager.run {
                this.activeNetwork != null && this.getNetworkCapabilities(this.activeNetwork) != null
            }
        } else {
            val info = connectivityManager.activeNetworkInfo
            info != null && info.isConnected
        }
    }

    fun networkCallback(
        callbackOnAvailable: () -> Unit,
        callbackOnLost: () -> Unit
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {

                override fun onAvailable(network: Network) {
                    super.onAvailable(network)

                    callbackOnAvailable()
                }

                override fun onLost(network: Network) {
                    super.onLost(network)

                    callbackOnLost()
                }
            })
        }
    }
}