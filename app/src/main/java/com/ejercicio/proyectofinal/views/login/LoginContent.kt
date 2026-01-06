package com.ejercicio.proyectofinal.views.login

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ejercicio.proyectofinal.components.Alert
import com.ejercicio.proyectofinal.navigation.AppScreen
import com.ejercicio.proyectofinal.viewModel.LoginViewModel

@Composable
fun LoginContent(
    navController: NavHostController,
    loginVM: LoginViewModel,
    modifier: Modifier = Modifier
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 22.dp)
                .padding(top = 22.dp, bottom = 26.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF21268A)
                ) {
                    IconButton(
                        onClick = { navController.navigate(AppScreen.RestaurantLogin.route) },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBox,
                            contentDescription = "Login restaurante",
                            tint = Color.White
                        )
                    }
                }
            }

            Text(
                text = "Inicia sesión",
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 14.dp)
            )

            DefaultOutlinedField(
                value = loginVM.email,
                onValueChange = loginVM::onEmailChange,
                placeholder = "Correo electrónico",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(12.dp))

            DefaultOutlinedField(
                value = loginVM.password,
                onValueChange = loginVM::onPasswordChange,
                placeholder = "Contraseña",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(Modifier.height(18.dp))

            Button(
                onClick = {
                    loginVM.loginWithEmailPassword {
                        navController.navigate(AppScreen.UserHome.route) {
                            popUpTo(AppScreen.UserLogin.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F5BFF))
            ) {
                Text("Iniciar sesión", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(18.dp))

            OrDivider(text = "Continuar con")

            Spacer(Modifier.height(14.dp))

            OutlinedButton(
                onClick = {
                    loginVM.loginWithGoogle(navController.context) {
                        navController.navigate(AppScreen.UserHome.route) {
                            popUpTo(AppScreen.UserLogin.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, Color(0xFF2A2F3A)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("Google", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    val activity = navController.context as? androidx.activity.ComponentActivity
                    if (activity != null) {
                        loginVM.loginWithGithub(activity) {
                            navController.navigate(AppScreen.UserHome.route) {
                                popUpTo(AppScreen.UserLogin.route) { inclusive = true }
                            }
                        }
                    } else {
                        loginVM.openAlert("No se pudo obtener la Activity para iniciar sesión con GitHub.")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                border = BorderStroke(1.dp, Color(0xFF2A2F3A)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
            ) {
                Text("GitHub", fontWeight = FontWeight.SemiBold)
            }

            Spacer(Modifier.height(18.dp))

            RegisterRow(
                onRegisterClick = {
                    navController.navigate(AppScreen.UserSignup.route)
                }
            )

            Spacer(Modifier.height(12.dp))
        }

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
private fun DefaultOutlinedField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: (@Composable (() -> Unit))?,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        singleLine = true,
        placeholder = { Text(placeholder, color = Color(0xFF8B92A1)) },
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFF232838),
            focusedBorderColor = Color(0xFF2F5BFF),
            unfocusedContainerColor = Color(0xFF0F1219),
            focusedContainerColor = Color(0xFF0F1219),
            cursorColor = Color(0xFF2F5BFF),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        visualTransformation = if (isPassword) androidx.compose.ui.text.input.PasswordVisualTransformation()
        else androidx.compose.ui.text.input.VisualTransformation.None
    )
}

@Composable
private fun OrDivider(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(modifier = Modifier.weight(1f), color = Color(0xFF202536))
        Text(
            text = text,
            color = Color(0xFF9AA2B1),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Divider(modifier = Modifier.weight(1f), color = Color(0xFF202536))
    }
}

@Composable
private fun RegisterRow(onRegisterClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("¿No tienes una cuenta? ", color = Color(0xFFB7BDC9), fontSize = 13.sp)
        Text(
            "Registrarse",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onRegisterClick() }
        )
    }
}