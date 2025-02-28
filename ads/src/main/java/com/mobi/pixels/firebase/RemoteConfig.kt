package com.mobi.pixels.firebase

import android.util.Log
import com.example.ads.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class InitializeRemoteConfig(refreshTime:Long, private val onFetchSuccess: () -> Unit) {

    private val remoteConfig: FirebaseRemoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = refreshTime
        }
        remoteConfig.setConfigSettingsAsync(configSettings)

        CoroutineScope(Dispatchers.IO).launch {
            remoteConfig.fetchAndActivate()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        onFetchSuccess()
                    }
                }
        }
    }
}