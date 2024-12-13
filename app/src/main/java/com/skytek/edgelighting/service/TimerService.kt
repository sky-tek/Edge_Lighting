package com.skytek.edgelighting.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log

class TimerService : Service() {

    private val tickIntent = Intent(ACTION_TICK)
    lateinit var handler: Handler
    var second:Long = 0

    override fun onCreate() {
        handler = Handler(Looper.getMainLooper())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        handler.postDelayed(runnable,0)
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private var runnable: Runnable = object : Runnable {
        override fun run() {
            second += 1
            tickIntent.putExtra(KEY, second)
            sendBroadcast(tickIntent)
            handler.postDelayed(this, 500)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        second = 0
    }

    companion object {
        const val ACTION_TICK = "com.skytek.edgelighting.ACTION_TICK"
        const val KEY = "seconds"
    }

}

