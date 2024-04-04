package com.service.endlessservicewtbroadcastreceiver.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class ServiceAdmin {
    private fun setServiceIntent(context: Context) {
        if (serviceIntent == null) {
            serviceIntent = Intent(context, LocationForegroundtService::class.java)
        }
    }

    fun launchService(context: Context?) {
        context?.let {
            setServiceIntent(it)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.startForegroundService(serviceIntent)
            } else {
                it.startService(serviceIntent)
            }
            Log.d(TAG, "launchService:  Service is starting....")
        }
    }

    companion object {
        private const val TAG = "ServiceAdmin"
        private var serviceIntent: Intent? = null
    }
}