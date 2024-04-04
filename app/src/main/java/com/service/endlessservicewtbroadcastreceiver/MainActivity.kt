package com.service.endlessservicewtbroadcastreceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.service.endlessservicewtbroadcastreceiver.broadcast.BroadcastHelper
import com.service.endlessservicewtbroadcastreceiver.permissions.PermissionManager
import com.service.endlessservicewtbroadcastreceiver.utils.LocationForegroundtService

class MainActivity : AppCompatActivity() {

    lateinit var textView: android.widget.TextView

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (PermissionManager.requestPermissionsIfNecessary(this)) {
                BroadcastHelper.startBroadcast(this, mBroadcastReceiver)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allPermissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                BroadcastHelper.startBroadcast(this, mBroadcastReceiver)
            }
        }
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == LocationForegroundtService.ACTION_FOO) {
                val param = intent.getStringExtra(LocationForegroundtService.EXTRA_PARAM_A)
                textView.text = param
            }
        }
    }
}