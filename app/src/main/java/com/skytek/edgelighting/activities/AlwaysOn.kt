package com.skytek.edgelighting.activities

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.SensorManager
import android.media.AudioManager
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.preference.PreferenceManager.getDefaultSharedPreferences
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.skytek.edgelighting.Listeners.AlwaysOnOnActiveSessionsChangedListener
import com.skytek.edgelighting.Listeners.AlwaysOnSensorEventListener
import com.skytek.edgelighting.R
import com.skytek.edgelighting.receiver.CombinedServiceReceiver
import com.skytek.edgelighting.service.NotificationService
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.utils.Rules
import com.skytek.edgelighting.utils.T
import com.skytek.edgelighting.views.AlwaysOnViewHolder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar

class AlwaysOn : OffActivity(), NotificationService.OnNotificationsChangedListener {

    companion object {
        private const val SENSOR_DELAY_SLOW: Int = 1000000
        private var instance: AlwaysOn? = null

        fun finish() {
            instance?.finish()
        }
    }

    @JvmField
    internal var servicesRunning: Boolean = false

    @JvmField
    internal var screenSize: Float = 0F

    private var offsetX: Float = 0f
    internal lateinit var viewHolder: AlwaysOnViewHolder
    internal lateinit var prefs: T

    //Threads
    private var aoEdgeGlowThread: Thread = Thread()
    private var animationThread: Thread = Thread()

    //Media Controls
    private var onActiveSessionsChangedListener: AlwaysOnOnActiveSessionsChangedListener? = null

    //Notifications
    @JvmField
    internal var notificationAvailable: Boolean = false

    //Battery saver
    private var userPowerSaving: Boolean = false

    //Proximity
    private var sensorManager: SensorManager? = null
    private var sensorEventListener: AlwaysOnSensorEventListener? = null

    //DND
    private var notificationManager: NotificationManager? = null
    private var notificationAccess: Boolean = false
    private var userDND: Int = NotificationManager.INTERRUPTION_FILTER_ALL

    //Call recognition
    private var onModeChangedListener: AudioManager.OnModeChangedListener? = null

    //Rules
    private val rulesHandler: Handler = Handler(Looper.getMainLooper())

    //BroadcastReceiver
    private val systemFilter: IntentFilter = IntentFilter()
    private val systemReceiver = object : BroadcastReceiver() {

        override fun onReceive(c: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                    if (level <= prefs.get(T.RULES_BATTERY, T.RULES_BATTERY_DEFAULT)) {
                        finishAndOff()
                        return
                    } else if (!servicesRunning) {
                        return
                    }
//                    viewHolder.customView.setBatteryStatus(
//                        level,
//                        intent.getIntExtra(
//                            BatteryManager.EXTRA_STATUS,
//                            -1
//                        ) == BatteryManager.BATTERY_STATUS_CHARGING
//                    )
                }

                Intent.ACTION_POWER_CONNECTED -> {
                    if (!Rules.matchesChargingState(this@AlwaysOn, prefs.prefs)) finishAndOff()
                }

                Intent.ACTION_POWER_DISCONNECTED -> {
                    if (!Rules.matchesChargingState(this@AlwaysOn, prefs.prefs)) finishAndOff()
                }
            }
        }
    }

    private var LAST_CLICK_TIME: Long = 0
    private val mDoubleClickInterval = 400

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StatusBarUtil.setTransparent(this)
        instance = this

        //Check prefs
        prefs = T(getDefaultSharedPreferences(this))

        //Cutouts
        if (prefs.get("hide_display_cutouts", false)) setTheme(R.style.CutoutHide)
        else setTheme(R.style.CutoutIgnore)

        setContentView(R.layout.activity_always_on)

        this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN)

        //View
        viewHolder = AlwaysOnViewHolder(this)
//        viewHolder.customView.scaleX = prefs.displayScale()
//        viewHolder.customView.scaleY = prefs.displayScale()
//        if (prefs.get(P.USER_THEME, P.USER_THEME_DEFAULT) == P.USER_THEME_SAMSUNG2) {
//            val size = Point()
//            (getSystemService(Context.DISPLAY_SERVICE) as DisplayManager)
//                .getDisplay(Display.DEFAULT_DISPLAY)
//                .getSize(size)
//            offsetX = (size.x - size.x * prefs.displayScale()) * (-.5f)
//            viewHolder.customView.translationX = offsetX
//        }

        viewHolder.rl.setOnClickListener {
            val doubleClickCurrentTime = System.currentTimeMillis()
            val currentClickTime = System.currentTimeMillis()
            if (currentClickTime - LAST_CLICK_TIME <= mDoubleClickInterval) {
//                Toast.makeText(this, "Double click detected", Toast.LENGTH_SHORT).show()
                AlwaysOn.finish()
            } else {
                LAST_CLICK_TIME = System.currentTimeMillis()
            }
        }

        Log.d("aweass", "kjkj")

        //Brightness
        if (prefs.get(T.FORCE_BRIGHTNESS, T.FORCE_BRIGHTNESS_DEFAULT)) {
            window.attributes.screenBrightness =
                prefs.get("ao_force_brightness_value", 50) / 255.toFloat()
        }

        //Variables
        userPowerSaving = (getSystemService(Context.POWER_SERVICE) as PowerManager).isPowerSaveMode

        //Show on lock screen
