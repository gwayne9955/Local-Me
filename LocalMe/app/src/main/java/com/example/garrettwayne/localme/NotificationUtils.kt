package com.example.garrettwayne.localme

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.example.garrettwayne.localme.data.EventContent
import java.util.*

class NotificationUtils {

    fun setNotification(timeInMilliSeconds: Long, activity: Activity, event: Event) {
        if (timeInMilliSeconds > 0) {

            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent =
                Intent(activity.applicationContext, AlarmReceiver::class.java) // AlarmReceiver = broadcast receiver

            alarmIntent.putExtra("reason", "notification")
            alarmIntent.putExtra("timestamp", timeInMilliSeconds)
            var eventTime = EventContent.getEventTimeString(event.start.time)
            val s = arrayListOf(event.name.text, eventTime)
            alarmIntent.putStringArrayListExtra("eventData", s)

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMilliSeconds

            val pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

        }
    }
}
