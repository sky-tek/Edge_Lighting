package com.mobi.pixels.adNativeOnDemand

interface AdNativeOnDemandListeners {
    fun onAdLoaded()
    fun onAdFailedToLoad(error: String)
}