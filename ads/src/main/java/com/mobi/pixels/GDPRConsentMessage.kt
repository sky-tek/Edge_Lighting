package com.mobi.pixels

import android.app.Activity
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import java.util.concurrent.atomic.AtomicBoolean


class GDPRMessage(activity: Activity){
    private var consentInformation: ConsentInformation
    private var activity = activity
    private var params: ConsentRequestParameters? = null
    private var consentGrantedCalled = AtomicBoolean(false)

    init {
       consentInformation = UserMessagingPlatform.getConsentInformation(activity)

   }

    fun consentMessageRequest(){
        // Set tag for under age of consent. false means users are not under age of consent.
        params = ConsentRequestParameters
            .Builder()
            .setTagForUnderAgeOfConsent(false)
            .build()
        }
    fun consentMessageRequest(deviceID: String, resetAfterConsent: Boolean){
        // for test purpose
        val debugSettings = ConsentDebugSettings.Builder(activity)
            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(deviceID)
            .build()

        params = ConsentRequestParameters
            .Builder()
            .setConsentDebugSettings(debugSettings)
            .build()
        if (resetAfterConsent) consentInformation.reset()
        }

    fun getConsent(callback: (consentGranted:Boolean) -> Unit){
        // You should request an update of the user's consent information at every app launch,
        // using requestConsentInfoUpdate(). This determines whether your user needs to provide consent
        // if they haven't done so already, or if their consent has expired.
        consentInformation.requestConsentInfoUpdate(activity,params, {
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) {
                //loadAndShowError callback called immediately if consent is not required and if consent status
                // is required the SDK loads a form and then The callback is called after the form is dismissed
                    loadAndShowError -> Log.w("Consent gathering fail", String.format("%s: %s", loadAndShowError?.errorCode, loadAndShowError?.message))
                // Consent has been gathered.
                if (consentInformation.canRequestAds()) {
                    consentGranted(callback)
                }
            }

        },
            {requestConsentError ->
                // error
                Log.w("fail", String.format("%s: %s", requestConsentError.errorCode, requestConsentError.message))
            })

        // It is possible consent has been obtained in the previous session. As a latency best practice,
        // we recommend not waiting for the callback to complete so you can start loading ads as soon as possible after your app launches.
        if (consentInformation.canRequestAds()) {
            consentGranted(callback)
        }

    }


    private fun consentGranted(callback: (consentGranted: Boolean) -> Unit) {
        if (consentGrantedCalled.getAndSet(true)) {
            Log.d("dsafgdfgdf", "badCall")
            return
        }
        Log.d("dsafgdfgdf", "goodCall")
        callback(true)
    }
}
