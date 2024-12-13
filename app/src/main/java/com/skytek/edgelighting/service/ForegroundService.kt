package com.skytek.edgelighting.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.service.quicksettings.TileService
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.skytek.edgelighting.R
import com.skytek.edgelighting.receiver.CombinedServiceReceiver
class ForegroundService : Service() {

    private val combinedServiceReceiver = CombinedServiceReceiver()

    override fun onCreate() {
        super.onCreate()

        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_BOOT_COMPLETED)
            addAction(Intent.ACTION_REBOOT)
            addAction(Intent.ACTION_LOCKED_BOOT_COMPLETED)
        }
        registerReceiver(combinedServiceReceiver, filter)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            TileService.requestListeningState(
                this, ComponentName(this, AlwaysOnTileService::class.java)
            )
        }

        createNotificationChannel()

        // Check if startForeground is allowed and required
        if (shouldStartForeground()) {
            startForegroundServiceProperly()
        } else {
            Log.e("ForegroundService", "startForeground not allowed or not required.")
        }
    }

    /**
     * Checks whether starting the service in the foreground is required and allowed.
     */
    private fun shouldStartForeground(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O // API 26 (Oreo) and higher require this
    }

    /**
     * Initializes the notification and starts the service in the foreground.
     */
    private fun startForegroundServiceProperly() {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentText("Edge Lighting is running in the background")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setShowWhen(false)
            .build()

        // Start the service in the foreground
        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(combinedServiceReceiver)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent == null) {
            Log.e("ForegroundService", "Received null intent, handling gracefully.")
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * Creates the notification channel required for foreground services.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, "Edge Lighting Service", NotificationManager.IMPORTANCE_LOW
            ).apply {
                setShowBadge(false)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID: String = "service_channel"
    }
}

