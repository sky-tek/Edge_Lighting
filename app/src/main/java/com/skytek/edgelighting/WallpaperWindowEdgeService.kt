package com.skytek.edgelighting

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.app.PendingIntent.getActivity
import android.app.PendingIntent.getService
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.views.EdgeLightingView

class WallpaperWindowEdgeService : Service() {

    private var edgeLightingView: EdgeLightingView? = null
    private var height = 0
    private var mNotificationManager: NotificationManager? = null
    private var params: WindowManager.LayoutParams? = null
    private var subView: View? = null
    private var width = 0
    private var windowManager: WindowManager? = null

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("isuuueeeeeeeeeeeeeee", "onCreate:yyyyyyyyyyyyyyyyyyyyyyyyyyyyyy ")
        initView()
    }

    @SuppressLint("WrongConstant")
    private fun initView() {
        windowManager = getSystemService("window") as WindowManager

        mNotificationManager = getSystemService("notification") as NotificationManager
        width = MySharePreferencesEdge.getInt(MySharePreferencesEdge.WIDTH, baseContext)
        height = MySharePreferencesEdge.getInt(MySharePreferencesEdge.HEIGHT, baseContext)
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
            params!!.height = i2
        } else {
            params!!.height = -1
        }
        if (Build.VERSION.SDK_INT >= 26) {
            params!!.type = 2038
        } else {
            params!!.type = 2010
        }

        val layoutParams2 = params
        layoutParams2!!.gravity = 48
        layoutParams2.format = -2
        layoutParams2.flags = 824

        subView!!.systemUiVisibility = 5122
        windowManager!!.addView(subView, params)
        edgeLightingView =
            subView!!.findViewById<View>(R.id.edvLightColorWindow) as EdgeLightingView

        Log.d("dfcvjklwf", "wallpaer service")

        edgeLightingView!!.setShape(Const.Action_DemoLiveWallpaper)
        edgeLightingView!!.changeColor(Const.Action_DemoLiveWallpaper)
        edgeLightingView!!.changeNotch(Const.Action_DemoLiveWallpaper)
        edgeLightingView!!.changeSize(Const.Action_DemoLiveWallpaper)
        edgeLightingView!!.changeSpeed(Const.Action_DemoLiveWallpaper)
        edgeLightingView!!.changeRadius(Const.Action_DemoLiveWallpaper)
        params!!.width = 1080
        params!!.height = 1920
        windowManager!!.updateViewLayout(subView, params)
        prepareNotification()

    }

    @SuppressLint("WrongConstant")
    override fun onConfigurationChanged(configuration: Configuration) {
        super.onConfigurationChanged(configuration)
        if (width == 0) {
            width = params!!.width
        }
        if (height == 0) {
            height = params!!.height
        }
        if (configuration.orientation == 2) {
            if (windowManager!!.defaultDisplay.rotation == 1) {
                edgeLightingView!!.rotationY = 180.0f
            } else {
                edgeLightingView!!.rotationY = 0.0f
            }
            val layoutParams = params
            layoutParams!!.width = height
            layoutParams.height = width
            edgeLightingView!!.changeRotate(true)
        } else if (configuration.orientation == 1) {
            val layoutParams2 = params
            layoutParams2!!.width = width
            layoutParams2.height = height
            edgeLightingView!!.changeRotate(false)
            edgeLightingView!!.rotationY = 0.0f
        }
        windowManager!!.updateViewLayout(subView, params)
    }

    @SuppressLint("WrongConstant")
    override fun onStartCommand(intent: Intent?, i: Int, i2: Int): Int {
        if (intent != null && intent.action != null) {
            if (intent.action == Const.ACTION.STOP_ACTION) {
                Log.d("checkwallpaperservice", "const action stop")
                stopForeground(true)
                stopSelf()
            } else if (intent.action != Const.APP_RUNNING) {
                Log.d("checkwallpaperservice", "const action running")
                edgeLightingView?.let {
                    it.setShape(Const.Action_DemoLiveWallpaper)
                    it.changeColor(Const.Action_DemoLiveWallpaper)
                    it.changeNotch(Const.Action_DemoLiveWallpaper)
                    it.changeSize(Const.Action_DemoLiveWallpaper)
                    it.changeSpeed(Const.Action_DemoLiveWallpaper)
                    it.changeRadius(Const.Action_DemoLiveWallpaper)
                }

                params?.let {
                    it.width = intent.getIntExtra(Const.WIDTH, 1080)
                    it.height = intent.getIntExtra(Const.HEIGHT, 1920)
                }

                windowManager?.updateViewLayout(subView, params)
                startForeground(Const.NOTIFICATION_ID_FOREGROUND_SERVICE, prepareNotification())
            } else if (intent.getBooleanExtra(Const.VALUE_APP_RUNNING, false)) {
                Log.d("checkwallpaperservice", "const action value app running")
                if (subView?.parent != null) {
                    windowManager?.removeView(subView)
                }
            } else if (subView?.parent == null) {
                Log.d("checkwallpaperservice", "const action last")
                windowManager?.addView(subView, params)
            }
        }
        return START_STICKY
    }


    var intent: Intent? = null
    var intent2: Intent? = null
    var remoteViews: RemoteViews? = null
    var activity: PendingIntent? = null

    @SuppressLint("WrongConstant")
    private fun prepareNotification(): Notification {
        val builder: NotificationCompat.Builder
        Log.d(TAG, "prepareNotification: ")
        if (Build.VERSION.SDK_INT >= 26 && mNotificationManager!!.getNotificationChannel(
                FOREGROUND_CHANNEL_ID
            ) == null
        ) {
            val notificationChannel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                getString(R.string.text_name_notification),
                3
            )
            notificationChannel.enableVibration(false)
            mNotificationManager!!.createNotificationChannel(notificationChannel)
        }

        try {
            intent = Intent(this, EdgeOverlaySettingsActivity::class.java)
            intent!!.action = Const.ACTION.MAIN_ACTION
            intent!!.flags = 268468224
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                activity = getActivity(this, 0, intent, FLAG_IMMUTABLE)
            } else {
                activity = getActivity(this, 0, intent, FLAG_UPDATE_CURRENT)
            }

            intent2 = Intent(this, WallpaperWindowEdgeService::class.java)
            intent2!!.action = Const.ACTION.STOP_ACTION
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                getService(this, 0, intent2!!, FLAG_IMMUTABLE)
            } else {
                getService(this, 0, intent2!!, FLAG_UPDATE_CURRENT)
            }
            remoteViews = RemoteViews(packageName, R.layout.layout_notification_service_edge)
            remoteViews!!.setOnClickPendingIntent(R.id.lnOverlayWindow, activity)
        } catch (e: Exception) {
        }
        builder = if (Build.VERSION.SDK_INT >= 26) {
            NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
        } else {
            NotificationCompat.Builder(this)
        }
        builder.setContent(remoteViews).setSmallIcon(R.mipmap.ic_launcher)
            .setCategory(NotificationCompat.CATEGORY_SERVICE).setOnlyAlertOnce(true)
            .setOngoing(true).setAutoCancel(true).setContentIntent(activity)
        if (Build.VERSION.SDK_INT >= 21) {
            builder.setVisibility(1)
        }
        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subView!!.parent != null) {
            windowManager!!.removeView(subView)
        }
    }

    inner class ListenerChangeWindowManager : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var stringExtra = ""
            if (intent.action == Const.Action_ChangeWindownManager && intent.getStringExtra(Const.CONTROLWINDOW)
                    .also { stringExtra = it!! } != null
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
        }
    }


    companion object {
        private const val FOREGROUND_CHANNEL_ID = "foreground_channel_id"
        private const val TAG = "MyWallpaperWindowMServi"
    }
}
