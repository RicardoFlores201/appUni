package com.ejercicio.app.navigation

/**
 * Sealed class que define todas las rutas de navegación de Nutrideli
 * Organizado por tipo de usuario: General, Usuario Final, y Restaurante
 */
sealed class AppScreen(val route: String) {

    // ===== RUTAS GENERALES =====
    object Blank : AppScreen("blank")

    // ===== RUTAS DE USUARIO FINAL =====
    object UserLogin : AppScreen("user_login")
    object UserSignup : AppScreen("user_signup")
    object UserHome : AppScreen("user_home")
    object DishDetail : AppScreen("dish_detail/{dishId}") {
        fun createRoute(dishId: String) = "dish_detail/$dishId"
    }
    object Cart : AppScreen("cart")
    object Checkout : AppScreen("checkout")
    object OrderHistory : AppScreen("order_history")
    object OrderDetail : AppScreen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }

    // ===== RUTAS DE RESTAURANTE =====
    object RestaurantSignup : AppScreen("restaurant_signup")
    object RestaurantLogin : AppScreen("restaurant_login")
    object RestaurantDashboard : AppScreen("restaurant_dashboard")
    object AddDish : AppScreen("add_dish")
    object EditDish : AppScreen("edit_dish/{dishId}") {
        fun createRoute(dishId: String) = "edit_dish/$dishId"
    }
    object RestaurantOrders : AppScreen("restaurant_orders")
    object RestaurantProfile : AppScreen("restaurant_profile")
}