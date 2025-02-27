package com.skytek.edgelighting.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.firebase.Firebase
import com.google.firebase.remoteconfig.remoteConfig
import com.mobi.pixels.GDPRMessage
import com.mobi.pixels.adBannerOnDemand.loadOnDemandBannerAd
import com.mobi.pixels.adInterstitial.AdInterstitialLoadListeners
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.adNativePreload.AdNativePreload
import com.mobi.pixels.adNativePreload.AdNativePreloadListeners
import com.mobi.pixels.enums.BannerAdType
import com.mobi.pixels.firebase.InitializeRemoteConfig
import com.mobi.pixels.firebase.fireEvent
import com.mobi.pixels.isOnline
import com.skytek.edgelighting.App
import com.skytek.edgelighting.BuildConfig
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.onboarding.EdgeAndWallpaperOnboarding
import com.skytek.edgelighting.activities.onboarding.OnBoardingLanguageScreen
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.ads.OpenAd
import com.skytek.edgelighting.databinding.ActivitySplashBinding
import com.skytek.edgelighting.utils.AdResources
import com.skytek.edgelighting.utils.AdResources.ElBtnClickCount
import com.skytek.edgelighting.utils.AdResources.ElWallpaperTimeCount
import com.skytek.edgelighting.utils.AdResources.INTROOnBoardingAdId
import com.skytek.edgelighting.utils.AdResources.InAppOnBoardingAdId
import com.skytek.edgelighting.utils.AdResources.NativeToBanner
import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.bannerAdId
import com.skytek.edgelighting.utils.AdResources.bannerToNative
import com.skytek.edgelighting.utils.AdResources.inAppOnboardingShow
import com.skytek.edgelighting.utils.AdResources.mainScreenAdShow
import com.skytek.edgelighting.utils.AdResources.nativeAdId
import com.skytek.edgelighting.utils.AdResources.onboardingShow
import com.skytek.edgelighting.utils.AdResources.openAppAdId
import com.skytek.edgelighting.utils.AdResources.openAppAdShow
import com.skytek.edgelighting.utils.AdResources.permissionOnBoardingAdId
import com.skytek.edgelighting.utils.AdResources.permissionOnboardingAdShow
import com.skytek.edgelighting.utils.AdResources.preloadedAd
import com.skytek.edgelighting.utils.AdResources.preloadedAd2
import com.skytek.edgelighting.utils.AdResources.settingScreenNativeAdShow
import com.skytek.edgelighting.utils.AdResources.splashBannerAdId
import com.skytek.edgelighting.utils.AdResources.splashBannerAdShow
import com.skytek.edgelighting.utils.AdResources.splashScreenAdId
import com.skytek.edgelighting.utils.AdResources.splashScreenAdShow
import com.skytek.edgelighting.utils.AdResources.version
import com.skytek.edgelighting.utils.AdResources.wallpaperOnboardingAdShow
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.utils.Utills
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private var remainingTime: Long = 20000
    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    lateinit var handler: Handler
    var mInterstitial = false

    var countDownTimer: CountDownTimer? = null

    private lateinit var binding: ActivitySplashBinding
    private fun preloadAd() {

        Log.d("onboardingdebug", "preloading OnBoarding Ad: ")
        val adLoader = AdLoader.Builder(this, InAppOnBoardingAdId)
            .forNativeAd { nativeAd ->
                preloadedAd = nativeAd // Store the preloaded ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Log or handle the ad loading failure
                    preloadedAd = null

                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun preloadAd2() {

        Log.d("onboardingdebug", "preloading OnBoarding Ad: ")
        val adLoader = AdLoader.Builder(this, InAppOnBoardingAdId)
            .forNativeAd { nativeAd ->
                preloadedAd2 = nativeAd // Store the preloaded ad
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    // Log or handle the ad loading failure
                    preloadedAd2 = null

                }
            })
            .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        StatusBarUtil.setTransparent(this@SplashActivity)
        binding = ActivitySplashBinding.inflate(layoutInflater)
//        lifecycleScope.launch {
//            Interstitial.load(this@SplashActivity,"ca-app-pub-3940256099942544/1033173712")
//        }
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()

                    .build()
            )
            StrictMode.setVmPolicy(
                VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()

                    .build()
            )
        }
        setContentView(binding.root)


        progressBar = findViewById(R.id.progressBar)
        progressText = findViewById(R.id.progressText)
        progressText.text = "0%"
        GlobalScope.launch(Dispatchers.IO) {
            Utills.getHeightScreen(this@SplashActivity)
        }

        // gettingAllSharedPreferenceValuesForDefaultEdgeLighting()

        Log.d("whatshcsbcbc", "${remainingTime}: ${mInterstitial}  ")
        startCountdown(remainingTime)
        isinterstitialvisible = true
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        fireEvent("RV_${packageInfo.versionCode}_Splash_Activity_Total ")
        MobileAds.initialize(this) {}
        if (isOnline(this)) {
            fireEvent("RV_${packageInfo.versionCode}_Splash_Activity_net_Available ")
            val consent = GDPRMessage(this)
            consent.consentMessageRequest()
            consent.getConsent {
                if (it) {
                    if (BuildConfig.DEBUG) {
                        Log.d("getting value here", "insewide debug ")
                        splashScreenAdId = "ca-app-pub-3940256099942544/1033173712"
                        activitiesAdId = "ca-app-pub-3940256099942544/1033173712"
                        nativeAdId = "ca-app-pub-3940256099942544/2247696110"
                        bannerAdId = "ca-app-pub-3940256099942544/6300978111"
                        splashBannerAdId = "ca-app-pub-3940256099942544/6300978111"
                        openAppAdId = "ca-app-pub-3940256099942544/1033173712"
                        permissionOnBoardingAdId = "ca-app-pub-3940256099942544/2247696110"
                        INTROOnBoardingAdId = "ca-app-pub-3940256099942544/2247696110"
                        onboardingShow = true

                        loadInterstitialAd(splashScreenAdId)
                        loadOnDemandBannerAd(
                            this, binding.adContainer, splashBannerAdId, BannerAdType.Banner

                        ).enableShimmerEffect(true).load()

                    } else {
                        Log.d("", "insewide consenmt ")
                        InitializeRemoteConfig(86400) {
                            Log.d("getting value here", "start to get value  ")
                            splashScreenAdId = Firebase.remoteConfig.getString("EL_I_Splash")
                            activitiesAdId = Firebase.remoteConfig.getString("EL_I_Activities")
                            nativeAdId = Firebase.remoteConfig.getString("EL_NativeAd")
                            bannerAdId = Firebase.remoteConfig.getString("EL_B_Main")
                            splashBannerAdId = Firebase.remoteConfig.getString("Splash_banner_id")
                            InAppOnBoardingAdId = Firebase.remoteConfig.getString("in_app_lan_native_id")
                            openAppAdId = Firebase.remoteConfig.getString("EL_Open_App_Ad_Id")
                            permissionOnBoardingAdId =
                                Firebase.remoteConfig.getString("permission_on_boarding_ad_id")
                            INTROOnBoardingAdId =
                                Firebase.remoteConfig.getString("INTRO_on_boarding_ad_id")
                            splashScreenAdShow =
                                Firebase.remoteConfig.getBoolean("Splash_Screen_Ad_Show")
                            inAppOnboardingShow =
                                Firebase.remoteConfig.getBoolean("inApp_onboarding_show")

//                            staticWallpaperAdShow =
//                                Firebase.remoteConfig.getBoolean("Static_Wallpaper_Screen_Ad_Show")
//                            liveWallpaperScreenAdShow =
//                                Firebase.remoteConfig.getBoolean("live_Wallpaper_Screen_Ad_Show")
//                            settingScreenAdShow =
//                                Firebase.remoteConfig.getBoolean("Setting_Screen_Ad_Show")
                            settingScreenNativeAdShow =
                                Firebase.remoteConfig.getBoolean("Setting_Screen_native_Ad_Show")
                            wholeScreenAdShow =
                                Firebase.remoteConfig.getBoolean("Whole_Screen_Ad_Show")
                            mainScreenAdShow =
                                Firebase.remoteConfig.getBoolean("Main_Screen_Ad_Show")
                            wholeInterAdShow =
                                Firebase.remoteConfig.getBoolean("Whole_Inter_Ad_Show")
                            bannerToNative =
                                Firebase.remoteConfig.getBoolean("Banner_to_Native_Swap")
                            splashBannerAdShow =
                                Firebase.remoteConfig.getBoolean("Splash_Banner_Ad_Show")
                            NativeToBanner =
                                Firebase.remoteConfig.getBoolean("Native_to_Banner_Swap")
                            onboardingShow = Firebase.remoteConfig.getBoolean("onboarding_show")
                            Log.d(
                                "gdiwuggccfegd", "$onboardingShow "
                            )
                            version = Firebase.remoteConfig.getString("version_json")

                            Log.d(
                                "UpdateCheck", "$version "
                            )
                            ElWallpaperTimeCount =
                                (Firebase.remoteConfig.getString("EL_Wallpaper_Time_Count")).toInt()
                            ElBtnClickCount =
                                (Firebase.remoteConfig.getString("EL_Btn_click_Count")).toInt()
                            openAppAdShow = Firebase.remoteConfig.getBoolean("EL_Open_App_Ad_Show")
                            permissionOnboardingAdShow =
                                Firebase.remoteConfig.getBoolean("permission_onboarding_Screen_Ad_Show")
                            wallpaperOnboardingAdShow =
                                Firebase.remoteConfig.getBoolean("wallpaper_onboarding_Screen_Ad_Show")
                            Log.d("getting value here", "All value Are Ready   ")
                            if (isFirstTime()) {
                                   preloadAd()
                                preloadAd2()

                            }
                            if (splashScreenAdShow && wholeScreenAdShow && wholeInterAdShow) {
                                Log.d("getting value here", "Splash tre and  Are Ready   ")
                                loadInterstitialAd(splashScreenAdId)

                            } else {
                                Log.d("getting value here", "Splash false ")
                                startCountdownWithOutInternet(4000)
                            }

                            if (wholeScreenAdShow && splashScreenAdShow && splashBannerAdShow) {
                                loadOnDemandBannerAd(
                                    this, binding.adContainer, splashBannerAdId, BannerAdType.Banner

                                ).enableShimmerEffect(true).load()
                            }
                        }
                    }


                }
            }
        } else {
            fireEvent("RV_${packageInfo.versionCode}_Splash_Activity_net_Not_Available ")

            startCountdownWithOutInternet(4000)

        }


    }


    override fun onResume() {
        super.onResume()

        Log.d("abcd", "onResume: ${countDownTimer == null}")
        if (countDownTimer == null) {
            startCountdown(remainingTime)
        }
    }

    private fun startCountdownWithOutInternet(timeInMillis: Long) {
        Log.d("fdwjfdxw", "startCountdown: ")

        Log.d("fdwjfdxw", "isOnline: ")

        // Create a CountDownTimer with 100ms interval
        countDownTimer = object : CountDownTimer(timeInMillis, 100) {

            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished

                // Calculate the progress based on the time remaining
                val progress = ((timeInMillis - remainingTime) * 100 / timeInMillis).toInt()
                progressBar.progress = progress
                progressText.text = "$progress%"

                Log.d("abcd", remainingTime.toString())
            }

            override fun onFinish() {
                progressBar.progress = 100
                progressText.text = "100%"
                navigateToNextActivity()
            }

        }.start()

    }

    private fun startCountdown(timeInMillis: Long) {
        Log.d("fdwjfdxw", "startCountdown: ")
        if (isOnline(this)) {
            Log.d("fdwjfdxw", "isOnline: ")
            countDownTimer = object : CountDownTimer(timeInMillis, 100) {

                override fun onTick(millisUntilFinished: Long) {

                    remainingTime = millisUntilFinished

                    val progress = ((timeInMillis - remainingTime) * 100 / timeInMillis).toInt()
                    progressBar.progress = progress
                    progressText.text = "$progress%"
                    Log.d("abcd", remainingTime.toString())

                    if (mInterstitial) {

                        progressBar.progress = 100
                        progressText.text = "100%"
                        Handler(Looper.getMainLooper()).postDelayed({
                            navigateToNextActivity()
                            if (countDownTimer != null) {
                                countDownTimer!!.cancel()
                            }
                            Interstitial.show(
                                this@SplashActivity,
                                object : AdInterstitialShowListeners {
                                    override fun onDismissed() {
                                        Log.d("fujhfytdydtydtydt", "${version}: ")



                                        isinterstitialvisible = false
                                    }

                                    override fun onError(error: String) {

                                        isinterstitialvisible = false
                                    }

                                    override fun onShowed() {

                                    }
                                })
                        }, 0)


                    }

                }

                override fun onFinish() {

                    if (mInterstitial) {
                        navigateToNextActivity()

                        Interstitial.show(
                            this@SplashActivity,
                            object : AdInterstitialShowListeners {
                                override fun onDismissed() {

                                    isinterstitialvisible = false
                                }

                                override fun onError(error: String) {

                                    isinterstitialvisible = false
                                }

                                override fun onShowed() {

                                }
                            })

                    } else {
                        navigateToNextActivity()

                    }

                }

            }.start()
        }
    }

    private fun isFirstTime(): Boolean {
        val sharedPreferences = getSharedPreferences("onboardinglan", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFirstTimelan", true)
    }

    private fun navigateToNextActivity() {
        updateLastAdShownTime()
        Log.d("adIsLoaded", "why: ")

        if (!MySharePreferencesEdge.getAccessibilityEnabled(
                MySharePreferencesEdge.ACCESSIBILITY_BROADCAST, App.context
            )
        ) {
            Log.d("dsjkdsjkdhskd", "onCreate: if ${isFirstTime()}")

                startActivity(
                    Intent(
                        this@SplashActivity, RequestPermissionActivity::class.java
                    )
                )



        } else {
            Log.d("dsjkdsjkdhskd", "onCreate: ")

            if (onboardingShow) {
                startActivity(
                    Intent(
                        this@SplashActivity, EdgeAndWallpaperOnboarding::class.java
                    )
                )
            } else {
                startActivity(
                    Intent(
                        this@SplashActivity, MainActivity::class.java
                    )
                )
            }

        }
        finish()
    }

    override fun onPause() {
        super.onPause()
        Log.d("abcd", "onPause: ${countDownTimer == null}")
        if (countDownTimer != null) {


            countDownTimer!!.cancel()
            countDownTimer = null
            Log.d("abcd", "onPause:${countDownTimer} ")

        }
    }


    private fun loadInterstitialAd(id: String?) {
        Log.d("whyWhatHappen", "strdsf: ")
        Interstitial.load(this, id!!, object : AdInterstitialLoadListeners {
            override fun onFailedToLoad(error: String) {
                if (wholeScreenAdShow && openAppAdShow) {
                    OpenAd(applicationContext)
                }
            }

            override fun onLoaded() {
                if (wholeScreenAdShow && openAppAdShow) {
                    OpenAd(applicationContext)
                }
                mInterstitial = true


                Log.d("whyWhatHappen", "onFailedToLoad: ")
            }


            override fun onPreviousAdLoading() {
                Log.d("sdfjkhdsf", "mainonPreviousAdLoading")
            }

        })

    }
}

