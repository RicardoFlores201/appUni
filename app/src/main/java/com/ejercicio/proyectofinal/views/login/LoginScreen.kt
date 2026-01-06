package com.ejercicio.proyectofinal.views.login

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.ejercicio.proyectofinal.viewModel.LoginViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LoginScreen(navController: NavHostController, loginVM: LoginViewModel){
    Scaffold(
        topBar = {},
        content = {LoginContent(navController, loginVM)},
        bottomBar = {

        }
    )
}