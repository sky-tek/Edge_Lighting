package com.skytek.edgelighting

import android.app.Activity
import android.app.Application
import android.content.Context

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









