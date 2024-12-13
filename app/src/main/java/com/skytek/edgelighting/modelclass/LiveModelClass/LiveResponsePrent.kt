package com.skytek.edgelighting.modelclass.LiveModelClass
import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
@Keep
data class LiveResponsePrent(
    @SerialName("response")
    val response: ArrayList<LiveResponse>
)
@Serializable
@Keep
data class LiveResponse(
    @SerialName("category")
    val category: String,
    @SerialName("viewType")
    val viewType: String,
    @SerialName("wallpapers")
    val wallpapers: List<LiveWallpaper>
)
@Serializable
@Keep
data class LiveWallpaper(
    @SerialName("Category")
    val Category: String,
//    val Downloads: String,
    @SerialName("blurPath")
    val blurPath: String,
    @SerialName("cat_id")
    val cat_id: String,
    @SerialName("cat_name")
    val cat_name: String,
    @SerialName("cat_order")
    val cat_order: String,
    @SerialName("cat_show")
    val cat_show: String,
    @SerialName("downloads")
    val downloads: String,
    @SerialName("id")
    val id: String,
    @SerialName("img_path")
    val img_path: String,
    @SerialName("img_url")
    val img_url: String,
    @SerialName("likes")
    val likes: String,
    @SerialName("profile_picture")
    val profile_picture: String,
    @SerialName("source")
    val source: String,
    @SerialName("thumbPath")
    val thumbPath: String,
    @SerialName("username")
    val username: String

)