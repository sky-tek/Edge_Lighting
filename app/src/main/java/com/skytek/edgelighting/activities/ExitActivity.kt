package com.skytek.edgelighting.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewManagerFactory


import com.skytek.edgelighting.databinding.ActivityExitBinding

class ExitActivity : AppCompatActivity() {

    var binding: ActivityExitBinding? = null
//    private val onBackPressedCallback = object : OnBackPressedCallback(enabled = true) {
//        override fun handleOnBackPressed() {
//            Log.d("nvksdbsififebfiu", "handleOnBackPressed: ")
//            startActivity(Intent(this@ExitActivity, MainActivity::class.java))
//            finish()
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityExitBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


//        OnBackPressedDispatcher().addCallback(this, onBackPressedCallback)
        // Use the onBackPressedDispatcher that is tied to the activity

        shareBtnClicked = true

        binding!!.yes.setOnClickListener {

            finishAffinity()
        }

        binding!!.no.setOnClickListener {
//            super.onBackPressed()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding!!.back.setOnClickListener {
//            super.onBackPressed()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


    }

    private var shareBtnClicked = false

    override fun onResume() {
        super.onResume()
        if (shareBtnClicked) {
            shareBtnClicked = false
            val manager = ReviewManagerFactory.create(this@ExitActivity)
            val request = manager.requestReviewFlow()
            request.addOnSuccessListener { reviewInfo ->
                if (reviewInfo != null) {
                    val flow = manager.launchReviewFlow(this@ExitActivity, reviewInfo)
                } else {
                    Log.e("reviewJBNIBBKBB", "Manager or reviewInfo is null.")
                }
            }
            request.addOnFailureListener { e ->
                Log.e("reviewJBNIBBKBB", e.message.toString())
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }


}

