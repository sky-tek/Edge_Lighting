package com.skytek.edgelighting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.SliderData
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private val imageResources: List<SliderData>) :
    SliderViewAdapter<SliderAdapter.SliderViewHolder>() {

        lateinit var context: Context

    override fun getCount(): Int {
        return imageResources.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderViewHolder {
        context = parent!!.context
        val inflate: View = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.slider_item_layout, parent, false)
        return SliderViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder?, position: Int) {
        viewHolder?.imageView?.let {
            Glide.with(context)
                .load(imageResources[position].image)
                .fitCenter()
                .into(it)
        }
        viewHolder?.imageText!!.text = imageResources[position].title
        viewHolder?.imageText2!!.text = imageResources[position].description


    }

    class SliderViewHolder(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.myimage)
        val imageText: TextView = itemView.findViewById(R.id.imageText)
        val imageText2: TextView = itemView.findViewById(R.id.imageText2)





    }
}