package com.skytek.edgelighting

import android.content.Context
import android.opengl.GLSurfaceView
import com.google.android.exoplayer2.ExoPlayer

internal abstract class GLWallpaperRenderer(protected val context: Context) : GLSurfaceView.Renderer {

    abstract fun setSourcePlayer(exoPlayer: ExoPlayer)
    abstract fun setScreenSize(width: Int, height: Int)
    abstract fun setVideoSizeAndRotation(width: Int, height: Int, rotation: Int)
    abstract fun setOffset(xOffset: Float, yOffset: Float)
    abstract fun setExoPlayer(exoPlayer: ExoPlayer)
}