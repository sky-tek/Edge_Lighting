package com.skytek.edgelighting.adapter


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.StaticCategoryWallpaperPreviewActivity
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.modelclass.Response
import com.skytek.edgelighting.modelclass.Responses
import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.adTimeTraker.isIntervalElapsed
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.skytek.edgelighting.utils.checkContext


class CategoryAdapter1(
    private var categories: Response,
    private val clickListener: CategoryItemClickListener, // Add interface as a parameter
    private var context: Context?
) : RecyclerView.Adapter<CategoryAdapter1.CategoryViewHolder>() {


    private val spanCount = 3 // Set span count to 3
    private val viewType4Items: List<Responses> = categories.response.filter { it.viewType == "4" }
    var alertDialog: AlertDialog? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {

        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("ajaygaaaaa", "size is=  ${categories.response.size}")
        return viewType4Items.size

    }

    fun customdialog() {
        val builder = AlertDialog.Builder(context!!)
        val viewGroup = (context!! as Activity).findViewById<ViewGroup>(android.R.id.content)
        val dialogView =
            LayoutInflater.from(context!!).inflate(R.layout.customview, viewGroup, false)
        builder.setView(dialogView)
        alertDialog = builder.create()
        alertDialog!!.setCancelable(false)
        alertDialog!!.show()
    }

    private fun loadInterstitialAd(id: String?, imgPath: String, intent: Intent) {
        Log.d("axcgcsgcuevb uvcu vedbu", "${context!!.javaClass.simpleName} ")
        if (context == null) return
        if (checkContext(context!!)) {
            Interstitial.show(context as Activity, object : AdInterstitialShowListeners {
                override fun onDismissed() {

                    isinterstitialvisible = false
                    intent.putExtra("wallpaperPath", imgPath)
                    context!!.startActivity(intent)
                    updateLastAdShownTime()
                }

                override fun onError() {

                    Interstitial.load(
                        context as Activity,
                        activitiesAdId,
                    )
                    if (alertDialog != null) {
                        try {
                            alertDialog!!.dismiss()

                            alertDialog = null
                        } catch (e: java.lang.Exception) {
                        }



                        intent.putExtra("wallpaperPath", imgPath)
                        context!!.startActivity(intent)
                    }

                }

                override fun onShowed() {
                    isinterstitialvisible = true
                    if (alertDialog != null) {
                        try {
                            alertDialog!!.dismiss()
                            alertDialog = null
                        } catch (e: java.lang.Exception) {
                        }
                    }

                    Interstitial.load(
                        context as Activity,
                        activitiesAdId,
                    )

                    fireEvent("SHOW_EL_static_Wall_click")

                }
            })
        }


    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = viewType4Items[position]
        val imgPath = category.wallpapers.firstOrNull()?.thumbPath

        holder.categoryImage.load(imgPath) {}

        holder.itemView.setOnClickListener {
            Log.d("axcgcsgcuevb uvcu vedbu", "${context!!.javaClass.simpleName} ")
            val i = Intent(context!!, StaticCategoryWallpaperPreviewActivity::class.java)
            Log.d("bjksdbfbvjvbjvbujv", "${category.wallpapers[0].img_path} ")
            if (isIntervalElapsed() && wholeScreenAdShow && wholeInterAdShow) {
                customdialog()

                loadInterstitialAd(activitiesAdId, category.wallpapers[0].img_path, i)
            } else {
                if (alertDialog != null) {
                    try {
                        alertDialog!!.dismiss()
                        alertDialog = null
                    } catch (e: java.lang.Exception) {
                    }
                }


                i.putExtra("wallpaperPath", imgPath)
                context!!.startActivity(i)
            }


        }



        Log.d("img", "onBindViewHolder first img path: " + category.wallpapers[0].img_path)


    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage1)


    }

    // Function to get the span count
    fun getSpanCount(): Int {
        return spanCount
    }


    interface CategoryItemClickListener {
        fun onItemClicked(wallpaperPath: String) // Pass any necessary data when an item is clicked
    }

}
