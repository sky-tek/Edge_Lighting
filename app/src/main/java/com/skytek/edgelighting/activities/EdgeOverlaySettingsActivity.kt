package com.skytek.edgelighting.activities


import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Dialog
import android.app.ProgressDialog
import android.app.WallpaperManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ServiceInfo
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.Settings.SettingNotFoundException
import android.service.quicksettings.TileService
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.accessibility.AccessibilityManager
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.multidex.MultiDex
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.mobi.pixels.adBannerOnDemand.loadOnDemandBannerAd
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.adNativeOnDemand.AdNativeOnDemandListeners
import com.mobi.pixels.adNativeOnDemand.loadOnDemandNativeAd
import com.mobi.pixels.enums.BannerAdType
import com.mobi.pixels.enums.NativeAdIcon
import com.mobi.pixels.enums.NativeAdType
import com.mobi.pixels.enums.ShimmerColor
import com.mobi.pixels.firebase.fireEvent
import com.mobi.pixels.isOnline
import com.overlayhelper.OverlayHelper
import com.skytek.WindowService
import com.skytek.WindowService.Companion.batteryAbove20
import com.skytek.WindowService.Companion.chargingBroacast
import com.skytek.WindowService.Companion.timerReceiver
import com.skytek.edgelighting.App
import com.skytek.edgelighting.CreateOrUpdateEdgesLight
import com.skytek.edgelighting.Listeners.BorderColorClickListners
import com.skytek.edgelighting.Listeners.BorderColorClickListners.Companion.convertIntToString
import com.skytek.edgelighting.Listeners.BorderTypeClickListeners
import com.skytek.edgelighting.Listeners.BorderTypeClickListeners.Companion.initType
import com.skytek.edgelighting.Listeners.CheckedChangeListeners
import com.skytek.edgelighting.Listeners.SeekbarChangeListeners
import com.skytek.edgelighting.Listeners.SpinnerItemSelectedListener
import com.skytek.edgelighting.MyAccessibilityService
import com.skytek.edgelighting.MyAccessibilityService.Companion.edgeLightingView
import com.skytek.edgelighting.R
import com.skytek.edgelighting.WallpaperService
import com.skytek.edgelighting.WallpaperWindowEdgeService
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.api.WallpaperWebService
import com.skytek.edgelighting.databinding.ActivityEdgeOverlaySettingsBinding
import com.skytek.edgelighting.materialrangebar.RangeBar
import com.skytek.edgelighting.models.Theme
import com.skytek.edgelighting.repository.WallpaperRepository
import com.skytek.edgelighting.service.AlwaysOnTileService
import com.skytek.edgelighting.service.ForegroundService
import com.skytek.edgelighting.service.TimerService
import com.skytek.edgelighting.thread.AsynchronousTask
import com.skytek.edgelighting.utils.AdResources.NativeToBanner
import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.bannerAdId
import com.skytek.edgelighting.utils.AdResources.nativeAdId
import com.skytek.edgelighting.utils.AdResources.settingScreenNativeAdShow
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.Global
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.utils.adTimeTraker.isIntervalElapsed
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.skytek.edgelighting.utils.checkContext
import com.skytek.edgelighting.views.AmbilWarnaDialog
import org.greenrobot.eventbus.EventBus

//object Adcounter {
//    var counter = 0
//}

class EdgeOverlaySettingsActivity : AppCompatActivity() {

    private var overlayHelper: OverlayHelper? = null
    var alertDialog: AlertDialog? = null
    private val active = ""
    var progressDialog: ProgressDialog? = null
    lateinit var warningValuesSharedPreferences: SharedPreferences
    lateinit var warningValuesSharedPreferencesEditor: SharedPreferences.Editor

    private var check = 0
    private lateinit var prefsEditor: SharedPreferences.Editor
    lateinit var borderTypes: MutableList<ImageView>
    lateinit var borderSeekbars: MutableList<SeekBar>
    lateinit var notchSeekbars: MutableList<SeekBar>

    private val PREFS_NAME = "MyPrefsFile"
    private val PREF_ANIMATION_SHOWN = "animation_shown"

    var h: Handler? = null
    var runnable: Runnable? = null

    private var width = 0
    private val handler = Handler(Looper.getMainLooper())

    var sharedPref: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null

    private fun firstLoadData() {
        if (firstLoadData) {
            handler.postDelayed({
                AsynchronousTask(
                    "getThemeData", this@EdgeOverlaySettingsActivity
                ).execute("")
            }, 300)
            firstLoadData = false
        }
    }

    fun isAccessibilityServiceEnabled(
        context: Context, service: Class<out AccessibilityService?>
    ): Boolean {
        val am = context.getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        for (enabledService in enabledServices) {
            val enabledServiceInfo: ServiceInfo = enabledService.resolveInfo.serviceInfo
            if (enabledServiceInfo.packageName.equals(context.packageName) && enabledServiceInfo.name.equals(
                    service.name
                )
            ) return true
        }
        return false
    }

