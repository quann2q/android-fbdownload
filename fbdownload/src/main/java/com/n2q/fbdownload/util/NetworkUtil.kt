package com.n2q.fbdownload.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.util.Log

object NetworkUtil {

    fun isNetworkConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.activeNetworkInfo?.let {
            return it.isConnected
        }
        return false
    }

    fun isNetworkConnected(context: Context, callback: NetworkCallback) {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cm.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    ConnectivityManager.TYPE_WIFI
                    Log.e("isNetworkConnected", "onAvailable  socketFactory: = ${network.socketFactory} networkHandle = ${network.networkHandle}")
                    callback.onConnected()
                }

                override fun onLost(network: Network) {
                    Log.e("isNetworkConnected", "onLost  socketFactory: = ${network.socketFactory} networkHandle = ${network.networkHandle}")
                    callback.onDisconnect()
                }
            })
        } else {
            cm.activeNetworkInfo?.let {
                if (it.isConnected) {
                    callback.onConnected()
                    return
                }
            }
            callback.onDisconnect()
        }
    }

    interface NetworkCallback {
        fun onConnected()
        fun onDisconnect()
    }

}