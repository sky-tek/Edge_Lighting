package com.skytek.edgelighting

import android.app.Activity
import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
//import com.mobi.pixels.openAd.InitializeOpenAd
import java.util.*

private const val LOG_TAG = "MyApplication"

class App : Application() {


    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()

        context = applicationContext



    }

    companion object{
       var context: Context? = null

        var openAd = true



    }



}









