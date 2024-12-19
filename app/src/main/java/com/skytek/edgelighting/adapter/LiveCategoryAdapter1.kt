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
import com.mobi.pixels.adInterstitial.AdInterstitialShowListeners
import com.mobi.pixels.adInterstitial.Interstitial
import com.mobi.pixels.firebase.fireEvent
import com.skytek.edgelighting.R
import com.skytek.edgelighting.ads.IsShowingOpenAd.isinterstitialvisible
import com.skytek.edgelighting.modelclass.LiveModelClass.LiveResponse
import com.skytek.edgelighting.modelclass.LiveModelClass.LiveResponsePrent
import com.skytek.edgelighting.utils.AdResources
import com.skytek.edgelighting.utils.AdResources.activitiesAdId
import com.skytek.edgelighting.utils.AdResources.clicks
import com.skytek.edgelighting.utils.AdResources.wholeInterAdShow
import com.skytek.edgelighting.utils.AdResources.wholeScreenAdShow
import com.skytek.edgelighting.utils.adTimeTraker.isIntervalElapsed
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime
import com.skytek.edgelighting.utils.checkContext
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
import java.util.concurrent.TimeUnit

class LiveCategoryAdapter1(
    private var categories: LiveResponsePrent,
    private val clickListener: LiveCategoryItemClickListener,
    private var context: Context
) : RecyclerView.Adapter<LiveCategoryAdapter1.CategoryViewHolder>() {


    private val spanCount = 3 // Set span count to 3
    private val viewType4Items: List<LiveResponse> =
        categories.response.filter { it.viewType == "4" }

//    private val downloadingMap = mutableMapOf<Int, Boolean>()

    var clickedItemPosition: Int? = null
    val MAX_RETRY_COUNT = 3
    var downloadThread: Thread? = null

    companion object {
        var isDownloadEnabled = true
        lateinit var stringpath: File
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_category1, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        Log.d("ajaygaaaaa", "size is=  ${viewType4Items.size}")
        return viewType4Items.size


    }


    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = viewType4Items[position]


        holder.categoryImage.load(category.wallpapers[0].thumbPath) {
            // Additional options can be added here
        }




        holder.itemView.setOnClickListener {
            Log.d("rrrrrrrrrrrrrrrrr", "onBindViewHolder: ")
            // Set the clicked item position
            clickedItemPosition = position
clicks++
            isDownloadEnabled = true

            val videoUrl = category.wallpapers[0].img_path


            val fileName = category.wallpapers[0].id + ".mp4"

            val directory = File(context.getExternalFilesDir("live")?.absolutePath)

            if (!directory.exists()) {
                directory.mkdirs()
            }

            val outputFile = File(directory, fileName)
            stringpath = outputFile
            Log.d("rrrrrrrrrrrrrrrrr", fileName)
            Log.d("rrrrrrrrrrrrrrrrr", videoUrl)

            val view = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null)

            val cancelBtn = view.findViewById<TextView>(R.id.cancelBtn)
            val downloadProgressbar = view.findViewById<TextView>(R.id.downloadProgressbar)

            if (outputFile.exists()) {
                if ((isIntervalElapsed() || AdResources.clicks >= AdResources.ElBtnClickCount) && wholeScreenAdShow && wholeInterAdShow) {
                    if (checkContext(context)) {
                        Interstitial.show(
                            context as Activity,
                            object : AdInterstitialShowListeners {
                                override fun onDismissed() {
                                    isinterstitialvisible = false
                                    clickListener.onItemClicked(false, stringpath.absolutePath)
                                    updateLastAdShownTime()
                                    clicks=0
                                }

                                override fun onError(error: String) {
                                    Interstitial.load(
                                        context as Activity,
                                        activitiesAdId,
                                    )
                                    clickListener.onItemClicked(false, stringpath.absolutePath)
                                }

                                override fun onShowed() {
                                    isinterstitialvisible = true

                                    Interstitial.load(
                                        context as Activity,
                                        activitiesAdId,
                                    )



                                }
                            })
                    }
                } else {
                    clickListener.onItemClicked(false, stringpath.absolutePath)
                }


            } else {
                (context as? Activity)?.window?.decorView?.findViewById<ViewGroup>(android.R.id.content)
                    ?.addView(view)
                if ((isIntervalElapsed() || clicks>= AdResources.ElBtnClickCount)&& wholeScreenAdShow && wholeInterAdShow) {
                if (checkContext(context)){
                    Interstitial.show(context as Activity, object : AdInterstitialShowListeners {
                        override fun onDismissed() {
                            isinterstitialvisible = false
                            downloadVideo(
                                context,
                                videoUrl,
                                outputFile,
                                view,
                                cancelBtn,
                                downloadProgressbar,
                                clickListener
                            )
                            updateLastAdShownTime()
                            clicks=0
                        }

                        override fun onError(error: String) {
                            Interstitial.load(
                                context as Activity,
                                activitiesAdId,
                            )
                            downloadVideo(
                                context,
                                videoUrl,
                                outputFile,
                                view,
                                cancelBtn,
                                downloadProgressbar,
                                clickListener
                            )

                        }

                        override fun onShowed() {

                            isinterstitialvisible = true

                            Interstitial.load(
                                context as Activity,
                                activitiesAdId,
                            )



                        }
                    })
                }
                } else {
                    downloadVideo(
                        context,
                        videoUrl,
                        outputFile,
                        view,
                        cancelBtn,
                        downloadProgressbar,
                        clickListener
                    )
                }

            }
        }


    }

    fun loadInterstitialAd() {


    }

    fun downloadVideo(
        context: Context,
        videoUrl: String,
        outputFile: File,
        view: View,
        cancelBtn: TextView,
        downloadProgressbar: TextView,
        clickListener: LiveCategoryItemClickListener
    ) {
        var downloadJob: Job? = null

        downloadJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                val client = OkHttpClient.Builder().callTimeout(400, TimeUnit.SECONDS).build()
                val requestBuilder = Request.Builder().url(videoUrl)

                // Handle resuming the download by adding a Range header
                if (outputFile.length() > 0) {
                    requestBuilder.addHeader("Range", "bytes=${outputFile.length()}-")
                }

                val request = requestBuilder.build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    val contentLength = response.body?.contentLength() ?: -1
                    var bytesRead: Long = outputFile.length() // Start reading from where the file left off

                    val inputStream = response.body?.byteStream()
                    if (inputStream != null) {
                        withContext(Dispatchers.Main) {
                            cancelBtn.setOnClickListener {
                                downloadJob?.cancel()
                                outputFile.delete()
                                (view.parent as? ViewGroup)?.removeView(view)
                            }
                        }

                        // Open FileOutputStream in append mode to continue the download
                        val outputStream = FileOutputStream(outputFile, true)
                        inputStream.use { input ->
                            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                            var bytesCopied: Int
                            while (input.read(buffer).also { bytesCopied = it } != -1) {
                                // Write the binary data to the file
                                outputStream.write(buffer, 0, bytesCopied)
                                bytesRead += bytesCopied

                                // Log download progress
                                val progress = ((bytesRead * 100 / contentLength).toInt()).coerceIn(0, 100)
                                withContext(Dispatchers.Main) {
                                    downloadProgressbar.text = "$progress %"
                                    Log.d("DownloadProgress", "ye wala chal raha hai nProgress: $progress%")
                                }
                            }
                        }

                        // Ensure stream is flushed and closed
                        outputStream.flush()
                        outputStream.close()

                        // File downloaded successfully
                        withContext(Dispatchers.Main) {
                            (view.parent as? ViewGroup)?.removeView(view)
                            clickListener.onItemClicked(false, outputFile.absolutePath)
                        }
                    } else {
                        throw IOException("Failed to open input stream")
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

    private fun notifyUser(message: String) {
        runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }/*fun setCancelDownload(position: Int, cancel: Boolean) {
        downloadingMap[position] = cancel
    }*/


    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage1)


    }

    // Function to get the span count
    fun getSpanCount(): Int {
        return spanCount
    }

    interface LiveCategoryItemClickListener {
        fun onItemClicked(
            wallpaperPath: Boolean, absolutePath: String
        ) // Pass any necessary data when an item is clicked
    }


}
