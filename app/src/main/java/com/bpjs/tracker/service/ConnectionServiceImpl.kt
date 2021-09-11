package com.bpjs.tracker.service

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.bpjs.tracker.constant.ConnectionType

class ConnectionServiceImpl : ConnectionService {

    override fun getConnectionType(context: Context): Int {
        var result = ConnectionType.NO_CONNECTION // Returns connection type. 0: none; 1: mobile data; 2: wifi
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        cm?.run {
            cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                when {
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        result = ConnectionType.CELLULAR
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        result = ConnectionType.WIFI
                    }
                    hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> {
                        result = ConnectionType.VPN
                    }
                }
            }
        }
        return result
    }
}