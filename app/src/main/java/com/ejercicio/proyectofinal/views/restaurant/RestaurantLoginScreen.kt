package com.ejercicio.proyectofinal.views.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ejercicio.proyectofinal.components.Alert
import com.ejercicio.proyectofinal.viewModel.RestaurantAuthViewModel

@Composable
fun RestaurantLoginScreen(
    navController: NavHostController,
    restaurantVM: RestaurantAuthViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con botón back
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // Título
            Text(
                text = "Acceso restaurante",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            // Subtítulo
            Text(
                text = "Ingresa con tu cuenta de restaurante para gestionar tus platillos.",
                fontSize = 15.sp,
                color = Color(0xFFB7BDC9),
                lineHeight = 22.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp)
            )

            // Campo: Email
            RestaurantLoginTextField(
                value = restaurantVM.email,
                onValueChange = restaurantVM::onEmailChange,
                placeholder = "Correo electrónico",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(16.dp))

            // Campo: Password
            RestaurantLoginTextField(
                value = restaurantVM.password,
                onValueChange = restaurantVM::onPasswordChange,
                placeholder = "Contraseña",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(Modifier.height(32.dp))

            // Botón de login
            Button(
                onClick = {
                    restaurantVM.loginRestaurant {
                        // Navegar al dashboard del restaurante
                        navController.navigate("restaurant_dashboard") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF218A85),
                    disabledContainerColor = Color(0xFF1A2840)
                ),
                enabled = !restaurantVM.isLoading
            ) {
                if (restaurantVM.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        "Iniciar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Link para registro
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿No tienes cuenta de restaurante? ",
                    color = Color(0xFFB7BDC9),
                    fontSize = 13.sp
                )
                Text(
                    "Regístrate",
                    color = Color(0xFF218A85),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("restaurant_signup")
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // Alertas
        if (restaurantVM.showAlert) {
            Alert(
                title = "Alerta",
                message = restaurantVM.alertMessage,
                confirmText = "Aceptar",
                onConfirmClick = { restaurantVM.closeAlert() }
            ) {}
        }
    }
}

@Composable
private fun RestaurantLoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: (@Composable (() -> Unit))?,
    keyboardType: KeyboardType,
    isPassword: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        singleLine = true,
        placeholder = {
            Text(
                placeholder,
                color = Color(0xFF6B7280),
                fontSize = 14.sp
            )
        },
        leadingIcon = leadingIcon?.let {
            {
                Box(modifier = Modifier.padding(start = 4.dp)) {
                    it()
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFF232838),
            focusedBorderColor = Color(0xFF218A85),
            unfocusedContainerColor = Color(0xFF0F1219),
            focusedContainerColor = Color(0xFF0F1219),
            cursorColor = Color(0xFF218A85),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLeadingIconColor = Color(0xFF218A85),
            unfocusedLeadingIconColor = Color(0xFFB7BDC9)
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None
    )
}