package com.skytek.edgelighting.adapter

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager.BadTokenException
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.LiveWallpaperActivity
import com.skytek.edgelighting.activities.LiveWallpaperActivity.Companion.loader
import com.skytek.edgelighting.fragment.OnItemClickListner
import com.skytek.edgelighting.fragment.OnItemClickListner2
import com.skytek.edgelighting.models.WallpaperChildItem

class WallpaperChildAdapter() : RecyclerView.Adapter<WallpaperChildAdapter.WallpaperChildViewHolder>() {

    lateinit var wallpaperChildItemList: List<WallpaperChildItem>
    lateinit var context:Context
    lateinit var tag:String
    private var onItemClickListner: OnItemClickListner? = null
    private var onItemClickListner2: OnItemClickListner2? = null

    lateinit var countDownTimer: CountDownTimer
    private var dialogslowinternet: Dialog? = null

    var layoutID:Int? = null

    constructor(layoutID:Int,context: Context,wallpaperChildItemList: List<WallpaperChildItem>,onItemClickListner: OnItemClickListner,tag:String) : this(){
        this.layoutID = layoutID
        this.context = context
        this.wallpaperChildItemList = wallpaperChildItemList
        this.onItemClickListner = onItemClickListner
        this.tag = tag
    }

    constructor(layoutID:Int,context: Context,wallpaperChildItemList: List<WallpaperChildItem>,onItemClickListner2: OnItemClickListner2,tag:String) : this(){
        this.layoutID = layoutID
        this.context = context
        this.wallpaperChildItemList = wallpaperChildItemList
        this.onItemClickListner2 = onItemClickListner2
        this.tag = tag
    }

    lateinit var view:View

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): WallpaperChildViewHolder {
//        var view:View? = null
        view = LayoutInflater.from(viewGroup.context).inflate(layoutID!!, viewGroup, false)
        return WallpaperChildViewHolder(view)
    }

    override fun onBindViewHolder(childViewHolder: WallpaperChildViewHolder, position: Int) {
        val childItem = wallpaperChildItemList[position]
        childViewHolder.progressBar.visibility = View.VISIBLE

        Glide.with(context).load(childItem.getWallpaperChildItemImageThumbnail()).listener(object : RequestListener<Drawable> {


            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                childViewHolder.progressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                childViewHolder.progressBar.visibility = View.GONE
                return false
            }
        }).into(childViewHolder.ChildItemWallpaper)

        childViewHolder.ChildItemWallpaper.setOnClickListener {
            if (childViewHolder.adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

//            countDownTimer.start()

            loader = LiveWallpaperActivity.LoadingDialog(context)
            loader!!.show()

            countDownTimer = object : CountDownTimer(30000,1000) {
                override fun onTick(millisUntilFinished: Long) {}

                override fun onFinish() {
                    try {
                        if (loader != null && loader!!.isShowing()) {
                            loader!!.dismiss()
                            loader = null
                        }
                        countDownTimer.cancel()
                        dialogslowinternet = Dialog(context)
                        dialogslowinternet!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
                        dialogslowinternet!!.setContentView(R.layout.netslowdialog)
                        dialogslowinternet!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                        dialogslowinternet!!.setCancelable(true)
                        if(!(context as Activity).isFinishing && LiveWallpaperActivity.pause == false)  // used if statement to avoid bad token exception
                            dialogslowinternet!!.show()
                    } catch (e: BadTokenException) {}
                }
            }.start()

            Glide.with(context)
                .asBitmap()
                .load(childItem.getWallpaperChildItemImageBackground())
                .into(object : CustomTarget<Bitmap>(){
                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {

                        if(loader != null){
                            countDownTimer.cancel()
                            idnew = childItem.getWallpaperChildItemImageID()
                            if(tag.equals("wallpaper")){
                                loader!!.dismiss()
                                onItemClickListner!!. onAdapterItemClick(bitmap!!)
                            }
                            else if(tag.equals("magical_wallpaper")){
                                val url = "https://mobipixels.net/3d-Live-wallpapers-api/Live_wall_single_wall.php?id=${childItem.getWallpaperChildItemImageID()}"
                                onItemClickListner2!!.onAdapterItemClick(url,bitmap)
                            }
                        }
                    }
                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

    override fun getItemCount(): Int {
        return wallpaperChildItemList.size
    }

    inner class WallpaperChildViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var ChildItemWallpaper: ImageView
        var progressBar:ProgressBar

        init {
            ChildItemWallpaper = itemView.findViewById(com.skytek.edgelighting.R.id.img)
            progressBar = itemView.findViewById(com.skytek.edgelighting.R.id.progressBar)
        }
    }

    companion object{
        var idnew:String? = null
    }

}

