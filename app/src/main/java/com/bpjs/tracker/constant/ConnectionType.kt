package com.bpjs.tracker.constant

interface ConnectionType {
    companion object{
        val NO_CONNECTION = 0
        val CELLULAR = 1
        val WIFI = 2
        val VPN = 3
    }
}