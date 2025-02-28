package com.mobi.pixels.adInterstitial


interface AdInterstitialLoadListeners {
    fun onLoaded()
    fun onFailedToLoad(error: String)
    fun onPreviousAdLoading()

}