package com.skytek.edgelighting.utils

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.service.quicksettings.TileService
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager
import com.skytek.edgelighting.service.AlwaysOnTileService

internal object Global {

    const val LOG_TAG: String = "AlwaysOn"

    const val ALWAYS_ON_STATE_CHANGED: String =
        "io.github.domi04151309.alwayson.ALWAYS_ON_STATE_CHANGED"

    fun currentAlwaysOnState(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean("always_on", false)
    }

    fun changeAlwaysOnState(context: Context): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val value = !prefs.getBoolean("always_on", false)
        prefs.edit().putBoolean("always_on", value).apply()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            TileService.requestListeningState(
                context,
                ComponentName(context, AlwaysOnTileService::class.java)
            )
        }
//        context.sendBroadcast(
//            Intent(context, AlwaysOnAppWidgetProvider::class.java)
//                .setAction(ALWAYS_ON_STATE_CHANGED)
//        )
        return value
    }
}
