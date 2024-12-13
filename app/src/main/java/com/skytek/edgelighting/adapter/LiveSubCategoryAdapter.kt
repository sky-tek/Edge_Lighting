package com.skytek.edgelighting.adapter

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.skytek.edgelighting.R
import com.skytek.edgelighting.model2.StaticWallCat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.chromium.base.ThreadUtils.runOnUiThread
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class LiveSubCategoryAdapter(
    private val categoryName: String,
    private var categories: List<StaticWallCat>,
    private val clickListener: CategoryItemClickListener,
    private var context: Context?
) : RecyclerView.Adapter<LiveSubCategoryAdapter.CategoryViewHolder>() {


    val MAX_RETRY_COUNT = 3
    var downloadThread: Thread? = null

    companion object {
        var isDownloadEnabled = true
        var isDownloadCompleted = false
        lateinit var stringpath: File
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("isIt", "getItemCount: ")
        return categories.size
    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {

        val categoryItem = categories[position] // Accessing the list
        holder.categoryImage.load(categoryItem.thumbPath) {
            // Use Coil or any other image loading library to load images
        }

        holder.itemView.setOnClickListener {
            Log.d("rrrrrrrrrrrrrrrrr", "onBindViewHolder:${context!!.javaClass.simpleName} ")
            // Set the clicked item position
            isDownloadEnabled = true

            isDownloadCompleted=false
            val videoUrl = categoryItem.img_path
            val cacheDir = context!!.cacheDir

            val fileName = categoryItem.id + ".mp4"
            val outputFile = File(cacheDir, fileName)
            stringpath = outputFile
            Log.d("abcd", outputFile.name)
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null)

            val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
            val downloadProgressbar = view.findViewById<TextView>(R.id.downloadProgressbar)

            var downloadJob: Job? = null

            if (outputFile.exists()) {
                clickListener.onItemClicked(false, stringpath.absolutePath)
            } else {
                (context as? Activity)?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
                    ?.addView(view)

                downloadJob = CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val client =
                            OkHttpClient.Builder().callTimeout(400, TimeUnit.SECONDS).build()
                        val requestBuilder = Request.Builder().url(videoUrl)

                        if (outputFile.length() > 0) {
                            requestBuilder.addHeader("Range", "bytes=${outputFile.length()}-")
                        }

                        val request = requestBuilder.build()

                        client.newCall(request).execute().use { response ->
                            if (!response.isSuccessful) throw IOException("Unexpected code $response")

                            val contentLength = response.body?.contentLength() ?: -1
                            var bytesRead: Long =
                                outputFile.length()  // Start reading from where the file left off

                            val inputStream = response.body?.byteStream()
                            if (inputStream != null) {
                                withContext(Dispatchers.Main) {
                                    cancelBtn.setOnClickListener {
                                        downloadJob?.cancel()
                                        outputFile.delete()
                                        (view.parent as? ViewGroup)?.removeView(view)

                                    }
                                }

                                val outputStream = FileOutputStream(
                                    outputFile,
                                    true
                                ) // Append to the existing file
                                inputStream.use { input ->
                                    val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                                    var bytesCopied: Int
                                    while (input.read(buffer).also { bytesCopied = it } != -1) {
                                        outputStream.write(buffer, 0, bytesCopied)
                                        bytesRead += bytesCopied
                                        // Log download progress
                                        val progress =
                                            ((bytesRead * 100 / contentLength).toInt()).coerceIn(
                                                0,
                                                100
                                            )
                                        withContext(Dispatchers.Main) {
                                            downloadProgressbar.text = "$progress %"
                                            Log.d("DownloadProgress", "Progress: $progress%")
                                        }
                                    }
                                }

                                outputStream.flush()
                                outputStream.close()

                                // File downloaded successfully
                                if (isDownloadEnabled && outputFile.name == stringpath.name) {
                                    withContext(Dispatchers.Main) {
                                        (view.parent as? ViewGroup)?.removeView(view)
                                        clickListener.onItemClicked(false, stringpath.absolutePath)
                                        isDownloadEnabled = false
                                    }
                                }
                            }
                        }
                    } catch (e: IOException) {
                        Log.e("DownloadError", "Error during download: ${e.message}")
                        // Handle error gracefully
                        outputFile.delete()
                        withContext(Dispatchers.Main) {
                            (view.parent as? ViewGroup)?.removeView(view)
                            notifyUser("Error during download")
                        }
                    }
                }
            }
        }



    }


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage1)


    }
    fun notifyUser(message: String) {
        runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    fun getSpanCount(): Int {
        return 3 // Change this value to the desired span count
    }

    interface CategoryItemClickListener {
        fun onItemClicked(wallpaperPath: Boolean, absolutePath: String)
    }


}