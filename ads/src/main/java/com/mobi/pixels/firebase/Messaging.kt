package com.mobi.pixels.firebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class Messaging : FirebaseMessagingService() {

    companion object {
        var channelId: String? = null
        var icon: Int? = null
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
        val imageUrl = remoteMessage.notification?.imageUrl

        showNotification(title, message, imageUrl)
    }

    private fun showNotification(title: String?, message: String?, imageUrl: Uri?) {

        val builder: NotificationCompat.Builder = NotificationCompat.Builder(this, channelId!!)
            .setContentTitle(title)
            .setAutoCancel(true)
            .setSmallIcon(icon!!)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentText(message)

        if (imageUrl != null) {
            Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        builder.setLargeIcon(resource)
                        builder.setStyle(
                            NotificationCompat.BigPictureStyle().bigPicture(resource)
                                .bigLargeIcon(null as Bitmap?)
                        )
                        show(builder)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        val largeIconBitmap = BitmapFactory.decodeResource(resources, icon!!)
                        builder.setLargeIcon(largeIconBitmap)
                        show(builder)
                    }

                })
        } else {
            val largeIconBitmap = BitmapFactory.decodeResource(resources, icon!!)
            builder.setLargeIcon(largeIconBitmap)
            show(builder)
        }

    }

    @SuppressLint("MissingPermission")
    private fun show(builder: NotificationCompat.Builder) {
        val managerCompat = NotificationManagerCompat.from(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            fireEvent("NotifyPermNotGrantForMessaging")
            return
        }
        managerCompat.notify(999, builder.build())
    }
}
