package com.mobi.pixels.adRewarded

import com.google.android.gms.ads.LoadAdError

interface AdRewardedLoadListeners {
    fun onLoaded()
    fun onFailedToLoad(error : String)
    fun onPreviousAdLoading()

}