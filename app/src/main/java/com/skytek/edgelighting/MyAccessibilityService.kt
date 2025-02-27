package com.skytek.edgelighting

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.service.quicksettings.TileService
import android.util.Log
import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.preference.PreferenceManager

import com.skytek.WindowService
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.displayOverlay
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.drawOverlay
import com.skytek.edgelighting.service.AlwaysOnTileService
import com.skytek.edgelighting.service.ForegroundService
import com.skytek.edgelighting.service.TimerService
import com.skytek.edgelighting.thread.AsynchronousTask
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.Global
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.views.EdgeLightingView


class MyAccessibilityService : AccessibilityService() {


    // Declare phonePermissionReceiver as a class-level property
    private var phonePermissionReceiver: PhonePermissionButtonReceiver? = null


    private var height = 0
    private var mNotificationManager: NotificationManager? = null
    private var params: WindowManager.LayoutParams? = null
    private var width = 0
    private lateinit var prefsEditor: SharedPreferences.Editor
    private lateinit var orientationEventListener: OrientationEventListener
    override fun onCreate() {
        super.onCreate()

        // Declare a variable to keep track of the last known orientation
        orientationEventListener = object : OrientationEventListener(this) {
            private var isInPortrait = false // Flag to track if currently in portrait mode
            private var isInLandscape = false // Flag to track if currently in landscape mode

            override fun onOrientationChanged(orientation: Int) {

                val switchEnabled = MySharePreferencesEdge.getDisplayOverLayBooleanValue(
                    MySharePreferencesEdge.DISPLAY_OVERLAY, App.context
                )
                Log.d("notConnectedgjgg", "switchenabled${switchEnabled}: ")

                // Check if the new orientation is valid for handling
                if (orientation == ORIENTATION_UNKNOWN) return

                // Determine if the current orientation is portrait or landscape
                val currentOrientation = resources.configuration.orientation

                val isCurrentPortrait = currentOrientation != Configuration.ORIENTATION_LANDSCAPE
                val isCurrentLandscape = currentOrientation == Configuration.ORIENTATION_LANDSCAPE
                Log.d("isCurrentLandscapehgiu", "isCurrentPortrait: $isCurrentPortrait")
                Log.d("isCurrentLandscapehgiu", "isCurrentLandscape: $isCurrentLandscape")

                Log.d("isCurrentLandscapehgiu", "isInLandscape: $isInLandscape")
                Log.d("isCurrentLandscapehgiu", "isInPortrait: $isInPortrait")
                Log.d("isCurrentLandscapehgiu", "orientation: $orientation")

                // Only handle orientation changes if we're not currently in that orientation
                if (switchEnabled) {
                    if (isCurrentLandscape && !isInLandscape) {
                        Log.d("OrientationChange", "Entering Landscape")
                        // Remove the existing view before re-adding it with the new configuration
                        removeExistingView()

                        // Update dimensions
                        width = MySharePreferencesEdge.getInt(
                            MySharePreferencesEdge.HEIGHT, this@MyAccessibilityService
                        )
                        height = MySharePreferencesEdge.getInt(
                            MySharePreferencesEdge.WIDTH, this@MyAccessibilityService
                        )

                        // Check the phone permission and handle the state accordingly
                        val yourLocked = checkPhonePermission()
                        if (yourLocked) {
                            handleLockedState()
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                handleUnlockedState()
                            }
                        }
                        // Update flags
                        isInPortrait = false
                        isInLandscape = true

                    } else if (isCurrentPortrait && !isInPortrait) {
                        Log.d("OrientationChange", "Entering Portrait")
                        // Remove the existing view before re-adding it with the new configuration
                        removeExistingView()

                        // Update dimensions
                        width = MySharePreferencesEdge.getInt(
                            MySharePreferencesEdge.WIDTH, this@MyAccessibilityService
                        )
                        height = MySharePreferencesEdge.getInt(
                            MySharePreferencesEdge.HEIGHT, this@MyAccessibilityService
                        )

                        // Check the phone permission and handle the state accordingly
                        val yourLocked = checkPhonePermission()
                        if (yourLocked) {
                            handleLockedState()
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                handleUnlockedState()
                            }
                        }
                        // Update flags
                        isInPortrait = true
                        isInLandscape = false

                    } else {
                        Log.d(
                            "OrientationChange",
                            "Ignoring orientation change, already in the same mode"
                        )
                    }
                } else {
                    Log.e("notConnectedgjgg", "notConnectedgjgg: ")
                }
            }
        }


        /*        orientationEventListener = object : OrientationEventListener(this) {
                    override fun onOrientationChanged(orientation: Int) {
                        Log.d(TAG, "received->$BCAST_CONFIGCHANGED")
                        val switchenabled = MySharePreferencesEdge.getDisplayOverLayBooleanValue(
                            MySharePreferencesEdge.DISPLAY_OVERLAY, App.context
                        )
                        Log.d("notConnectedgjgg", "switchenabled${switchenabled}: ")

                        // Check if the new orientation is valid for handling
                        if (orientation == ORIENTATION_UNKNOWN) return

                        // Determine if the current orientation is portrait or landscape
                        val isCurrentPortrait = orientation in 315..360 || orientation in 0..45
                        val isCurrentLandscape = orientation in 45..135 || orientation in 225..315
                        Log.d("isCurrentLandscapehgiu", "isCurrentPortrait: ${isCurrentPortrait}")
                        Log.d("isCurrentLandscapehgiu", "isCurrentLandscape: ${isCurrentLandscape}")
                        Log.d("isCurrentLandscapehgiu", "lastOrientation: ${lastOrientation}")
                        Log.d("isCurrentLandscapehgiu", "orientation: ${orientation}")
                        // Execute only if the orientation has changed significantly
                        if (switchenabled && lastOrientation != orientation) {
                            if (isCurrentPortrait && lastOrientation != orientation) {
                                Log.d("OrientationChange", "Portrait")
                                // Remove the existing view before re-adding it with the new configuration
                                removeExistingView()

                                val yourLocked = checkPhonePermission()
                                width = MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.WIDTH, this@MyAccessibilityService
                                )
                                height = MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.HEIGHT, this@MyAccessibilityService
                                )
                                if (yourLocked) {
                                    handleLockedState()
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        handleUnlockedState()
                                    }
                                }
                            } else if (isCurrentLandscape && lastOrientation != orientation) {
                                Log.d("OrientationChange", "Landscape")
                                width = MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.HEIGHT, this@MyAccessibilityService
                                )
                                height = MySharePreferencesEdge.getInt(
                                    MySharePreferencesEdge.WIDTH, this@MyAccessibilityService
                                )
                                // Remove the existing view before re-adding it with the new configuration
                                removeExistingView()

                                val yourLocked = checkPhonePermission()

                                if (yourLocked) {
                                    handleLockedState()
                                } else {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        handleUnlockedState()
                                    }
                                }
                            }

                            // Update the last known orientation
                            lastOrientation = orientation
                        } else {
                            Log.e("notConnectedgjgg", "notConnectedgjgg: ")
                        }
                    }
                }*/


    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        try {
            Log.d("tyutyut", "4")
            windowManager = getSystemService("window") as WindowManager
            mNotificationManager = getSystemService("notification") as NotificationManager

            subView = LayoutInflater.from(this)
                .inflate(R.layout.layout_windowmananger_edge, null as ViewGroup?)
            val layoutParams = WindowManager.LayoutParams()
            params = layoutParams

            val i = width
            if (i != 0) {
                layoutParams.width = i
            } else {
                layoutParams.width = -1
            }
            val i2 = height
            if (i2 != 0) {
                params?.height = i2
            } else {
                params?.height = -1
            }
            if (Build.VERSION.SDK_INT >= 26) {
                params?.type = 2038
            } else {
                params?.type = 2010
            }

            val layoutParams2 = params
            layoutParams2?.gravity = 48
            layoutParams2?.format = -2
            layoutParams2?.type = if (Build.VERSION.SDK_INT >= 25) 2032 else 2005
            layoutParams3 = params
            layoutParams3?.flags = 824
            layoutParams3?.alpha = 0.8f
            subView?.systemUiVisibility = 5122
            windowManager?.addView(subView, layoutParams3)
            edgeLightingView =
                subView?.findViewById<View>(R.id.edvLightColorWindow) as EdgeLightingView


        } catch (e: WindowManager.BadTokenException) {
            Log.d("tyutyut", "5")
        }

    }


    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        Log.d(TAG, "onAccessibilityEvent: ")
    }

    override fun onInterrupt() {
        Toast.makeText(this, "on interreput", Toast.LENGTH_LONG).show()
    }

    class LateClass {
        lateinit var listenerChangeWindowManager: ListenerChangeWindowManager
        val isThingInitialized get() = this::listenerChangeWindowManager.isInitialized
    }


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onDestroy() {
        Log.d("whatHappenIsWithService", "onDestroy: Service is off ")

        try {
            phonePermissionReceiver?.let { unregisterReceiver(it) }
        } catch (e: IllegalArgumentException) {
            Log.d("helloNullCheck", "onDestroy: Receiver not registered")
        }

        try {
            // Save preferences
            MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                MySharePreferencesEdge.DISPLAY_OVERLAY, false, applicationContext
            )
            MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                MySharePreferencesEdge.SWITCH_CHARGING, false, applicationContext
            )

            // Disable service if supported
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                instance?.disableSelf()
            }

            // Stop additional services
            stopService(Intent(this, TimerService::class.java))
            stopService(Intent(this, WindowService::class.java))

            // Perform cleanup for EdgeOverlaySettingsActivity
            EdgeOverlaySettingsActivity.activity?.let {
                AsynchronousTask("setEdgeBorderColor", it).execute("")
            }
        } catch (e: NullPointerException) {
            Log.e("onDestroy", "Error occurred during onDestroy", e)
        }

        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onServiceConnected() {
        Log.d("AccessibilityService", "onServiceConnected: Service Connected")
        super.onServiceConnected()
        // Enable the listener if it can detect orientation
        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        }
        initPreferences()

        val yourLocked = checkPhonePermission()
        this.width = MySharePreferencesEdge.getInt(MySharePreferencesEdge.WIDTH, this)
        this.height = MySharePreferencesEdge.getInt(MySharePreferencesEdge.HEIGHT, this)
        if (yourLocked) {
            handleLockedState()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                handleUnlockedState()

            }
        }
        late?.listenerChangeWindowManager = ListenerChangeWindowManager()
        changeWindowManager = late?.listenerChangeWindowManager

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                registerReceiver(
                    late?.listenerChangeWindowManager,
                    IntentFilter(Const.Action_ChangeWindownManager),
                    RECEIVER_EXPORTED
                )
            }
        } catch (e: Exception) {
            Log.e("AccessibilityService", "setupEdgeLighting: Error registering receiver", e)
        }
        try {
            MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                MySharePreferencesEdge.DISPLAY_OVERLAY, true, App.context
            )


        } catch (e: NullPointerException) {
            Log.e(
                "AccessibilityService", "setupEdgeLighting: Error setting display overlay", e
            )
        }
        instance = this
        drawOverlay = true
        MySharePreferencesEdge.setAccessibilityEnabled(
            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, true, App.context
        )
        startForegroundService()
    }

    private fun initPreferences() {

        late = LateClass()

    }

    private fun checkPhonePermission(): Boolean {
        val sharedPreferences = getSharedPreferences("phone_permission", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("phone_permission", false)
    }

    private fun handleLockedState() {
        Log.d("AccessibilityService", "handleLockedState: Phone permission locked")

        if (!MySharePreferencesEdge.getAccessibilityEnabled(
                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, App.context
            )
        ) {
            MySharePreferencesEdge.setAccessibilityEnabled(
                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, true, App.context
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleUnlockedState() {
        Log.d("AccessibilityService", "handleUnlockedState: Phone permission unlocked")


        Log.d("AccessibilityService", "handleUnlockedState: Initializing overlay settings")
        setupEdgeLighting()


    }

    // Function to remove the previous view
    private fun removeExistingView() {
        try {
            if (subView != null && windowManager != null) {
                windowManager?.removeView(subView)
                subView = null
                edgeLightingView = null
            }
        } catch (e: IllegalArgumentException) {
            // Handle cases where the view might not exist or is already removed
            Log.d("MyAccessibilityService", "View was already removed or not present: ${e.message}")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setupEdgeLighting() {

        initView()


        setupEdgeLightingView()

        setupBindings()
    }


    private fun setupBindings() {
        try {
            binding?.edgeLightView?.visibility = View.GONE
            setupAlwaysOnDisplay()
        } catch (e: NullPointerException) {
            Log.e("AccessibilityService", "setupBindings: Error setting up bindings", e)
        }
    }

    private fun setupAlwaysOnDisplay() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        prefsEditor = prefs.edit()
        if (binding?.swtichAlways?.isChecked == true) {
            prefs.edit().putBoolean("always_on", true).apply()
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                TileService.requestListeningState(
                    applicationContext, ComponentName(this, AlwaysOnTileService::class.java)
                )
            }

            ContextCompat.startForegroundService(
                applicationContext, Intent(this, ForegroundService::class.java)
            )
            LocalBroadcastManager.getInstance(applicationContext)
                .sendBroadcast(Intent().setAction(Global.ALWAYS_ON_STATE_CHANGED))

            prefsEditor.putBoolean("setup_complete", true).apply()
            MySharePreferencesEdge.putAlwaysOnDisplayBooleanValue(
                MySharePreferencesEdge.ALWAYS_ON_DISPLAY, true, EdgeOverlaySettingsActivity.activity
            )

        }
    }

    private fun startForegroundService() {
        try {
            binding?.let { obj ->
                obj.edgeLightView.visibility = View.GONE
                if (obj.switchCharging.isChecked) {
                    MySharePreferencesEdge.putAlwaysOnDisplayBooleanValue(
                        MySharePreferencesEdge.SHOW_WHILE_CHARGING, true, App.context
                    )
                    startService(Intent(this, WindowService::class.java))

                }

            }

        } catch (e: NullPointerException) {
            Log.e(
                "AccessibilityService",
                "startForegroundService: Error starting foreground service",
                e
            )
        }

        startForeground(Const.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification())
    }


    private fun setupEdgeLightingView() {
        try {
            val warningValuesSharedPreferences: SharedPreferences =
                getSharedPreferences("WARNING_VALUES", Context.MODE_PRIVATE)
            val warningMaxVal: Int = warningValuesSharedPreferences.getInt("WARNING_MAX", 15)
            val warningMinwal: Int = warningValuesSharedPreferences.getInt("WARNING_MIN", 5)
            Log.d(
                "notConnectedgjgg",
                "binding?.switchDisplay?.isChecked==true${binding?.switchDisplay?.isChecked == true}: "
            )

            Log.d(TAG, "setupEdgeLightingView: ")
            Log.d(
                "AccessibilityService",
                "setupEdgeLighting: Error registering receiver${edgeLightingView}"
            )

            var color1 = if (MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR1_VALUE, this
                ) == -1
            ) {
                -65536
            } else {
                MySharePreferencesEdge.getIntValue(MySharePreferencesEdge.COLOR1_VALUE, this)
            }
            val color2 = if (MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR2_VALUE, this
                ) == -1
            ) {
                -16776961
            } else {
                MySharePreferencesEdge.getIntValue(MySharePreferencesEdge.COLOR2_VALUE, this)
            }
            val color3 = if (MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR3_VALUE, this
                ) == 1
            ) {
                -65281
            } else {
                MySharePreferencesEdge.getIntValue(MySharePreferencesEdge.COLOR3_VALUE, this)
            }

            val color4 = if (MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR4_VALUE, this
                ) == -1
            ) {
                -65536
            } else {
                MySharePreferencesEdge.getIntValue(MySharePreferencesEdge.COLOR4_VALUE, this)
            }
            val color5 = if (MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR5_VALUE, this
                ) == -1
            ) {
                -16776961
            } else {
                MySharePreferencesEdge.getIntValue(MySharePreferencesEdge.COLOR5_VALUE, this)
            }
            val color6 = if (MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR6_VALUE, this
                ) == 1
            ) {
                -65281
            } else {
                MySharePreferencesEdge.getIntValue(MySharePreferencesEdge.COLOR6_VALUE, this)
            }
            var colorArray = IntArray(6)
            val batteryPct = warningValuesSharedPreferences.getInt("BATTERY_PERCENTAGE", 0)
            Log.d("kyeBatteryHqai", "batteryPct:${batteryPct} ")
            Log.d("kyeBatteryHqai", "warningMaxVal:${warningMaxVal} ")
            Log.d("kyeBatteryHqai", "warningMinwal:${warningMinwal} ")
            val switch = MySharePreferencesEdge.getDisplayOverLayBooleanValue(
                MySharePreferencesEdge.SHOW_WHILE_CHARGING, App.context
            )
            if (switch && (batteryPct > warningMinwal && batteryPct <= warningMaxVal)) {
                val c = Color.parseColor("#FFAC1C")
                colorArray = intArrayOf(c, c, c, c, c, c)
            } else if (switch && batteryPct <= warningMinwal) {
                val c = Color.parseColor("#EB1111")
                colorArray = intArrayOf(c, c, c, c, c, c)
            } else {
                colorArray = intArrayOf(color1, color2, color3, color4, color5, color6)
            }
            val iArr = colorArray
            val _type = MySharePreferencesEdge.getString(MySharePreferencesEdge.SHAPE, this)
            val _size = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SIZE, this)
            val _speed = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SPEED, this)
            val _radiusTop = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSTOP, this)
            val _radiusBottom =
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSBOTTOM, this)
            val _checkNotch =
                MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.CHECKNOTCH, this)
            val _notchTop = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHTOP, this)
            val _notchBottom =
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHBOTTOM, this)
            val _notchRadiusTop =
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSTOP, this)
            val _notchRadiusBottom =
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, this)
            val _notchHeight =
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHHEIGHT, this)

            edgeLightingView?.apply {
                setShape(Const.Action_DemoLiveWallpaper)

                changeNotch(Const.Action_DemoLiveWallpaper)
                changeSize(Const.Action_DemoLiveWallpaper)
                changeSpeed(Const.Action_DemoLiveWallpaper)
                changeRadius(Const.Action_DemoLiveWallpaper)
                changeColor(iArr)
            }
            val v = LayoutInflater.from(this)
                .inflate(R.layout.activity_edge_overlay_settings, null as ViewGroup?)
            val egView = v.findViewById<EdgeLightingView>(R.id.edge_light_view)
            Log.d(
                "whatIsVisibilty",
                "setupEdgeLightingView: ${edgeLightingView?.visibility == View.VISIBLE} "
            )

            Log.d("whatIsVisibilty", "edgeLightingView != null: ${edgeLightingView != null} ")

        } catch (e: NullPointerException) {
            Log.e(
                "AccessibilityService",
                "setupEdgeLightingView: Error setting up edge lighting view",
                e
            )
        }

        params?.width = 1080
        params?.height = 1920
    }

    var intent: Intent? = null
    var intent2: Intent? = null
    var remoteViews: RemoteViews? = null
    var activity: PendingIntent? = null

    @SuppressLint("WrongConstant")
    private fun prepareNotification(): Notification {
        val builder: NotificationCompat.Builder
        Log.d(TAG, "prepareNotification: ")
        if (Build.VERSION.SDK_INT >= 26 && mNotificationManager?.getNotificationChannel(
                FOREGROUND_CHANNEL_ID
            ) == null
        ) {
            val notificationChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID, getString(R.string.text_name_notification), 3
            )
            notificationChannel.enableVibration(false)
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }

        try {
            Log.d(TAG, "prepareNotification: 12 ")
            intent = Intent(this, EdgeOverlaySettingsActivity::class.java)
            intent?.action = Const.ACTION.MAIN_ACTION
            intent?.flags = 268468224
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                activity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                activity =
                    PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            }