    fun checkAccessibilityPermission(): Boolean {

        var accessEnabled = 0
        try {
            accessEnabled =
                Settings.Secure.getInt(this.contentResolver, Settings.Secure.ACCESSIBILITY_ENABLED)
        } catch (e: SettingNotFoundException) {
            e.printStackTrace()
        }
//        return if (accessEnabled == 0 && edgeLightingView == null) {
        return if (accessEnabled == 0) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            false
        } else {
            true
        }
    }

    var wallpaperWebService: WallpaperWebService? = null
    var wallpaperRepository: WallpaperRepository? = null
    lateinit var linearLayoutManager: LinearLayoutManager

    var draw: LinearLayout? = null
    var always: LinearLayout? = null


    @SuppressLint("NewApi", "ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        MultiDex.install(this)

        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        binding = ActivityEdgeOverlaySettingsBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        // binding!!.colorsList.visibility = View.GONE

        context = applicationContext
        activity = this@EdgeOverlaySettingsActivity

        // Check if the animation has been shown before
        sharedPref = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedPref!!.edit()


        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        fireEvent("RV_${packageInfo.versionCode}_Edge_Activity ")
        warningValuesSharedPreferences =
            getSharedPreferences("WARNING_VALUES", Context.MODE_PRIVATE)
        warningValuesSharedPreferencesEditor = warningValuesSharedPreferences.edit()
        val topLabels = arrayOf("0", "5", "10", "15", "20", "25", "30")
        binding!!.rangebar1.setTickTopLabels(topLabels)
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        val switchenabled = MySharePreferencesEdge.getDisplayOverLayBooleanValue(
            MySharePreferencesEdge.DISPLAY_OVERLAY, App.context
        )
        Log.d("jkdshdskhd", "onCreate: a gya iss mai ")
//        Log.d("jkdshdskhd", "onCreate: a gya iss mai ${binding!!.rememberColors.text} ")
        if (switchenabled) {
            Log.d("jkdshdskhd", "onCreate: a gya iss mai ")

            val sharedPref = getSharedPreferences(
                "mySharedPreferences", Context.MODE_PRIVATE
            )
            val colorState1Value = sharedPref!!.getBoolean(
                MySharePreferencesEdge.COLOR1_VALUE, true
            )
            val colorState2Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR2_VALUE, true)
            val colorState3Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR3_VALUE, true)
            val colorState4Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR4_VALUE, false)
            val colorState5Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR5_VALUE, false)
            val colorState6Value = sharedPref.getBoolean(MySharePreferencesEdge.COLOR6_VALUE, false)
            Log.d(
                "slkjdsjdksjd",
                "onCreate: $colorState1Value   $colorState2Value   $colorState3Value ${colorState4Value}"
            )
            if (!colorState1Value) {
                Log.d("slkjdsjdksjd", "onCreate: 1")
                binding!!.imageColor1.setBackgroundColor(Color.GRAY)
                binding!!.imageColor1.foreground = null
            } else {
                val color = MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR1_VALUE, this@EdgeOverlaySettingsActivity
                )
                if (color != -1) {
                    Log.d("fjdfkdjhfkdj", "onCreate: $color")
                    binding?.imageColor1?.setBackgroundColor(color)
                }
            }
            if (!colorState2Value) {
                Log.d("slkjdsjdksjd", "onCreate: 2")
                binding!!.imageColor2.setBackgroundColor(Color.GRAY)
                binding!!.imageColor2.foreground = null
            } else {
                val color = MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR2_VALUE, this@EdgeOverlaySettingsActivity
                )
                if (color != -1) {
                    Log.d("fjdfkdjhfkdj", "onCreate: $color")
                    binding?.imageColor2?.setBackgroundColor(color)
                }
            }
            if (!colorState3Value) {
                Log.d("sdasdsadasdsa", "onCreate: 3 if")
                binding!!.imageColor3.setBackgroundColor(Color.GRAY)
                binding!!.imageColor3.foreground = null
            } else {
                val color = MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR3_VALUE, this@EdgeOverlaySettingsActivity
                )
                if (color != -1) {
                    Log.d("sdasdsadasdsa", "onCreate: 3 else  $color")
                    binding?.imageColor3?.setBackgroundColor(color)
                }
            }
            if (!colorState4Value) {
                Log.d("slkjdsjdksjd", "onCreate: 1")
                binding!!.imageColor4.setBackgroundColor(Color.GRAY)
                binding!!.imageColor4.foreground = null
            } else {
                binding?.imageColor4?.foreground =
                    activity!!.resources.getDrawable(R.drawable.group_445)
                val color = MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR4_VALUE, this@EdgeOverlaySettingsActivity
                )
                if (color != -1) {
                    Log.d("fjdfkdjhfkdj", "onCreate: $color")
                    binding?.imageColor4?.setBackgroundColor(color)
                }
            }
            if (!colorState5Value) {
                Log.d("slkjdsjdksjd", "onCreate: 1")
                binding!!.imageColor5.setBackgroundColor(Color.GRAY)
                binding!!.imageColor5.foreground = null
            } else {
                binding?.imageColor5?.foreground =
                    activity!!.resources.getDrawable(R.drawable.group_445)
                val color = MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR5_VALUE, this@EdgeOverlaySettingsActivity
                )
                if (color != -1) {
                    Log.d("fjdfkdjhfkdj", "onCreate: $color")
                    binding?.imageColor5?.setBackgroundColor(color)
                }
            }
            if (!colorState6Value) {
                Log.d("slkjdsjdksjd", "onCreate: 1")
                binding!!.imageColor6.setBackgroundColor(Color.GRAY)
                binding!!.imageColor6.foreground = null
            } else {
                binding?.imageColor6?.foreground =
                    activity!!.resources.getDrawable(R.drawable.group_445)
                val color = MySharePreferencesEdge.getIntValue(
                    MySharePreferencesEdge.COLOR6_VALUE, this@EdgeOverlaySettingsActivity
                )
                if (color != -1) {
                    Log.d("fjdfkdjhfkdj", "onCreate: $color")
                    binding?.imageColor6?.setBackgroundColor(color)
                }
            }
        }





        binding?.rangebar1?.setOnRangeBarChangeListener(object : RangeBar.OnRangeBarChangeListener {
            override fun onRangeChangeListener(
                rangeBar: RangeBar,
                leftPinIndex: Int,
                rightPinIndex: Int,
                leftPinValue: String,
                rightPinValue: String
            ) {

                try {

                    val arrayList = ArrayList<Int>()
                    val secondarrayList = ArrayList<Int>()

                    for (i in 0..7) {
                        secondarrayList.add(Color.WHITE)
                        rangeBar.tickColors = secondarrayList

                        //  binding?.rangebar1?.setConnectingLine(resources.getColor(R.color.colorPrimary))
                    }

                    for (i in 0..leftPinIndex) {
                        arrayList.add(Color.RED)
                        rangeBar.tickColors = arrayList
                        //  binding?.rangebar1?.setConnectingLine(resources.getColor(R.color.colorPrimary))
                    }


                    if (rightPinIndex < 1) {
                        rangeBar.setRangePinsByIndices(leftPinIndex, 1)
                    }
                    if (leftPinIndex >= rightPinIndex && leftPinIndex > 0) {
                        rangeBar.setRangePinsByIndices(rightPinIndex - 1, rightPinIndex)
                    }
                    // Calculate the new color based on the progress percentage
                    val newColor = Color.RED

                    // set color for left-hand side unconnected lines

                    rangeBar.setBarColor(newColor)

                    rangeBar.setConnectingLineColor(getResources().getColor(R.color.md_orange_500))
                    warningValuesSharedPreferencesEditor.putInt("WARNING_MIN", leftPinValue.toInt())
                    warningValuesSharedPreferencesEditor.apply()
                    warningValuesSharedPreferencesEditor.putInt(
                        "WARNING_MAX", rightPinValue.toInt()
                    )
                    warningValuesSharedPreferencesEditor.apply()
                    warningValuesSharedPreferencesEditor.putInt(
                        "WARNING_MIN_PROGRESS_LOC", leftPinIndex.toInt()
                    )
                    warningValuesSharedPreferencesEditor.apply()
                    warningValuesSharedPreferencesEditor.putInt(
                        "WARNING_PROGRESS_LOC", rightPinIndex.toInt()
                    )
                    warningValuesSharedPreferencesEditor.apply()
                    if (MySharePreferencesEdge.getShowChargingBooleanValue(
                            MySharePreferencesEdge.SHOW_WHILE_CHARGING,
                            this@EdgeOverlaySettingsActivity
                        )
                    ) {
                        if (isMyServiceRunning(WindowService::class.java)) {
                            stopService(
                                Intent(
                                    this@EdgeOverlaySettingsActivity, TimerService::class.java
                                )
                            )
                            stopService(
                                Intent(
                                    this@EdgeOverlaySettingsActivity, WindowService::class.java
                                )
                            )
                        }
                        val intent_ =
                            Intent(this@EdgeOverlaySettingsActivity, WindowService::class.java)
                        startService(intent_)
                        val bundle = Bundle()
                        bundle.putString("low_battery_edge_on", "1")
                        Firebase.analytics.logEvent("low_battery_edge_lighting_event", bundle)
                    }

                } catch (e: Exception) {

                }
            }

            override fun onTouchEnded(rangeBar: RangeBar) {
                Log.d("checkrangebar", "on touch end called")
            }

            override fun onTouchStarted(rangeBar: RangeBar) {

            }
        })


        MySharePreferencesEdge.putWallpaperBooleanValue(
            MySharePreferencesEdge.WALL_PAPER, true, this@EdgeOverlaySettingsActivity
        )

        firstLoadData = true
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels




        if (isOnline(this@EdgeOverlaySettingsActivity) && wholeScreenAdShow && settingScreenNativeAdShow) {
            binding!!.adlayout.visibility = View.VISIBLE
//            val adsManagerJava = AdsManagerJava()
//            binding!!.adtext.text = "Fetching ads..."
            Log.d("whyShowBannerAlways", "whyShowBannerAlways: ${NativeToBanner} ")
            if (NativeToBanner) {
                loadOnDemandNativeAd(
                    this@EdgeOverlaySettingsActivity,
                    binding!!.nativeAdExit2,
                    nativeAdId,
                    NativeAdType.NativeSmall
                ).setBackgroundColor(resources.getString(R.color.round_background))
                    .setTextColorButton("#ffffff").setTextColorTitle("#ffffff")
                    .setTextColorDescription("#ffffff").setButtonColor("#FF5589F1")
                    .setButtonRoundness(15).setAdIcon(NativeAdIcon.White).enableShimmerEffect(true)
                    .setShimmerBackgroundColor("#000000").setShimmerColor(ShimmerColor.White)
                    .adListeners(object : AdNativeOnDemandListeners {
                        override fun onAdFailedToLoad(error: String) {

                            binding!!.adtext.visibility = View.GONE
                        }

                        override fun onAdLoaded() {
                            binding!!.adtext.visibility = View.GONE
                        }


                    }).load()

            } else {
                loadOnDemandBannerAd(
                    this@EdgeOverlaySettingsActivity,
                    binding!!.nativeAdExit2,
                    bannerAdId,
                    BannerAdType.Banner
                ).enableShimmerEffect(true).setShimmerBackgroundColor("#000000").load()


            }
        } else {
            binding!!.adtext.text = ""
            binding!!.adlayout.visibility = View.VISIBLE
            binding!!.adlayout.layoutParams.height = getStatusBarAndTitleBarHeight(window).first
        }



        Log.d("myt", Build.MODEL)

