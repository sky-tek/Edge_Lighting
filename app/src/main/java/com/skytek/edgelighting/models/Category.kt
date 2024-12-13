package com.skytek.edgelighting.models

import com.google.gson.annotations.SerializedName

data class CategoryResponse(
    @SerializedName("data") val data: List<Category>
)


data class Wallpaper1(
    val id: String,
    val cat_order: String,
    val cat_name: String,
    val thumbPath: String
)

data class Category(
    val category: String,
    val viewType: String,
    val wallpapers: List<Wallpaper1>
)

data class ApiResponse(
    val response: List<Category>
)
