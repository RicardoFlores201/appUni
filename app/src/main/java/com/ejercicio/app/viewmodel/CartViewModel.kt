package com.ejercicio.app.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ejercicio.app.model.DishModel

/**
 * Modelo para un item en el carrito
 */
data class CartItem(
    val dish: DishModel,
    val quantity: Int
) {
    val subtotal: Double
        get() = dish.price * quantity
}

class CartViewModel : ViewModel() {

    // ===== UI State =====
    var cartItems by mutableStateOf<List<CartItem>>(emptyList())
        private set

    var showAlert by mutableStateOf(false)
        private set

    var alertMessage by mutableStateOf("")
        private set

    // ===== Computed Properties =====
    val itemCount: Int
        get() = cartItems.sumOf { it.quantity }

    val total: Double
        get() = cartItems.sumOf { it.subtotal }

    val restaurantId: String?
        get() = cartItems.firstOrNull()?.dish?.restaurantId

    // ===== Alerts =====
    fun closeAlert() { showAlert = false }

    private fun openAlert(msg: String) {
        alertMessage = msg
        showAlert = true
    }

    // ============================================================
    // ADD TO CART
    // ============================================================
    fun addToCart(dish: DishModel, quantity: Int = 1) {
        // Validación: Solo permitir platillos del mismo restaurante
        if (cartItems.isNotEmpty() && restaurantId != dish.restaurantId) {
            openAlert("Solo puedes agregar platillos del mismo restaurante.\nVacía tu carrito primero.")
            return
        }

        val existingItem = cartItems.find { it.dish.dishId == dish.dishId }

        cartItems = if (existingItem != null) {
            // Actualizar cantidad
            cartItems.map { item ->
                if (item.dish.dishId == dish.dishId) {
                    val newQuantity = (item.quantity + quantity).coerceAtMost(10)
                    item.copy(quantity = newQuantity)
                } else {
                    item
                }
            }
        } else {
            // Agregar nuevo item
            cartItems + CartItem(dish, quantity.coerceAtMost(10))
        }
    }

    // ============================================================
    // UPDATE QUANTITY
    // ============================================================
    fun updateQuantity(dishId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(dishId)
            return
        }

        cartItems = cartItems.map { item ->
            if (item.dish.dishId == dishId) {
                item.copy(quantity = newQuantity.coerceAtMost(10))
            } else {
                item
            }
        }
    }

    // ============================================================
    // REMOVE FROM CART
    // ============================================================
    fun removeFromCart(dishId: String) {
        cartItems = cartItems.filter { it.dish.dishId != dishId }
    }

    // ============================================================
    // CLEAR CART
    // ============================================================
    fun clearCart() {
        cartItems = emptyList()
    }

    // ============================================================
    // INCREMENT/DECREMENT
    // ============================================================
    fun incrementQuantity(dishId: String) {
        val item = cartItems.find { it.dish.dishId == dishId }
        if (item != null && item.quantity < 10) {
            updateQuantity(dishId, item.quantity + 1)
        }
    }

    fun decrementQuantity(dishId: String) {
        val item = cartItems.find { it.dish.dishId == dishId }
        if (item != null) {
            updateQuantity(dishId, item.quantity - 1)
        }
    }
}

