package com.service.endlessservicewtbroadcastreceiver.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.util.Timer
import java.util.TimerTask

class AutoStartService : Service() {
    var counter = 0
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "my_channel_id"
            val channelName = "My Channel"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            val notification = NotificationCompat.Builder(this, channelId).build()
            startForeground(1, notification)
        }
        startTimer(this)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.i(TAG, "onDestroy: Service is destroyed :( ")
        val broadcastIntent = Intent(this, RestartBroadcastReceiver::class.java)
        sendBroadcast(broadcastIntent)
        stoptimertask()
    }

    private fun startTimer(context: Context?) {
        timer = Timer()
        initialiseTimerTask(context)
        //schedule the timer, to wake up every 1 second
        timer!!.schedule(timerTask, 1000, 1000) //
    }

    private fun initialiseTimerTask(context: Context?) {
        timerTask = object : TimerTask() {
            override fun run() {
                Log.i(TAG, "Timer is running " + counter++)
                broadcastActionBaz(context, counter.toString())
            }
        }
    }

    private fun stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer!!.cancel()
            timer = null
        }
    }

    companion object {
        private const val TAG = "AutoService"
        const val ACTION_FOO = "com.gahlot.neverendingservice.FOO"
        const val EXTRA_PARAM_A = "com.gahlot.neverendingservice.PARAM_A"
        fun broadcastActionBaz(context: Context?, param: String?) {
            val intent = Intent(ACTION_FOO)
            intent.putExtra(EXTRA_PARAM_A, param)
            val bm = LocalBroadcastManager.getInstance(context!!)
            bm.sendBroadcast(intent)
        }
    }
}
