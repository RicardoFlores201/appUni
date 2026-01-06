package com.ejercicio.proyectofinal.views.register

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.ejercicio.proyectofinal.navigation.AppScreen
import com.ejercicio.proyectofinal.viewModel.LoginViewModel

@Composable
fun SignUpScreen(
    navController: NavHostController,
    loginVM: LoginViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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
                .padding(top = 16.dp, bottom = 26.dp)
        ) {
            // Header con botón back y help
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
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

                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF21268A)
                ) {
                    IconButton(
                        onClick = { navController.navigate(AppScreen.RestaurantSignup.route) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBox,
                            contentDescription = "Registrar empresa",
                            tint = Color.White
                        )
                    }
                }
            }

            Text(
                text = "Crea tu cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Text(
                text = "Completa los siguientes datos para registrarte y acceder a todas las funciones de la aplicación.",
                fontSize = 15.sp,
                color = Color(0xFFB7BDC9),
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Text(
                text = "Nombre de usuario",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFB7BDC9),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ModernTextField(
                value = username,
                onValueChange = { username = it },
                placeholder = "Ingresa tu nombre de usuario",
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardType = KeyboardType.Text
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Correo electrónico",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFB7BDC9),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ModernTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Ingresa tu dirección de correo electrónico",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Contraseña",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFB7BDC9),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ModernTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Crea una contraseña segura",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    loginVM.createUser(email, password, username) {
                        navController.navigate(AppScreen.UserHome.route) {
                            popUpTo(AppScreen.UserLogin.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2F5BFF),
                    disabledContainerColor = Color(0xFF1A2840)
                ),
                enabled = username.isNotBlank() && email.isNotBlank() && password.isNotBlank()
            ) {
                Text(
                    "Registrarse",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿Ya tienes una cuenta? ",
                    color = Color(0xFFB7BDC9),
                    fontSize = 13.sp
                )
                Text(
                    "Iniciar sesión",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.popBackStack()
                    }
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        // Alertas
        if (loginVM.showAlert) {
            Alert(
                title = "Alerta",
                message = loginVM.alertMessage,
                confirmText = "Aceptar",
                onConfirmClick = { loginVM.closeAlert() }
            ) {}
        }
    }
}

@Composable
private fun ModernTextField(
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
            focusedBorderColor = Color(0xFF2F5BFF),
            unfocusedContainerColor = Color(0xFF0F1219),
            focusedContainerColor = Color(0xFF0F1219),
            cursorColor = Color(0xFF2F5BFF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLeadingIconColor = Color(0xFF2F5BFF),
            unfocusedLeadingIconColor = Color(0xFFB7BDC9)
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None
    )
}