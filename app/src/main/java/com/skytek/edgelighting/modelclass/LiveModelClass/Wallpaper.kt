package com.skytek.edgelighting.modelclass.LiveModelClass

import androidx.annotation.Keep

@Keep
data class Wallpaper(
    val Category: String,
//    val Downloads: String,
    val blurPath: String,
    val cat_id: String,
    val cat_name: String,
    val cat_order: String,
    val cat_show: String,
    val downloads: String,
    val id: String,
    val img_path: String,
    val img_url: String,
    val likes: String,
    val profile_picture: String,
    val source: String,
    val thumbPath: String,
    val username: String
)