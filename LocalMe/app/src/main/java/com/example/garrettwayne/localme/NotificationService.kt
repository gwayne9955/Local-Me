package com.example.garrettwayne.localme

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import java.util.*
import android.app.NotificationChannel
import android.media.AudioAttributes
import android.net.Uri
import android.preference.PreferenceManager

class NotificationService : IntentService("NotificationService") {
    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    @SuppressLint("NewApi")
    private fun createChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library

            val context = this.applicationContext
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.deleteNotificationChannel(CHANNEL_ID)

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(PreferenceManager.getDefaultSharedPreferences(context).getBoolean("notifications_new_message_vibrate", true))

            // Ringtone functionality currently not working
            val uri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString("notifications_new_message_ringtone", ""))
            val att = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build()
            notificationChannel.setSound(uri, att)

            // Continue to build notification
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#e8334a")
            notificationChannel.description = "Channel for the event notification"
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    companion object {
        const val CHANNEL_ID = "com.example.garrettwayne.localmeCHANNEL_ID"
        const val CHANNEL_NAME = "Event Notification"
    }

    override fun onHandleIntent(intent: Intent?) {
        //Create Channel
        createChannel()

        var timestamp: Long = 0
        if (intent != null && intent.extras != null) {
            timestamp = intent.extras!!.getLong("timestamp")
        }

        if (timestamp > 0 && intent != null) {
            val context = this.applicationContext
            val notifyIntent = Intent(this, MainActivity::class.java)
            val eventData = intent.extras!!.getStringArrayList("eventData")

            val title = eventData!![0]
            val message = "This upcoming event will begin at ${eventData[1]}"

            notifyIntent.putExtra("title", title)
            notifyIntent.putExtra("message", message)
            notifyIntent.putExtra("notification", true)

            notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timestamp

            val pendingIntent = PendingIntent.getActivity(context, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT)
            val res = this.resources
            val uri = Uri.parse(PreferenceManager.getDefaultSharedPreferences(context).getString("notifications_new_message_ringtone", ""))

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                mNotification = Notification.Builder(this, CHANNEL_ID)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.localme_small_with_background)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.localme_small_with_background))
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setStyle(Notification.BigTextStyle()
                                .bigText(message))
                        .setSound(uri)
                        .setContentText(message).build()
            } else {

                mNotification = Notification.Builder(this)
                        // Set the intent that will fire when the user taps the notification
                        .setContentIntent(pendingIntent)
                        .setSmallIcon(R.drawable.localme_small_with_background)
                        .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.localme_small_with_background))
                        .setAutoCancel(true)
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentTitle(title)
                        .setStyle(Notification.BigTextStyle()
                                .bigText(message))
                        .setSound(uri)
                        .setContentText(message).build()

            }

            var notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // mNotificationId is a unique int for each notification that you must define
            notificationManager.notify(mNotificationId, mNotification)
        }
    }
}