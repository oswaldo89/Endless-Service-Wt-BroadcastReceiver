package com.service.endlessservicewtbroadcastreceiver.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.service.endlessservicewtbroadcastreceiver.utils.AutoStartService
import com.service.endlessservicewtbroadcastreceiver.utils.RestartBroadcastReceiver

object BroadcastHelper {

    fun startBroadcast(context: Context, receiver: BroadcastReceiver) {
        RestartBroadcastReceiver.scheduleJob(context.applicationContext)
        val handler = Handler(Looper.getMainLooper())
        val delayMillis = 1000L

        handler.postDelayed({
            val filter = IntentFilter()
            filter.addAction(AutoStartService.ACTION_FOO)
            val bm = LocalBroadcastManager.getInstance(context)
            bm.registerReceiver(receiver, filter)
        }, delayMillis)
    }
}
