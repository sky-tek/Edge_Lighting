package com.skytek.live.wallpapers.LiveWallpaperService

import android.content.Context
import android.graphics.SurfaceTexture
import android.media.MediaMetadataRetriever
import android.opengl.GLSurfaceView
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Size
import android.view.View
import android.view.WindowManager
import com.google.android.exoplayer2.SimpleExoPlayer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.util.Locale
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.coroutines.coroutineContext

abstract class Renderer : GLSurfaceView.Renderer {
//    private val quadVertices: FloatBuffer = floatArrayOf(
//        -1.0f, -1.0f, 0.0f, // bottom left
//        -1.0f, +1.0f, 0.0f, // top left
//        +1.0f, -1.0f, 0.0f, // bottom right
//        +1.0f, +1.0f, 0.0f  // top right
//    ).let { coords ->
//        ByteBuffer.allocateDirect(coords.size * 4)
//            .order(ByteOrder.nativeOrder())
//            .asFloatBuffer()
//            .put(coords)
//    }
//
//    private val quadTexCoords: FloatBuffer = floatArrayOf(
//        0.0f, 1.0f,
//        0.0f, 0.0f,
//        1.0f, 1.0f,
//        1.0f, 0.0f).let { coords ->
//        ByteBuffer.allocateDirect(coords.size * 4)
//            .order(ByteOrder.nativeOrder())
//            .asFloatBuffer()
//            .put(coords)
//    }

//    fun getScreenWidth(context: Context): Int {
//        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        return displayMetrics.widthPixels
//    }
//
//    fun getScreenHeight(context: Context): Int {
//        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val displayMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(displayMetrics)
//        return displayMetrics.heightPixels
//    }
//    fun getVideoSize(videoPath:String) : Size {
//        try {
//            val media = MediaMetadataRetriever()
//            media.setDataSource(videoPath)
//            val rotation = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)?:"-1").toInt()
//            var videoW:Int
//            var videoH:Int
//            if (rotation == 90) {
//
//                videoW = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "-1").toInt()
//                videoH = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "-1").toInt()
//            } else {
//                videoW = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH) ?: "-1").toInt()
//                videoH = (media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT) ?: "-1").toInt()
//            }
//            media.release()
//            return Size(videoW, videoH)
//        } catch (e:RuntimeException) {
//            return Size(1, 1)
//        }
//
//
//    }
//    private var videoWidth = 100
//    private var videoHeight = 100
//    private var surfaceWidth = 0
//    private var surfaceHeight = 0
//    private var surfaceTexture: SurfaceTexture? = null
//    private var isDirty = false
//    private var isAllocated = false
//    var mode: LiveService.Mode = mode
//        set(value) {
//            field = value
//            updateVertices()
//        }
    abstract fun setSourcePlayer(exoPlayer: SimpleExoPlayer)
    abstract fun setScreenSize(width: Int, height: Int)
    abstract fun setVideoSizeAndRotation(width: Int, height: Int, rotation: Int)
    abstract fun setOffset(xOffset: Float, yOffset: Float)
//    private fun updateVertices(context : Context) {
//
//        if (surfaceWidth == 0 || getScreenHeight(context) == 0) {
//            return
//        }
//
//        var videoDetails = dataPath?.let { getVideoSize(it) }
//
//
//        val isHorizontal = videoDetails!!.width < surfaceWidth
//        val values = when (mode) {
//            LiveService.Mode.FIT_START -> {
//                if (isHorizontal) {
//                    val width = videoDetails?.width!! * 2f / surfaceWidth
//                    if (isLtr) {
//                        // Align left
//                        val right = -1 + width
//                        floatArrayOf(
//                            -1f, -1f, 0f,
//                            -1f, +1f, 0f,
//                            right, -1f, 0f,
//                            right, +1f, 0f)
//                    } else {
//                        // Align right
//                        val left = 1 - width
//                        floatArrayOf(
//                            left, -1f, 0f,
//                            left, +1f, 0f,
//                            1f, -1f, 0f,
//                            1f, +1f, 0f)
//                    }
//                } else {
//                    // Align top
//                    val bottom = 1 - (videoDetails!!.height * 2f / surfaceHeight)
//                    floatArrayOf(
//                        -1f, bottom, 0f,
//                        -1f, 1f, 0f,
//                        +1f, bottom, 0f,
//                        +1f, 1f, 0f)
//                }
//            }
//            LiveService.Mode.FIT_CENTER -> {
//                if (isHorizontal) {
//                    // center horizontally
//                    val width = videoDetails!!.width / surfaceWidth.toFloat()
//                    floatArrayOf(
//                        -width, -1f, 0f,
//                        -width, +1f, 0f,
//                        +width, -1f, 0f,
//                        +width, +1f, 0f)
//                } else {
//                    // center vertically
//                    val height = videoDetails!!.height / surfaceHeight.toFloat()
//                    floatArrayOf(
//                        -1f, -height, 0f,
//                        -1f, +height, 0f,
//                        +1f, -height, 0f,
//                        +1f, +height, 0f)
//                }
//            }
//            LiveService.Mode.FIT_END -> {
//                if (isHorizontal) {
//                    val width = videoDetails!!.width * 2f / surfaceWidth
//                    if (!isLtr) {
//                        // Align left
//                        val right = -1 + width
//                        floatArrayOf(
//                            -1f, -1f, 0f,
//                            -1f, +1f, 0f,
//                            right, -1f, 0f,
//                            right, +1f, 0f)
//                    } else {
//                        // Align right
//                        val left = 1 - width
//                        floatArrayOf(
//                            left, -1f, 0f,
//                            left, +1f, 0f,
//                            1f, -1f, 0f,
//                            1f, +1f, 0f)
//                    }
//                } else {
//                    // Align bottom
//                    val top = -1f + (videoDetails!!.height * 2f / surfaceHeight)
//                    floatArrayOf(
//                        -1f, -1f, 0f,
//                        -1f, top, 0f,
//                        +1f, -1f, 0f,
//                        +1f, top, 0f)
//                }
//            }
//            LiveService.Mode.FIT_XY -> floatArrayOf(
//                -1f, -1f, 0f,
//                -1f, +1f, 0f,
//                +1f, -1f, 0f,
//                +1f, +1f, 0f)
//            LiveService.Mode.CENTER_CROP -> {
//                val maxScale = Math.max(surfaceWidth / videoDetails!!.width.toFloat(), surfaceHeight / videoDetails!!.height.toFloat())
//                val width = videoDetails!!.width * maxScale / surfaceWidth
//                val height = videoDetails!!.height * maxScale / surfaceHeight
//                floatArrayOf(
//                    -width, -height, 0f,
//                    -width, +height, 0f,
//                    +width, -height, 0f,
//                    +width, +height, 0f)
//            }
//
//            else -> {""}
//        }
//
//        quadVertices.position(0)
//        quadVertices.put(values)
//        quadVertices.position(0)
//    }
//    private val isLtr: Boolean
//        get() = TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) ==
//                View.LAYOUT_DIRECTION_LTR
}
