package com.bpjs.tracker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "network_info_wifi")
data class NetworkInfoWifi(
        @PrimaryKey(autoGenerate = true) val uid: Int,
        @ColumnInfo(name = "bssid") val BSSID: String?,
        @ColumnInfo(name = "ssid") val SSID: String?,
        @ColumnInfo(name = "capabilities") val capabilities: String?,
        @ColumnInfo(name = "frequency") val frequency: Int?,
        @ColumnInfo(name = "channel_width") val channelWidth: Int?,
        @ColumnInfo(name = "level") val level: Int?
)
