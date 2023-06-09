package com.kelompokempat.kelasku.fcm

import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationCompat
import com.crocodic.core.base.activity.NoViewModelActivity.BetterActivityResult.Companion.registerForActivityResult
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kelompokempat.kelasku.R
import com.kelompokempat.kelasku.data.Const
import com.kelompokempat.kelasku.ui.detail.DetailActivity
import timber.log.Timber

class FirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token : String) {
        super.onNewToken(token)
        sendRegistrationToServer(token)

    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val context: Context = applicationContext

        Timber.d("firebase_receive_message_title : ${message.data["user_id"]}")
        Timber.d("firebase_receive_message_title : ${message.data["title"]}")
        Timber.d("firebase_receive_message_message : ${message.data["message"]}")

        val notificationId = System.currentTimeMillis().toInt()
        showNotification(context, notificationId, message.data["user_id"].toString(), message.data["title"].toString(), message.data["message"].toString())

    }

}

private fun sendRegistrationToServer(token: String?) {
    // TODO: Implement this method to send token to your app server.
    Timber.d("sendRegistrationTokenToServer($token)")
}

@SuppressLint("ObsoleteSdkInt", "UnspecifiedImmutableFlag")
fun showNotification(context: Context, notificationId: Int, userId: String, title: String, message: String) {
    // Notification Manager
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Notification for Oreo >
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "CHANNEL_ID",
            "My Channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = "Mhm"
        channel.enableLights(true)
        channel.enableVibration(true)
        notificationManager.createNotificationChannel(channel)
    }

    // Create an intent to open the activity when the notification is clicked
    val intent = Intent(context, DetailActivity::class.java)
    intent.putExtra(Const.FRIENDS.FRIENDS_ID, userId)

    val pendingIntent = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
        PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    } else {
        PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    val builder = NotificationCompat.Builder(context, "CHANNEL_ID")
        .setSmallIcon(R.drawable.logo)
        .setContentInfo(userId)
        .setContentTitle(title)
        .setContentText(message)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)

    notificationManager.notify(notificationId, builder.build())
}
