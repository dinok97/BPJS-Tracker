package com.bpjs.tracker

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bpjs.tracker.constant.ConnectionType
import com.bpjs.tracker.service.ConnectionServiceImpl


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val connectionService = ConnectionServiceImpl()
        val btn = findViewById<Button>(R.id.test)
        val txt = findViewById<TextView>(R.id.tv_test)
        btn.setOnClickListener {
            when (connectionService.getConnectionType(applicationContext)) {
                ConnectionType.NO_CONNECTION -> {
                    txt.text = "no connection"
                }
                ConnectionType.CELLULAR -> {
                    txt.text = "cellular"
                }
                ConnectionType.WIFI -> {
                    txt.text = "Wi-Fi"
                }
                ConnectionType.VPN -> {
                    txt.text = "VPN"
                }
            }
        }
    }
}