package com.messenger.toaster

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.messenger.toaster.api.impl.UserApiImpl
import com.messenger.toaster.data.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FirebaseService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d(TAG, "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d(TAG, "Message Notification Body: ${it.body}")
        }
        sendNotification(
            remoteMessage.data["sender"].toString(),
            remoteMessage.data["message"].toString()
        )
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        if (User.EMAIL != null) {
            sendRegistrationToServer(token)
        }
    }

    private fun sendRegistrationToServer(token: String?) {
        val userApi = UserApiImpl()
        val response = userApi.updateToken(token.toString(), User.getCredentials())
        response.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 200) {
                    Log.d("server", "Токен обновлен")
                } else {
                    Log.d(
                        "server",
                        response.code().toString()
                    )
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("server", t.message.toString())
            }
        })
        Log.d(TAG, "sendRegistrationTokenToServer($token)")
    }

    private fun sendNotification(sender:String, message:String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val requestCode = 0
        val pendingIntent = PendingIntent.getActivity(
            this,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE,
        )

        val channelId = "fcm_default_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.icon)
            .setContentTitle(
                (
                        "Сообщение от " +
                                sender
                        )
            )
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Channel human readable title",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationId = 0
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    companion object {
        private const val TAG = "FireBaseService"
    }
}