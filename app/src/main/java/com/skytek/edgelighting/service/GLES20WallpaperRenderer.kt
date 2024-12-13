package com.skytek.live.wallpapers.LiveWallpaperService

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext
import android.opengl.GLES20
import android.opengl.Matrix
import android.util.Log
import android.view.Surface
import com.google.android.exoplayer2.SimpleExoPlayer
import com.skytek.edgelighting.R

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class  GLES20WallpaperRenderer(private val context: Context):Renderer(){

    private val BYTES_PER_FLOAT = 4
    private val BYTES_PER_INT = 4
    private var vertices: FloatBuffer? = null
    private var texCoords: FloatBuffer? = null
    private var indices: IntBuffer? = null
    private var textures: IntArray? = null
    private var buffers: IntArray? = null
    private var mvp: FloatArray? = null
    private var program = 0
    private var mvpLocation = 0
    private var positionLocation = 0
    private var texCoordLocation = 0
    private var surfaceTexture: SurfaceTexture? = null
    private var screenWidth = 0
    private var screenHeight = 0
    private var videoWidth = 0
    private var videoHeight = 0
    private var videoRotation = 0
    private var xOffset = 0f
    private var yOffset = 0f
    private var maxXOffset = 0f
    private var maxYOffset = 0f

    // Fix bug like https://stackoverflow.com/questions/14185661/surfacetexture-onframeavailablelistener-stops-being-called
    private var updatedFrame: Long = 0
    private var renderedFrame: Long = 0

    init {
        // Those replaced glGenBuffers() and glBufferData().
        val vertexArray = floatArrayOf( // x, y
            // bottom left
            -1.0f, -1.0f,  // top left
            -1.0f, 1.0f,  // bottom right
            1.0f, -1.0f,  // top right
            1.0f, 1.0f
        )
        vertices = ByteBuffer.allocateDirect(
            vertexArray.size * BYTES_PER_FLOAT
        ).order(ByteOrder.nativeOrder()).asFloatBuffer()
        vertices?.put(vertexArray)?.position(0)
        val texCoordArray = floatArrayOf( // u, v
            // bottom left
            0.0f, 1.0f,  // top left
            0.0f, 0.0f,  // bottom right
            1.0f, 1.0f,  // top right
            1.0f, 0.0f
        )
        texCoords = ByteBuffer.allocateDirect(
            texCoordArray.size * BYTES_PER_FLOAT
        ).order(ByteOrder.nativeOrder()).asFloatBuffer()
        texCoords?.put(texCoordArray)?.position(0)
        val indexArray = intArrayOf(
            0, 1, 2,
            3, 2, 1
        )
        indices = ByteBuffer.allocateDirect(
            indexArray.size * BYTES_PER_INT
        ).order(ByteOrder.nativeOrder()).asIntBuffer()
        indices?.put(indexArray)?.position(0)
        buffers = IntArray(3)
        textures = IntArray(1)
        mvp = floatArrayOf(
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        )
    }

    override fun onSurfaceCreated(gl10: GL10?, eglConfig: EGLConfig?) {
        surfaceTexture = SurfaceTexture(10)
        // No depth test for 2D video.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST)
        GLES20.glDepthMask(false)
        GLES20.glDisable(GLES20.GL_CULL_FACE)
        GLES20.glDisable(GLES20.GL_BLEND)
        GLES20.glGenTextures(textures!!.size, textures, 0)
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures!![0])
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MIN_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_MAG_FILTER,
            GLES20.GL_LINEAR
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_S,
            GLES20.GL_CLAMP_TO_EDGE
        )
        GLES20.glTexParameteri(
            GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
            GLES20.GL_TEXTURE_WRAP_T,
            GLES20.GL_CLAMP_TO_EDGE
        )
        program = linkProgramGLES20(compileShaderResourceGLES20(context, GLES20.GL_VERTEX_SHADER, R.raw.vertex_20), compileShaderResourceGLES20(
            context, GLES20.GL_FRAGMENT_SHADER, R.raw.fragment_20
            )
        )
        mvpLocation = GLES20.glGetUniformLocation(program, "mvp")
        // Locations are NOT set in shader sources.
        positionLocation = GLES20.glGetAttribLocation(program, "in_position")
        texCoordLocation = GLES20.glGetAttribLocation(program, "in_tex_coord")
        GLES20.glGenBuffers(buffers!!.size, buffers, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers!![0])
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER, vertices!!.capacity() * BYTES_PER_FLOAT,
            vertices, GLES20.GL_STATIC_DRAW
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers!![1])
        GLES20.glBufferData(
            GLES20.GL_ARRAY_BUFFER,
            texCoords!!.capacity() * BYTES_PER_FLOAT,
            texCoords,
            GLES20.GL_STATIC_DRAW
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers!![2])
        GLES20.glBufferData(
            GLES20.GL_ELEMENT_ARRAY_BUFFER,
            indices!!.capacity() * BYTES_PER_INT,
            indices,
            GLES20.GL_STATIC_DRAW
        )
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
    }

    override fun onSurfaceChanged(gl10: GL10?, width: Int, height: Int) {
        surfaceTexture = SurfaceTexture(10)
        GLES20.glViewport(0, 0, width, height)
    }

    override fun onDrawFrame(gl10: GL10?) {
        try {
            if (surfaceTexture == null) {
                return
            }
            if (renderedFrame < updatedFrame) {
                surfaceTexture?.updateTexImage()
                ++renderedFrame
                // Utils.debug(
                //     TAG, "renderedFrame: " + renderedFrame + " updatedFrame: " + updatedFrame
                // );
            }
        } catch (e: NullPointerException) {
            //
            Log.e("exception", "Exception is " + e.message)
        }
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        GLES20.glUseProgram(program)
        GLES20.glUniformMatrix4fv(mvpLocation, 1, false, mvp, 0)
        // No vertex array in OpenGL ES 2.
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers!![0])
        GLES20.glEnableVertexAttribArray(positionLocation)
        GLES20.glVertexAttribPointer(
            positionLocation,
            2,
            GLES20.GL_FLOAT,
            false,
            2 * BYTES_PER_FLOAT,
            0
        )
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, buffers!![1])
        GLES20.glEnableVertexAttribArray(texCoordLocation)
        GLES20.glVertexAttribPointer(
            texCoordLocation,
            2,
            GLES20.GL_FLOAT,
            false,
            2 * BYTES_PER_FLOAT,
            0
        )
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, buffers!![2])
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, 6, GLES20.GL_UNSIGNED_INT, 0)
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0)
        GLES20.glDisableVertexAttribArray(texCoordLocation)
        GLES20.glDisableVertexAttribArray(positionLocation)
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0)
        GLES20.glUseProgram(0)
    }

    override fun setSourcePlayer(exoPlayer: SimpleExoPlayer) {
        // Re-create SurfaceTexture when getting a new player.
        // Because maybe a new video is loaded.
        createSurfaceTexture()
        exoPlayer.setVideoSurface(Surface(surfaceTexture))
    }

    override fun setScreenSize(width: Int, height: Int) {
        if (screenWidth != width || screenHeight != height) {
            screenWidth = width
            screenHeight = height

            maxXOffset =
                (1.0f - screenWidth.toFloat() / screenHeight / (videoWidth.toFloat() / videoHeight)) / 2
            maxYOffset =
                (1.0f - screenHeight.toFloat() / screenWidth / (videoHeight.toFloat() / videoWidth)) / 2
            updateMatrix()
        }
    }

    override fun setVideoSizeAndRotation(width: Int, height: Int, rotation: Int) {
        // MediaMetadataRetriever always give us raw width and height and won't rotate them.
        // So we rotate them by ourselves.
        var width = width
        var height = height
        if (rotation % 180 != 0) {
            val swap = width
            width = height
            height = swap
        }
        if (videoWidth != width || videoHeight != height || videoRotation != rotation) {
            videoWidth = width
            videoHeight = height
            videoRotation = rotation
            maxXOffset =
                (1.0f - screenWidth.toFloat() / screenHeight / (videoWidth.toFloat() / videoHeight)) / 2
            maxYOffset =
                (1.0f - screenHeight.toFloat() / screenWidth / (videoHeight.toFloat() / videoWidth)) / 2
            updateMatrix()
        }
    }

    override fun setOffset(xOffset: Float, yOffset: Float) {
        var xOffset = xOffset
        var yOffset = yOffset
        if (xOffset > maxXOffset) {
            xOffset = maxXOffset
        }
        if (xOffset < -maxXOffset) {
            xOffset = -maxXOffset
        }
        if (yOffset > maxYOffset) {
            yOffset = maxYOffset
        }
        if (yOffset < -maxXOffset) {
            yOffset = -maxYOffset
        }
        if (this.xOffset != xOffset || this.yOffset != yOffset) {
            this.xOffset = xOffset
            this.yOffset = yOffset
            updateMatrix()
        }
    }

    private fun createSurfaceTexture() {
        if (surfaceTexture != null) {
            surfaceTexture!!.release()
            surfaceTexture = null
        }
        updatedFrame = 0
        renderedFrame = 0
        surfaceTexture = SurfaceTexture(textures!![0])
        surfaceTexture!!.setDefaultBufferSize(videoWidth, videoHeight)
        surfaceTexture!!.setOnFrameAvailableListener { ++updatedFrame }
    }

    private fun updateMatrix() {
        // Players are buggy and unclear, so we do crop by ourselves.
        // Start with an identify matrix.
        for (i in 0..15) {
            mvp!![i] = 0.0f
        }
        mvp!![15] = 1.0f
        mvp!![10] = mvp!![15]
        mvp!![5] = mvp!![10]
        mvp!![0] = mvp!![5]
        // OpenGL model matrix: scaling, rotating, translating.
        val videoRatio = videoWidth.toFloat() / videoHeight
        val screenRatio = screenWidth.toFloat() / screenHeight
        if (videoRatio >= screenRatio) {
            // Treat video and screen width as 1, and compare width to scale.
            Matrix.scaleM(
                mvp, 0,
                videoWidth.toFloat() / videoHeight / (screenWidth.toFloat() / screenHeight),
                1f, 1f
            )
            // Some video recorder save video frames in direction differs from recoring,
            // and add a rotation metadata. Need to detect and rotate them.
            if (videoRotation % 360 != 0) {
                Matrix.rotateM(mvp, 0, -videoRotation.toFloat(), 0f, 0f, 1f)
            }
            Matrix.translateM(mvp, 0, xOffset, 0f, 0f)
        } else {
            // Treat video and screen height as 1, and compare height to scale.
            Matrix.scaleM(
                mvp, 0, 1f,
                videoHeight.toFloat() / videoWidth / (screenHeight.toFloat() / screenWidth), 1f
            )
            // Some video recorder save video frames in direction differs from recoring,
            // and add a rotation metadata. Need to detect and rotate them.
            if (videoRotation % 360 != 0) {
                Matrix.rotateM(mvp, 0, -videoRotation.toFloat(), 0f, 0f, 1f)
            }
            Matrix.translateM(mvp, 0, 0f, yOffset, 0f)
        }
        // This is a 2D center crop, so we only need model matrix, no view and projection.
    }
    fun linkProgramGLES20(vertShader: Int, fragShader: Int): Int {
        val program = GLES20.glCreateProgram()
        if (program == 0) {
            throw RuntimeException("Failed to create program")
        }
        GLES20.glAttachShader(program, vertShader)
        GLES20.glAttachShader(program, fragShader)
        GLES20.glLinkProgram(program)
        val status = IntArray(1)
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0)
        if (status[0] == 0) {
            val log = GLES20.glGetProgramInfoLog(program)
            GLES20.glDeleteProgram(program)
            throw RuntimeException(log)
        }
        return program
    }
    fun compileShaderResourceGLES20(context: Context, shaderType: Int, shaderRes: Int): Int {
        val inputStream = context.resources.openRawResource(shaderRes)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        try {
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
                stringBuilder.append('\n')
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return 0
        }
        val shaderSource = stringBuilder.toString()
        val shader = GLES20.glCreateShader(shaderType)
        if (shader == 0) {
            throw RuntimeException("Failed to create shader")
        }
        GLES20.glShaderSource(shader, shaderSource)
        GLES20.glCompileShader(shader)
        val status = IntArray(1)
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
        if (status[0] == 0) {
            val log = GLES20.glGetShaderInfoLog(shader)
            GLES20.glDeleteShader(shader)
            throw RuntimeException(log)
        }
        return shader
    }
}