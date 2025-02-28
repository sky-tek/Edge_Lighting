package com.mobi.pixels.adBannerOnDemand


interface AdBannerOnDemandListeners {
    fun onAdLoaded()
    fun onAdFailedToLoad(error: String)
}