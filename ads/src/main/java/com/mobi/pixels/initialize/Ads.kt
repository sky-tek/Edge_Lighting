package com.mobi.pixels.initialize

import android.app.Activity
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object Ads {
    internal var IsAdsAllowed   = true
    fun initialize(ctx: Activity, isAdsAllowed:Boolean? = null){
        if (isAdsAllowed != null) {
            IsAdsAllowed  = isAdsAllowed
        }
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            MobileAds.initialize(ctx) {}
        }
    }
}