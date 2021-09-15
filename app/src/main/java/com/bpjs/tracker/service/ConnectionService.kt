package com.bpjs.tracker.service

import android.content.Context
import com.bpjs.tracker.data.NetworkInfoWifi

interface ConnectionService {
    fun getConnectionType(context: Context): Int
    fun getConnectionBssid(context: Context): String
    fun scanNetwork(context: Context)
    fun getAllNetwork(context: Context): List<NetworkInfoWifi>
    fun deleteAllNetwork(context: Context)
}