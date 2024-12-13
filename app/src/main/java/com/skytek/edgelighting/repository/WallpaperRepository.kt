package com.skytek.edgelighting.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skytek.edgelighting.api.WallpaperWebService
import com.skytek.edgelighting.model2.MagicalWallpapers
import com.skytek.edgelighting.models.Wallpapers

class WallpaperRepository() {

//    private val mutableLiveData = MutableLiveData<TypedArray>()
//    var localWallpapers:TypedArray? = null
//
//    fun getMutableLiveData(): MutableLiveData<TypedArray> {
//        val res = EdgeOverlaySettingsActivity.context!!.resources
//        localWallpapers = res.obtainTypedArray(R.array.wallpapers)
//        mutableLiveData.value = localWallpapers
//        return mutableLiveData
//    }

    constructor(wallpaperWebService: WallpaperWebService) : this() {
        this.wallpaperWebService = wallpaperWebService
    }

    private lateinit var wallpaperWebService: WallpaperWebService
    private val wallpaperLiveData = MutableLiveData<Wallpapers>()
    val wallpaper : LiveData<Wallpapers>
    get() = wallpaperLiveData


    private val magicalWallpaperLiveData = MutableLiveData<MagicalWallpapers>()
    val magicalWallpaper : LiveData<MagicalWallpapers>
        get() = magicalWallpaperLiveData

//    suspend fun getWallpapers(){
//        val result = wallpaperWebService.getWallpapers()
//        if(result!!.body()!= null){
//            wallpaperLiveData.postValue(result.body())
//        }
//    }

//    suspend fun getMagicalWallpapers(){
//        val result = wallpaperWebService.getMagicalWallpapers()
//        if(result!!.body()!= null){
//            magicalWallpaperLiveData.postValue(result.body())
//        }
//    }

}

