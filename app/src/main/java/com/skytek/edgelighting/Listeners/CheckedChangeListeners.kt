package com.skytek.edgelighting.Listeners

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ScrollView
import androidx.appcompat.app.AlertDialog
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.firebase.fireEvent
import com.mobi.pixels.isOnline
import com.skytek.edgelighting.MyAccessibilityService
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.activity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.activities.MainActivity
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible

import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.Const
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.skytek.edgelighting.utils.adTimeTraker.isIntervalElapsed
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.skytek.edgelighting.utils.checkContext

class CheckedChangeListeners(private val context: Context) :
    CompoundButton.OnCheckedChangeListener {
    var alertDialog: AlertDialog? = null

    @SuppressLint("WrongConstant")
    override fun onCheckedChanged(compoundButton: CompoundButton?, b: Boolean) {
        when (compoundButton!!.id) {
            R.id.switch_notch -> {


                try {
                    if (b) {
                        Log.d("notchfix", "notch says ${b}")

                        loadAndShowInterstitialAd()
                    } else {
                        Log.d("notchfix", "notch says else part ${b}")
                        binding?.lnControlNotch?.visibility = 8
                        binding?.scrollView?.post(object : Runnable {
                            override fun run() {
                               if(binding!=null){
                                   binding!!.scrollView.fullScroll(ScrollView.FOCUS_UP)
                               }
                            }
                        })
                    }
                    MySharePreferencesEdge.putBoolean(
                        MySharePreferencesEdge.CHECKNOTCH,
                        b,
                        activity
                    )
                    EdgeOverlaySettingsActivity.checkNotch = b
                    if (MyAccessibilityService.edgeLightingView != null && MySharePreferencesEdge.getAccessibilityEnabled(
                            MySharePreferencesEdge.ACCESSIBILITY_BROADCAST,
                            activity
                        )
                    ) {
                        MyAccessibilityService.edgeLightingView!!.changeNotch(
                            EdgeOverlaySettingsActivity.checkNotch,
                            EdgeOverlaySettingsActivity.notchTop,
                            EdgeOverlaySettingsActivity.notchBottom,
                            EdgeOverlaySettingsActivity.notchHeight,
                            EdgeOverlaySettingsActivity.notchRadiusTop,
                            EdgeOverlaySettingsActivity.notchRadiusBottom
                        )
                    } else {
                        binding?.edgeLightView?.changeNotch(
                            EdgeOverlaySettingsActivity.checkNotch,
                            EdgeOverlaySettingsActivity.notchTop,
                            EdgeOverlaySettingsActivity.notchBottom,
                            EdgeOverlaySettingsActivity.notchHeight,
                            EdgeOverlaySettingsActivity.notchRadiusTop,
                            EdgeOverlaySettingsActivity.notchRadiusBottom
                        )
                    }
                    if (MySharePreferencesEdge.getBooleanValue(
                            MySharePreferencesEdge.ControlWindowManager,
                            activity
                        )
                    ) {
                        val intent = Intent(Const.Action_ChangeWindownManager)
                        intent.putExtra(Const.CONTROLWINDOW, Const.NOTCH)
                        intent.setPackage(activity!!.packageName)
                        activity!!.sendBroadcast(intent)
                    }
                } catch (e: NullPointerException) {
                }
            }

        }
    }

    private fun customdialog() {
        val builder = AlertDialog.Builder(context)
        val viewGroup = (context as? MainActivity)?.findViewById<ViewGroup>(android.R.id.content)
        val dialogView = LayoutInflater.from(context).inflate(R.layout.customview, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.show()
    }

    private fun loadAndShowInterstitialAd() {
        val binding = binding ?: return
        Log.d(
            "fvyfy7d6td6tdt64d",
            "${isOnline(context) && wholeScreenAdShow && wholeInterAdShow && isIntervalElapsed()}: "
        )
        if (isOnline(context) && wholeScreenAdShow && wholeInterAdShow && isIntervalElapsed()) {
            loadInterstitialAd(activitiesAdId)
        } else {

            EdgeOverlaySettingsActivity.binding?.let { safeBinding ->
                safeBinding.lnControlNotch.visibility = View.VISIBLE
                safeBinding.scrollView.post {
                    safeBinding.scrollView.fullScroll(
                        ScrollView.FOCUS_DOWN
                    )
                }
            }
        }
    }

    private fun loadInterstitialAd(id: String?) {

        if (checkContext(context)) {
            Interstitial.show(context as Activity, object : AdInterstitialShowListeners {
                override fun onDismissed() {
                    updateLastAdShownTime()
                    isinterstitialvisible = false
                    Log.d("asdasassss", "onAdDismissedFullScreenContent: ad dismissted ")
                    binding?.let { safeBinding ->
                        safeBinding.lnControlNotch.visibility = View.VISIBLE
                        safeBinding.scrollView.post {
                            safeBinding.scrollView.fullScroll(
                                ScrollView.FOCUS_DOWN
                            )
                        }
                    }
                }

                override fun onError(error: String) {
                    isinterstitialvisible = false
                    Interstitial.load(
                        context,
                        activitiesAdId,
                    )
                    binding?.lnControlNotch?.visibility = View.VISIBLE
                    binding?.scrollView?.post(object : Runnable {
                        override fun run() {
                            binding?.scrollView?.fullScroll(ScrollView.FOCUS_DOWN)
                        }
                    })


                }

                override fun onShowed() {
                    isinterstitialvisible = true

                    Interstitial.load(context, activitiesAdId)
                    fireEvent("SHOW_EL_notch_btn_click")

                }
            })
        }
    }


}