//       tv = binding!!.edgeTxt

        AmbilWarnaDialog.setGradient()


//        initViews()
        firstLoadData()

        Log.d("whatistheBorderType", "BorderType: ${intent.getStringExtra("borderType")} ")
        val borderType = intent.getStringExtra("borderType")
        Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
        when (borderType) {
            Const.SNOW -> {
                Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                type = Const.SNOW
                initType()
            }

            Const.CHRISMISTREE -> {
                Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                type = Const.CHRISMISTREE
                initType()
            }

            Const.MOON -> {
                Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                type = Const.MOON
                initType()
            }

            Const.SUN -> {
                Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                type = Const.SUN
                initType()
            }

            Const.STAR -> {
                Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                type = Const.STAR
                initType()
            }

            else -> {
                Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "nothing ")
            }

        }
//        if (intent.getStringExtra("borderType") == Const.SNOW) {
//
//            Log.d("whatistheBorderType", "BorderType: $type ")
//        }

        prefsEditor = PreferenceManager.getDefaultSharedPreferences(this).edit()

        overlayHelper = OverlayHelper(this.applicationContext,
            object : OverlayHelper.OverlayPermissionChangedListener {
                override fun onOverlayPermissionCancelled() {
                    Toast.makeText(
                        this@EdgeOverlaySettingsActivity,
                        getString(R.string.draw_over_cancelled),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onOverlayPermissionGranted() {
                    Toast.makeText(
                        this@EdgeOverlaySettingsActivity,
                        getString(R.string.draw_over_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                    displayOverlay = true
                    try {
                        if (binding!!.switchDisplay.isChecked) MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                            MySharePreferencesEdge.DISPLAY_OVERLAY, true, App.context
                        )

                        if (binding!!.switchCharging.isChecked) MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                            MySharePreferencesEdge.SHOW_WHILE_CHARGING, true, App.context
                        )
                        if (binding!!.swtichAlways.isChecked) {
                            try {
                                prefs.edit().putBoolean("always_on", true).apply()
                                TileService.requestListeningState(
                                    applicationContext, ComponentName(
                                        this@EdgeOverlaySettingsActivity,
                                        AlwaysOnTileService::class.java
                                    )
                                )
                                ContextCompat.startForegroundService(
                                    applicationContext, Intent(
                                        this@EdgeOverlaySettingsActivity,
                                        ForegroundService::class.java
                                    )
                                )
                                LocalBroadcastManager.getInstance(applicationContext)
                                    .sendBroadcast(Intent().setAction(Global.ALWAYS_ON_STATE_CHANGED))
                                prefsEditor.putBoolean("setup_complete", true).apply()
                                MySharePreferencesEdge.putAlwaysOnDisplayBooleanValue(
                                    MySharePreferencesEdge.ALWAYS_ON_DISPLAY, true, activity
                                )
                            } catch (e: NoClassDefFoundError) {
                            }
                        } else {
                            if (!MySharePreferencesEdge.getAccessibilityEnabled(
                                    MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                                )
                            ) {
                                showAccessibilityPermissionDialog()
                            }
                        }
                    } catch (e: NullPointerException) {
                    }
                }

                override fun onOverlayPermissionDenied() {
                    Toast.makeText(
                        this@EdgeOverlaySettingsActivity,
                        getString(R.string.draw_over_denied),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        overlayHelper!!.startWatching()

        mDisplayOverlayListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            fireEvent("RV_${packageInfo.versionCode}_Edge_Activity_permission_on ")
            Log.d("whatHappenbsjhvsa", "onCreate: $b ")
            if (b) {
                Log.d(
                    "fdsfdfhghjh", "onCreate: ${
                        MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                        )
                    }"
                )
                if (MySharePreferencesEdge.getAccessibilityEnabled(
                        MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                    ) && isOnline(this@EdgeOverlaySettingsActivity)
                ) {
                    Log.d(
                        "whatisIssue",
                        "${isOnline(this@EdgeOverlaySettingsActivity) && wholeScreenAdShow && wholeInterAdShow && isIntervalElapsed()}"
                    )
                    if (isOnline(this@EdgeOverlaySettingsActivity) && wholeScreenAdShow && wholeInterAdShow && isIntervalElapsed()) {
                        Log.d("whatisIssue", "insidee")
                        loadInterstitialAd(activitiesAdId)
                    }
                }
                if (edgeLightingView != null) {
                    if (edgeLightingView?.visibility == View.VISIBLE) {
                        binding?.edgeLightView?.visibility = View.INVISIBLE
                    } else {
                        binding?.edgeLightView?.visibility = View.VISIBLE
                    }
                }
                try {

                    if (MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                        ) && binding?.switchDisplay?.isChecked == true
                    ) {
                        drawOverlay = true
                        MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                            MySharePreferencesEdge.DISPLAY_OVERLAY, true, activity
                        )
                        binding?.edgeLightView?.visibility = View.GONE
                        if (edgeLightingView?.visibility == View.INVISIBLE || edgeLightingView?.visibility == View.GONE) {
                            edgeLightingView?.visibility = View.VISIBLE
                        }
                    }
                    if (!binding!!.switchCharging.isChecked && !MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                        )
                    ) {
                        showAccessibilityPermissionDialog()
                    } else {
                        drawOverlay = true
                        MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                            MySharePreferencesEdge.DISPLAY_OVERLAY, true, activity
                        )
                    }
                    if (chargingBroacast != null && timerReceiver == null) {
                        binding!!.edgeLightView.visibility = View.INVISIBLE
                        MyAccessibilityService.apply {
                            edgeLightingView!!.visibility = View.VISIBLE
                            edgeLightingView!!.changeType(
                                MySharePreferencesEdge.getString(
                                    MySharePreferencesEdge.SHAPE, App.context
                                )
                            )
                            edgeLightingView!!.changeColor(color)
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
                        MySharePreferencesEdge.setAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, true, App.context
                        )
                    }
                    setAllColors()
                } catch (e: NullPointerException) {
                }
            }
            else {
                drawOverlay = false
                MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                    MySharePreferencesEdge.DISPLAY_OVERLAY, false, activity
                )
                MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                    MySharePreferencesEdge.SWITCH_CHARGING, false, activity
                )
                try {
                    Log.d(
                        "whatHappenbsjhvsa",
                        "!binding!!.switchCharging.isChecked: ${!binding!!.switchCharging.isChecked} "
                    )
                    if (!binding!!.switchCharging.isChecked) {
                        if (edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(
                                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                            )
                        ) {
                            if (!binding!!.swtichAlways.isChecked && !binding!!.switchCharging.isChecked && !binding!!.switchDisplay.isChecked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    binding!!.apply {
                                        edgeLightView.visibility = View.VISIBLE
                                        edgeLightView.changeType(type)
                                        edgeLightView.changeColor(color)
                                        edgeLightView.changeSize(size)
                                        edgeLightView.changeSpeed(speed)
                                        edgeLightView.changeBorder(cornerTop, cornerBottom)
                                        edgeLightView.changeNotch(
                                            checkNotch,
                                            notchTop,
                                            notchBottom,
                                            Companion.notchHeight,
                                            Companion.notchRadiusTop,
                                            Companion.notchRadiusBottom
                                        )
                                    }
                                    if (binding?.edgeLightView?.visibility == View.VISIBLE && edgeLightingView?.visibility == View.VISIBLE) {
                                        binding?.edgeLightView?.visibility = View.INVISIBLE
                                    } else {
                                        Log.d("checkedgelighting", "both are not visible")
                                    }
                                }
                            }

                        } else {
                            AsynchronousTask(
                                "removeWallpaperAndEdge", activity!!
                            ).execute("")
                        }
                    } else {
                        binding!!.warningLayoutSuperParent.visibility = View.GONE
                        binding?.switchCharging?.let {
                            it.isChecked = false
                        }
                        MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                            MySharePreferencesEdge.SHOW_WHILE_CHARGING,
                            false,
                            this@EdgeOverlaySettingsActivity
                        )
                        stopService(
                            Intent(
                                this@EdgeOverlaySettingsActivity, TimerService::class.java
                            )
                        )
                        stopService(
                            Intent(
                                this@EdgeOverlaySettingsActivity, WindowService::class.java
                            )
                        )

                        AsynchronousTask(
                            "setEdgeBorderColor", this@EdgeOverlaySettingsActivity
                        ).execute("")

                        Log.d("sldjsdlsjdsds", "onCreate: 1 ")
                        val listenr = BorderColorClickListners()
                        listenr.checkAllImageViews()

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (binding!!.switchDisplay.isChecked) {
                                try {
                                    if (!binding!!.switchCharging.isChecked && !binding!!.swtichAlways.isChecked && !binding!!.switchDisplay.isChecked) {

                                        edgeLightingView!!.visibility = View.VISIBLE
                                        edgeLightingView!!.changeType(type)
                                        edgeLightingView!!.changeColor(color)
                                        edgeLightingView!!.changeSize(size)
                                        edgeLightingView!!.changeSpeed(speed)
                                        edgeLightingView!!.changeBorder(cornerTop, cornerBottom)
                                        edgeLightingView!!.changeNotch(
                                            checkNotch,
                                            notchTop,
                                            notchBottom,
                                            notchHeight,
                                            notchRadiusTop,
                                            notchRadiusBottom
                                        )
                                    }
                                } catch (e: NullPointerException) {
                                }
                            }
                        }, 500)
                    }
                } catch (e: NullPointerException) {
                }
                Log.d("sldjsdlsjdsds", "onCreate: 1 ")

                if (chargingBroacast != null && timerReceiver == null) {
                    // Toast.makeText(applicationContext,"batrr",Toast.LENGTH_LONG).show()
                    try {
                        if (!binding!!.swtichAlways.isChecked && !binding!!.switchCharging.isChecked && !binding!!.switchDisplay.isChecked) {
                            edgeLightingView!!.visibility = View.GONE
                            // MySharePreferencesEdge.setAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, false, App.context)
                            binding!!.apply {
                                edgeLightView.visibility = View.VISIBLE
                                edgeLightView.changeType(
                                    MySharePreferencesEdge.getString(
                                        MySharePreferencesEdge.SHAPE, App.context
                                    )
                                )
                                edgeLightView.changeColor(color)
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

                            if (binding!!.edgeLightView.visibility == View.VISIBLE && edgeLightingView!!.visibility == View.VISIBLE) {
                                binding?.edgeLightView?.visibility = View.INVISIBLE
                            } else {
                                Log.d("checkedgelighting", "both are not visible")
                            }
                        }

                    } catch (e: NullPointerException) {
                    }
                }
            }
        }

