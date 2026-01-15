package com.ejercicio.app.views.restaurant

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ejercicio.app.model.DishModel
import com.ejercicio.app.navigation.AppScreen
import com.ejercicio.app.viewModel.DishViewModel
import com.ejercicio.app.viewModel.RestaurantAuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDashboardScreen(
    navController: NavHostController,
    restaurantVM: RestaurantAuthViewModel,
    dishVM: DishViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    var showDeleteDialog by remember { mutableStateOf(false) }
    var dishToDelete by remember { mutableStateOf<DishModel?>(null) }
    var showMenuDropdown by remember { mutableStateOf(false) }

    // Cargar platillos al iniciar
    LaunchedEffect(Unit) {
        dishVM.getRestaurantDishes()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Restaurant, // ← ÍCONO MEJORADO
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                "Mis Platillos",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                "${dishVM.dishes.size} ${if (dishVM.dishes.size == 1) "platillo" else "platillos"}",
                                fontSize = 12.sp,
                                color = Color(0xFFB7BDC9)
                            )
                        }
                    }
                },
                actions = {
                    // Menú desplegable
                    Box {
                        IconButton(onClick = { showMenuDropdown = true }) {
                            Icon(
                                Icons.Filled.MoreVert,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
                        }

                        DropdownMenu(
                            expanded = showMenuDropdown,
                            onDismissRequest = { showMenuDropdown = false },
                            modifier = Modifier
                                .background(Color(0xFF0F1219))
                                .width(200.dp)
                        ) {
                            // Ver pedidos
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Receipt, // ← ÍCONO MEJORADO
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50)
                                        )
                                        Text("Pedidos", color = Color.White)
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    navController.navigate(AppScreen.RestaurantOrders.route)
                                }
                            )

                            // Perfil
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.Store, // ← ÍCONO MEJORADO
                                            contentDescription = null,
                                            tint = Color(0xFF2196F3)
                                        )
                                        Text("Mi Perfil", color = Color.White)
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    navController.navigate(AppScreen.RestaurantProfile.route)
                                }
                            )

                            Divider(color = Color(0xFF232838))

                            // Cerrar sesión
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Outlined.Logout, // ← ÍCONO MEJORADO
                                            contentDescription = null,
                                            tint = Color(0xFFE53935)
                                        )
                                        Text("Cerrar sesión", color = Color(0xFFE53935))
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    restaurantVM.logoutRestaurant()
                                    navController.navigate(AppScreen.Blank.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    dishVM.clearForm()
                    navController.navigate(AppScreen.AddDish.route)
                },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White,
                icon = {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                },
                text = {
                    Text(
                        "Agregar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
        ) {
            // Loading state
            if (dishVM.isLoading && dishVM.dishes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "Cargando platillos...",
                            fontSize = 14.sp,
                            color = Color(0xFFB7BDC9)
                        )
                    }
                }
            }
            // Empty state MEJORADO
            else if (dishVM.dishes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Ícono ilustrativo más grande
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = RoundedCornerShape(60.dp),
                            color = Color(0xFF1A2F2A)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.RestaurantMenu, // ← ÍCONO MEJORADO
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        // Título principal
                        Text(
                            "¡Empieza tu menú!",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        // Descripción
                        Text(
                            "Aún no tienes platillos registrados.\nAgrega tu primer platillo para comenzar a recibir pedidos.",
                            fontSize = 15.sp,
                            color = Color(0xFFB7BDC9),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(32.dp))

                        // Tarjeta con pasos
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0F1219)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    "Pasos para agregar un platillo:",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF4CAF50)
                                )

                                StepItem(
                                    number = "1",
                                    icon = Icons.Filled.AddPhotoAlternate,
                                    text = "Sube una foto atractiva"
                                )

                                StepItem(
                                    number = "2",
                                    icon = Icons.Filled.Edit,
                                    text = "Agrega nombre, precio y descripción"
                                )

                                StepItem(
                                    number = "3",
                                    icon = Icons.Filled.Category,
                                    text = "Selecciona la categoría"
                                )

                                StepItem(
                                    number = "4",
                                    icon = Icons.Filled.CheckCircle,
                                    text = "¡Listo! Tu platillo estará visible"
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Indicación del FAB
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Toca el botón ",
                                fontSize = 14.sp,
                                color = Color(0xFF8B92A1)
                            )
                            Surface(
                                modifier = Modifier.size(28.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = Color(0xFF4CAF50).copy(alpha = 0.2f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                " para comenzar",
                                fontSize = 14.sp,
                                color = Color(0xFF8B92A1)
                            )
                        }
                    }
                }
            }
            // Lista de platillos
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(dishVM.dishes) { dish ->
                        ImprovedDishCard(
                            dish = dish,
                            onEditClick = {
                                navController.navigate(AppScreen.EditDish.createRoute(dish.dishId))
                            },
                            onDeleteClick = {
                                dishToDelete = dish
                                showDeleteDialog = true
                            }
                        )
                    }

                    // Espaciado final para el FAB
                    item {
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }

            // Dialog de confirmación de eliminación
            if (showDeleteDialog && dishToDelete != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    icon = {
                        Icon(
                            Icons.Outlined.Delete, // ← ÍCONO MEJORADO
                            contentDescription = null,
                            tint = Color(0xFFE53935),
                            modifier = Modifier.size(48.dp)
                        )
                    },
                    title = { Text("Eliminar platillo", fontWeight = FontWeight.Bold) },
                    text = { Text("¿Estás seguro de eliminar \"${dishToDelete?.name}\"? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                dishToDelete?.let { dish ->
                                    dishVM.deleteDish(dish.dishId) {
                                        dishVM.getRestaurantDishes()
                                    }
                                }
                                showDeleteDialog = false
                                dishToDelete = null
                            }
                        ) {
                            Text("Eliminar", color = Color(0xFFE53935))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    },
                    containerColor = Color(0xFF0F1219),
                    titleContentColor = Color.White,
                    textContentColor = Color(0xFFB7BDC9)
                )
            }
        }
    }
}

@Composable
private fun StepItem(
    number: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Número del paso
        Surface(
            modifier = Modifier.size(32.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF1A2F2A)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    number,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }

        // Ícono
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(20.dp)
        )

        // Texto
        Text(
            text,
            fontSize = 14.sp,
            color = Color(0xFFB7BDC9)
        )
    }
}

@Composable
private fun ImprovedDishCard(
    dish: DishModel,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1219)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Imagen
            AsyncImage(
                model = dish.imageUrl,
                contentDescription = dish.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1A1F2E)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.width(12.dp))

            // Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dish.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Filled.Category,
                        contentDescription = null,
                        tint = Color(0xFF8B92A1),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = dish.category,
                        fontSize = 12.sp,
                        color = Color(0xFF8B92A1)
                    )
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "$${String.format("%.2f", dish.price)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )

                Spacer(Modifier.height(8.dp))

                // Botones
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Botón editar
                    Surface(
                        onClick = onEditClick,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF1A2F2A),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Edit, // ← ÍCONO MEJORADO
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Editar",
                                fontSize = 13.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Botón eliminar
                    Surface(
                        onClick = onDeleteClick,
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF2F1A1A),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Delete, // ← ÍCONO MEJORADO
                                contentDescription = null,
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Eliminar",
                                fontSize = 13.sp,
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}