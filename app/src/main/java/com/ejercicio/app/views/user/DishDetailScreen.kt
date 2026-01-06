package com.ejercicio.app.views.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ejercicio.app.model.DishModel
import com.ejercicio.app.navigation.AppScreen
import com.ejercicio.app.viewModel.CartViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DishDetailScreen(
    navController: NavHostController,
    dishId: String,
    cartVM: CartViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    var dish by remember { mutableStateOf<DishModel?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var quantity by remember { mutableStateOf(1) }
    var showAddedSnackbar by remember { mutableStateOf(false) }

    // Cargar platillo
    LaunchedEffect(dishId) {
        isLoading = true
        try {
            val doc = FirebaseFirestore.getInstance()
                .collection("dishes")
                .document(dishId)
                .get()
                .await()

            dish = doc.toObject(DishModel::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartVM.itemCount > 0) {
                                Badge(containerColor = Color(0xFF4CAF50)) {
                                    Text("${cartVM.itemCount}", fontSize = 10.sp)
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = { navController.navigate(AppScreen.Cart.route) }) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Carrito",
                                tint = Color.White
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            if (dish != null) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF0F1219),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Quantity selector
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (quantity > 1) Color(0xFF1A2F2A) else Color(0xFF1A1F2E),
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Menos",
                                    tint = if (quantity > 1) Color(0xFF4CAF50) else Color(0xFF444444)
                                )
                            }

                            Text(
                                text = "$quantity",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.widthIn(min = 30.dp)
                            )

                            IconButton(
                                onClick = { if (quantity < 10) quantity++ },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(Color(0xFF1A2F2A), RoundedCornerShape(8.dp))
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Más",
                                    tint = Color(0xFF4CAF50)
                                )
                            }
                        }

                        // Add to cart button
                        Button(
                            onClick = {
                                dish?.let {
                                    cartVM.addToCart(it, quantity)
                                    showAddedSnackbar = true
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 16.dp)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Agregar $${String.format("%.2f", (dish?.price ?: 0.0) * quantity)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else if (dish != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // Imagen principal
                    item {
                        AsyncImage(
                            model = dish!!.imageUrl,
                            contentDescription = dish!!.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Info principal
                    item {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // Restaurante
                            Text(
                                text = dish!!.restaurantName,
                                fontSize = 14.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(Modifier.height(8.dp))

                            // Nombre del platillo
                            Text(
                                text = dish!!.name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 34.sp
                            )

                            Spacer(Modifier.height(8.dp))

                            // Categoría
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1A1F2E)
                            ) {
                                Text(
                                    text = dish!!.category,
                                    fontSize = 12.sp,
                                    color = Color(0xFFB7BDC9),
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            // Precio
                            Text(
                                text = "$${String.format("%.2f", dish!!.price)}",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }

                    // Descripción
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Descripción",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = dish!!.description,
                                fontSize = 14.sp,
                                color = Color(0xFFB7BDC9),
                                lineHeight = 22.sp
                            )
                        }
                    }

                    // Tags dietéticos
                    if (dish!!.dietaryTags.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                            ) {
                                Text(
                                    text = "Características",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(12.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(dish!!.dietaryTags) { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = Color(0xFF1A2F2A),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                Color(0xFF4CAF50)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(
                                                    horizontal = 12.dp,
                                                    vertical = 8.dp
                                                ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(Modifier.width(6.dp))
                                                Text(
                                                    text = tag,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF4CAF50),
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Ingredientes
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
                        ) {
                            Text(
                                text = "Ingredientes (${dish!!.ingredients.size})",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(12.dp))
                            dish!!.ingredients.chunked(2).forEach { rowItems ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowItems.forEach { ingredient ->
                                        Row(
                                            modifier = Modifier.weight(1f),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Info,
                                                contentDescription = null,
                                                tint = Color(0xFF4CAF50),
                                                modifier = Modifier.size(8.dp)
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                text = ingredient,
                                                fontSize = 14.sp,
                                                color = Color(0xFFB7BDC9)
                                            )
                                        }
                                    }
                                    if (rowItems.size == 1) {
                                        Spacer(Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Platillo no encontrado",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }

            // Snackbar
            if (showAddedSnackbar) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showAddedSnackbar = false
                }
                Snackbar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = Color(0xFF4CAF50)
                ) {
                    Text("✓ Agregado al carrito", color = Color.White)
                }
            }
        }
    }
}

