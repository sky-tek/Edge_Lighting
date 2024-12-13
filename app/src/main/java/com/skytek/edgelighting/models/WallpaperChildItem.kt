package com.skytek.edgelighting.models

class WallpaperChildItem {

//    private var WallpaperChildItemTitle: String? = null
    private var WallpaperChildItemImageID: String? = null
    private var WallpaperChildItemImageThumbnail: String? = null
    private var WallpaperChildItemImageBackground: String? = null

    constructor(wallpaperChildItemImageID:String,wallpaperChildItemImageThumbnail:String,wallpaperChildItemImageBackground:String) : this(){
        this.WallpaperChildItemImageID = wallpaperChildItemImageID
        this.WallpaperChildItemImageThumbnail = wallpaperChildItemImageThumbnail
        this.WallpaperChildItemImageBackground = wallpaperChildItemImageBackground
    }

    constructor()

    fun getWallpaperChildItemImageID(): String? {
        return WallpaperChildItemImageID
    }
    fun getWallpaperChildItemImageThumbnail(): String? {
        return WallpaperChildItemImageThumbnail
    }
    fun getWallpaperChildItemImageBackground(): String? {
        return WallpaperChildItemImageBackground
    }

//    fun setWallpaperChildItemTitle(wallpaperChildItemTitle: String?) {
//       WallpaperChildItemTitle = wallpaperChildItemTitle
//    }
}
