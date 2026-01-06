package com.ejercicio.proyectofinal.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ejercicio.proyectofinal.viewModel.CartViewModel
import com.ejercicio.proyectofinal.viewModel.DishViewModel
import com.ejercicio.proyectofinal.viewModel.LoginViewModel
import com.ejercicio.proyectofinal.viewModel.RestaurantAuthViewModel
import com.ejercicio.proyectofinal.views.BlankView
import com.ejercicio.proyectofinal.views.login.LoginScreen
import com.ejercicio.proyectofinal.views.register.SignUpScreen
import com.ejercicio.proyectofinal.views.restaurant.AddDishScreen
import com.ejercicio.proyectofinal.views.restaurant.EditDishScreen
import com.ejercicio.proyectofinal.views.restaurant.RestaurantDashboardScreen
import com.ejercicio.proyectofinal.views.restaurant.RestaurantLoginScreen
import com.ejercicio.proyectofinal.views.restaurant.RestaurantSignUpScreen
import com.ejercicio.proyectofinal.views.user.CartScreen
import com.ejercicio.proyectofinal.views.user.DishDetailScreen
import com.ejercicio.proyectofinal.views.user.UserHomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    loginVM: LoginViewModel,
    restaurantVM: RestaurantAuthViewModel,
    dishVM: DishViewModel,
    cartVM: CartViewModel
) {
    NavHost(
        navController = navController,
        startDestination = AppScreen.Blank.route
    ) {

        // ==================== PANTALLA INICIAL ====================
        composable(AppScreen.Blank.route) {
            BlankView(navController)
        }

        // ==================== RUTAS DE USUARIO FINAL ====================
        composable(route = AppScreen.UserLogin.route) {
            LoginScreen(navController, loginVM)
        }

        composable(route = AppScreen.UserSignup.route) {
            SignUpScreen(navController, loginVM)
        }

        composable(route = AppScreen.UserHome.route) {
            UserHomeScreen(navController, loginVM)
        }

        composable(
            route = AppScreen.DishDetail.route,
            arguments = listOf(
                navArgument("dishId") { type = NavType.StringType }
            )
        ) {
            val dishId = it.arguments?.getString("dishId") ?: ""
            DishDetailScreen(
                navController = navController,
                dishId = dishId,
                cartVM = cartVM
            )
        }

        composable(route = AppScreen.Cart.route) {
            CartScreen(
                navController = navController,
                cartVM = cartVM
            )
        }

        composable(route = AppScreen.Checkout.route) {
            // TODO: Crear CheckoutScreen
            // Por ahora muestra mensaje temporal
            BlankView(navController)
        }

        composable(route = AppScreen.OrderHistory.route) {
            // TODO: Crear OrderHistoryScreen
            BlankView(navController)
        }

        composable(
            route = AppScreen.OrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) {
            // TODO: Crear OrderDetailScreen
            BlankView(navController)
        }

        // ==================== RUTAS DE RESTAURANTE ====================
        composable(AppScreen.RestaurantSignup.route) {
            RestaurantSignUpScreen(
                navController = navController,
                restaurantVM = restaurantVM
            )
        }

        composable(AppScreen.RestaurantLogin.route) {
            RestaurantLoginScreen(
                navController = navController,
                restaurantVM = restaurantVM
            )
        }

        composable(AppScreen.RestaurantDashboard.route) {
            RestaurantDashboardScreen(
                navController = navController,
                restaurantVM = restaurantVM,
                dishVM = dishVM
            )
        }

        composable(AppScreen.AddDish.route) {
            AddDishScreen(
                navController = navController,
                dishVM = dishVM
            )
        }

        composable(
            route = AppScreen.EditDish.route,
            arguments = listOf(
                navArgument("dishId") { type = NavType.StringType }
            )
        ) {
            val dishId = it.arguments?.getString("dishId") ?: ""
            EditDishScreen(
                navController = navController,
                dishVM = dishVM,
                dishId = dishId
            )
        }

        composable(AppScreen.RestaurantOrders.route) {
            // TODO: Crear RestaurantOrdersScreen
            BlankView(navController)
        }

        composable(AppScreen.RestaurantProfile.route) {
            // TODO: Crear RestaurantProfileScreen
            BlankView(navController)
        }
    }
}