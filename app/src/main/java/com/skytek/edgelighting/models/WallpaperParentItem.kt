package com.skytek.edgelighting.models

class WallpaperParentItem(var wallpaperParentItemTitle: String, WallpaperChildItemList: List<WallpaperChildItem>) {

    var WallpaperChildItemList: List<WallpaperChildItem>

    fun getWallpaperChildItem_List(): List<WallpaperChildItem> {
        return WallpaperChildItemList
    }

    fun getWallpaperParentItem_Title(): String? {
        return wallpaperParentItemTitle
    }

    fun setWallpaperParentItem_Title(wallpaperParentItemTitle: String) {
        this.wallpaperParentItemTitle = wallpaperParentItemTitle
    }

    fun setWallpaperChildItem_List(wallpaperChildItemList: List<WallpaperChildItem>) {
        WallpaperChildItemList = wallpaperChildItemList
    }

    init {
        this.WallpaperChildItemList = WallpaperChildItemList
    }
}
