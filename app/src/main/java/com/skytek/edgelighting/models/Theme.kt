package com.skytek.edgelighting.models

class Theme(

    var id: Int,
    var title: String,
    var speed: Int,
    var size: Int,
    var cornerTop: Int,
    var cornerBottom: Int,
    var color: IntArray,
    var shape: String,
    var checkBackground: Int,
    var colorBg: String,
    var linkBg: String,
    var notchTop: Int,
    var notchBottom: Int,
    var notchHeight: Int,
    var notchRadiusBottom: Int,
    var notchRadiusTop: Int,
    var isNotchCheck: Boolean
)