package com.skytek.edgelighting.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skytek.edgelighting.modelclass.Response

class StaticWallpaperViewModel : ViewModel(){
    val wallpaperResponse = MutableLiveData<Response>()

}