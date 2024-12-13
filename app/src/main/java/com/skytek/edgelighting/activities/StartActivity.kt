package com.skytek.edgelighting.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.mobi.pixels.isOnline


import com.skytek.edgelighting.R

import com.skytek.edgelighting.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    lateinit var binding: ActivityStartBinding
    lateinit var handler:Handler
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
enableEdgeToEdge()
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(isOnline(applicationContext)){
            binding.splashLayout.alpha = 0.5F
            binding.relativeBack.visibility = View.VISIBLE
            binding.frame.visibility = View.VISIBLE
            handler = Handler(Looper.getMainLooper())

        }else{
//            startActivity(Intent(this@StartActivity,EdgeOverlaySettingsActivity::class.java))
//            finish()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this@StartActivity)) {
                startActivity(Intent(this@StartActivity,MainActivity::class.java))
                finish()
            }else{
                startActivity(Intent(this@StartActivity,RequestPermissionActivity::class.java))
                finish()
            }
        }

    }

//    fun stopHandler(){
//        handler.removeCallbacks(runnable)
//    }

//    private var runnable: Runnable = object : Runnable {
//        override fun run() {
//            if(mInterstitial !=null){
////                startActivity(Intent(this@StartActivity,EdgeOverlaySettingsActivity::class.java))
////                finish()
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this@StartActivity)) {
//                    startActivity(Intent(this@StartActivity,MainActivity::class.java))
//                    finish()
//                }
//                else
//                {
//                    startActivity(Intent(this@StartActivity,MainActivity::class.java))
//                    finish()
//                }
////                adsManagerJava!!.showInterstitialAd(this@StartActivity)
//                stopHandler()
//            } else{
//                handler.postDelayed(this, 1000)
//            }
//        }
//    }

}

