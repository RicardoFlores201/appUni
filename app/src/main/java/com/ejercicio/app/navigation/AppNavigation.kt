package com.ejercicio.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ejercicio.app.viewModel.CartViewModel
import com.ejercicio.app.viewModel.DishViewModel
import com.ejercicio.app.viewModel.LoginViewModel
import com.ejercicio.app.viewModel.OrderViewModel
import com.ejercicio.app.viewModel.RestaurantAuthViewModel
import com.ejercicio.app.views.BlankView
import com.ejercicio.app.views.login.LoginScreen
import com.ejercicio.app.views.register.SignUpScreen
import com.ejercicio.app.views.restaurant.AddDishScreen
import com.ejercicio.app.views.restaurant.EditDishScreen
import com.ejercicio.app.views.restaurant.RestaurantDashboardScreen
import com.ejercicio.app.views.restaurant.RestaurantLoginScreen
import com.ejercicio.app.views.restaurant.RestaurantSignUpScreen
import com.ejercicio.app.views.user.CartScreen
import com.ejercicio.app.views.user.CheckoutScreen
import com.ejercicio.app.views.user.DishDetailScreen
import com.ejercicio.app.views.user.OrderDetailScreen
import com.ejercicio.app.views.user.OrderHistoryScreen
import com.ejercicio.app.views.user.UserHomeScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    loginVM: LoginViewModel,
    restaurantVM: RestaurantAuthViewModel,
    dishVM: DishViewModel,
    cartVM: CartViewModel,
    orderVM: OrderViewModel
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
            CheckoutScreen(
                navController = navController,
                cartVM = cartVM,
                orderVM = orderVM
            )
        }

        composable(route = AppScreen.OrderHistory.route) {
            OrderHistoryScreen(
                navController = navController,
                orderVM = orderVM
            )
        }

        composable(
            route = AppScreen.OrderDetail.route,
            arguments = listOf(
                navArgument("orderId") { type = NavType.StringType }
            )
        ) {
            val orderId = it.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(
                navController = navController,
                orderId = orderId,
                orderVM = orderVM
            )
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