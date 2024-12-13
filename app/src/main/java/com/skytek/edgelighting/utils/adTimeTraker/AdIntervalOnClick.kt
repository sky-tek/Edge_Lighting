package com.skytek.edgelighting.utils.adTimeTraker

import android.os.SystemClock
import android.util.Log
import com.skytek.edgelighting.utils.AdResources.ElWallpaperTimeCount


private var lastAdShownTime: Long = 0

fun isIntervalElapsed(): Boolean {
    return SystemClock.elapsedRealtime() - lastAdShownTime >= ElWallpaperTimeCount
}

fun updateLastAdShownTime() {
    Log.d(
        "isIdsAvailable",
        "adInterval: $ElWallpaperTimeCount $lastAdShownTime  ${isIntervalElapsed()}"
    )
    lastAdShownTime = SystemClock.elapsedRealtime()
}

fun getLastAdShownTime(): Long {
    return lastAdShownTime
}
