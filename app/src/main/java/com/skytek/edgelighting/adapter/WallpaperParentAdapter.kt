package com.skytek.edgelighting.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.skytek.edgelighting.fragment.OnItemClickListner
import com.skytek.edgelighting.fragment.OnItemClickListner2
import com.skytek.edgelighting.models.WallpaperParentItem

class WallpaperParentAdapter() : RecyclerView.Adapter<WallpaperParentAdapter.WallpaperParentViewHolder>() {

    private val viewPool = RecycledViewPool()
    lateinit var wallpaperParentItemList: List<WallpaperParentItem>
    lateinit var context: Context
    lateinit var tag:String
    private var onItemClickListner: OnItemClickListner? = null
    private var onItemClickListner2: OnItemClickListner2? = null
    lateinit var childItemAdapter:WallpaperChildAdapter

    constructor(context: Context,wallpaperParentItemList: List<WallpaperParentItem>,onItemClickListner: OnItemClickListner,tag:String) : this(){
        this.context = context
        this.wallpaperParentItemList = wallpaperParentItemList
        this.onItemClickListner = onItemClickListner
        this.tag = tag
    }

    constructor(context: Context,wallpaperParentItemList: List<WallpaperParentItem>,onItemClickListner2: OnItemClickListner2,tag:String) : this(){
        this.context = context
        this.wallpaperParentItemList = wallpaperParentItemList
        this.onItemClickListner2 = onItemClickListner2
        this.tag = tag
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): WallpaperParentViewHolder {
        val view: View = LayoutInflater.from(viewGroup.context).inflate(com.skytek.edgelighting.R.layout.wallpaper_parent_item_layout, viewGroup, false)
        return WallpaperParentViewHolder(view)
    }

    private lateinit var productImageList: RecyclerView

    override fun onBindViewHolder(parentViewHolder: WallpaperParentViewHolder, position: Int) {

        val parentItem = wallpaperParentItemList[position]
        parentViewHolder.ParentItemTitle.setText(parentItem.getWallpaperParentItem_Title())

        val layoutManager = LinearLayoutManager(parentViewHolder.ChildRecyclerView.context, LinearLayoutManager.HORIZONTAL, false)
        layoutManager.initialPrefetchItemCount = parentItem.getWallpaperChildItem_List().size
        if(tag.equals("wallpaper")){
            childItemAdapter = WallpaperChildAdapter(com.skytek.edgelighting.R.layout.wallpaper_child_item_layout,context,parentItem.getWallpaperChildItem_List(), onItemClickListner!!,tag)
        }else if(tag.equals("magical_wallpaper")){
            childItemAdapter = WallpaperChildAdapter(com.skytek.edgelighting.R.layout.wallpaper_child_item_layout,context,parentItem.getWallpaperChildItem_List(), onItemClickListner2!!,tag)
        }
        parentViewHolder.ChildRecyclerView.layoutManager = layoutManager
        parentViewHolder.ChildRecyclerView.adapter = childItemAdapter
        parentViewHolder.ChildRecyclerView.setRecycledViewPool(viewPool)

        parentViewHolder.arrow.setOnClickListener {
//            if(parentViewHolder.ChildRecyclerView.canScrollHorizontally(1)){
//                Log.d("vvbcc", "onBindViewHolder: ")
//            }
//            else{
//                parentViewHolder.backarrow.visibility = View.VISIBLE
//                Log.d("vvbcc", "onBindViewHolder: else")
//            }
            parentViewHolder.ChildRecyclerView.smoothScrollToPosition(layoutManager.findLastVisibleItemPosition() + 4)
        }
    }

    override fun getItemCount(): Int {
        return wallpaperParentItemList.size
    }

    inner class WallpaperParentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ParentItemTitle: TextView
        val ChildRecyclerView: RecyclerView
        val arrow:ImageView
        val backarrow:ImageView

        init {
            ParentItemTitle = itemView.findViewById(com.skytek.edgelighting.R.id.parent_item_title)
            ChildRecyclerView = itemView.findViewById(com.skytek.edgelighting.R.id.child_recyclerview)
            arrow = itemView.findViewById(com.skytek.edgelighting.R.id.arrow)
            backarrow = itemView.findViewById(com.skytek.edgelighting.R.id.backarrow)
        }
    }

}

