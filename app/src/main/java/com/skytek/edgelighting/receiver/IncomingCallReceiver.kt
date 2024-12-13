package com.skytek.edgelighting.receiver

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.skytek.edgelighting.App
import com.skytek.edgelighting.MyAccessibilityService
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.context
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.utils.checkContext
import com.skytek.edgelighting.views.EdgeLightingView


//class IncomingCallReceiver : BroadcastReceiver() {
//    private val REQUEST_PHONE_STATE_PERMISSION_CODE = 123
//    private var isBlinking = false
//
//    var edgeLightingView: EdgeLightingView? = null
//    var subView: View? = null
//    var layoutParams3: WindowManager.LayoutParams? = null
//    var windowManager: WindowManager? = null
//
//
//    var instance: IncomingCallReceiver? = null
//    var changeWindowManager: ListenerChangeWindowManager? = null
//    var late: LateClass? = null
//
//
//    //    for edge lighting
//    private var height = 0
//    private var mNotificationManager: NotificationManager? = null
//    private var params: WindowManager.LayoutParams? = null
//    private var width = 0
//    private lateinit var prefsEditor: SharedPreferences.Editor
//    var drawOverlay = false
//
//
//    @SuppressLint("WrongConstant", "UnspecifiedRegisterReceiverFlag")
//    override fun onReceive(context: Context, intent: Intent?) {
//        // Log incoming call
//        Log.d("abcdefgh", "Incoming call 1111111")
//
//        // Initialize preferences and other variables
//        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
//        prefsEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
//        late = LateClass()
//
//        // Inflate layout and find EdgeLightingView
//        subView = LayoutInflater.from(context).inflate(R.layout.layout_windowmananger_edge, null)
//        edgeLightingView =
//            subView?.findViewById<View>(R.id.edvLightColorWindow) as? EdgeLightingView
//
//        // Check if the READ_PHONE_STATE permission is granted
//        if (context.let {
//                ContextCompat.checkSelfPermission(
//                    it, Manifest.permission.READ_PHONE_STATE
//                )
//            } == PackageManager.PERMISSION_GRANTED) {
//
//            // Handle incoming call
//            if (intent?.action == TelephonyManager.ACTION_PHONE_STATE_CHANGED) {
//                val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE)
//                val incomingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER)
//                when (state) {
//                    TelephonyManager.EXTRA_STATE_RINGING -> {
//                        if (!incomingNumber.isNullOrBlank()) {
//                            Log.d("abcdefgh", "Incoming call 333333")
//                            // Handle incoming call
//                        } else {
//                            Log.d("abcdefgh", "else Incoming call ")
//
//                            // Handle edge lighting on call
//                            if (!MySharePreferencesEdge.getAccessibilityEnabled(
//                                    MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, App.context
//                                )
//                            ) {
//                                try {
//                                    // Enable display overlay
//                                    MySharePreferencesEdge.putDisplayOverLayBooleanValue(
//                                        MySharePreferencesEdge.DISPLAY_OVERLAY, true, App.context
//                                    )
//                                    val bundle = Bundle()
//                                    bundle.putString("edge_border_display_overlay_on", "1")
//                                    Firebase.analytics.logEvent(
//                                        "display_over_other_apps_event", bundle
//                                    )
//                                } catch (e: NullPointerException) {
//                                    // Handle exception
//                                }
//
//                                // Initialize and set up edge lighting
//                                instance = this
//                                initView()
//                                late!!.listenerChangeWindowManager = ListenerChangeWindowManager()
//                                changeWindowManager = late!!.listenerChangeWindowManager
//                                try {
//                                    context.registerReceiver(
//                                        MyAccessibilityService.late!!.listenerChangeWindowManager,
//                                        IntentFilter(Const.Action_ChangeWindownManager)
//                                    )
//                                } catch (e: Exception) {
//                                    // Handle exception
//                                }
//                                // Set edge lighting properties
//                                MyAccessibilityService.edgeLightingView?.apply {
//                                    setShape(Const.Action_DemoLiveWallpaper)
//                                    changeColor(Const.Action_DemoLiveWallpaper)
//                                    changeNotch(Const.Action_DemoLiveWallpaper)
//                                    changeSize(Const.Action_DemoLiveWallpaper)
//                                    changeSpeed(Const.Action_DemoLiveWallpaper)
//                                    changeRadius(Const.Action_DemoLiveWallpaper)
//                                }
//                                // Set params width and height
//                                params?.width = 1080
//                                params?.height = 1920
//                                initView()
//                                drawOverlay = true
//
//                                // Enable accessibility
//                                MySharePreferencesEdge.setAccessibilityEnabled(
//                                    MySharePreferencesEdge.ACCESSIBILITY_BROADCAST,
//                                    true,
//                                    App.context
//                                )
//                                // Handle other cases
//                            }
//                        }
//                    }
//                }
//            }
//        } else {
//            // Request phone state permission
//
//            if (checkContext(context = context)) {
//                requestPhoneStatePermission(context)
//            }
//        }
//    }
//
//
//    @SuppressLint("WrongConstant")
//    private fun initView() {
//        val context = context ?: return
//        try {
//            Log.d("abcdefgh", "initView: to display edge  ")
//            width = MySharePreferencesEdge.getInt(MySharePreferencesEdge.WIDTH, getBaseContext())
//            height = MySharePreferencesEdge.getInt(MySharePreferencesEdge.HEIGHT, getBaseContext())
//
//            subView = LayoutInflater.from(context)
//                .inflate(R.layout.layout_windowmananger_edge, null as ViewGroup?)
//            val layoutParams = WindowManager.LayoutParams()
//            params = layoutParams
//
//            val i = width
//            if (i != 0) {
//                layoutParams.width = i
//            } else {
//                layoutParams.width = -1
//            }
//            val i2 = height
//            if (i2 != 0) {
//                params!!.height = i2
//            } else {
//                params!!.height = -1
//            }
//            if (Build.VERSION.SDK_INT >= 26) {
//                params!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else {
//                params!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR
//            }
//
//
//            val layoutParams2 = params
//            layoutParams2!!.gravity = Gravity.TOP
//            layoutParams2.format = -2
//            layoutParams2.type =
//                if (Build.VERSION.SDK_INT >= 25) WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY else 2005
//            layoutParams3 = params!!
//            layoutParams3!!.flags = 824
//            layoutParams3!!.alpha = 0.8f
//            subView!!.systemUiVisibility = 5122
//            windowManager!!.addView(subView, layoutParams3)
//            edgeLightingView =
//                subView!!.findViewById<View>(R.id.edvLightColorWindow) as EdgeLightingView
//
//
//        } catch (e: WindowManager.BadTokenException) {
//
//            Log.e("initView", "Failed to add view: BadTokenException", e)
//        }
//
//    }
//
//    fun getBaseContext(): Context? {
//        return context ?: App.context
//    }
//
//    class LateClass {
//        lateinit var listenerChangeWindowManager: ListenerChangeWindowManager
//        val isThingInitialized get() = this::listenerChangeWindowManager.isInitialized
//    }
//
//
//    var intent: Intent? = null
//    var intent2: Intent? = null
//    var remoteViews: RemoteViews? = null
//    var activity: PendingIntent? = null
//
//    @SuppressLint("WrongConstant")
//
//    /*   private fun prepareNotification(): Notification {
//
//           val builder:NotificationCompat.Builder
//           Log.d(MyAccessibilityService.TAG, "prepareNotification: ")
//           if (Build.VERSION.SDK_INT >= 26 && mNotificationManager!!.getNotificationChannel(
//                   MyAccessibilityService.FOREGROUND_CHANNEL_ID
//               ) == null) {
//
//   //            val notificationChannel = NotificationChannel(FOREGROUND_CHANNEL_ID, getString(R.string.text_name_notification), 3)
//
//               val notificationChannel= NotificationChannel(FOREGROUND_CHANNEL_ID,"Start service",3)
//               notificationChannel.enableVibration(false)
//               mNotificationManager!!.createNotificationChannel(notificationChannel)
//           }
//
//           try {   Log.d(MyAccessibilityService.TAG, "prepareNotification: 12 ")
//              intent = Intent(context, EdgeOverlaySettingsActivity::class.java)
//               intent!!.action = Const.ACTION.MAIN_ACTION
//               intent!!.flags = 268468224
//               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                   activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//               } else {
//                   activity = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
//               }
//
//
//   //            if(MySharePreferencesEdge.getDisplayOverLayBooleanValue(MySharePreferencesEdge.DISPLAY_OVERLAY , App.context))
//   //            {
//   //
//   //            }
//               Log.d(MyAccessibilityService.TAG, "prepareNotification:123 ")
//               intent2 = Intent(context, WallpaperWindowEdgeService::class.java)
//               intent2!!.action = Const.ACTION.STOP_ACTION
//               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//                   Log.d(MyAccessibilityService.TAG, "prepareNotification:  if")
//                   PendingIntent.getService(context, 0, intent2!!, PendingIntent.FLAG_IMMUTABLE)
//               } else {
//                   Log.d(MyAccessibilityService.TAG, "prepareNotification:  else")
//                   PendingIntent.getService(context, 0, intent2!!, PendingIntent.FLAG_UPDATE_CURRENT)
//               }
//
//               remoteViews = RemoteViews(context?.packageName, R.layout.layout_notification_service_edge)
//               remoteViews!!.setOnClickPendingIntent(R.id.lnOverlayWindow, activity)
//           } catch (e: Exception) {}
//           builder = if (Build.VERSION.SDK_INT >= 26) ({
//               context?.let {
//                   NotificationCompat.Builder(
//                       it,
//                       MyAccessibilityService.FOREGROUND_CHANNEL_ID)
//               }
//           })!! else ({
//               context?.let { NotificationCompat.Builder(it) }
//           })!!
//
//           Log.d(MyAccessibilityService.TAG, "prepareNotificat1231232312ion: ")
//           builder.setContent(remoteViews).setSmallIcon(R.mipmap.ic_launcher)
//               .setCategory(NotificationCompat.CATEGORY_SERVICE).setOnlyAlertOnce(true)
//               .setOngoing(true).setAutoCancel(true).setContentIntent(activity)
//           if (Build.VERSION.SDK_INT >= 21) {
//               builder.setVisibility(1)
//           }
//           return builder.build()
//       }*/
//
//
//    inner class ListenerChangeWindowManager : BroadcastReceiver() {
//
//        override fun onReceive(context: Context, intent: Intent) {
//
//            try {
//                var stringExtra = ""
//                if (intent.action == Const.Action_ChangeWindownManager && intent.getStringExtra(
//                        Const.CONTROLWINDOW
//                    ).also { stringExtra = it!! } != null
//                ) {
//
//                    if (stringExtra == Const.COLOR) {
//                        MyAccessibilityService.edgeLightingView!!.changeColor(Const.Action_DemoLiveWallpaper)
//                    } else if (stringExtra == Const.BORDER) {
//                        MyAccessibilityService.edgeLightingView!!.changeSize(Const.Action_DemoLiveWallpaper)
//                        MyAccessibilityService.edgeLightingView!!.changeSpeed(Const.Action_DemoLiveWallpaper)
//                        MyAccessibilityService.edgeLightingView!!.changeRadius(Const.Action_DemoLiveWallpaper)
//                    } else if (stringExtra == Const.NOTCH) {
//                        MyAccessibilityService.edgeLightingView!!.changeNotch(Const.Action_DemoLiveWallpaper)
//                    } else {
//                        MyAccessibilityService.edgeLightingView!!.changeColor(Const.Action_DemoLiveWallpaper)
//                        MyAccessibilityService.edgeLightingView!!.changeSize(Const.Action_DemoLiveWallpaper)
//                        MyAccessibilityService.edgeLightingView!!.changeSpeed(Const.Action_DemoLiveWallpaper)
//                        MyAccessibilityService.edgeLightingView!!.changeRadius(Const.Action_DemoLiveWallpaper)
//                        MyAccessibilityService.edgeLightingView!!.changeNotch(Const.Action_DemoLiveWallpaper)
//                    }
//                    MyAccessibilityService.edgeLightingView!!.setShape(Const.Action_DemoLiveWallpaper)
//                }
//            } catch (e: NullPointerException) {
//            }
//        }
//    }
//
//
//    private fun requestPhoneStatePermission(context: Context) {
//        context.let {
//            ActivityCompat.requestPermissions(
//                it as Activity,
//                arrayOf(Manifest.permission.READ_PHONE_STATE),
//                REQUEST_PHONE_STATE_PERMISSION_CODE
//            )
//        }
//    }
//}
