package com.example.garrettwayne.localme.data

import com.example.garrettwayne.localme.Event
import com.example.garrettwayne.localme.EventSearch
import com.example.garrettwayne.localme.UserLocation
import java.util.*

object EventContent {

    /**
     * An array of Event items.
     */
    val ITEMS: MutableList<Event> = ArrayList()

    /**
     * A map of Event items, by ID.
     */
    val ITEM_MAP: MutableMap<String, Event> = HashMap()

    var eventCategories: MutableMap<String, String> = HashMap()

    lateinit var currentEventSearch: EventSearch

    var userLocation = UserLocation(35.2786, -120.662) // Hardcoded to San Luis Obispo by default

    var events: MutableList<Event> = ArrayList()

    val preEventNotificationTable = mapOf(60000 to "1 Minute",
        300000 to "5 Minutes",
        600000 to "10 Minutes",
        900000 to "15 Minutes",
        1800000 to "30 Minutes",
        2700000 to "45 Minutes",
        3600000 to "1 Hour",
        7200000 to "2 Hours",
        10800000 to "3 Hours",
        14400000 to "4 Hours")

    var ringtoneString = "content://settings/system/notification_sound"

    var toResetScroll = true

    var toResetMap = true

    init {
    }

    fun addEventstoItems() {
        ITEMS.clear()
        ITEM_MAP.clear()
        for (e in events) {
            ITEMS.add(e)
            ITEM_MAP[e.id] = e
        }
    }

    fun getUserLocationCoordinates() : UserLocation {
        return userLocation
    }

    fun getEventTimeString(date: Date): String {
        val s = date.toLocaleString() // "Dec 3, 2018 6:00:00 PM"
        val halfOfDay = s.subSequence(s.length - 3, s.length)
        val minutes = s.subSequence(s.length - 9, s.length - 6)
        var hours = date.hours
        if (hours > 12)
            hours -= 12
        val time = hours.toString() + minutes + halfOfDay

        return time
    }

    fun getEventDateString(date: Date): String {
        val d = date.toString()
        val day = d.subSequence(0, d.length - 18).toString()
        val year = d.subSequence(d.length - 4, d.length).toString()
        val dateString = "$day, $year"
        return dateString
    }

}
