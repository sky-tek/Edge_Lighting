package com.mobi.pixels

import android.app.Activity
import com.google.android.play.core.review.ReviewManagerFactory

fun Activity.inAppReview(){
    val manager = ReviewManagerFactory.create(this)
    val request = manager.requestReviewFlow()
    request.addOnCompleteListener { task ->
        if (task.isSuccessful) {
            val flow = manager.launchReviewFlow(this, task.result)
            flow.addOnCompleteListener {
            }
        }
    }
}