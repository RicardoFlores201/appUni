package com.ejercicio.proyectofinal.model

import com.google.firebase.Timestamp

data class DishModel(
    val dishId: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val name: String = "",
    val description: String = "",
    val ingredients: List<String> = emptyList(),
    val dietaryTags: List<String> = emptyList(),
    val category: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val createdAt: Timestamp = Timestamp.now()
)