package com.ejercicio.app.model

import com.google.firebase.database.ServerValue

/**
 * Modelo de Pedido para guardar en Firebase Realtime Database
 */
data class OrderModel(
    val orderId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val restaurantId: String = "",
    val restaurantName: String = "",
    val items: List<OrderItem> = emptyList(),
    val subtotal: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val total: Double = 0.0,
    val deliveryAddress: String = "",
    val deliveryInstructions: String = "",
    val paymentMethod: String = "Efectivo", // "Efectivo", "Tarjeta", etc.
    val status: String = "pending", // pending, confirmed, preparing, on_delivery, delivered, cancelled
    val createdAt: Any = ServerValue.TIMESTAMP,
    val updatedAt: Any = ServerValue.TIMESTAMP
) {
    /**
     * Convierte el modelo a un Map para guardar en Realtime Database
     */
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "orderId" to orderId,
            "userId" to userId,
            "userName" to userName,
            "userEmail" to userEmail,
            "restaurantId" to restaurantId,
            "restaurantName" to restaurantName,
            "items" to items.map { it.toMap() },
            "subtotal" to subtotal,
            "deliveryFee" to deliveryFee,
            "total" to total,
            "deliveryAddress" to deliveryAddress,
            "deliveryInstructions" to deliveryInstructions,
            "paymentMethod" to paymentMethod,
            "status" to status,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}

/**
 * Item individual dentro de un pedido
 */
data class OrderItem(
    val dishId: String = "",
    val dishName: String = "",
    val dishImageUrl: String = "",
    val quantity: Int = 0,
    val price: Double = 0.0,
    val subtotal: Double = 0.0
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "dishId" to dishId,
            "dishName" to dishName,
            "dishImageUrl" to dishImageUrl,
            "quantity" to quantity,
            "price" to price,
            "subtotal" to subtotal
        )
    }
}

/**
 * Estados posibles del pedido
 */
object OrderStatus {
    const val PENDING = "pending"           // Pendiente de confirmación
    const val CONFIRMED = "confirmed"       // Confirmado por el restaurante
    const val PREPARING = "preparing"       // En preparación
    const val ON_DELIVERY = "on_delivery"   // En camino
    const val DELIVERED = "delivered"       // Entregado
    const val CANCELLED = "cancelled"       // Cancelado

    /**
     * Obtiene el texto legible del estado
     */
    fun getStatusText(status: String): String {
        return when (status) {
            PENDING -> "Pendiente"
            CONFIRMED -> "Confirmado"
            PREPARING -> "En preparación"
            ON_DELIVERY -> "En camino"
            DELIVERED -> "Entregado"
            CANCELLED -> "Cancelado"
            else -> "Desconocido"
        }
    }

    /**
     * Obtiene el color del estado
     */
    fun getStatusColor(status: String): Long {
        return when (status) {
            PENDING -> 0xFFFF9800      // Naranja
            CONFIRMED -> 0xFF2196F3    // Azul
            PREPARING -> 0xFF9C27B0    // Morado
            ON_DELIVERY -> 0xFF00BCD4  // Cyan
            DELIVERED -> 0xFF4CAF50    // Verde
            CANCELLED -> 0xFFE53935    // Rojo
            else -> 0xFF9E9E9E         // Gris
        }
    }
}