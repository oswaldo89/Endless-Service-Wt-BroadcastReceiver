package com.service.endlessservicewtbroadcastreceiver.utils

import android.app.job.JobParameters
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi

class JobService : android.app.job.JobService() {
    override fun onStartJob(jobParameters: JobParameters): Boolean {
        val serviceAdmin = ServiceAdmin()
        serviceAdmin.launchService(this)
        instance = this
        Companion.jobParameters = jobParameters
        return false
    }

    override fun onStopJob(jobParameters: JobParameters): Boolean {
        Log.i(TAG, "Stopping job")
        val broadcastIntent = Intent(Globals.RESTART_INTENT)
        sendBroadcast(broadcastIntent)
        Handler().postDelayed({ unregisterReceiver(restartBroadcastReceiver) }, 1000)
        return false
    }

    companion object {
        private const val TAG = "JobService"
        private val restartBroadcastReceiver: RestartBroadcastReceiver? = null
        private var instance: JobService? = null
        private var jobParameters: JobParameters? = null
    }
}