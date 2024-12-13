package com.skytek.edgelighting.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding

class CustomEdgeImage : AppCompatImageView {
    private var h = 0
    private var w = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?) : super(
        context!!, attributeSet
    ) {
        init()
    }

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context!!, attributeSet, i
    ) {
        init()
    }

    private fun init() {
        w = context.resources.displayMetrics.widthPixels
        h = context.resources.displayMetrics.heightPixels
    }

}

