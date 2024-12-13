package com.skytek.edgelighting.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class CircleImageView : AppCompatImageView {
    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attributeSet: AttributeSet?) : super(
        context!!, attributeSet
    ) {
    }

    constructor(context: Context?, attributeSet: AttributeSet?, i: Int) : super(
        context!!, attributeSet, i
    ) {
    }

    public override fun onMeasure(i: Int, i2: Int) {
        super.onMeasure(i, i2)
        setMeasuredDimension(i, i)
    }
}