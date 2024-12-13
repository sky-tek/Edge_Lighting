package com.skytek.edgelighting.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import com.overlayhelper.OverlayHelper
import com.skytek.edgelighting.App
import com.skytek.edgelighting.databinding.ActivityRequestPermissionBinding
import com.skytek.edgelighting.utils.MySharePreferencesEdge
import com.thecode.onboardingviewagerexamples.activities.OnboardingExample4Activity


class RequestPermissionActivity : AppCompatActivity() {

    var binding:ActivityRequestPermissionBinding? = null

    private var overlayHelper: OverlayHelper? = null

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
enableEdgeToEdge()
        binding = ActivityRequestPermissionBinding.inflate(layoutInflater)
        setContentView(binding!!.root)







    }
    fun onClick(view: View) {
        when (view.id) {
//            R.id.btn_example1 -> {
//                val intent =
//                    Intent(applicationContext, OnboardingExample1Activity::class.java)
//                startActivity(intent)
//            }
//
//            R.id.btn_example2 -> {
//                val intent =
//                    Intent(applicationContext, OnboardingExample2Activity::class.java)
//                startActivity(intent)
//            }
//            R.id.btn_example3 -> {
//                val intent =
//                    Intent(applicationContext, OnboardingExample3Activity::class.java)
//                startActivity(intent)
//            }
//            R.id.btn_example4 -> {
//
//            }
        }

    }


    override fun onResume() {
        super.onResume()
        Log.d("abcd","request")
        if(MySharePreferencesEdge.getAccessibilityEnabled(MySharePreferencesEdge.ACCESSIBILITY_BROADCAST , App.context))
        {
            startActivity(Intent(this@RequestPermissionActivity , SliderActivity::class.java))
        }
        else
        {
            val intent = Intent(applicationContext, OnboardingExample4Activity::class.java)
            startActivity(intent)
            finish()
        }

    }
}