//            if(MySharePreferencesEdge.getDisplayOverLayBooleanValue(MySharePreferencesEdge.DISPLAY_OVERLAY , this))
//            {
//
//            }
            Log.d(TAG, "prepareNotification:123 ")
            intent2 = Intent(this, WallpaperWindowEdgeService::class.java)
            intent2?.action = Const.ACTION.STOP_ACTION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Log.d(TAG, "prepareNotification:  if")
                PendingIntent.getService(this, 0, intent2!!, PendingIntent.FLAG_IMMUTABLE)
            } else {
                Log.d(TAG, "prepareNotification:  else")
                PendingIntent.getService(this, 0, intent2!!, PendingIntent.FLAG_UPDATE_CURRENT)
            }

            remoteViews = RemoteViews(packageName, R.layout.layout_notification_service_edge)
            remoteViews?.setOnClickPendingIntent(R.id.lnOverlayWindow, activity)
        } catch (e: Exception) {
        }
        builder = if (Build.VERSION.SDK_INT >= 26) {
            NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }
        Log.d(TAG, "prepareNotificat1231232312ion: ")
        builder.setContent(remoteViews).setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(NotificationCompat.CATEGORY_SERVICE).setOnlyAlertOnce(true)
            .setOngoing(true).setAutoCancel(true).setContentIntent(activity)
        if (Build.VERSION.SDK_INT >= 21) {
            builder.setVisibility(1)
        }
        return builder.build()
    }

    inner class ListenerChangeWindowManager : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("successFullyRun", "ListenerChangeWindowManager: ${intent.action}")
            try {
                var stringExtra = ""
                if ((intent.action == Const.Action_ChangeWindownManager) && (intent.getStringExtra(
                        Const.CONTROLWINDOW
                    ).also { stringExtra = it!! } != null)
                ) {
                    if (stringExtra == Const.COLOR) {
                        edgeLightingView!!.changeColor(Const.Action_DemoLiveWallpaper)
                    } else if (stringExtra == Const.BORDER) {
                        edgeLightingView!!.changeSize(Const.Action_DemoLiveWallpaper)
                        edgeLightingView!!.changeSpeed(Const.Action_DemoLiveWallpaper)
                        edgeLightingView!!.changeRadius(Const.Action_DemoLiveWallpaper)
                    } else if (stringExtra == Const.NOTCH) {
                        edgeLightingView!!.changeNotch(Const.Action_DemoLiveWallpaper)
                    } else {
                        edgeLightingView!!.changeColor(Const.Action_DemoLiveWallpaper)
                        edgeLightingView!!.changeSize(Const.Action_DemoLiveWallpaper)
                        edgeLightingView!!.changeSpeed(Const.Action_DemoLiveWallpaper)
                        edgeLightingView!!.changeRadius(Const.Action_DemoLiveWallpaper)
                        edgeLightingView!!.changeNotch(Const.Action_DemoLiveWallpaper)
                    }
                    edgeLightingView!!.setShape(Const.Action_DemoLiveWallpaper)
                }
            } catch (e: NullPointerException) {
            }
        }
    }

    inner class PhonePermissionButtonReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            Log.d("successFullyRun", "PhonePermissionButtonReceiver: ${intent!!.action}")
            if (intent.action == "PHONE_PERMISSION_BUTTON_ACTION") {
                // Check if binding is not null and switchDisplay is not null
                binding?.let { safeBinding ->
                    safeBinding.switchDisplay.isChecked = false
                    if (!MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST,
                            this@MyAccessibilityService
                        )
                    ) {
                        MySharePreferencesEdge.setAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST,
                            true,
                            this@MyAccessibilityService
                        )
                    }
                }
            }
        }

    }
//    inner class BootCompletedReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent) {
//            Log.d("successFullyRun", "onReceive: ${intent.action}")
//            if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
//                // Ensure the service starts after reboot
//                Log.d("successFullyRun", "onReceive: iff ")
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    setupEdgeLighting()
//                }
//
//            }
//        }
//    }

    companion object {
        const val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
        const val TAG = "MyWallpaperWindowMServi"
        var changeWindowManager: ListenerChangeWindowManager? = null
        var edgeLightingView: EdgeLightingView? = null
        var windowManager: WindowManager? = null
        var layoutParams3: WindowManager.LayoutParams? = null
        var subView: View? = null
        var instance: MyAccessibilityService? = null
        var late: LateClass? = null

    }
}


