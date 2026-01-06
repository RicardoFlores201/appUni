package com.ejercicio.app.model

import com.google.firebase.Timestamp

data class RestaurantModel(
    val restaurantId: String = "",
    val email: String = "",
    val restaurantName: String = "",
    val ownerName: String = "",
    val description: String = "",
    val phone: String = "",
    val address: String = "",
    val logoUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)