package com.ejercicio.proyectofinal.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ejercicio.proyectofinal.navigation.AppScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@Composable
fun BlankView(navController: NavController) {

    // Verificar si hay un usuario autenticado
    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Hay un usuario autenticado, verificar en qué colección existe
            val userId = currentUser.uid
            val db = FirebaseFirestore.getInstance()

            try {
                // Primero verificar si es un restaurante
                val restaurantDoc = db.collection("restaurants")
                    .document(userId)
                    .get()
                    .await()

                if (restaurantDoc.exists()) {
                    // Es un restaurante, ir al dashboard de restaurante
                    navController.navigate(AppScreen.RestaurantDashboard.route) {
                        popUpTo(AppScreen.Blank.route) { inclusive = true }
                    }
                    return@LaunchedEffect
                }

                // Si no es restaurante, verificar si es usuario normal
                val userDoc = db.collection("users")
                    .document(userId)
                    .get()
                    .await()

                if (userDoc.exists()) {
                    // Es un usuario normal, ir al home de usuario
                    navController.navigate(AppScreen.UserHome.route) {
                        popUpTo(AppScreen.Blank.route) { inclusive = true }
                    }
                    return@LaunchedEffect
                }

                // Si existe en Auth pero no en ninguna colección (caso raro),
                // cerrar sesión y mostrar pantalla de selección
                FirebaseAuth.getInstance().signOut()

            } catch (e: Exception) {
                // En caso de error, mostrar pantalla de selección
                e.printStackTrace()
            }
        }
        // Si no hay usuario autenticado o hubo error, se muestra la UI de selección
    }

    // UI de selección de tipo de usuario
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF4CAF50),
                        Color(0xFF2E7D32)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {

            // Logo o título de la app
            Text(
                text = "🥗 Nutrideli",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Comida saludable a tu puerta",
                fontSize = 18.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Título de selección
            Text(
                text = "¿Cómo deseas ingresar?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Botón para Usuario
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(AppScreen.UserLogin.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Usuario",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 16.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Soy Cliente",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32)
                            )
                            Text(
                                text = "Quiero ordenar comida saludable",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // Botón para Restaurante
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(AppScreen.RestaurantLogin.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = "Restaurante",
                            modifier = Modifier
                                .size(48.dp)
                                .padding(end = 16.dp),
                            tint = Color(0xFFFF9800)
                        )
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Soy Restaurante",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = "Quiero gestionar mis platillos",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto adicional
            Text(
                text = "Comida vegana • Sin gluten • Orgánica",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}