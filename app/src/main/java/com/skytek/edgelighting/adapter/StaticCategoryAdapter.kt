package com.skytek.edgelighting.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.StaticCategoryWallpaperPreviewActivity
import com.skytek.edgelighting.model2.StaticWallCat



class StaticCategoryAdapter(  private val categoryName: String, private var categories: List<StaticWallCat>, private val clickListener: CategoryItemClickListener)
    : RecyclerView.Adapter<StaticCategoryAdapter.CategoryViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_category1, parent, false)
        return CategoryViewHolder(view)
    }

    // ... existing code ...

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val categoryItem = categories[position] // Accessing the list

        holder.categoryImage.load(categoryItem.thumbPath) {
            // Use Coil or any other image loading library to load images
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, StaticCategoryWallpaperPreviewActivity::class.java)
            intent.putExtra("wallpaperPath", categoryItem.img_path)
            holder.itemView.context.startActivity(intent)
        }
    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage1)


    }
    fun getSpanCount(): Int {
        return 3 // Change this value to the desired span count
    }
    interface CategoryItemClickListener {
        fun onItemClicked(wallpaperPath: String)
    }
}