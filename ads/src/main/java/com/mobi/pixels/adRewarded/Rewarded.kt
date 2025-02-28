package com.mobi.pixels.adRewarded

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.mobi.pixels.initialize.Ads
import com.mobi.pixels.isOnline
import com.mobi.pixels.openAd.InitializeOpenAd

object Rewarded {

    private var rewardedAd: RewardedAd? = null
    private var isPreviousAdLoading = false
    internal var isShowingRewardedAd = false

    fun load(ctx: Activity, id: String,loadListener: AdRewardedLoadListeners? = null){
        if (!isOnline(ctx)) {
            loadListener?.onFailedToLoad("Internet connection not detected. Please check your connection and try again.")
            return
        }
        if (!Ads.IsAdsAllowed) {
            loadListener?.onFailedToLoad("Ads disabled by developer")
            return
        }
        if (rewardedAd !=null){
            loadListener?.onLoaded()
            return
        }

        val callback = object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                isPreviousAdLoading = false
                rewardedAd =ad
                loadListener?.onLoaded()
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                isPreviousAdLoading = false
                loadListener?.onFailedToLoad(adError.message)
            } }

        if (!isPreviousAdLoading){
            isPreviousAdLoading = true

            var adRequest = AdRequest.Builder().build()

            RewardedAd.load(ctx,id, adRequest,callback)
        }
        else{
            loadListener?.onPreviousAdLoading()
        }

    }


    fun show(ctx: Activity,showListener: AdRewardedShowListeners? = null){
        if (!Ads.IsAdsAllowed) {
            showListener?.onError("Ads disabled by developer.")
            return
        }
        if (rewardedAd ==null) {
            showListener?.onError("Ad is not loaded yet.")
            return
        }
        if (isShowingRewardedAd || InitializeOpenAd.isShowingOpenAd){
            showListener?.onError("Rewarded Ad or openAd is already showing on the screen.")
            return
        }

        rewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
                isShowingRewardedAd = false
                showListener?.onDismissed()

            }
            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
                rewardedAd = null
                isShowingRewardedAd = true
                showListener?.onShowed()

            }
        }

        rewardedAd?.show(ctx, OnUserEarnedRewardListener { rewardItem ->
            showListener?.onCompleted()
            // Handle the reward.
            val rewardAmount = rewardItem.amount
            val rewardType = rewardItem.type
            Log.d("hjjh", "User earned the reward.")
        })


    }

}