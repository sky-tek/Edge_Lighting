package com.skytek.edgelighting.utils

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.util.Log

fun checkContext(context: Context): Boolean {
    when (context) {
        is Activity -> {
            Log.d("ContextChecker", "Context is from an Activity: ${context.localClassName}")
            return true
        }

        is Service -> {
            Log.d("ContextChecker", "Context is from a Service: ${context.javaClass.simpleName}")
            return false
        }

        is Application -> {
            Log.d(
                "ContextChecker",
                "Context is from an Application: ${context.javaClass.simpleName}"
            )
            return false
        }

        else -> {
            Log.d("ContextChecker", "Context is of unknown type: ${context.javaClass.simpleName}")
            return false
        }
    }

}