package com.bpjs.tracker.data

data class NetworkInfoWifi(
        var BSSID: String = "",
        var SSID: String = "",
        var capabilities: String = "",
        var frequency: Int = 0,
        var channelWidth: Int = 0,
        var level: Int = 0
)