//        Handler(Looper.getMainLooper()).postDelayed({
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        turnOnScreen()
//        }, 300L)

        if (!MySharePreferencesEdge.getShowChargingBooleanValue(
                MySharePreferencesEdge.SHOW_WHILE_CHARGING,
                this@AlwaysOn
            )
        ) {
//        if(!EdgeOverlaySettingsActivity.binding!!.switchCharging.isChecked){
            Log.d(
                "usd",
                "onStop: " + MySharePreferencesEdge.getString(
                    MySharePreferencesEdge.SHAPE,
                    this@AlwaysOn
                )
            )
            viewHolder.edgeLightingView.visibility = View.VISIBLE
            viewHolder.edgeLightingView.changeType(
                MySharePreferencesEdge.getString(
                    MySharePreferencesEdge.SHAPE,
                    this@AlwaysOn
                )
            )
            viewHolder.edgeLightingView.changeColor(EdgeOverlaySettingsActivity.color)
            viewHolder.edgeLightingView.changeSize(
                MySharePreferencesEdge.getInt(
                    MySharePreferencesEdge.SIZE,
                    this@AlwaysOn
                )
            )
            viewHolder.edgeLightingView.changeSpeed(
                MySharePreferencesEdge.getInt(
                    MySharePreferencesEdge.SPEED,
                    this@AlwaysOn
                )
            )
            viewHolder.edgeLightingView.changeBorder(
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSTOP, this@AlwaysOn),
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSBOTTOM, this@AlwaysOn)
            )
            viewHolder.edgeLightingView.changeNotch(
                MySharePreferencesEdge.getBooleanValue(
                    MySharePreferencesEdge.CHECKNOTCH,
                    this@AlwaysOn
                ),
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHTOP, this@AlwaysOn),
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHBOTTOM, this@AlwaysOn),
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHHEIGHT, this@AlwaysOn),
                MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSTOP, this@AlwaysOn),
                MySharePreferencesEdge.getInt(
                    MySharePreferencesEdge.NOTCHRADIUSBOTTOM,
                    this@AlwaysOn
                )
            )
        } else {
            viewHolder.edgeLightingView.visibility = View.GONE
        }

        viewHolder.time.format24Hour = "hh:mm a"
        val dateFormat: DateFormat = SimpleDateFormat("EE, dd MMM")
        viewHolder.date.text = dateFormat.format(Calendar.getInstance().time)

        //Hide UI
//        fullscreen(viewHolder.edgeLightingView)
//        window.decorView.setOnSystemUiVisibilityChangeListener { visibility ->
//            if (visibility and View.SYSTEM_UI_FLAG_FULLSCREEN == 0)
//                fullscreen(viewHolder.edgeLightingView)
//        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        /*window.decorView.setOnApplyWindowInsetsListener { _, windowInsets ->
            if (WindowInsetsCompat.toWindowInsetsCompat(windowInsets).isVisible(
                    WindowInsetsCompat.Type.statusBars()
                            or WindowInsetsCompat.Type.captionBar()
                            or WindowInsetsCompat.Type.navigationBars()
                )
            ) fullscreen(viewHolder.frame)
            windowInsets
        }*/

        //Battery
        systemFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        systemFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        systemFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)

        //Music Controls
