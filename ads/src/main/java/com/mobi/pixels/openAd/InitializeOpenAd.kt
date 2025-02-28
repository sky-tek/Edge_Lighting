package com.mobi.pixels.openAd

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.adRewarded.Rewarded
import com.mobi.pixels.initialize.Ads
import java.util.Date

class InitializeOpenAd(val context: Context, val adUnit:String, val screenDoNotWantToShow:String? = null) : Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {
    private var appOpenAd: AppOpenAd? = null
    private var currentActivity: Activity? = null
    companion object{
        var isShowingOpenAd = false
    }

    private var loadTime: Long = 0
    private  var isAdLoadingInProgress = false

    init {
        isShowingOpenAd = false
        (context as Application).registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }


    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    fun fetchAd() {
        if (isAdAvailable() || isAdLoadingInProgress || !Ads.IsAdsAllowed) return
        isAdLoadingInProgress = true
        val loadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(openAd: AppOpenAd) {
                super.onAdLoaded(openAd)
                Log.d("asfs","loaded")
                appOpenAd = openAd
                loadTime = Date().time
                isAdLoadingInProgress = false
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
                super.onAdFailedToLoad(error)
                Log.d("asfs","failed")
                isAdLoadingInProgress = false
            }
        }
        val request = AdRequest.Builder().build()
        AppOpenAd.load(context, adUnit, request, loadCallback)
    }

    private fun showAdIfAvailable() {
        Log.d("gsfgdf",context::class.java.simpleName)

        if (Ads.IsAdsAllowed && !isShowingOpenAd && isAdAvailable() && currentActivity!=null && !Interstitial.isShowingInterstitialAd && !Rewarded.isShowingRewardedAd) {
            isShowingOpenAd = true
            val fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    appOpenAd = null
                    isShowingOpenAd = false
                    fetchAd()
                }

            }
            appOpenAd?.fullScreenContentCallback =fullScreenContentCallback
            appOpenAd?.show(currentActivity!!)
        }
        else {
             fetchAd()
        }


    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        if (activity::class.java.simpleName != screenDoNotWantToShow) currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {

    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    // this method will only call when app popups in foreground after app minimize
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        showAdIfAvailable()
    }
}