package com.bpjs.tracker

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bpjs.tracker.constant.ConnectionType
import com.bpjs.tracker.service.BluetoothLeService
import com.bpjs.tracker.service.ConnectionServiceImpl


class MainActivity : AppCompatActivity() {

    private val connectionService = ConnectionServiceImpl()
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())

    private val bluetoothLeService = BluetoothLeService()
    lateinit var leDeviceListAdapter: LeDeviceAdapter

    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDeviceListAdapter.addDevice(result.device)
            leDeviceListAdapter.notifyDataSetChanged()
        }
    }

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    companion object {
        private const val FINE_LOCATION_PERMISSION_CODE = 100
        private const val COARSE_LOCATION_PERMISSION_CODE = 101
        private const val BLUETOOTH_ACTIVATION_CODE = 103
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSave = findViewById<Button>(R.id.btn_save)
        val btnStartScan = findViewById<Button>(R.id.btn_start_scan)
        val btnStartConnect = findViewById<Button>(R.id.btn_start_connect)
        val btnStartTracking = findViewById<Button>(R.id.btn_start_tracking)
        val btnStop = findViewById<Button>(R.id.btn_stop)
        val imgStatus = findViewById<ImageView>(R.id.iv_status)
        val imgCheck1 = findViewById<ImageView>(R.id.iv_step1_ok)
        val imgCheck2 = findViewById<ImageView>(R.id.iv_step2_ok)
        val imgCheck3 = findViewById<ImageView>(R.id.iv_step3_ok)
        val imgNo1 = findViewById<ImageView>(R.id.iv_step1_no)
        val imgNo2 = findViewById<ImageView>(R.id.iv_step2_no)
        val imgNo3 = findViewById<ImageView>(R.id.iv_step3_no)
        val txtStatus = findViewById<TextView>(R.id.tv_status_exact)
        val txtName = findViewById<TextView>(R.id.tv_set_name)
        val txtConnectedDevice = findViewById<TextView>(R.id.tv_connected_device)
        val cvName = findViewById<CardView>(R.id.cv_name)
        val progressBar = findViewById<ProgressBar>(R.id.progress_bar)

        btnStartConnect.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
        btnStartTracking.setBackgroundColor(ContextCompat.getColor(this, R.color.grey))
        btnStartConnect.isClickable = false
        btnStartTracking.isClickable = false
        txtConnectedDevice.visibility = View.GONE

        if (connectionService.getConnectionType(applicationContext) == ConnectionType.NO_CONNECTION) {
            AlertDialog.Builder(this).setTitle("Perhatian")
                .setMessage("Anda harus terhubung ke internet untuk menggunakan aplikasi ini")
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
                .show()
        }

        if (bluetoothAdapter == null) {
            AlertDialog.Builder(this).setTitle("Perhatian")
                .setMessage("Perangkat ini tidak support Bluetooth, Anda tidak dapat menggunakan aplikasi ini")
                .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
                .show()
            finish()
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    Log.d("ACTIVITY-RESULT", result.toString())
                }
            }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            resultLauncher.launch(enableBtIntent)
        }

        btnSave.setOnClickListener {
            Toast.makeText(this, "data berhasil disimpan", Toast.LENGTH_SHORT).show()
        }

        cvName.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Identitas")
            val input = EditText(this)
            input.hint = "Nama Anda"
            input.inputType = InputType.TYPE_CLASS_TEXT
            builder.setView(input)
            builder.setPositiveButton("OK") { _, _ ->
                txtName.text = input.text.toString()

            }
            builder.setNegativeButton(
                "Batal"
            ) { dialog, _ -> dialog.cancel() }
            builder.show()
        }


        btnStartScan.setOnClickListener {
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, FINE_LOCATION_PERMISSION_CODE)
            checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                COARSE_LOCATION_PERMISSION_CODE
            )
            if (connectionService.getConnectionType(applicationContext) != ConnectionType.NO_CONNECTION) {
                connectionService.scanNetwork(applicationContext)
            } else {
                AlertDialog.Builder(this).setTitle("Perhatian")
                    .setMessage("Anda harus terhubung ke internet untuk menggunakan aplikasi ini")
                    .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
                    .show()
            }

            progressBar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    progressBar.visibility = View.GONE
                    imgNo1.visibility = View.GONE
                    imgCheck1.visibility = View.VISIBLE
                    btnStartConnect.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.primaryDark
                        )
                    )
                    btnStartConnect.isClickable = true
                    Toast.makeText(this, "scanning berhasil!", Toast.LENGTH_SHORT).show()
                },
                10000
            )
        }

        btnStartConnect.setOnClickListener {
            leDeviceListAdapter = LeDeviceAdapter(this, ArrayList())
            progressBar.visibility = View.VISIBLE
            if (!scanning) {
                handler.postDelayed({
                    scanning = false
                    bluetoothLeScanner?.stopScan(leScanCallback)
                    progressBar.visibility = View.GONE
                    imgNo2.visibility = View.GONE
                    imgCheck2.visibility = View.VISIBLE
                    btnStartTracking.setBackgroundColor(
                        ContextCompat.getColor(
                            this,
                            R.color.primaryDark
                        )
                    )
                    btnStartTracking.isClickable = true
                    AlertDialog.Builder(this)
                        .setTitle("BLE Device")
                        .setAdapter(
                            leDeviceListAdapter
                        ) { dialogInterface, which ->
                            val device: BluetoothDevice = leDeviceListAdapter.getDevice(which)
                            txtConnectedDevice.visibility = View.VISIBLE

                            val deviceName = device.name
                            val deviceAddress = device.address
                            val txt = "Device: $deviceAddress"
                            txtConnectedDevice.text = txt
                            Log.d("DEVICE-INFO", "onListItemClick " + device.name)
                            Log.d(
                                "DEVICE-INFO",
                                "Device Name:" + device.name + "   " + "Device Name:" + device.address
                            )

                            if (bluetoothLeService.connect(deviceAddress)) {
                                Log.d("CONNECT-DEVICE-INFO", "Connect request success")
                            } else {
                                Log.d("CONNECT-DEVICE-INFO", "Connect request fail")
                            }
                            dialogInterface.dismiss()
                        }
                        .setOnCancelListener {
                            it.dismiss()
                        }.create().show()
                    Toast.makeText(this, "koneksi berhasil!", Toast.LENGTH_SHORT).show()
                }, SCAN_PERIOD)
                scanning = true
                bluetoothLeScanner?.startScan(leScanCallback)
            } else {
                scanning = false
                bluetoothLeScanner?.stopScan(leScanCallback)
            }
        }

        btnStartTracking.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    progressBar.visibility = View.GONE
                    imgNo3.visibility = View.GONE
                    imgCheck3.visibility = View.VISIBLE
                    Toast.makeText(this, "berhasil!", Toast.LENGTH_SHORT).show()
                    txtStatus.text = "Aktif"
                    imgStatus.setColorFilter(
                        ContextCompat.getColor(this, R.color.green),
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                },
                3000
            )
            Handler(Looper.getMainLooper()).postDelayed(
                {
                    AlertDialog.Builder(this).setTitle("Perhatian")
                        .setMessage("Anda keluar dari lokasi perawatan, informasi akan kami kirim ke RS")
                        .setPositiveButton(android.R.string.ok) { dialog, _ -> dialog.cancel() }
                        .show()
                },
                90000
            )
        }

        btnStop.setOnClickListener {
            AlertDialog.Builder(this).setTitle("Perhatian")
                .setMessage("Apakah Anda yakin ingin menghentikan proses tracking ini?")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    run {
                        progressBar.visibility = View.VISIBLE
                        Handler(Looper.getMainLooper()).postDelayed(
                            {
                                btnStartConnect.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.grey
                                    )
                                )
                                btnStartTracking.setBackgroundColor(
                                    ContextCompat.getColor(
                                        this,
                                        R.color.grey
                                    )
                                )
                                btnStartConnect.isClickable = false
                                btnStartTracking.isClickable = false
                                imgNo1.visibility = View.VISIBLE
                                imgNo2.visibility = View.VISIBLE
                                imgNo3.visibility = View.VISIBLE
                                imgCheck1.visibility = View.GONE
                                imgCheck2.visibility = View.GONE
                                imgCheck3.visibility = View.GONE
                                txtStatus.text = "Tidak Aktif"
                                imgStatus.setColorFilter(
                                    ContextCompat.getColor(this, R.color.red),
                                    android.graphics.PorterDuff.Mode.SRC_IN
                                )
                                txtConnectedDevice.visibility = View.GONE
                                connectionService.deleteAllNetwork(applicationContext)
                                Toast.makeText(this, "berhasil stop!", Toast.LENGTH_SHORT).show()
                                progressBar.visibility = View.GONE
                            },
                            5000
                        )
                    }
                }
                .setNegativeButton("batal") { dialog, _ -> dialog.cancel() }
                .show()
        }

    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            Log.d("PERMISSION", "Permission already granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
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


//        val uploadWorkRequest: WorkRequest = PeriodicWorkRequestBuilder<ExampleWorker>(1, TimeUnit.MINUTES)
//            .addTag("active-wifi").build()
//
//        WorkManager
//            .getInstance(applicationContext)
//            .enqueue(uploadWorkRequest)