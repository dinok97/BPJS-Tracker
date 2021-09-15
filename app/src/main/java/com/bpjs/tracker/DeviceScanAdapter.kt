package com.bpjs.tracker

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.*

class DeviceScanAdapter(
    private val mainContext: Context,
    private val mLeDevices: ArrayList<Device>
) : BaseAdapter() {

    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(mainContext)
    }

    fun addDevice(device: Device) {
        if (!mLeDevices.contains(device)) {
            mLeDevices.add(device)
        }
    }

    fun getDevice(position: Int): Device {
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

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View? {
        var view = view
        val viewHolder: ViewHolder
        // General ListView optimization code.
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
            deviceName else viewHolder.deviceName?.text =
            "R.string.unknown_device"
        viewHolder.deviceAddress?.text = device.address
        return view
    }
}

internal class ViewHolder {
    var deviceName: TextView? = null
    var deviceAddress: TextView? = null
}

data class Device(
    val address: String?,
    val name: String?
)