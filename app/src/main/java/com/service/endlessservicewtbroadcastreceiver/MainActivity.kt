package com.service.endlessservicewtbroadcastreceiver

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.service.endlessservicewtbroadcastreceiver.permissions.PermissionManager
import com.service.endlessservicewtbroadcastreceiver.utils.AutoStartService
import com.service.endlessservicewtbroadcastreceiver.utils.RestartBroadcastReceiver

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
                startBroadcast()
            }
        }
    }

    private fun startBroadcast() {
        RestartBroadcastReceiver.scheduleJob(applicationContext)
        val handler = Handler(Looper.getMainLooper())
        val delayMillis = 1000L

        handler.postDelayed({
            val filter = IntentFilter()
            filter.addAction(AutoStartService.ACTION_FOO)
            val bm = LocalBroadcastManager.getInstance(this)
            bm.registerReceiver(mBroadcastReceiver, filter)
        }, delayMillis)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Verificar si los permisos fueron concedidos
            var allPermissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                startBroadcast()
            }
        }
    }

    private val mBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (intent.action == AutoStartService.ACTION_FOO) {
                val param = intent.getStringExtra(AutoStartService.EXTRA_PARAM_A)
                textView.text = param
            }
        }
    }
}