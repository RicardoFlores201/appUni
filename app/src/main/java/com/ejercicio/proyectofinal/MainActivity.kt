package com.ejercicio.proyectofinal

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
import com.ejercicio.proyectofinal.navigation.AppNavigation
import com.ejercicio.proyectofinal.ui.theme.NutrideliUPIICSATheme
import com.ejercicio.proyectofinal.ui.theme.NutrideliUPIICSATheme
import com.ejercicio.proyectofinal.viewModel.CartViewModel
import com.ejercicio.proyectofinal.viewModel.DishViewModel
import com.ejercicio.proyectofinal.viewModel.LoginViewModel
import com.ejercicio.proyectofinal.viewModel.RestaurantAuthViewModel

class MainActivity : ComponentActivity() {
    private lateinit var navController: NavHostController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ViewModels para la aplicación
        val loginVM: LoginViewModel by viewModels()
        val restaurantAuthViewModel: RestaurantAuthViewModel by viewModels()
        val dishVM: DishViewModel by viewModels()
        val cartVM: CartViewModel by viewModels()

        // TODO: Agregar OrderViewModel cuando lo crees
        // val orderVM: OrderViewModel by viewModels()

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
                        cartVM = cartVM
                    )
                }
            }
        }
    }
}