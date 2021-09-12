package com.bpjs.tracker.database

import androidx.room.*
import com.bpjs.tracker.data.NetworkInfoWifi

@Dao
interface NetworkInfoWifiDao {
    @Query("SELECT * FROM network_info_wifi")
    fun getAll(): List<NetworkInfoWifi>

    @Query("DELETE FROM network_info_wifi")
    fun deleteAll()

    @Query("SELECT * FROM network_info_wifi WHERE bssid IN (:bssid)")
    fun loadAllByIBssid(bssid: IntArray): List<NetworkInfoWifi>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    fun insertAll(vararg wifiInfo: NetworkInfoWifi)

    @Delete
    fun delete(wifiInfo: NetworkInfoWifi)
}