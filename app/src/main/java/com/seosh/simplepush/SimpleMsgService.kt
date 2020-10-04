package com.seosh.simplepush

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class SimpleMsgService : FirebaseMessagingService() {
    companion object {
        const val TAG = "[SimpleMsgService]"
        const val REQ_CODE = 0
        const val NOTIFICATION_CHANNEL_ID = "SimplePush"
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        super.onMessageReceived(msg)

        Log.d("seosh", "${TAG} message : ${msg.notification.toString()}")

        val title = msg.notification?.title ?: "No title"
        val message = msg.notification?.body ?: ""

        createPushMsg(title = title, msg = message)
    }

    private fun createPushMsg(title: String, msg: String) {
        val intent = Intent(this, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        val pendingIntent = PendingIntent.getActivity(this, REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(this).apply {
            setSmallIcon(R.mipmap.ic_launcher)
            setContentTitle(title)
            setContentText(msg)
            setAutoCancel(false)
            setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
            setContentIntent(pendingIntent)
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "Push", NotificationManager.IMPORTANCE_HIGH).apply {
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(20L, 300L, 200L, 100L)

            }

            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        notificationManager.notify(REQ_CODE, mBuilder.build())
    }
}