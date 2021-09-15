package com.bpjs.tracker

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class LeDeviceAdapter(
    mainContext: Context,
    private val mLeDevices: ArrayList<BluetoothDevice>
) : BaseAdapter() {

    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(mainContext)
    }

    fun addDevice(device: BluetoothDevice) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device)
        }
    }

    fun getDevice(position: Int): BluetoothDevice {
        return mLeDevices[position]
    }

    fun clear() {
        mLeDevices.clear()
    }

    override fun getCount(): Int {
        return mLeDevices.size
    }

    override fun getItem(i: Int): Any {
        return mLeDevices[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    @SuppressLint("InflateParams")
    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
        var view = view
        val viewHolder: ViewHolder
        if (view == null) {
            view = mInflator.inflate(R.layout.listitem_device, null)
            viewHolder = ViewHolder()
            viewHolder.deviceAddress = view
                .findViewById<View>(R.id.device_address) as TextView
            viewHolder.deviceName = view
                .findViewById<View>(R.id.device_name) as TextView
            println("mInflator.inflate  getView")
            view.tag = viewHolder
        } else {
            viewHolder = view.tag as ViewHolder
        }
        val device = mLeDevices[i]
        val deviceName = device.name
        if (deviceName != null && deviceName.length > 0) viewHolder.deviceName?.text =
            deviceName else viewHolder.deviceName?.text = "No Device"
        viewHolder.deviceAddress?.text = device.address
        return view
    }
}

internal class ViewHolder {
    var deviceName: TextView? = null
    var deviceAddress: TextView? = null
}