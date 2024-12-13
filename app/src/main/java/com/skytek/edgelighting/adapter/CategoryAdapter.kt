package com.skytek.edgelighting.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.bumptech.glide.Glide
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity
import com.skytek.edgelighting.activities.EdgeOverlaySettingsActivity.Companion.context
import com.skytek.edgelighting.activities.StaticCategoryWallpapersActivity
import com.skytek.edgelighting.activities.WallpaperPreview
import com.skytek.edgelighting.modelclass.Response
import com.skytek.edgelighting.models.Wallpapers


class CategoryAdapter(private var categories: Response, private val clickListener: CategoryAdapter.CategoryItemClickListener1 // Add interface as a parameter
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var id: String? = null
    private var Response1: Response? = null
    lateinit var context: Context
    private var intentFired = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        context = parent.context
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {

        return  categories.response[0].wallpapers.size
    }

    fun onItemClicked1(wallpaperPath: String) {
        // Your implementation here
    }
    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories.response[0]
        holder.categoryName.text = category.wallpapers[position].cat_name
        holder.categoryImage.load(category.wallpapers[position].thumbPath) {


        }


        // Handle clicks for any category ID
        holder.itemView.setOnClickListener {
            if (!intentFired) { // Check if intent is not fired already
                intentFired = true // Set flag to true
                val categoryId = category.wallpapers[position].id  // Get the category ID of the clicked item
                val categoryName=category.wallpapers[position].cat_name
                val intent = Intent(context, StaticCategoryWallpapersActivity::class.java)
                intent.putExtra("categoryId", categoryId)
                intent.putExtra("categoryName", categoryName)
                context?.startActivity(intent)
                intentFired =false

            }
        }










    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)


    }

    interface CategoryItemClickListener1 {
        fun onItemClicked1(wallpaperPath: String)
    }

}
