package com.skytek

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.skytek.edgelighting.App
import com.skytek.edgelighting.MyAccessibilityService
import com.skytek.edgelighting.MyAccessibilityService.Companion.edgeLightingView
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.activity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.service.TimerService
import com.skytek.edgelighting.thread.AsynchronousTask
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge

class WindowService : Service() {
    private var isTimerReceiverRegistered = false
    private var isBatteryBroadcastRegistered = false
    private var isChargingBroadcastRegistered = false

    lateinit var colorSharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        Log.d("whatHappenIsWithService", "onCreate: Service is Onnnn ")
        service_context = this
    }

    override fun onDestroy() {
        Log.d("whatHappenIsWithService", "onDestroy: Service is off ")
        super.onDestroy()

        // Unregister Receivers Safely
        try {
            if (isTimerReceiverRegistered && timerReceiver != null) {
                unregisterReceiver(timerReceiver)
                isTimerReceiverRegistered = false
                timerReceiver = null
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        try {
            if (isBatteryBroadcastRegistered && batteryBroacast != null) {
                unregisterReceiver(batteryBroacast)
                isBatteryBroadcastRegistered = false
                batteryBroacast = null
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }

        try {
            if (isChargingBroadcastRegistered && chargingBroacast != null) {
                unregisterReceiver(chargingBroacast)
                isChargingBroadcastRegistered = false
                chargingBroacast = null
            }
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            if (batteryBroacast == null && !isBatteryBroadcastRegistered) {
                batteryBroacast = BatteryLevel(this)
                val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                registerReceiver(batteryBroacast, intentFilter)
                isBatteryBroadcastRegistered = true
            }

            if (chargingBroacast == null && !isChargingBroadcastRegistered) {
                chargingBroacast = DetectChargerReceiver(this)
                val intentChargingFilter = IntentFilter()
                intentChargingFilter.addAction(Intent.ACTION_POWER_CONNECTED)
                intentChargingFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
                registerReceiver(chargingBroacast, intentChargingFilter)
                isChargingBroadcastRegistered = true
            }

            // TimerReceiver registration if applicable
            if (timerReceiver == null && !isTimerReceiverRegistered) {
                timerReceiver = TimerReceiver()
                val timerFilter = IntentFilter(TimerService.ACTION_TICK)
                registerReceiver(timerReceiver, timerFilter)
                isTimerReceiverRegistered = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return START_STICKY
    }


    inner class TimerReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent == null || edgeLightingView == null) return
            when (intent.action) {
                TimerService.ACTION_TICK -> {
                    val second = intent.getLongExtra(TimerService.KEY, 0)

                    if (second % 2 == 0L) edgeLightingView!!.visibility = View.VISIBLE
                    else edgeLightingView!!.visibility = View.INVISIBLE
                }
            }
        }
    }

    inner class BatteryLevel(var context: Context) : BroadcastReceiver() {

        private fun setColor(i: Int, code: String) {
            EdgeOverlaySettingsActivity.color[i] = Color.parseColor(code)
        }


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onReceive(context: Context?, intent: Intent) {
            try {
                if (context == null) return
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                val batteryPct = level * 100 / scale.toFloat()

                if (isMyServiceRunning(TimerService::class.java)) {
                    stopService(Intent(this@WindowService, TimerService::class.java))
                }
                val warningValuesSharedPreferences: SharedPreferences =
                    getSharedPreferences("WARNING_VALUES", Context.MODE_PRIVATE)
                val warningMaxVal: Int = warningValuesSharedPreferences.getInt("WARNING_MAX", 15)
                val warningMinwal: Int = warningValuesSharedPreferences.getInt("WARNING_MIN", 5)
                Log.d("helloDear", "warningMaxVal: $warningMaxVal")
                Log.d("helloDear", "warningMinwal: $warningMinwal")
                Log.d(
                    "helloDear",
                    "(batteryPct <= warningMinwal): ${(batteryPct <= warningMinwal)}"
                )
                Log.d(
                    "helloDear",
                    "(edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, context): ${
                        (edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, context
                        ))
                    }"
                )
                warningValuesSharedPreferences.edit().apply {
                    putInt("BATTERY_PERCENTAGE", batteryPct.toInt())
                    commit()
                }


                batteryBroacastIsRegistered = true

                // for orange color
                if (batteryPct > warningMinwal && batteryPct <= warningMaxVal) {

                    batteryAbove20 = false


                    for (i in 0..6) {
                        setColor(i, "#FFAC1C")
                    }
                    MySharePreferencesEdge.putChargedColorValue(
                        MySharePreferencesEdge.COLOR_WHILE_CHARGING,
                        Color.parseColor("#FFAC1C"),
                        context
                    )



                    try {
                        if (edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(
                                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, context
                            )
                        ) {
                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                            if (!isMyServiceRunning(TimerService::class.java)) {
                                context.startService(Intent(context, TimerService::class.java))
                                timerReceiver = TimerReceiver()

                                context.registerReceiver(
                                    timerReceiver,
                                    IntentFilter(TimerService.ACTION_TICK),
                                    RECEIVER_EXPORTED
                                )

                            }
                        } else {
                            binding!!.edgeLightView.changeColor(EdgeOverlaySettingsActivity.color)
                        }
                        if (MySharePreferencesEdge.getBooleanValue(
                                MySharePreferencesEdge.ControlWindowManager, activity!!
                            )
                        ) {
                            val i = Intent(Const.Action_ChangeWindownManager)
                            i.putExtra(Const.CONTROLWINDOW, Const.COLOR)
                            i.setPackage(activity!!.packageName)
                            activity!!.sendBroadcast(intent)
                        }
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                        Log.d("error", "onReceive: ")
                    }
                } else if (batteryPct <= warningMinwal) {
                    batteryAbove20 = false
                    for (i in 0..6) {
                        setColor(i, "#EB1111")
                    }
                    MySharePreferencesEdge.putChargedColorValue(
                        MySharePreferencesEdge.COLOR_WHILE_CHARGING,
                        Color.parseColor("#EB1111"),
                        context
                    )

                    try {
                        if (edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(
                                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, context
                            )
                        ) {
                            edgeLightingView?.changeColor(EdgeOverlaySettingsActivity.color)

                            if (!isMyServiceRunning(TimerService::class.java)) {
                                val i = Intent(context, TimerService::class.java)
                                context.startService(i)

                                if (timerReceiver == null) {
                                    timerReceiver = TimerReceiver()

                                    context.registerReceiver(
                                        timerReceiver,
                                        IntentFilter(TimerService.ACTION_TICK),
                                        RECEIVER_EXPORTED
                                    )

                                }
                            }

                        } else {
                            binding?.edgeLightView?.changeColor(EdgeOverlaySettingsActivity.color)
                        }
                        if (MySharePreferencesEdge.getBooleanValue(
                                MySharePreferencesEdge.ControlWindowManager, activity!!
                            )
                        ) {
                            val i = Intent(Const.Action_ChangeWindownManager)
                            i.putExtra(Const.CONTROLWINDOW, Const.COLOR)
                            i.setPackage(activity!!.packageName)
                            activity!!.sendBroadcast(intent)
                        }
                    } catch (e: NullPointerException) {
                        e.printStackTrace()
                        Log.d("error", "onReceive: ")
                    }
                } else {

                    colorSharedPreferences =
                        EdgeOverlaySettingsActivity.context!!.getSharedPreferences(
                            "COLOR_CHANGE_HERE", Context.MODE_PRIVATE
                        )


                    val imageViews = listOf(
                        binding!!.imageColor1,
                        binding!!.imageColor2,
                        binding!!.imageColor3,
                        binding!!.imageColor4,
                        binding!!.imageColor5,
                        binding!!.imageColor6
                    )

                    val selectedColors = imageViews.filter { it.foreground != null }
                        .map { (it.background as? ColorDrawable)?.color!! }
                    for ((index, color) in selectedColors.withIndex()) {
                        Log.d("selectedColors", "Color at index $index: $color")
                    }

                    when (selectedColors.size) {
                        1 -> {
                            Log.d("whhhhhhhhhhhhhhhhhhhhhhh", "onReceive: 197")
                            setColorsAndPreferences(
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[0]
                            )

                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)

                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)

                            edgeLightingView!!.visibility = View.VISIBLE

                            batteryAbove20 = true
                        }

                        2 -> {
                            Log.d("whhhhhhhhhhhhhhhhhhhhhhh", "onReceive: 209")
                            setColorsAndPreferences(
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[1],
                                selectedColors[1],
                                selectedColors[1]
                            )
                            MyAccessibilityService.apply {
                                edgeLightingView!!.changeType(
                                    MySharePreferencesEdge.getString(
                                        MySharePreferencesEdge.SHAPE, App.context
                                    )
                                )
                                edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                                edgeLightingView!!.changeSize(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SIZE, App.context
                                    )
                                )
                                edgeLightingView!!.changeSpeed(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SPEED, App.context
                                    )
                                )
                                edgeLightingView!!.changeBorder(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSBOTTOM, App.context
                                    )
                                )
                                edgeLightingView!!.changeNotch(
                                    MySharePreferencesEdge.getBooleanValue(
                                        MySharePreferencesEdge.CHECKNOTCH, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHBOTTOM, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHHEIGHT, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSBOTTOM, App.context
                                    )
                                )
                            }

                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)

                            edgeLightingView!!.visibility = View.VISIBLE

                            batteryAbove20 = true

                        }

                        3 -> {
                            Log.d("whhhhhhhhhhhhhhhhhhhhhhh", "onReceive: 239")
                            setColorsAndPreferences(
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[1],
                                selectedColors[1],
                                selectedColors[2],
                                selectedColors[2]
                            )
                            MyAccessibilityService.apply {
                                edgeLightingView!!.changeType(
                                    MySharePreferencesEdge.getString(
                                        MySharePreferencesEdge.SHAPE, App.context
                                    )
                                )
                                edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                                edgeLightingView!!.changeSize(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SIZE, App.context
                                    )
                                )
                                edgeLightingView!!.changeSpeed(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SPEED, App.context
                                    )
                                )
                                edgeLightingView!!.changeBorder(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSBOTTOM, App.context
                                    )
                                )
                                edgeLightingView!!.changeNotch(
                                    MySharePreferencesEdge.getBooleanValue(
                                        MySharePreferencesEdge.CHECKNOTCH, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHBOTTOM, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHHEIGHT, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSBOTTOM, App.context
                                    )
                                )
                            }

                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)

                            edgeLightingView!!.visibility = View.VISIBLE

                            batteryAbove20 = true

                        }

                        4 -> {
                            Log.d("whhhhhhhhhhhhhhhhhhhhhhh", "onReceive: 270")
                            setColorsAndPreferences(
                                selectedColors[0],
                                selectedColors[1],
                                selectedColors[2],
                                selectedColors[3],
                                selectedColors[1],
                                selectedColors[2]
                            )
                            MyAccessibilityService.apply {
                                edgeLightingView!!.changeType(
                                    MySharePreferencesEdge.getString(
                                        MySharePreferencesEdge.SHAPE, App.context
                                    )
                                )
                                edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                                edgeLightingView!!.changeSize(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SIZE, App.context
                                    )
                                )
                                edgeLightingView!!.changeSpeed(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SPEED, App.context
                                    )
                                )
                                edgeLightingView!!.changeBorder(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSBOTTOM, App.context
                                    )
                                )
                                edgeLightingView!!.changeNotch(
                                    MySharePreferencesEdge.getBooleanValue(
                                        MySharePreferencesEdge.CHECKNOTCH, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHBOTTOM, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHHEIGHT, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSBOTTOM, App.context
                                    )
                                )
                            }

                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                            edgeLightingView!!.visibility = View.VISIBLE

                            batteryAbove20 = true
                        }

                        5 -> {
                            Log.d("whhhhhhhhhhhhhhhhhhhhhhh", "onReceive: 299")
                            setColorsAndPreferences(
                                selectedColors[0],
                                selectedColors[0],
                                selectedColors[1],
                                selectedColors[2],
                                selectedColors[3],
                                selectedColors[4]
                            )
                            MyAccessibilityService.apply {
                                edgeLightingView!!.changeType(
                                    MySharePreferencesEdge.getString(
                                        MySharePreferencesEdge.SHAPE, App.context
                                    )
                                )
                                edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                                edgeLightingView!!.changeSize(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SIZE, App.context
                                    )
                                )
                                edgeLightingView!!.changeSpeed(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SPEED, App.context
                                    )
                                )
                                edgeLightingView!!.changeBorder(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSBOTTOM, App.context
                                    )
                                )
                                edgeLightingView!!.changeNotch(
                                    MySharePreferencesEdge.getBooleanValue(
                                        MySharePreferencesEdge.CHECKNOTCH, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHBOTTOM, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHHEIGHT, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSBOTTOM, App.context
                                    )
                                )
                            }

                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                            edgeLightingView!!.visibility = View.VISIBLE




                            batteryAbove20 = true
                        }

                        6 -> {
                            Log.d("whhhhhhhhhhhhhhhhhhhhhhh", "onReceive: 330")
                            setColorsAndPreferences(
                                selectedColors[0],
                                selectedColors[1],
                                selectedColors[2],
                                selectedColors[3],
                                selectedColors[4],
                                selectedColors[5]
                            )
                            MyAccessibilityService.apply {
                                edgeLightingView!!.changeType(
                                    MySharePreferencesEdge.getString(
                                        MySharePreferencesEdge.SHAPE, App.context
                                    )
                                )
                                edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                                edgeLightingView!!.changeSize(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SIZE, App.context
                                    )
                                )
                                edgeLightingView!!.changeSpeed(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.SPEED, App.context
                                    )
                                )
                                edgeLightingView!!.changeBorder(
                                    MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.RADIUSBOTTOM, App.context
                                    )
                                )
                                edgeLightingView!!.changeNotch(
                                    MySharePreferencesEdge.getBooleanValue(
                                        MySharePreferencesEdge.CHECKNOTCH, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHBOTTOM, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHHEIGHT, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSTOP, App.context
                                    ), MySharePreferencesEdge.getInt(
                                        MySharePreferencesEdge.NOTCHRADIUSBOTTOM, App.context
                                    )
                                )
                            }

                            edgeLightingView!!.changeColor(EdgeOverlaySettingsActivity.color)
                            edgeLightingView!!.visibility = View.VISIBLE


                            batteryAbove20 = true

                        }
                    }

                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                Log.d("error", "onReceive: ")
            }
        }

        @SuppressLint("WrongConstant")
        fun isMyServiceRunning(cls: Class<*>): Boolean {
            for (runningServiceInfo in (context.getSystemService("activity") as ActivityManager).getRunningServices(
                Int.MAX_VALUE
            )) {
                if (cls.name == runningServiceInfo.service.className) {
                    return true
                }
            }
            return false
        }
    }

    private fun setColorsAndPreferences(
        color1: Int, color2: Int, color3: Int, color4: Int, color5: Int, color6: Int
    ) {
        activity
        EdgeOverlaySettingsActivity.color[0] = color1
        EdgeOverlaySettingsActivity.color[1] = color2
        EdgeOverlaySettingsActivity.color[2] = color3
        EdgeOverlaySettingsActivity.color[3] = color4
        EdgeOverlaySettingsActivity.color[4] = color5
        EdgeOverlaySettingsActivity.color[5] = color6

    }


    inner class DetectChargerReceiver(val context: Context) : BroadcastReceiver() {

        private var isTimerReceiverRegistered = false

        override fun onReceive(context: Context, intent: Intent) {
            try {
                val action = intent.action
                if (action.equals(Intent.ACTION_POWER_CONNECTED, ignoreCase = true)) {
                    edgeLightingView?.visibility = View.GONE

                    if (isTimerReceiverRegistered && timerReceiver != null) {
                        context.unregisterReceiver(timerReceiver)
                        isTimerReceiverRegistered = false
                        timerReceiver = null
                    }

                    if (binding?.switchDisplay?.isChecked == true) {
                        AsynchronousTask("setEdgeBorderColor", activity!!).execute("")
                        edgeLightingView?.visibility = View.VISIBLE
                    } else {
                        MySharePreferencesEdge.setAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, false, App.context
                        )
                        binding?.apply {
                            edgeLightView.visibility = View.VISIBLE
                            edgeLightView.changeType(
                                MySharePreferencesEdge.getString(
                                    MySharePreferencesEdge.SHAPE, App.context
                                )
                            )
                            edgeLightView.changeColor(EdgeOverlaySettingsActivity.color)
                            edgeLightView.changeSize(
                                MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.SIZE, App.context
                                )
                            )
                            edgeLightView.changeSpeed(
                                MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.SPEED, App.context
                                )
                            )
                            edgeLightView.changeBorder(
                                MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.RADIUSTOP, App.context
                                ), MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.RADIUSBOTTOM, App.context
                                )
                            )
                            edgeLightView.changeNotch(
                                MySharePreferencesEdge.getBooleanValue(
                                    MySharePreferencesEdge.CHECKNOTCH, App.context
                                ), MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.NOTCHTOP, App.context
                                ), MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.NOTCHBOTTOM, App.context
                                ), MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.NOTCHHEIGHT, App.context
                                ), MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.NOTCHRADIUSTOP, App.context
                                ), MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.NOTCHRADIUSBOTTOM, App.context
                                )
                            )
                        }
                    }
                } else if (action.equals(Intent.ACTION_POWER_DISCONNECTED, ignoreCase = true)) {
                    batteryBroacastIsRegistered = false
                    binding?.edgeLightView?.visibility = View.INVISIBLE

                    MyAccessibilityService.apply {
                        edgeLightingView?.changeType(
                            MySharePreferencesEdge.getString(
                                MySharePreferencesEdge.SHAPE, App.context
                            )
                        )
                        edgeLightingView?.changeColor(EdgeOverlaySettingsActivity.color)
                        edgeLightingView?.changeSize(
                            MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.SIZE, App.context
                            )
                        )
                        edgeLightingView?.changeSpeed(
                            MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.SPEED, App.context
                            )
                        )
                        edgeLightingView?.changeBorder(
                            MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.RADIUSTOP, App.context
                            ), MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.RADIUSBOTTOM, App.context
                            )
                        )
                        edgeLightingView?.changeNotch(
                            MySharePreferencesEdge.getBooleanValue(
                                MySharePreferencesEdge.CHECKNOTCH, App.context
                            ), MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.NOTCHTOP, App.context
                            ), MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.NOTCHBOTTOM, App.context
                            ), MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.NOTCHHEIGHT, App.context
                            ), MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.NOTCHRADIUSTOP, App.context
                            ), MySharePreferencesEdge.getInt(
                                MySharePreferencesEdge.NOTCHRADIUSBOTTOM, App.context
                            )
                        )
                    }

                    if (!isTimerReceiverRegistered) {
                        timerReceiver = TimerReceiver()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            context.registerReceiver(
                                timerReceiver,
                                IntentFilter(TimerService.ACTION_TICK),
                                RECEIVER_EXPORTED
                            )
                        } else {
                            context.registerReceiver(
                                timerReceiver, IntentFilter(TimerService.ACTION_TICK)
                            )
                        }
                        isTimerReceiverRegistered = true
                    }

                    MySharePreferencesEdge.setAccessibilityEnabled(
                        MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, true, App.context
                    )
                }
            } catch (e: NullPointerException) {
                e.printStackTrace() // Handle the NullPointerException appropriately
            } catch (e: IllegalArgumentException) {
                e.printStackTrace() // Handle the IllegalArgumentException appropriately
            }
        }
    }


    companion object {
        var timerReceiver: TimerReceiver? = null
        var chargingBroacast: DetectChargerReceiver? = null
        var batteryBroacast: BatteryLevel? = null
        var service_context: Context? = null
        var batteryBroacastIsRegistered = false
        var batteryAbove20 = false
    }
}

