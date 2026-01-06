package com.ejercicio.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ejercicio.app.navigation.AppNavigation
import com.ejercicio.app.ui.theme.NutrideliUPIICSATheme
import com.ejercicio.app.viewModel.CartViewModel
import com.ejercicio.app.viewModel.DishViewModel
import com.ejercicio.app.viewModel.LoginViewModel
import com.ejercicio.app.viewModel.OrderViewModel
import com.ejercicio.app.viewModel.RestaurantAuthViewModel

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModels para la aplicación
        val loginVM: LoginViewModel by viewModels()
        val restaurantAuthViewModel: RestaurantAuthViewModel by viewModels()
        val dishVM: DishViewModel by viewModels()
        val cartVM: CartViewModel by viewModels()
        val orderVM: OrderViewModel by viewModels()

        enableEdgeToEdge()

        setContent {
            NutrideliUPIICSATheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        loginVM = loginVM,
                        restaurantVM = restaurantAuthViewModel,
                        dishVM = dishVM,
                        cartVM = cartVM,
                        orderVM = orderVM
                    )
                }
            }
        }
    }
}