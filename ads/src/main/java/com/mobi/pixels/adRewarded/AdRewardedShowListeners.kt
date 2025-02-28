package com.mobi.pixels.adRewarded

interface AdRewardedShowListeners {
    fun onShowed()
    fun onError(error: String)
    fun onDismissed()
    fun onCompleted()

}