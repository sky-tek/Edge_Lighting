package com.skytek.edgelighting.ads


import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import com.google.firebase.analytics.FirebaseAnalytics
import com.skytek.edgelighting.ads.IsShowingOpenAd.isShowingAd
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.utils.AdResources.openAppAdId

import java.util.Date

object IsShowingOpenAd{
  var  isShowingAd=false
   var isinterstitialvisible=false
}
class OpenAd(val context: Context) : Application.ActivityLifecycleCallbacks,
    DefaultLifecycleObserver {

    private var analytics: FirebaseAnalytics? = null
    private var appOpenAdManager: AppOpenAdManager? = null
    private var currentActivity: Activity? = null

    init {
        (context as Application).registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        appOpenAdManager = AppOpenAdManager()
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        // Show the ad (if available) when the app moves to foreground.
        currentActivity?.let { appOpenAdManager!!.showAdIfAvailable(it) }
    }

    /** ActivityLifecycleCallback methods.  */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {
        // An ad activity is started when an ad is showing, which could be AdActivity class from Google
        // SDK or another activity class implemented by a third party mediation partner. Updating the
        // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
        // one that shows the ad.
        Log.d("whyyyyyyyyyyyyyyyy", "${activity.javaClass.simpleName}: ")
        if (!isShowingAd) {
            currentActivity = activity
        }
    }

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}

    /**
     * Load an app open ad.
     *
     * @param activity the activity that shows the app open ad
     */


    fun showAdIfAvailable(
        activity: Activity, onShowAdCompleteListener: OnShowAdCompleteListener
    ) {
        // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
        // class.
        appOpenAdManager!!.showAdIfAvailable(activity, onShowAdCompleteListener)
    }


    interface OnShowAdCompleteListener {
        fun onShowAdComplete()
    }


    private class AppOpenAdManager {

        private var appOpenAd: AppOpenAd? = null
        private var isLoadingAd = false


        private var loadTime: Long = 0


        fun loadAd(context: Context) {
            if (true) {
                Log.d("ghjhgjhgjhg", "loadAd: 1")
                // Do not load ad if there is an unused ad or one is already loading.
                if (isLoadingAd || isAdAvailable) {
                    return
                }
                isLoadingAd = true
                val request = AdRequest.Builder().build()
                AppOpenAd.load(
                    context,
                    openAppAdId,
                    request,
                    object : AppOpenAdLoadCallback() {

                        override fun onAdLoaded(ad: AppOpenAd) {
                            appOpenAd = ad
                            isLoadingAd = false
                            loadTime = Date().time
                            Log.d(LOG_TAG, "onAdLoaded.")

                        }


                        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                            isLoadingAd = false
                            Log.d(LOG_TAG, "onAdFailedToLoad: " + loadAdError.message)

                        }
                    })
            }
        }


        private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
            val dateDifference = Date().time - loadTime
            val numMilliSecondsPerHour: Long = 3600000
            return dateDifference < numMilliSecondsPerHour * numHours
        }

        private val isAdAvailable: Boolean
            private get() =// Ad references in the app open beta will time out after four hours, but this time limit
            // may change in future beta versions. For details, see:
                // https://support.google.com/admob/answer/9341964?hl=en
                appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)

        fun showAdIfAvailable(
            activity: Activity,
            onShowAdCompleteListener: OnShowAdCompleteListener =
                object : OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        // Empty because the user will go back to the activity that shows the ad.
                    }
                }
        ) {
            Log.d("ghjhgjhgjhg", "loadAd: 2")
            // If the app open ad is already showing, do not show the ad again.
            if (isShowingAd) {
                Log.d(LOG_TAG, "The app open ad is already showing.")
                return
            }
            Log.d("ghjhgjhgjhg", "loadAd: 3")
            // If the app open ad is not available yet, invoke the callback then load the ad.
            if (!isAdAvailable) {
                Log.d(LOG_TAG, "The app open ad is not ready yet.")
                onShowAdCompleteListener.onShowAdComplete()

                loadAd(activity)

                return
            }
            Log.d("ghjhgjhgjhg", "loadAd: 4")
            Log.d(LOG_TAG, "Will show ad.")
            appOpenAd!!.fullScreenContentCallback = object : FullScreenContentCallback() {

                override fun onAdDismissedFullScreenContent() {
                    // Set the reference to null so isAdAvailable() returns false.
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "onAdDismissedFullScreenContent.")
                    onShowAdCompleteListener.onShowAdComplete()
                    // AppopenupdateLastAdShownTime()
                    loadAd(activity)

                }


                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    appOpenAd = null
                    isShowingAd = false
                    Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.message)

                    onShowAdCompleteListener.onShowAdComplete()

                    loadAd(activity)

                }


                override fun onAdShowedFullScreenContent() {
                    Log.d(LOG_TAG, "onAdShowedFullScreenContent.")

                }
            }

            Log.d("ghjhgjhgjhg", "showAdIfAvailable: " + isinterstitialvisible)
            if (!isinterstitialvisible) {
                if (activity::class.java.simpleName != "SplashActivity") {
                    isShowingAd = true
                    appOpenAd!!.show(activity)
                }
            }
        }

        companion object {
            private const val LOG_TAG = "AppOpenAdManager"

        }
    }


}
