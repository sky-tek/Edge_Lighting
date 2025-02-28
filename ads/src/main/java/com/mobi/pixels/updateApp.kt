package com.mobi.pixels

import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.mobi.pixels.enums.UpdateType
import java.util.UUID
import kotlin.system.exitProcess

fun Activity.updateApp(updateType: UpdateType) {
    val appUpdateManager = AppUpdateManagerFactory.create(this)

    val type = if (updateType == UpdateType.Flexible) 0 else 1
    val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showCompleteUpdate()
        }
        if (state.installStatus() == InstallStatus.CANCELED) {
            if (type == AppUpdateType.IMMEDIATE) {
                finishAffinity();
                exitProcess(0); } } }

    appUpdateManager.appUpdateInfo.addOnSuccessListener { result ->
        if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
            try {
                if (type == AppUpdateType.FLEXIBLE) { appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, this, 100) }
                else if (type == AppUpdateType.IMMEDIATE) { appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, this, 200)}
            } catch (e: IntentSender.SendIntentException) { e.printStackTrace() } }
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

}

fun Activity.updateAppWithRemoteConfig(jsonString: String) {

    val value = fetchDataForCurrentVersion(jsonString)
    Log.d("34ffd", value.toString())
    if (value != "-1") {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val installStateUpdatedListener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                showCompleteUpdate()
            }
            if (state.installStatus() == InstallStatus.CANCELED) {
                if (value == "1") {
                    finishAffinity();
                    exitProcess(0);
                }
            }
        }

        appUpdateManager.appUpdateInfo.addOnSuccessListener { result ->
            if (result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                try {
                    if (value == "0") { appUpdateManager.startUpdateFlowForResult(result, AppUpdateType.FLEXIBLE, this, 100)
                    } else if (value == "1") { appUpdateManager?.startUpdateFlowForResult(result, AppUpdateType.IMMEDIATE, this, 200) }
                } catch (e: IntentSender.SendIntentException) { e.printStackTrace() }
            }
            appUpdateManager.registerListener(installStateUpdatedListener)
        }
    }

}

private fun Activity.fetchDataForCurrentVersion(jsonString: String): String? {
    if (jsonString.isEmpty()) return "-1"
    val gson = Gson()
    val versionListType = object : TypeToken<List<JsonObject>>() {}.type
    val versionList: List<JsonObject> = gson.fromJson(jsonString, versionListType)
    return try {
        val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, 0)
        val versionName = packageInfo.versionName
        // Find the matching version and return its UpdateType
        val data = versionList.find { it.get("VersionName")?.asString == versionName }
            ?.get("UpdateType")?.asString
        data ?: "-1"
    } catch (e: PackageManager.NameNotFoundException) {
        "-1"
    }
}

private fun Activity.showCompleteUpdate() {
    val snackBar = Snackbar.make(
        findViewById(android.R.id.content),
        "New update is ready!",
        Snackbar.LENGTH_INDEFINITE
    )
    snackBar.setAction("Install") {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager.completeUpdate()
    }
    snackBar.show()
}


