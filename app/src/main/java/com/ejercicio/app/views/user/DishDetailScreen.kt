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
import androidx.compose.ui.draw.clip
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
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0F1219).copy(alpha = 0.8f)
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = Color.White
                            )
                        }
                    }
                },
                actions = {
                    Surface(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp),
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF0F1219).copy(alpha = 0.8f)
                    ) {
                        BadgedBox(
                            badge = {
                                if (cartVM.itemCount > 0) {
                                    Badge(
                                        containerColor = Color(0xFF4CAF50),
                                        modifier = Modifier.offset(x = (-4).dp, y = 4.dp)
                                    ) {
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
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            // Bottom Bar SIEMPRE VISIBLE
            if (dish != null) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    color = Color(0xFF0F1219),
                    shadowElevation = 16.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        // Selector de cantidad
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cantidad
                            Column {
                                Text(
                                    "Cantidad",
                                    fontSize = 12.sp,
                                    color = Color(0xFF8B92A1)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    // Botón menos
                                    IconButton(
                                        onClick = { if (quantity > 1) quantity-- },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                if (quantity > 1) Color(0xFF1A2F2A) else Color(0xFF1A1F2E),
                                                RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Menos",
                                            tint = if (quantity > 1) Color(0xFF4CAF50) else Color(0xFF444444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    // Cantidad
                                    Text(
                                        text = "$quantity",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.widthIn(min = 40.dp)
                                    )

                                    // Botón más
                                    IconButton(
                                        onClick = { if (quantity < 10) quantity++ },
                                        modifier = Modifier
                                            .size(44.dp)
                                            .background(
                                                if (quantity < 10) Color(0xFF1A2F2A) else Color(0xFF1A1F2E),
                                                RoundedCornerShape(12.dp)
                                            )
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Más",
                                            tint = if (quantity < 10) Color(0xFF4CAF50) else Color(0xFF444444),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }

                            // Precio total
                            Column(
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    "Total",
                                    fontSize = 12.sp,
                                    color = Color(0xFF8B92A1)
                                )
                                Text(
                                    text = "$${String.format("%.2f", (dish?.price ?: 0.0) * quantity)}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Botón agregar al carrito
                        Button(
                            onClick = {
                                dish?.let {
                                    cartVM.addToCart(it, quantity)
                                    showAddedSnackbar = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 4.dp,
                                pressedElevation = 8.dp
                            )
                        ) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Agregar al carrito",
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
                    contentPadding = PaddingValues(bottom = 0.dp)
                ) {
                    // Imagen principal con mejor diseño
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(320.dp)
                        ) {
                            AsyncImage(
                                model = dish!!.imageUrl,
                                contentDescription = dish!!.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Gradiente en la parte inferior de la imagen
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color.Transparent,
                                                Color(0xFF05060A)
                                            )
                                        )
                                    )
                            )
                        }
                    }

                    // Info principal
                    item {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            // Restaurante con icono
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    text = dish!!.restaurantName,
                                    fontSize = 14.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Nombre del platillo
                            Text(
                                text = dish!!.name,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 38.sp
                            )

                            Spacer(Modifier.height(12.dp))

                            // Categoría y precio
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = Color(0xFF1A1F2E)
                                ) {
                                    Text(
                                        text = dish!!.category,
                                        fontSize = 13.sp,
                                        color = Color(0xFFB7BDC9),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                    )
                                }

                                Text(
                                    text = "$${String.format("%.2f", dish!!.price)}",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    }

                    // Descripción
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Descripción",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = dish!!.description,
                                fontSize = 15.sp,
                                color = Color(0xFFB7BDC9),
                                lineHeight = 24.sp
                            )
                        }
                    }

                    // Tags dietéticos
                    if (dish!!.dietaryTags.isNotEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                            ) {
                                Text(
                                    text = "Características",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(Modifier.height(16.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    items(dish!!.dietaryTags) { tag ->
                                        Surface(
                                            shape = RoundedCornerShape(14.dp),
                                            color = Color(0xFF1A2F2A),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.5.dp,
                                                Color(0xFF4CAF50)
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(
                                                    horizontal = 14.dp,
                                                    vertical = 10.dp
                                                ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = Color(0xFF4CAF50),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(
                                                    text = tag,
                                                    fontSize = 14.sp,
                                                    color = Color(0xFF4CAF50),
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Ingredientes con mejor diseño
                    item {
                        Column(
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AccountBox,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Ingredientes",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = " (${dish!!.ingredients.size})",
                                    fontSize = 16.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }

                            Spacer(Modifier.height(16.dp))

                            dish!!.ingredients.forEach { ingredient ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier.size(8.dp),
                                        shape = RoundedCornerShape(4.dp),
                                        color = Color(0xFF4CAF50)
                                    ) {}

                                    Spacer(Modifier.width(12.dp))

                                    Text(
                                        text = ingredient,
                                        fontSize = 15.sp,
                                        color = Color(0xFFB7BDC9)
                                    )
                                }
                            }
                        }
                    }

                    // Espaciado final para el bottom bar
                    item {
                        Spacer(Modifier.height(20.dp))
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Platillo no encontrado",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Snackbar personalizado
            if (showAddedSnackbar) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2500)
                    showAddedSnackbar = false
                }

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp)
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF4CAF50),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "Agregado al carrito",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}