//        if (prefs.get(P.SHOW_MUSIC_CONTROLS, P.SHOW_MUSIC_CONTROLS_DEFAULT)) {
//            val mediaSessionManager =
//                getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
//            val notificationListener =
//                ComponentName(applicationContext, NotificationService::class.java.name)
//            onActiveSessionsChangedListener =
//                AlwaysOnOnActiveSessionsChangedListener(viewHolder.customView)
//            try {
//                mediaSessionManager.addOnActiveSessionsChangedListener(
//                    onActiveSessionsChangedListener
//                        ?: return, notificationListener
//                )
//                onActiveSessionsChangedListener?.onActiveSessionsChanged(
//                    mediaSessionManager.getActiveSessions(
//                        notificationListener
//                    )
//                )
//            } catch (e: Exception) {
//                Log.e(Global.LOG_TAG, e.toString())
//                viewHolder.customView.musicString =
//                    resources.getString(R.string.missing_permissions)
//            }
//            viewHolder.customView.onTitleClicked = {
//                if (onActiveSessionsChangedListener?.state == PlaybackState.STATE_PLAYING) onActiveSessionsChangedListener?.controller?.transportControls?.pause()
//                else if (onActiveSessionsChangedListener?.state == PlaybackState.STATE_PAUSED) onActiveSessionsChangedListener?.controller?.transportControls?.play()
//            }
//            viewHolder.customView.onSkipPreviousClicked = {
//                onActiveSessionsChangedListener?.controller?.transportControls?.skipToPrevious()
//            }
//            viewHolder.customView.onSkipNextClicked = {
//                onActiveSessionsChangedListener?.controller?.transportControls?.skipToNext()
//            }
//        }

        //Notifications
        if (prefs.get(
                T.SHOW_NOTIFICATION_COUNT,
                T.SHOW_NOTIFICATION_COUNT_DEFAULT
            ) || prefs.get(
                T.SHOW_NOTIFICATION_ICONS,
                T.SHOW_NOTIFICATION_ICONS_DEFAULT
            ) || prefs.get(T.EDGE_GLOW, T.EDGE_GLOW_DEFAULT)
        ) {
            NotificationService.listeners.add(this)
        }

        //Fingerprint icon
//        if (prefs.get(P.SHOW_FINGERPRINT_ICON, P.SHOW_FINGERPRINT_ICON_DEFAULT)) {
//            viewHolder.fingerprintIcn.visibility = View.VISIBLE
//            (viewHolder.fingerprintIcn.layoutParams as ViewGroup.MarginLayoutParams)
//                .bottomMargin = prefs.get(P.FINGERPRINT_MARGIN, P.FINGERPRINT_MARGIN_DEFAULT)
//            val longPressDetector = LongPressDetector({
//                finish()
//            })
//            viewHolder.fingerprintIcn.setOnTouchListener { v, event ->
//                longPressDetector.onTouchEvent(event)
//                v.performClick()
//            }
//        }

        //Proximity
        if (prefs.get(T.POCKET_MODE, T.POCKET_MODE_DEFAULT)) {
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensorEventListener = AlwaysOnSensorEventListener(viewHolder)
        }

        //DND
        if (prefs.get(T.DO_NOT_DISTURB, T.DO_NOT_DISTURB_DEFAULT)) {
            notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationAccess = notificationManager?.isNotificationPolicyAccessGranted ?: false
            }
            if (notificationAccess) if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                userDND = notificationManager?.currentInterruptionFilter
                    ?: NotificationManager.INTERRUPTION_FILTER_ALL
            }
        }
        //Call recognition
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            onModeChangedListener = AudioManager.OnModeChangedListener { mode ->
                if (mode == AudioManager.MODE_RINGTONE) finish()
            }
            (getSystemService(AUDIO_SERVICE) as AudioManager).addOnModeChangedListener(
                mainExecutor, onModeChangedListener ?: throw IllegalStateException()
            )
        }

        //Broadcast Receivers
        registerReceiver(systemReceiver, systemFilter)

    }

    override fun onDestroy() {
        Log.d("usd", "onDestroy: ")
        super.onDestroy()
        instance = null
        CombinedServiceReceiver.isAlwaysOnRunning = false
        if (prefs.get(T.EDGE_GLOW, T.EDGE_GLOW_DEFAULT)) aoEdgeGlowThread.interrupt()
        animationThread.interrupt()
        if (prefs.get(
                T.SHOW_NOTIFICATION_COUNT,
                T.SHOW_NOTIFICATION_COUNT_DEFAULT
            ) || prefs.get(
                T.SHOW_NOTIFICATION_ICONS,
                T.SHOW_NOTIFICATION_ICONS_DEFAULT
            ) || prefs.get(T.EDGE_GLOW, T.EDGE_GLOW_DEFAULT)
        ) {
            NotificationService.listeners.remove(this)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && onModeChangedListener != null) {
            (getSystemService(AUDIO_SERVICE) as AudioManager).removeOnModeChangedListener(
                onModeChangedListener ?: throw IllegalStateException()
            )
        }
        unregisterReceiver(systemReceiver)
    }

    override fun finishAndOff() {
        CombinedServiceReceiver.hasRequestedStop = true
        super.finishAndOff()
    }

    override fun onNotificationsChanged() {
        if (!servicesRunning) return
    }

}

