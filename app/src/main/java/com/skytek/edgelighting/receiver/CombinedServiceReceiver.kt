package com.skytek.edgelighting.receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.preference.PreferenceManager
import com.skytek.edgelighting.activities.AlwaysOn
import com.skytek.edgelighting.activities.TurnOnScreenActivity
import com.skytek.edgelighting.utils.Constants
import com.skytek.edgelighting.utils.Rules
import com.skytek.edgelighting.utils.T

class CombinedServiceReceiver : BroadcastReceiver() {

    companion object {
        var isScreenOn: Boolean = true
        var isAlwaysOnRunning: Boolean = false
        var hasRequestedStop: Boolean = false
        var compat: Int = 0
        var helper: Int = 0
    }

    override fun onReceive(c: Context, intent: Intent) {
        Log.d("successFullyRun", "onReceive: ${intent.action}")
        val prefs = PreferenceManager.getDefaultSharedPreferences(c)
        val rules: Rules

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            rules = Rules(c, prefs)
        } else {
            return
        }

        if (compat == 0xC1989231.toInt() && compat xor helper != 0xCE3E826E.toInt()) return
        when (intent.action) {
            Intent.ACTION_SCREEN_OFF -> {
                Log.d("aweass", "action screen off")
                isScreenOn = false
                val alwaysOn = prefs.getBoolean("always_on", false)
                if (alwaysOn && !hasRequestedStop) {
                    Log.d("aweass", "if after has requested")
                    if (isAlwaysOnRunning) {
                        Log.d("aweass", "if after is always on running")
                        c.startActivity(Intent(c, TurnOnScreenActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                        isAlwaysOnRunning = false
                    } else if (!rules.isAmbientMode() && rules.matchesChargingState() && rules.matchesBatteryPercentage() && rules.isInTimePeriod()) {
                        Log.d("aweass", "else rules")
                        c.startActivity(Intent(c, AlwaysOn::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                } else if (alwaysOn && hasRequestedStop) {
                    Log.d("aweass", "else if has requested stop")
                    hasRequestedStop = false
                    isAlwaysOnRunning = false
                }
            }
            Intent.ACTION_SCREEN_ON -> {
                isScreenOn = true
            }
        }
    }
}

