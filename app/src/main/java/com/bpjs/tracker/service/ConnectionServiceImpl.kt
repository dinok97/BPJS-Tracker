package com.bpjs.tracker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.util.Log
import com.bpjs.tracker.constant.ConnectionType
import com.bpjs.tracker.data.NetworkInfoWifi
import com.bpjs.tracker.database.NetworkDatabase

class ConnectionServiceImpl : ConnectionService {

    override fun getConnectionType(context: Context): Int {
        var result = ConnectionType.NO_CONNECTION
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

    override fun getConnectionBssid(context: Context): String {
        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wm.connectionInfo.bssid
    }

    @Suppress("DEPRECATION")
    override fun scanNetwork(context: Context) {

        val db = NetworkDatabase.getDatabase(context)

        val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val wifiScanReceiver: BroadcastReceiver
        wifiScanReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                if (success) {
                    scanSuccess(wm, db)
                    context.unregisterReceiver(this)
                } else {
                    scanFailure()
                }
            }
        }

        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(wifiScanReceiver, intentFilter)

        val success = wm.startScan()
        if (!success) {
            scanFailure()
        }
    }

    fun scanSuccess(wm: WifiManager, db: NetworkDatabase) {
        Log.d("SCAN-RESULT", "scan success")
        val results = wm.scanResults
        if (results != null) {
            for (res in results) {
                val networkInfo = NetworkInfoWifi(0, res.BSSID, res.SSID, res.capabilities, res.frequency, res.channelWidth, res.level)
                Log.d("Network Info: ", networkInfo.toString())
                db.networkInfoWifiDao().insertAll(networkInfo)
            }
            Log.d("------------------------------------", "End Info")
        } else {
            Log.d("SCAN-RESULT", "NULL")
            Log.d("------------------------------------", "End Info")
        }
    }

    fun scanFailure() {
        Log.d("SCAN-RESULT", "scan failure")
    }

    override fun getAllNetwork(context: Context): List<NetworkInfoWifi> {
        val db = NetworkDatabase.getDatabase(context)
        return db.networkInfoWifiDao().getAll()
    }

    override fun deleteAllNetwork(context: Context) {
        val db = NetworkDatabase.getDatabase(context)
        db.networkInfoWifiDao().deleteAll()
    }
}