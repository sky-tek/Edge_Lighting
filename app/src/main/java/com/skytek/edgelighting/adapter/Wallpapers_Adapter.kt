package com.skytek.edgelighting.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.binding
import com.skytek.edgelighting.databinding.ItemLayoutBinding
import com.skytek.edgelighting.models.Wallpaper
import com.skytek.edgelighting.models.Wallpapers

class Wallpapers_Adapter() : RecyclerView.Adapter<Wallpapers_Adapter.ViewHolder>() {

    private lateinit var context: Context
//    private var onItemClickListner: OnItemClickListner? = null
    private var wallpapers_details : List<Wallpaper> = listOf()
    private var tag: String? = null
    var firstItem_flag = 0

//    constructor(context: Context,onItemClickListner: OnItemClickListner) : this() {
//        this.context = context
//        this.onItemClickListner = onItemClickListner
//    }
//
//    constructor(context: Context,wallpapers_details : List<Wallpaper>,tag : String,onItemClickListner: OnItemClickListner) : this() {
//        this.context = context
//        this.wallpapers_details = wallpapers_details
//        this.tag = tag
//        this.onItemClickListner = onItemClickListner
//    }

    var selected_position = -1
    var img_selectedPosition = -1

    private var wallpapers: Wallpapers? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemLayoutBinding = ItemLayoutBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(itemLayoutBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {

        if (tag.equals("wallpapers")) {
            holder.itemLayoutBinding.wlCategory.visibility = View.GONE
            holder.itemLayoutBinding.imgPanel.visibility = View.VISIBLE
            Glide.with(context).load(wallpapers_details.get(position).Thumbnail).placeholder(R.drawable.image_loading).into(holder.itemLayoutBinding.img)
            if(img_selectedPosition == position){
                holder.itemLayoutBinding.tick.visibility = View.VISIBLE
            }else{
                holder.itemLayoutBinding.tick.visibility = View.GONE
            }
            holder.itemLayoutBinding.img.setOnClickListener(View.OnClickListener {
                if (holder.adapterPosition == RecyclerView.NO_POSITION) return@OnClickListener
//                onItemClickListner!!.onAdapterItemClick(wallpapers_details.get(position).Background)
                img_selectedPosition = holder.adapterPosition
                notifyDataSetChanged()
            })
        }else{
            var categoryTitle = wallpapers!!.response!!.get(position).Category
            when(categoryTitle){
                "Abstract" -> {
                    holder.itemLayoutBinding.wlCategory.setText(R.string.abstract_)
                }
                "Nature" -> {
                    holder.itemLayoutBinding.wlCategory.setText(R.string.nature)
                }
                "Neon" -> {
                    holder.itemLayoutBinding.wlCategory.setText(R.string.neon)
                }
                "Quotes" -> {
                    holder.itemLayoutBinding.wlCategory.setText(R.string.quotes)
                }
                "Sunset" -> {
                    holder.itemLayoutBinding.wlCategory.setText(R.string.sunset)
                }
                "Top" -> {
                    holder.itemLayoutBinding.wlCategory.setText(R.string.top)
                }
            }
            holder.itemLayoutBinding.wlCategory.setOnClickListener {
                selected_position = position
                notifyDataSetChanged()
                binding!!.wallpaperList.visibility = View.VISIBLE
                val horizontal = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                var wlist = wallpapers!!.response.get(position).Wallpapers
//                wallpaperAdapter = Wallpapers_Adapter(context,wlist,"wallpapers",onItemClickListner!!)
                binding!!.apply {
                    wallpaperList.layoutManager = horizontal
                    wallpaperList.adapter = wallpaperAdapter
                }
            }
        }

        if(selected_position == position){
            holder.itemLayoutBinding.wlCategory.setSelected(true)
        }else{
            if (firstItem_flag == 0) {
                holder.itemLayoutBinding.wlCategory.setSelected(true)
                firstItem_flag = 1
            }else
                holder.itemLayoutBinding.wlCategory.setSelected(false)
        }
    }

    var wallpaperAdapter: Wallpapers_Adapter? = null

    override fun getItemCount(): Int {
        return if (wallpapers != null) {
            wallpapers!!.response!!.size
        }else if(tag.equals("wallpapers")){
            wallpapers_details.size
        } else {
            0
        }
    }

    fun setWallpaper(wallpapers: Wallpapers) {
        this.wallpapers = wallpapers
        notifyDataSetChanged()
    }

    inner class ViewHolder(var itemLayoutBinding: ItemLayoutBinding) : RecyclerView.ViewHolder(itemLayoutBinding.root)

}

