package com.example.garrettwayne.localme

data class EventSearch(
    val latitude: Double,
    val longitude: Double,
    val range: Int,
    val keyword: String,
    val dateKeyword: String,
    val price: String,
    val category: String,
    val sortBy: String,
    val allowAdultEvents: Boolean
)