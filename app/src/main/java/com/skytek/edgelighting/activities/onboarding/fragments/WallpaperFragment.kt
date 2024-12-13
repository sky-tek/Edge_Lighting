package com.skytek.edgelighting.activities.onboarding.fragments

import android.content.Intent
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.skytek.edgelighting.R
import com.skytek.edgelighting.activities.MainActivity
import com.skytek.edgelighting.activities.StaticCategoryWallpaperPreviewActivity
import com.skytek.edgelighting.activities.StaticWallpaperActivity
import com.skytek.edgelighting.databinding.FragmentWallpaper2Binding
import com.skytek.edgelighting.utils.adTimeTraker.updateLastAdShownTime


class WallpaperFragment : Fragment() {
    private var _binding: FragmentWallpaper2Binding? = null
    private val binding get() = _binding!!

    // Lists of image URLs, Lottie views, ImageViews, and Containers
    private val imageUrls = listOf(
        "https://airnet-technologies.com/3d-live-wallpaper/Static_Wallpapers/Entertainment/thumb/Entertainment_colorfulguitarbeachthu.webp",
        "https://www.airnet-technologies.com/3d-live-wallpaper/Static_Wallpapers/Skull/thumb/Skull_aigenerated7735361thu.webp",
        "https://www.airnet-technologies.com/3d-live-wallpaper/Static_Wallpapers/Flower/thumb/Flower_aigenerated7747244thu.webp",
        "https://www.airnet-technologies.com/3d-live-wallpaper/Static_Wallpapers/Waterfall/thumb/Waterfall_aigenerated7831695thu.webp",
        "https://www.airnet-technologies.com/3d-live-wallpaper/Static_Wallpapers/Car/thumb/Car_aigenerated7806672thu.webp",
        "https://www.airnet-technologies.com/3d-live-wallpaper/Static_Wallpapers/categories_thumbnail/fc414bbaccwinterthumbnail.jpg"
    )

    private val lottieViews by lazy {
        listOf(
            binding.lottieAnimationView,
            binding.lottieAnimationView2,
            binding.lottieAnimationView3,
            binding.lottieAnimationView4,
            binding.lottieAnimationView5,
            binding.lottieAnimationView6
        )
    }

    private val imageViews by lazy {
        listOf(
            binding.wallpaper1,
            binding.wallpaper2,
            binding.wallpaper3,
            binding.wallpaper4,
            binding.wallpaper5,
            binding.wallpaper6
        )
    }

    private val containers by lazy {
        listOf(
            binding.wallpaper1Container,
            binding.wallpaper2Container,
            binding.wallpaper3Container,
            binding.wallpaper4Container,
            binding.wallpaper5Container,
            binding.wallpaper6Container
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWallpaper2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        underlineAndSetColor(binding.tvHomeBtn)
        underlineAndSetColor(binding.letsGo)

        // Load images with Glide and handle Lottie animations
        imageUrls.forEachIndexed { index, url ->
            loadImageWithGlide(url, imageViews[index], lottieViews[index])
        }

        // Handle button clicks for navigation
        binding.tvHomeBtn.setOnClickListener {
            updateLastAdShownTime()
            navigateToActivity(MainActivity::class.java)
        }
        binding.letsGo.setOnClickListener {
            navigateToActivity(
                StaticWallpaperActivity::class.java,
                "onBoardingScreen" to true
            )
        }

        // Handle wallpaper clicks for preview
        containers.forEachIndexed { index, container ->
            container.setOnClickListener { openPreviewActivity(imageUrls[index]) }
        }

        // Handle back button click
        binding.liveBackBtn.setOnClickListener { findNavController().navigate(R.id.edgeFragment) }
    }

    private fun underlineAndSetColor(textView: TextView) {
        textView.apply {
            paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
            setTextColor(ContextCompat.getColor(requireContext(), R.color.color_orange))
        }
    }

    private fun loadImageWithGlide(
        url: String,
        imageView: ImageView,
        lottieView: LottieAnimationView
    ) {
        Glide.with(this).load(url).listener(object : RequestListener<Drawable> {
                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean
                ): Boolean {
                    lottieView.visibility = View.GONE
                    imageView.setImageDrawable(resource)
                    return true
                }

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("Glide", "Failed to load image: $url")
                    return false
                }
            }).into(imageView)
    }

    private fun navigateToActivity(activityClass: Class<*>, vararg extras: Pair<String, Any>) {
        Intent(requireActivity(), activityClass).apply {
            extras.forEach { (key, value) ->
                when (value) {
                    is Boolean -> putExtra(key, value)
                    is String -> putExtra(key, value)
                }
            }
            startActivity(this)
        }
    }

    private fun openPreviewActivity(wallpaperPath: String) {
        Intent(requireActivity(), StaticCategoryWallpaperPreviewActivity::class.java).apply {
            putExtra("onBoardingScreen", true)
            putExtra("wallpaperPath", wallpaperPath)
            startActivity(this)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
