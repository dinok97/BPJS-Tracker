package com.bpjs.tracker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.bpjs.tracker.constant.ConnectionType
import com.bpjs.tracker.service.ConnectionServiceImpl
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val connectionService = ConnectionServiceImpl()

    companion object {
        private const val FINE_LOCATION_PERMISSION_CODE = 100
        private const val COARSE_LOCATION_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn = findViewById<Button>(R.id.test)
        val btnShow = findViewById<Button>(R.id.show)
        val btnDelete = findViewById<Button>(R.id.delete)
        val btnConnectedInfo = findViewById<Button>(R.id.get_connect_info)

//        val uploadWorkRequest: WorkRequest = PeriodicWorkRequestBuilder<ExampleWorker>(1, TimeUnit.MINUTES)
//            .addTag("active-wifi").build()
//
//        WorkManager
//            .getInstance(applicationContext)
//            .enqueue(uploadWorkRequest)


        if (connectionService.getConnectionType(applicationContext) == ConnectionType.NO_CONNECTION) {
            AlertDialog.Builder(this).setTitle("Perhatian")
                    .setMessage("Anda harus terhubung ke internet untuk menggunakan aplikasi ini")
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
                    .show()
        }

        btn.setOnClickListener {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE)
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION, COARSE_LOCATION_PERMISSION_CODE)
            if (connectionService.getConnectionType(applicationContext) != ConnectionType.NO_CONNECTION) {
                connectionService.scanNetwork(applicationContext)
            } else {
                AlertDialog.Builder(this).setTitle("Perhatian")
                        .setMessage("Anda harus terhubung ke internet untuk menggunakan aplikasi ini")
                        .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
                        .show()
            }
        }

        btnConnectedInfo.setOnClickListener {

        }
        btnShow.setOnClickListener {
            connectionService.getNetwork(applicationContext)
        }
        btnDelete.setOnClickListener {
            connectionService.deleteNetworkInDB(applicationContext)
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Log.d("PERMISSION", "Permission already granted")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == FINE_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Fine Location Permission Granted")
            } else {
                Log.d("PERMISSION", "Fine Location Permission Denied")
            }
        } else if (requestCode == COARSE_LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Coarse Location Permission Granted")
            } else {
                Log.d("PERMISSION", "Coarse Location Permission Denied")
            }
        }
    }

}