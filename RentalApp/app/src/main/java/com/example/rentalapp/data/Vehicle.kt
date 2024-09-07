package com.example.rentalapp.data

import com.google.firebase.firestore.GeoPoint

data class Vehicle(
    val id: String = "",
    val type: String = "",
    val make: String = "",
    val model: String = "",
    val year: Int = 0,
    val location: GeoPoint? = null,
    val dailyRentalPrice: Double = 0.0,
    val startDate: String = "",
    val endDate: String = "",
    val imageUrls: List<String> = emptyList(),
    val ownerName: String = "",
    val ownerPhone: String = ""
)