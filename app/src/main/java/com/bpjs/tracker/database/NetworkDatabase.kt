package com.bpjs.tracker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bpjs.tracker.data.NetworkInfoWifi

@Database(entities = [NetworkInfoWifi::class], version = 1, exportSchema = false)
public abstract class NetworkDatabase : RoomDatabase() {

    abstract fun networkInfoWifiDao(): NetworkInfoWifiDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: NetworkDatabase? = null

        fun getDatabase(context: Context): NetworkDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NetworkDatabase::class.java,
                    "network_database"
                ).allowMainThreadQueries().build()
                INSTANCE = instance
                instance
            }
        }
    }
}