package com.mobi.pixels.adInterstitial

import android.app.Activity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.mobi.pixels.initialize.Ads
import com.mobi.pixels.isOnline
import com.mobi.pixels.openAd.InitializeOpenAd
import com.mobi.pixels.openAd.InitializeOpenAd.Companion.isShowingOpenAd

object Interstitial {
        private var mInterstitialAd: InterstitialAd? = null
        private var isPreviousAdLoading = false
        internal var isShowingInterstitialAd = false

        fun load(ctx: Activity, id: String,loadListener: AdInterstitialLoadListeners? = null) {
            if (!isOnline(ctx)) {
                loadListener?.onFailedToLoad("Internet connection not detected. Please check your connection and try again.")
                return
            }
             if (!Ads.IsAdsAllowed) {
                loadListener?.onFailedToLoad("Ads disabled by developer.")
                return
            }
            if (mInterstitialAd != null) {
                loadListener?.onLoaded()
                return
            }

            val callback = object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    isPreviousAdLoading = false
                    mInterstitialAd = interstitialAd
                    loadListener?.onLoaded()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    isPreviousAdLoading = false
                    loadListener?.onFailedToLoad(adError.message)
                }
            }
            if (!isPreviousAdLoading){
                isPreviousAdLoading = true
                val adRequest = AdRequest.Builder().build()
                InterstitialAd.load(ctx, id, adRequest,callback)
            }
            else{
                loadListener?.onPreviousAdLoading()
            }
        }


        fun show(ctx: Activity, showListener: AdInterstitialShowListeners? = null) {

            if (!Ads.IsAdsAllowed) {
                showListener?.onError("Ads disabled by developer.")
                return
            }

            if (mInterstitialAd == null) {
                showListener?.onError("Ad is not loaded yet.")
                return
            }
            if (isShowingInterstitialAd || isShowingOpenAd){
                showListener?.onError("Interstitial Ad or openAd is already showing on the screen.")
                return
            }

            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    super.onAdDismissedFullScreenContent()
                    isShowingInterstitialAd = false
                    showListener?.onDismissed()
                }

                override fun onAdShowedFullScreenContent() {
                    super.onAdShowedFullScreenContent()
                    isShowingInterstitialAd = true
                    mInterstitialAd = null
                    showListener?.onShowed()
                }
            }

            mInterstitialAd?.show(ctx)
        }

    }


