package com.bpjs.tracker.service

import android.content.Context

interface ConnectionService {
    fun getConnectionType(context: Context): Int
    fun scanNetwork(context: Context)
}