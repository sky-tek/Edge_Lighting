package com.skytek.edgelighting.modelclass

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Response(
    @SerialName("response")
    val response: List<Responses>
)


@Keep
@Serializable
data class Responses(
    @SerialName("category")
val category: String,
    @SerialName("viewType")
val viewType: String,
    @SerialName("wallpapers")
val wallpapers: List<Wallpaper>)

@Serializable
@Keep
data class Wallpaper(
    //val Downloads: String,
    @SerialName("blurPath")
    val blurPath: String,
    @SerialName("cat_id")
    val cat_id: String,
    @SerialName("cat_name")
    val cat_name: String,
    @SerialName("cat_order")
    val cat_order: String,
    @SerialName("category")
    val category: String,
    @SerialName("downloads")
    val downloads: String,
    @SerialName("id")
    val id: String,
    @SerialName("img_path")
    val img_path: String,
    @SerialName("likes")
    val likes: String,
    @SerialName("source")
    val source: String,
    @SerialName("tags")
    val tags: String,
    @SerialName("thumbPath")
    val thumbPath: String

)