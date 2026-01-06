package com.ejercicio.app.views.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.ejercicio.app.components.Alert
import com.ejercicio.app.viewModel.RestaurantAuthViewModel

@Composable
fun RestaurantSignUpScreen(
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
                .padding(top = 16.dp, bottom = 26.dp)
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

            // Título
            Text(
                text = "Registra tu restaurante",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Subtítulo
            Text(
                text = "Completa los datos de tu restaurante para comenzar a ofrecer platillos saludables.",
                fontSize = 15.sp,
                color = Color(0xFFB7BDC9),
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo: Nombre del restaurante
            FieldLabel("Nombre del restaurante *")
            RestaurantTextField(
                value = restaurantVM.restaurantName,
                onValueChange = restaurantVM::onRestaurantNameChange,
                placeholder = "Ej: Green Bites",
                leadingIcon = { Icon(Icons.Default.AccountBox, contentDescription = null) },
                keyboardType = KeyboardType.Text
            )

            Spacer(Modifier.height(20.dp))

            // Campo: Nombre del propietario
            FieldLabel("Nombre del propietario *")
            RestaurantTextField(
                value = restaurantVM.ownerName,
                onValueChange = restaurantVM::onOwnerNameChange,
                placeholder = "Ej: Juan Pérez",
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                keyboardType = KeyboardType.Text
            )

            Spacer(Modifier.height(20.dp))

            // Campo: Email
            FieldLabel("Correo electrónico *")
            RestaurantTextField(
                value = restaurantVM.email,
                onValueChange = restaurantVM::onEmailChange,
                placeholder = "restaurante@ejemplo.com",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardType = KeyboardType.Email
            )

            Spacer(Modifier.height(20.dp))

            // Campo: Contraseña
            FieldLabel("Contraseña *")
            RestaurantTextField(
                value = restaurantVM.password,
                onValueChange = restaurantVM::onPasswordChange,
                placeholder = "Mínimo 6 caracteres",
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(Modifier.height(20.dp))

            // Campo: Teléfono
            FieldLabel("Teléfono *")
            RestaurantTextField(
                value = restaurantVM.phone,
                onValueChange = restaurantVM::onPhoneChange,
                placeholder = "Ej: 5512345678",
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardType = KeyboardType.Phone
            )

            Spacer(Modifier.height(20.dp))

            // Campo: Dirección
            FieldLabel("Dirección *")
            RestaurantTextField(
                value = restaurantVM.address,
                onValueChange = restaurantVM::onAddressChange,
                placeholder = "Ej: Av. Principal #123, Col. Centro",
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                keyboardType = KeyboardType.Text
            )

            Spacer(Modifier.height(20.dp))

            // Campo: Descripción (opcional)
            FieldLabel("Descripción del restaurante (opcional)")
            RestaurantTextField(
                value = restaurantVM.description,
                onValueChange = restaurantVM::onDescriptionChange,
                placeholder = "Ej: Especialistas en comida vegana y sin gluten",
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                keyboardType = KeyboardType.Text,
                maxLines = 3
            )

            Spacer(Modifier.height(32.dp))

            // Botón de registro
            Button(
                onClick = {
                    restaurantVM.signUpRestaurant {
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
                        "Registrar restaurante",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Link para login de restaurante
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "¿Ya tienes una cuenta de restaurante? ",
                    color = Color(0xFFB7BDC9),
                    fontSize = 13.sp
                )
                Text(
                    "Iniciar sesión",
                    color = Color(0xFF218A85),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        navController.navigate("restaurant_login")
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
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFB7BDC9),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun RestaurantTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: (@Composable (() -> Unit))?,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        singleLine = maxLines == 1,
        maxLines = maxLines,
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