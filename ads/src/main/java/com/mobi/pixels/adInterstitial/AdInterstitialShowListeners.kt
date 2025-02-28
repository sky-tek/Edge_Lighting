package com.mobi.pixels.adInterstitial

interface AdInterstitialShowListeners {
    fun onShowed()
    fun onError(error: String)
    fun onDismissed()

}