package com.skytek.edgelighting.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skytek.edgelighting.model2.MagicalWallpapers
import com.skytek.edgelighting.models.Wallpapers
import com.skytek.edgelighting.repository.WallpaperRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WallpaperViewModel(private val wallpaperRepository: WallpaperRepository) : ViewModel() {

//    private val wallpaperRepository: WallpaperRepository
//    val allWallpapers: LiveData<TypedArray>
//        get() = wallpaperRepository!!.getMutableLiveData()

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }

//    init {
//        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//            wallpaperRepository.getWallpapers()
//        }
//
//        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
//            wallpaperRepository.getMagicalWallpapers()
//        }
//    }

    val wallpaper:LiveData<Wallpapers>
        get() = wallpaperRepository.wallpaper

    val magicalWallpaper:LiveData<MagicalWallpapers>
        get() = wallpaperRepository.magicalWallpaper
}

