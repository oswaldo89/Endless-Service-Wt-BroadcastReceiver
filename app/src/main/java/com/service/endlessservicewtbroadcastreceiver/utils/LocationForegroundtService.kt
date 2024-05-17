package com.service.endlessservicewtbroadcastreceiver.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.service.endlessservicewtbroadcastreceiver.storage.LocationStorageManager
import java.util.Timer
import java.util.TimerTask

class LocationForegroundtService : Service(), LocationListener {
    private var counter = 0
    private var timer: Timer? = null
    private var timerTask: TimerTask? = null
    private var locationManager: LocationManager? = null
    private var lastLocation: Location? = null
    private var INTERVAL_TIME = 1000L

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        requestLocationUpdates()
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Location permissions are not granted")
            return
        }
        locationManager?.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, this)
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
        locationManager?.removeUpdates(this)
        stopTimerTask()
    }

    private fun startTimer(context: Context) {
        timer = Timer()
        initialiseTimerTask(context)
        timer!!.schedule(
            timerTask, 1000,
            INTERVAL_TIME // Intervalo de 1 segundo
        )
    }

    private fun initialiseTimerTask(context: Context) {
        timerTask = object : TimerTask() {
            override fun run() {
                counter++
                val locationString = lastLocation?.let { "${it.latitude},${it.longitude}" } ?: "Location not available"

                lastLocation?.let { location ->
                    saveLocation(location)
                }

                broadcastActionBaz(context, locationString)
            }
        }
    }

    private fun saveLocation(location: Location) {
        val storageManager = LocationStorageManager(this)
        storageManager.saveLocation(location)
    }

    private fun stopTimerTask() {
        timer?.cancel()
        timer = null
    }

    override fun onLocationChanged(location: Location) {
        lastLocation = location
    }

    @Deprecated("Deprecated in Java")
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String) {}

    override fun onProviderDisabled(provider: String) {}

    companion object {
        private const val TAG = "AutoService"
        const val ACTION_FOO = "com.gahlot.neverendingservice.FOO"
        const val EXTRA_PARAM_A = "com.gahlot.neverendingservice.PARAM_A"

        fun broadcastActionBaz(context: Context, param: String) {
            val intent = Intent(ACTION_FOO)
            intent.putExtra(EXTRA_PARAM_A, param)
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
        }
    }
}