// Assuming you have a SharedPreferences object named "preferences"
        val isFirstTime = warningValuesSharedPreferences.getBoolean("isFirstTime", true)
        mShowWhileChargingListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            Log.d("checklisternrer ", "| asdsadsadsadsa")
            if (binding != null) {
                if (b) {
                    Log.d("dffdsfdsfds", "onCreate: ")

                    if (edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                        )
                    ) {


                        val firsttime = sharedPref!!.getBoolean(PREF_ANIMATION_SHOWN, false)
                        Log.d("gghfghfghfgh", "onCreate: " + firsttime)
                        if (!firsttime) {
                            Log.d("gghfghfghfgh", "onCreate: 1 ")
                            dialogwarning()
                            editor!!.putBoolean(PREF_ANIMATION_SHOWN, true)
                            editor!!.apply()
                            MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                                MySharePreferencesEdge.SHOW_WHILE_CHARGING, true, App.context
                            )
                            val intent_ = Intent(this, WindowService::class.java)
                            startService(intent_)
                            val bundle = Bundle()
                            bundle.putString("low_battery_edge_on", "1")
                            Firebase.analytics.logEvent("low_battery_edge_lighting_event", bundle)
                            binding?.warningLayoutSuperParent?.visibility = View.VISIBLE

                        } else {
                            Log.d("gghfghfghfgh", "onCreate: 2")
                            binding?.warningLayoutSuperParent?.visibility = View.VISIBLE
                            MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                                MySharePreferencesEdge.SHOW_WHILE_CHARGING, true, App.context
                            )
                            val intent_ = Intent(this, WindowService::class.java)
                            startService(intent_)
                            val bundle = Bundle()
                            bundle.putString("low_battery_edge_on", "1")
                            Firebase.analytics.logEvent("low_battery_edge_lighting_event", bundle)
                            binding?.warningLayoutSuperParent?.visibility = View.VISIBLE
                        }


                    } else {
                        if (!MySharePreferencesEdge.getAccessibilityEnabled(
                                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                            )
                        ) {
                            showAccessibilityPermissionDialog()
                        }
                    }
                    if (isMyServiceRunning(WindowService::class.java) && binding!!.switchCharging.isChecked) {
                        binding!!.edgeLightView.visibility = View.GONE
                    }

                } else {
                    try {
                        binding!!.warningLayoutSuperParent.visibility = View.GONE
                        if (binding!!.switchDisplay.isChecked) {
                            if (edgeLightingView!!.visibility == View.INVISIBLE || edgeLightingView!!.visibility == View.GONE) {
                                edgeLightingView!!.visibility = View.VISIBLE

                            }
                        } else {
                            binding!!.edgeLightView.visibility = View.VISIBLE
                        }
                        MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                            MySharePreferencesEdge.SHOW_WHILE_CHARGING,
                            false,
                            this@EdgeOverlaySettingsActivity
                        )
                        stopService(
                            Intent(
                                this@EdgeOverlaySettingsActivity, TimerService::class.java
                            )
                        )
                        stopService(
                            Intent(
                                this@EdgeOverlaySettingsActivity, WindowService::class.java
                            )
                        )

                        AsynchronousTask(
                            "setEdgeBorderColor", this@EdgeOverlaySettingsActivity
                        ).execute("")

                        Log.d("sldjsdlsjdsds", "onCreate: 1 ")
                        val listenr = BorderColorClickListners()
                        listenr.checkAllImageViews()

                        Handler(Looper.getMainLooper()).postDelayed({
                            if (binding!!.switchDisplay.isChecked) {
                                try {
                                    if (!binding!!.switchCharging.isChecked && !binding!!.swtichAlways.isChecked && !binding!!.switchDisplay.isChecked) {

                                        edgeLightingView!!.visibility = View.VISIBLE
                                        edgeLightingView!!.changeType(type)
                                        edgeLightingView!!.changeColor(color)
                                        edgeLightingView!!.changeSize(size)
                                        edgeLightingView!!.changeSpeed(speed)
                                        edgeLightingView!!.changeBorder(cornerTop, cornerBottom)
                                        edgeLightingView!!.changeNotch(
                                            checkNotch,
                                            notchTop,
                                            notchBottom,
                                            notchHeight,
                                            notchRadiusTop,
                                            notchRadiusBottom
                                        )
                                    }
                                  setAllColors()
                                } catch (e: NullPointerException) {
                                }
                            }
                        }, 500)
                    } catch (e: NullPointerException) {
                    }
                }
            }
        }

        mAlwaysOnDisplayListener = CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && MySharePreferencesEdge.getAccessibilityEnabled(
                        MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                    )
                ) {
                    try {
                        prefs.edit().putBoolean("always_on", true).apply()
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            // The requestListeningState() method is only available on Android Nougat (API 24) and above
                            TileService.requestListeningState(
                                applicationContext, ComponentName(
                                    this@EdgeOverlaySettingsActivity,
                                    AlwaysOnTileService::class.java
                                )
                            )
                        }

                        ContextCompat.startForegroundService(
                            applicationContext,
                            Intent(this@EdgeOverlaySettingsActivity, ForegroundService::class.java)
                        )
                        LocalBroadcastManager.getInstance(applicationContext)
                            .sendBroadcast(Intent().setAction(Global.ALWAYS_ON_STATE_CHANGED))
                        prefsEditor.putBoolean("setup_complete", true).apply()
                        MySharePreferencesEdge.putAlwaysOnDisplayBooleanValue(
                            MySharePreferencesEdge.ALWAYS_ON_DISPLAY, true, activity
                        )
                        val bundle = Bundle()
                        bundle.putString("always_on_display_edge_lighting", "1")
                        Firebase.analytics.logEvent("always_on_display_edge_lighting_event", bundle)
                    } catch (e: NoClassDefFoundError) {
                    }
                } else {
                    if (!MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                        )
                    ) {
                        showAccessibilityPermissionDialog()
                    }
                }
            } else {
                prefs.edit().putBoolean("always_on", false).apply()
                stopService(Intent(applicationContext, ForegroundService::class.java))
                MySharePreferencesEdge.putAlwaysOnDisplayBooleanValue(
                    MySharePreferencesEdge.ALWAYS_ON_DISPLAY, false, activity
                )
                try {
                    if (!isMyServiceRunning(WindowService::class.java) && !isMyServiceRunning(
                            WallpaperWindowEdgeService::class.java
                        )
                    ) {
                        if (MySharePreferencesEdge.getAccessibilityEnabled(
                                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                            )
                        ) {
                            if (!binding?.switchDisplay?.isChecked!! && !binding?.switchCharging?.isChecked!! && !binding?.swtichAlways?.isChecked!!) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    try {
                                        if (edgeLightingView != null) {
                                            binding!!.apply {
//                                                edgeLightView?.visibility = View.VISIBLE
//                                                edgeLightView?.changeType(type)
//                                                edgeLightView?.changeColor(color)
//                                                edgeLightView?.changeSize(size)
//                                                edgeLightView?.changeSpeed(speed)
//                                                edgeLightView?.changeBorder(cornerTop, cornerBottom)
//                                                edgeLightView?.changeNotch(checkNotch, notchTop, notchBottom, Companion.notchHeight, Companion.notchRadiusTop, Companion.notchRadiusBottom)
                                            }
                                        }
                                    } catch (e: java.lang.Exception) {
                                        e.printStackTrace()
                                    }

                                    // MyAccessibilityService.instance!!.disableSelf()
                                    //MySharePreferencesEdge.setAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, false, activity)

                                }/*if(binding?.edgeLightView?.getVisibility() == View.VISIBLE && edgeLightingView?.getVisibility() == View.VISIBLE)
                                {
                                    binding?.edgeLightView?.visibility = View.INVISIBLE
                                }
                                else
                                {
                                    Log.d("checkedgelighting" ,"both are not visible")
                                }*/
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
        try {
            borderTypes = mutableListOf(
                binding!!.typeLine,
                binding!!.typeFlower,
                binding!!.typeEmoji,
                binding!!.typeMoon1,
                binding!!.typeChrismis,
                binding!!.typeFoot,
                binding!!.typeSpaceship,
                binding!!.typeStar,
                binding!!.typeArt1,
                binding!!.typeHeart,
                binding!!.typeDot,
                binding!!.typeSun,
                binding!!.typeMoon,
                binding!!.typeSnow
            )

            borderSeekbars = mutableListOf(
                binding!!.animationSpeed,
                binding!!.borderSize,
                binding!!.radiusTop,
                binding!!.radiusBottom
            )
            notchSeekbars = mutableListOf(
                binding!!.notchWidthTop,
                binding!!.notchWidthBottom,
                binding!!.notchHeight,
                binding!!.notchRadiusTop,
                binding!!.notchRadiusBottom
            )
            colorNames = listOf(
                MySharePreferencesEdge.COLOR1,
                MySharePreferencesEdge.COLOR2,
                MySharePreferencesEdge.COLOR3,
                MySharePreferencesEdge.COLOR4,
                MySharePreferencesEdge.COLOR5,
                MySharePreferencesEdge.COLOR6
            )
        } catch (e: NullPointerException) {
        }
        setBorderTypeListener()
        setBorderSeekbarListner()
        setNotchSeekbarListner()
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
    fun setAllColors() {
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
    // end of onCreate()

    //    function custom dialouge box for low battery
    override fun onBackPressed() {
        Log.d(
            "fishuiofhiwegficvue",
            "onBackPressed:${intent.getBooleanExtra("onBoardingScreen", false)} "
        )
        if (intent.getBooleanExtra("onBoardingScreen", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        } else {
            super.onBackPressed()
        }
    }

    private fun showDialog(title: String) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.setCancelable(false)
        dialog?.setContentView(R.layout.low_charging_custom_layout)

        val body: TextView = dialog?.findViewById(R.id.textTitle)!!
        body.text = title/*
            val yesBtn = dialog.findViewById(R.id.buttonYes) as Button
            yesBtn.setOnClickListener {
                dialog.dismiss()
            }*/

        val noBtn: ImageView = dialog.findViewById(R.id.imageIcon)
        noBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun dialogwarning() {
        // Inflate your custom layout
        val customLayout =
            LayoutInflater.from(this).inflate(R.layout.low_battery_custom_dialog, null)

        val dismissBtn = customLayout.findViewById<TextView>(R.id.dismissBtn)
        val animationView = customLayout.findViewById<LottieAnimationView>(R.id.animation_view)

        // Set up the animation view
        animationView.setAnimation(R.raw.warning_animation)
        animationView.playAnimation()


        // Create an AlertDialog for the custom dialog
        val dialog = AlertDialog.Builder(this).setView(customLayout).create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // Set an onClickListener for the dismiss button
        dismissBtn.setOnClickListener {
            binding?.warningLayoutSuperParent?.visibility = View.VISIBLE
            dialog.dismiss() // Dismiss the dialog
            // Any additional handling can be added here if needed
        }

        // Show the dialog
        dialog.show()

    }


    /*    private fun initViews(){

        }*/

    fun setEdgeBorderColoring(c1: Int, c2: Int, c3: Int, c4: Int, c5: Int, c6: Int, c7: Int) {
        color[0] = c1
        color[1] = c2
        color[2] = c3
        color[3] = c4
        color[4] = c5
        color[5] = c6
        color[6] = c7
    }

    fun turnOffWallpaper() {
        try {
            WallpaperManager.getInstance(applicationContext).clear()
        } catch (e: Exception) {
        }
    }

    public override fun onActivityResult(i: Int, i2: Int, intent: Intent?) {
        super.onActivityResult(i, i2, intent)
        overlayHelper!!.onRequestDrawOverlaysPermissionResult(i)
        if (i == 200) {
            if (i2 == RESULT_OK) {
                try {
                    functionApply()
                    val intent = Intent(Const.Action_SetLiveWallpaper)
                    intent.putExtra(Const.Action_StopLiveWallpaper, Const.RUN)
                    intent.setPackage(packageName)
                    sendBroadcast(intent)
                } catch (e: Exception) {
                }
            } else {
            }
        }
    }

    fun applyBorderOverlay_Edited(theme: Theme?) {
        Log.d("themeSize", speed.toString() + "  :   " + size)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.SIZE, size, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSTOP, cornerTop, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSBOTTOM, cornerBottom, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHTOP, notchTop, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSTOP, notchRadiusTop, this)
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.NOTCHRADIUSBOTTOM, notchRadiusBottom, this
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHBOTTOM, notchBottom, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHHEIGHT, notchHeight, this)
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.COLOR1, convertIntToString(color[0])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.COLOR2, convertIntToString(color[1])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.COLOR3, convertIntToString(color[2])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.COLOR4, convertIntToString(color[3])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.COLOR5, convertIntToString(color[4])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.COLOR6, convertIntToString(color[5])
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.SPEED, speed, this)
        MySharePreferencesEdge.putBoolean(MySharePreferencesEdge.CHECKNOTCH, checkNotch, this)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.SHAPE, type)
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.BACKGROUND, theme!!.checkBackground, this
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.BACKGROUNDCOLOR, theme.colorBg
        )
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.BACKGROUNDLINK, theme.linkBg)
    }

    val data: Unit
        get() {
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
            _theme = themeArrayList!![0] as Theme?
            checkNotch = if (_checkNotch == false) _theme!!.isNotchCheck else _checkNotch
            notchTop = if (_notchTop == -1) _theme!!.notchTop else _notchTop
            notchBottom = if (_notchBottom == -1) _theme!!.notchBottom else _notchBottom
            notchRadiusTop = if (_notchRadiusTop == -1) _theme!!.notchRadiusTop else _notchRadiusTop
            notchRadiusBottom =
                if (_notchRadiusBottom == -1) _theme!!.notchRadiusBottom else _notchRadiusBottom
            notchHeight = if (_notchHeight == -1) _theme!!.notchHeight else _notchHeight
            size = if (_size == -1) 30 else _size
            speed = if (_speed == -1) 7 else _speed
            cornerTop = if (_radiusTop == -1) 50 else _radiusTop
            cornerBottom = if (_radiusBottom == -1) 50 else _radiusBottom
            type = _type ?: _theme!!.shape
            checkBackground = _theme!!.checkBackground
            colorBg = _theme!!.colorBg
            linkBg = _theme!!.linkBg
            Log.d("sizefgvcjswv", ": $size")
            Log.d("sizefgvcjswv", ": $_size")

            startApp()
            initColor()
            val borderType = intent.getStringExtra("borderType")
            Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
            when (borderType) {
                Const.SNOW -> {
                    Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                    type = Const.SNOW
                    initType()
                }

                Const.HEART -> {
                    Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                    type = Const.HEART
                    initType()
                }

                Const.MOON -> {
                    Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                    type = Const.MOON
                    initType()
                }

                Const.SUN -> {
                    Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                    type = Const.SUN
                    initType()
                }

                Const.STAR -> {
                    Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "datrs :$borderType ")
                    type = Const.STAR
                    initType()
                }

                else -> {
                    Log.d("nuuuuuuuuuuuuuuuuulllllllllllllll", "nothing ")
                    initType()
                }

            }
            initNotch()
            initBorder()

        }

    private fun initColor() {
        try {

            color = _theme!!.color
            Log.d("colorValue", "initColor:${MySharePreferencesEdge.COLOR1} ")
            Log.d("colorValue", "initColor:${MySharePreferencesEdge.COLOR2} ")
            Log.d("colorValue", "initColor:${MySharePreferencesEdge.COLOR3} ")
            Log.d("colorValue", "initColor:${MySharePreferencesEdge.COLOR4} ")
            Log.d("colorValue", "initColor:${MySharePreferencesEdge.COLOR5} ")
            Log.d("colorValue", "initColor:${MySharePreferencesEdge.COLOR6} ")
            MySharePreferencesEdge.setString(
                this, MySharePreferencesEdge.COLOR1, convertIntToString(color.get(0))
            )
            MySharePreferencesEdge.setString(
                this, MySharePreferencesEdge.COLOR2, convertIntToString(color.get(1))
            )
            MySharePreferencesEdge.setString(
                this, MySharePreferencesEdge.COLOR3, convertIntToString(color.get(2))
            )
            MySharePreferencesEdge.setString(
                this, MySharePreferencesEdge.COLOR4, convertIntToString(color.get(3))
            )
            MySharePreferencesEdge.setString(
                this, MySharePreferencesEdge.COLOR5, convertIntToString(color.get(4))
            )
            MySharePreferencesEdge.setString(
                this, MySharePreferencesEdge.COLOR6, convertIntToString(color.get(5))
            )
            check = 0
            colors_list = findViewById<View>(R.id.colors_list) as Spinner
            val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
                this, R.array.color_list, R.layout.color_spinner_layout
            )

            adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout)
            colors_list!!.adapter = adapter
            v = MySharePreferencesEdge.getSpinnerInt(MySharePreferencesEdge.SPINNER, this)
            Log.d("pak", v.toString())
            colors_list!!.setSelection(v!!)
            colors_list!!.onItemSelectedListener = SpinnerItemSelectedListener()
            setBorderColorsListener()


        } catch (e: NullPointerException) {
        }
    }

    private fun initBorder() {
        try {

            binding!!.apply {
                borderSize.progress = size
                animationSpeed.progress = speed * 2
                radiusTop.progress = cornerTop
                radiusBottom.progress = cornerBottom

                edgeLightView.changeSize(size)
                edgeLightView.changeSpeed(speed)
                edgeLightView.changeBorder(cornerTop, cornerBottom)
            }
        } catch (e: NullPointerException) {
        }
    }

    @SuppressLint("WrongConstant")
    private fun initNotch() {
        try {
            binding!!.apply {
                switchNotch.isChecked = checkNotch
                lnControlNotch.visibility = View.VISIBLE
            }
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            val i = displayMetrics.widthPixels / 2 - 200
            Log.d("loooog", "initNotch:${notchBottom} ")
            binding!!.apply {
                notchWidthTop.max = i
                notchWidthBottom.max = i
                notchHeight.max = 150
                notchWidthTop.progress = notchTop
                notchWidthBottom.progress = notchBottom
                notchRadiusTop.progress = Companion.notchRadiusTop
                notchRadiusBottom.progress = Companion.notchRadiusBottom
                notchHeight.progress = Companion.notchHeight
            }

            if (!checkNotch) {
                binding!!.lnControlNotch.visibility = 8
            } else {
                binding!!.lnControlNotch.visibility = View.VISIBLE
            }
            if (edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(
                    MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, this@EdgeOverlaySettingsActivity
                )
            ) {
                edgeLightingView!!.changeNotch(
                    checkNotch,
                    notchTop,
                    notchBottom,
                    notchHeight,
                    notchRadiusTop,
                    notchRadiusBottom
                )
            } else {
                binding!!.edgeLightView.changeNotch(
                    checkNotch,
                    notchTop,
                    notchBottom,
                    notchHeight,
                    notchRadiusTop,
                    notchRadiusBottom
                )
            }
        } catch (e: NullPointerException) {
        }
    }

    fun setBorderColorsListener() {
        binding!!.apply {
            imageColor1.setOnClickListener(BorderColorClickListners())
            imageColor2.setOnClickListener(BorderColorClickListners())
            imageColor3.setOnClickListener(BorderColorClickListners())
            imageColor4.setOnClickListener(BorderColorClickListners())
            imageColor5.setOnClickListener(BorderColorClickListners())
            imageColor6.setOnClickListener(BorderColorClickListners())
        }
    }

    fun init() {
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.TIME_COUNTER_RATE,
            MySharePreferencesEdge.getInt(MySharePreferencesEdge.TIME_COUNTER_RATE, this) + 1,
            this
        )
    }

    private fun startApp() {
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.SIZE, size, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSTOP, cornerTop, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.RADIUSBOTTOM, cornerBottom, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHTOP, notchTop, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHRADIUSTOP, notchRadiusTop, this)
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.NOTCHRADIUSBOTTOM, notchRadiusBottom, this
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHBOTTOM, notchBottom, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.NOTCHHEIGHT, notchHeight, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.SPEED, speed, this)
        MySharePreferencesEdge.putBoolean(MySharePreferencesEdge.CHECKNOTCH, checkNotch, this)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.SHAPE, type)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.BACKGROUND, checkBackground, this)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.BACKGROUNDCOLOR, colorBg)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.BACKGROUNDLINK, linkBg)
        if (isMyServiceRunning(WallpaperWindowEdgeService::class.java)) {
            val intent = Intent(Const.Action_ChangeWindownManager)
            intent.putExtra(Const.CONTROLWINDOW, Const.ALL)
            intent.setPackage(packageName)
            sendBroadcast(intent)
        }
    }

    @SuppressLint("WrongConstant")
    fun isMyServiceRunning(cls: Class<*>): Boolean {
        for (runningServiceInfo in (getSystemService("activity") as ActivityManager).getRunningServices(
            Int.MAX_VALUE
        )) {
            if (cls.name == runningServiceInfo.service.className) {
                return true
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
            alertDialog = null
        }
        try {
            if (binding!!.switchDisplay.isChecked || binding!!.switchCharging.isChecked) {
                if (edgeLightingView != null) {
                    if (edgeLightingView!!.visibility == View.INVISIBLE) {
                        edgeLightingView!!.visibility = View.VISIBLE
                    }
                }

            }
            if (!binding!!.switchDisplay.isChecked && !binding!!.switchCharging.isChecked) {
                if (edgeLightingView != null) {
                    if (edgeLightingView!!.visibility == View.VISIBLE) {
                        edgeLightingView!!.visibility = View.INVISIBLE
                    }
                }

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    public override fun onResume() {
        try {

            binding!!.rangebar1.setRangePinsByIndices(
                warningValuesSharedPreferences.getInt(
                    "WARNING_MIN_PROGRESS_LOC", 0
                ), warningValuesSharedPreferences.getInt("WARNING_PROGRESS_LOC", 6)
            )
            Log.d(
                "kyaYaaaar", "onResume:${
                    warningValuesSharedPreferences.getInt(
                        "WARNING_MIN_PROGRESS_LOC", 0
                    )
                } "
            )
            val display_overlay_prefs = MySharePreferencesEdge.getDisplayOverLayBooleanValue(
                MySharePreferencesEdge.DISPLAY_OVERLAY, this@EdgeOverlaySettingsActivity
            )

//            binding!!.switchDisplay.isChecked = display_overlay_prefs
            binding!!.apply {
                switchDisplay.setOnCheckedChangeListener(null)
                switchDisplay.isChecked = display_overlay_prefs
                switchDisplay.setOnCheckedChangeListener(mDisplayOverlayListener)
            }
            show_charging_prefs = MySharePreferencesEdge.getShowChargingBooleanValue(
                MySharePreferencesEdge.SHOW_WHILE_CHARGING, this@EdgeOverlaySettingsActivity
            )
//            binding!!.switchCharging.isChecked = show_charging_prefs!!
            binding!!.apply {
                switchCharging.setOnCheckedChangeListener(null)
                Log.d("sdsdsdssds", "onResume: $show_charging_prefs")
                switchCharging.isChecked = show_charging_prefs!!
                switchCharging.setOnCheckedChangeListener(mShowWhileChargingListener)
            }

            val always_on_display_prefs = MySharePreferencesEdge.getAlwaysOnDisplayBooleanValue(
                MySharePreferencesEdge.ALWAYS_ON_DISPLAY, this@EdgeOverlaySettingsActivity
            )
//            binding!!.swtichAlways.isChecked = always_on_display_prefs
            binding!!.apply {
                swtichAlways.setOnCheckedChangeListener(null)
                swtichAlways.isChecked = always_on_display_prefs
                swtichAlways.setOnCheckedChangeListener(mAlwaysOnDisplayListener)
            }
            Log.d(
                "whatIsButtonValues",
                "always_on_display_prefs: $always_on_display_prefs && show_charging_prefs : ${show_charging_prefs}   ,,,  ${display_overlay_prefs}"
            )
            if (binding!!.switchCharging.isChecked) {
                binding!!.warningLayoutSuperParent.visibility = View.VISIBLE
            }

            if (edgeLightingView != null) {
                if (edgeLightingView!!.visibility == View.INVISIBLE) {
                    edgeLightingView!!.visibility = View.VISIBLE
                }
            }
        } catch (e: NullPointerException) {
        }
        super.onResume()
    }

    public override fun onStart() {
        super.onStart()
        Log.d(
            "whatIsButtonValues",
            "onStart: ${(!isMyServiceRunning(MyAccessibilityService::class.java))} %%% $displayOverlay "
        )
        Log.d(
            "whatIsButtonValues", "onStart: ${
                (!isAccessibilityServiceEnabled(
                    this@EdgeOverlaySettingsActivity, MyAccessibilityService::class.java
                ))
            } %%% $displayOverlay "
        )
        if (isAccessibilityServiceEnabled(this, MyAccessibilityService::class.java)) {
            Log.d(
                "whatIsButtonValues",
                "onStart inSideIff: ${(isMyServiceRunning(MyAccessibilityService::class.java))}  "
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(this, MyAccessibilityService::class.java))
            } else {
                startService(Intent(this, MyAccessibilityService::class.java))
            }
            Log.d(
                "whatIsButtonValues",
                "onStart inSideIffAfter: ${(isMyServiceRunning(MyAccessibilityService::class.java))}  "
            )
        }
        if (!isAccessibilityServiceEnabled(
                this@EdgeOverlaySettingsActivity, MyAccessibilityService::class.java
            ) && !displayOverlay
        ) {
            MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                MySharePreferencesEdge.DISPLAY_OVERLAY, false, activity
            )
        }
        if (!isAccessibilityServiceEnabled(
                this@EdgeOverlaySettingsActivity, MyAccessibilityService::class.java
            ) && !displayOverlay
        ) {
            MySharePreferencesEdge.putDisplayOverLayBooleanValue(
                MySharePreferencesEdge.SHOW_WHILE_CHARGING, false, activity
            )
        }
        if (!isAccessibilityServiceEnabled(
                this@EdgeOverlaySettingsActivity, MyAccessibilityService::class.java
            ) && !displayOverlay
        ) {
            MySharePreferencesEdge.putAlwaysOnDisplayBooleanValue(
                MySharePreferencesEdge.ALWAYS_ON_DISPLAY, false, activity
            )
        }
        if (isMyServiceRunning(WallpaperWindowEdgeService::class.java)) {
            val intent =
                Intent(this@EdgeOverlaySettingsActivity, WallpaperWindowEdgeService::class.java)
            intent.action = Const.ACTION.STOP_ACTION
            stopService(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null

    }

    public override fun onStop() {
        super.onStop()

        if (isMyServiceRunning(WallpaperService::class.java)) {
            try {
                functionApply()
                val intent = Intent(Const.Action_SetLiveWallpaper)
                intent.putExtra(Const.Action_StopLiveWallpaper, Const.RUN)
                intent.setPackage(packageName)
                sendBroadcast(intent)
            } catch (e: Exception) {
            }
        }
    }

    private fun applyBorderLiveWallpaper() {
        var string = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR1, this)
        var string2 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR2, this)
        var string3 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR3, this)
        var string4 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR4, this)
        var string5 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR5, this)
        var string6 = MySharePreferencesEdge.getString(MySharePreferencesEdge.COLOR6, this)
        if (string == null) {
            string = "#EB1111"
        }
        if (string2 == null) {
            string2 = "#1A11EB"
        }
        if (string3 == null) {
            string3 = "#EB11DA"
        }
        if (string4 == null) {
            string4 = "#11D6EB"
        }
        if (string5 == null) {
            string5 = "#EBDA11"
        }
        if (string6 == null) {
            string6 = "#11EB37"
        }
        val i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SPEED, this)
        val i2 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.SIZE, this)
        val i3 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSTOP, this)
        val i4 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.RADIUSBOTTOM, this)
        val booleanValue =
            MySharePreferencesEdge.getBooleanValue(MySharePreferencesEdge.CHECKNOTCH, this)
        val i5 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHTOP, this)
        val i6 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSTOP, this)
        val i7 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHRADIUSBOTTOM, this)
        val i8 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHBOTTOM, this)
        val i9 = MySharePreferencesEdge.getInt(MySharePreferencesEdge.NOTCHHEIGHT, this)

        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SIZE, i2, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_RADIUSTOP, i3, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_RADIUSBOTTOM, i4, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHTOP, i5, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHRADIUSTOP, i6, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHRADIUSBOTTOM, i7, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHBOTTOM, i8, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHHEIGHT, i9, this)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.FINISH_COLOR1, string)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.FINISH_COLOR2, string2)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.FINISH_COLOR3, string3)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.FINISH_COLOR4, string4)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.FINISH_COLOR5, string5)
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.FINISH_COLOR6, string6)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SPEED, i, this)
        MySharePreferencesEdge.putBoolean(
            MySharePreferencesEdge.FINISH_CHECKNOTCH, booleanValue, this
        )
    }

    fun applyBorderLiveWallpaper(theme: Theme?) {
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SIZE, size, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_RADIUSTOP, cornerTop, this)
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.FINISH_RADIUSBOTTOM, cornerBottom, this
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHTOP, notchTop, this)
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.FINISH_NOTCHRADIUSTOP, notchRadiusTop, this
        )
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.FINISH_NOTCHRADIUSBOTTOM, notchRadiusBottom, this
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHBOTTOM, notchBottom, this)
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_NOTCHHEIGHT, notchHeight, this)
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_COLOR1, convertIntToString(color[0])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_COLOR2, convertIntToString(color[1])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_COLOR3, convertIntToString(color[2])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_COLOR4, convertIntToString(color[3])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_COLOR5, convertIntToString(color[4])
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_COLOR6, convertIntToString(color[5])
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_SPEED, speed, this)
        MySharePreferencesEdge.putBoolean(
            MySharePreferencesEdge.FINISH_CHECKNOTCH, checkNotch, this
        )
        MySharePreferencesEdge.setString(this, MySharePreferencesEdge.FINISH_SHAPE, type)
        MySharePreferencesEdge.setInt(
            MySharePreferencesEdge.FINISH_BACKGROUND, theme!!.checkBackground, this
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_BACKGROUNDCOLOR, theme.colorBg
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_BACKGROUNDLINK, theme.linkBg
        )
    }

    private fun applyBackgroundLiveWallpaper() {
        val i = MySharePreferencesEdge.getInt(MySharePreferencesEdge.BACKGROUND, this)
        val string = MySharePreferencesEdge.getString(MySharePreferencesEdge.BACKGROUNDCOLOR, this)
        val string2 = MySharePreferencesEdge.getString(MySharePreferencesEdge.BACKGROUNDLINK, this)
        MySharePreferencesEdge.setString(
            this,
            MySharePreferencesEdge.FINISH_SHAPE,
            MySharePreferencesEdge.getString(MySharePreferencesEdge.SHAPE, this)
        )
        MySharePreferencesEdge.setInt(MySharePreferencesEdge.FINISH_BACKGROUND, i, this)
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_BACKGROUNDCOLOR, string
        )
        MySharePreferencesEdge.setString(
            this, MySharePreferencesEdge.FINISH_BACKGROUNDLINK, string2
        )
    }

    @SuppressLint("WrongConstant")
    fun setNotchSeekbarListner() {
        binding?.switchNotch?.setOnCheckedChangeListener(CheckedChangeListeners(this@EdgeOverlaySettingsActivity))
        for (seekbar in notchSeekbars) {
            seekbar.setOnSeekBarChangeListener(SeekbarChangeListeners("notch"))
        }
    }

    fun setBorderSeekbarListner() {
        for (seekbar in borderSeekbars) {
            seekbar.setOnSeekBarChangeListener(SeekbarChangeListeners("border"))
        }
    }

    fun setBorderTypeListener() {
        for (imageview in borderTypes) {
            imageview.setOnClickListener(BorderTypeClickListeners())
        }
    }

    @SuppressLint("WrongConstant")
    fun functionApply() {
        try {
            val wallpaperInfo = WallpaperManager.getInstance(this).wallpaperInfo
            val isMyServiceRunning = isMyServiceRunning(WallpaperWindowEdgeService::class.java)

            applyBackgroundLiveWallpaper()
            applyBorderLiveWallpaper()
            if ((wallpaperInfo == null || wallpaperInfo.packageName != packageName) && !isMyServiceRunning) {
                checkBack = true
            } else {
                checkBack = true
                if (active == Const.UPDATE) {
                    Toast.makeText(this, resources.getString(R.string.Update_finish), 0).show()
                }
            }
            EventBus.getDefault().postSticky(CreateOrUpdateEdgesLight(true))
        } catch (e: Exception) {
        }
    }

    fun showAccessibilityPermissionDialog() {
        try {
            if (!MySharePreferencesEdge.getAccessibilityEnabled(
                    MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, activity
                )
            ) {
                var dialog = Dialog(this@EdgeOverlaySettingsActivity)
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                dialog.setContentView(R.layout.accessibility_permisssion_dialog)
                dialog.window!!.setLayout(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                dialog.setCancelable(false)
                dialog.show()
                dialog.findViewById<TextView>(R.id.later).setOnClickListener {
                    dialog.dismiss()
                    try {
                        if (binding!!.switchDisplay.isChecked) {
                            binding!!.apply {
                                switchDisplay.setOnCheckedChangeListener(null)
                                switchDisplay.isChecked = false
                                switchDisplay.setOnCheckedChangeListener(mDisplayOverlayListener)
                            }

                        } else if (binding!!.switchCharging.isChecked) {
                            binding!!.apply {
                                switchCharging.setOnCheckedChangeListener(null)
                                switchCharging.isChecked = false
                                switchCharging.setOnCheckedChangeListener(
                                    mShowWhileChargingListener
                                )
                            }
                        } else if (binding!!.swtichAlways.isChecked) {
                            binding!!.apply {
                                swtichAlways.setOnCheckedChangeListener(null)
                                swtichAlways.isChecked = false
                                swtichAlways.setOnCheckedChangeListener(mShowWhileChargingListener)
                            }

                        }
                    } catch (e: NullPointerException) {
                    }
                }
                dialog.findViewById<TextView>(R.id.yes).setOnClickListener {
                    dialog.dismiss()
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
            }

        } catch (e: WindowManager.BadTokenException) {
        }
    }

    companion object {

        var displayOverlay = false


        var RC_App_Update = 700

        lateinit var colorNames: List<String>

        var _color = 0
        var themeArrayList: MutableList<Any?>? = null
        var speed = 0
        var size = 0
        var cornerBottom = 0
        var cornerTop = 0
        var notchBottom = 0
        var notchHeight = 0
        var notchRadiusBottom = 0
        var notchRadiusTop = 0
        var notchTop = 0
        var checkBack = false
        var checkBottom = false
        var checkColor = ""
        var checkNotch = false
        var toast: Toast? = null
        var mAlwaysOnDisplayListener: CompoundButton.OnCheckedChangeListener? = null
        var mDisplayOverlayListener: CompoundButton.OnCheckedChangeListener? = null
        var mShowWhileChargingListener: CompoundButton.OnCheckedChangeListener? = null

        var binding: ActivityEdgeOverlaySettingsBinding? = null
        var power_connected = false

        var id = 0
        var colors_list: Spinner? = null
        var context: Context? = null
        var activity: EdgeOverlaySettingsActivity? = null

        @JvmField
//        var tv:TextView? = null

        var v: Int? = null
        var show_charging_prefs: Boolean? = null
        var drawOverlay = false
        var _theme: Theme? = null
        var type: String? = null
        var checkBackground = 0
        var colorBg: String? = null
        var linkBg: String? = null

        var color = IntArray(7)
        var _color1 = 0
        var _color2 = 0
        var _color3 = 0
        var _color4 = 0
        var _color5 = 0
        var _color6 = 0
        var img_c1 = 0
        var img_c2 = 0
        var img_c3 = 0
        var img_c4 = 0
        var img_c5 = 0
        var img_c6 = 0
        var background = 0
        var firstLoadData = false


    }

    fun showDialog() {
        customdialog()
    }

    fun customdialog() {
        val builder = AlertDialog.Builder(this)
        val viewGroup = findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(this@EdgeOverlaySettingsActivity)
            .inflate(R.layout.customview, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.show()
    }

    private fun loadInterstitialAd(id: String?) {
        Log.d(
            "whatisIssue",
            "${isOnline(this@EdgeOverlaySettingsActivity) && wholeScreenAdShow && wholeInterAdShow && isIntervalElapsed()}"
        )
        if (checkContext(context = this)) {
            Interstitial.show(this@EdgeOverlaySettingsActivity,
                object : AdInterstitialShowListeners {
                    override fun onDismissed() {
                        Log.d("whatisIssue", "onDismissed")
                        isinterstitialvisible = false
                        updateLastAdShownTime()

                    }

                    override fun onError() {
                        Log.d("whatisIssue", "onDismissed")
                        Interstitial.load(
                            this@EdgeOverlaySettingsActivity,
                            activitiesAdId,
                        )

                    }

                    override fun onShowed() {
                        isinterstitialvisible = true
                        Log.d("whatisIssue", "onDismissed")

                        Interstitial.load(
                            this@EdgeOverlaySettingsActivity,
                            activitiesAdId,
                        )
                        fireEvent("SHOW_EL_permission_btn_click")

                    }
                })
        }


    }

    private fun getStatusBarAndTitleBarHeight(window: Window): Pair<Int, Int> {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top



        Log.i("*** Elenasys :: ", "StatusBar Height= $statusBarHeight , TitleBar Height = 90")

        return Pair(statusBarHeight, 20)
    }
}
