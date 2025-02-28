package com.mobi.pixels.adNativePreload

interface AdNativePreloadListeners {
    fun onAdLoaded()
    fun onAdFailedToLoad(error: String)
    fun onPreviousAdLoading()
}