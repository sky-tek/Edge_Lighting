package com.skytek.edgelighting.model2

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

//@Keep
//data class Wallpaper(
//    val Downloads: String,
//    val id: String,
//    val likes: String,
//    val thumbPath: String
//)

@Keep
class Wallpaper {

    @SerializedName("Downloads")
    val Downloads: String? = null

    @SerializedName("id")
    val id: String? = null

    @SerializedName("likes")
    val likes: String? = null

    @SerializedName("thumbPath")
    val thumbPath: String? = null

}

@Keep
@Serializable
data class StaticWallCat(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("cat_id")
    val cat_id: String = "",
    @SerializedName("cat_name")
    val cat_name: String = "",
    @SerializedName("thumbPath")
    val thumbPath: String = "",
    @SerializedName("blurPath")
    val blurPath: String = "",
    @SerializedName("img_path")
    val img_path: String = "",
    @SerializedName("likes")
    val likes: String = "",
    @SerializedName("downloads")
    val downloads: String = ""
)