package com.example.garrettwayne.localme

import java.util.*

data class EventCategories(
    val categories: List<EventCategory>
)

data class EventCategory(
    val id: String,
    val name: String
)

data class EventResponse(
    val events: List<Event>
)

data class EventName(
    val text: String
)

data class EventDescription(
    val text: String
)

data class EventStart(
    val timeZone: String,
    val local: String,
    val utc: String,
    var time: Date
)

data class EventEnd(
    val timeZone: String,
    val local: String,
    val utc: String,
    var time: Date
)

data class EventLocation(
    val address: EventAddress,
    val id: String,
    val name: String
)

data class EventAddress(
    val address_1: String,
    val address_2: String?,
    val city: String,
    val region: String,
    val postal_code: String,
    val country: String,
    val latitude: String,
    val longitude: String,
    val localized_address_display: String,
    val localized_area_display: String,
    val localized_multi_line_address_display: List<String>
)

data class EventLogo(
    val id: String,
    val url: String,
    val aspect_ratio: String,
    val edge_color: String,
    val edge_color_set: Boolean
)

data class Event(
    val name: EventName,
    val description: EventDescription,
    val id: String,
    val url: String,
    val start: EventStart,
    val end: EventEnd,
    val venue_id: String,
    var locationInfo: EventLocation,
    val logo: EventLogo
)