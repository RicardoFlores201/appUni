package com.ejercicio.app.views.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ejercicio.app.model.DishModel
import com.ejercicio.app.navigation.AppScreen
import com.ejercicio.app.utils.DietaryTags
import com.ejercicio.app.utils.DishCategories
import com.ejercicio.app.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
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

    var dishes by remember { mutableStateOf<List<DishModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedTags by remember { mutableStateOf<Set<String>>(emptySet()) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var showMenuDropdown by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Cargar todos los platillos
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("dishes")
                .get()
                .await()

            dishes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(DishModel::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false
    }

    // Filtrar platillos
    val filteredDishes = dishes.filter { dish ->
        val categoryMatch = selectedCategory == null || dish.category == selectedCategory
        val tagsMatch = selectedTags.isEmpty() || selectedTags.any { it in dish.dietaryTags }
        categoryMatch && tagsMatch
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "🥗 Nutrideli",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${filteredDishes.size} platillos disponibles",
                            fontSize = 12.sp,
                            color = Color(0xFFB7BDC9)
                        )
                    }
                },
                actions = {
                    // Botón de filtros
                    IconButton(onClick = { showFilterDialog = true }) {
                        BadgedBox(
                            badge = {
                                if (selectedCategory != null || selectedTags.isNotEmpty()) {
                                    Badge(containerColor = Color(0xFF4CAF50)) {
                                        Text(
                                            "${(if (selectedCategory != null) 1 else 0) + selectedTags.size}",
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = "Filtros",
                                tint = Color(0xFF4CAF50)
                            )
                        }
                    }

                    // Botón de menú desplegable
                    Box {
                        IconButton(onClick = { showMenuDropdown = true }) {
                            Icon(
                                Icons.Default.MoreVert,
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
                            // Perfil del usuario
                            val currentUser = FirebaseAuth.getInstance().currentUser
                            if (currentUser != null) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = currentUser.displayName ?: "Usuario",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = currentUser.email ?: "",
                                        fontSize = 12.sp,
                                        color = Color(0xFFB7BDC9),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                                Divider(color = Color(0xFF232838))
                            }

                            // Mis pedidos
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.KeyboardArrowUp,
                                            contentDescription = null,
                                            tint = Color(0xFF4CAF50)
                                        )
                                        Text("Mis pedidos", color = Color.White)
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    navController.navigate(AppScreen.OrderHistory.route)
                                }
                            )

                            // Favoritos
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Favorite,
                                            contentDescription = null,
                                            tint = Color(0xFFE91E63)
                                        )
                                        Text("Favoritos", color = Color.White)
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    // TODO: Implementar favoritos
                                }
                            )

                            // Direcciones
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.LocationOn,
                                            contentDescription = null,
                                            tint = Color(0xFF2196F3)
                                        )
                                        Text("Mis direcciones", color = Color.White)
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    // TODO: Implementar direcciones
                                }
                            )

                            Divider(color = Color(0xFF232838))

                            // Configuración
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = null,
                                            tint = Color(0xFF9E9E9E)
                                        )
                                        Text("Configuración", color = Color.White)
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    // TODO: Implementar configuración
                                }
                            )

                            // Ayuda
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Call,
                                            contentDescription = null,
                                            tint = Color(0xFF9E9E9E)
                                        )
                                        Text("Ayuda", color = Color.White)
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    // TODO: Implementar ayuda
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
                                            Icons.Default.ExitToApp,
                                            contentDescription = null,
                                            tint = Color(0xFFE53935)
                                        )
                                        Text("Cerrar sesión", color = Color(0xFFE53935))
                                    }
                                },
                                onClick = {
                                    showMenuDropdown = false
                                    showLogoutDialog = true
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Filtros activos
                if (selectedCategory != null || selectedTags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        if (selectedCategory != null) {
                            item {
                                FilterChip(
                                    selected = true,
                                    onClick = { selectedCategory = null },
                                    label = { Text(selectedCategory!!, fontSize = 12.sp) },
                                    trailingIcon = {
                                        Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF4CAF50)
                                    )
                                )
                            }
                        }
                        items(selectedTags.toList()) { tag ->
                            FilterChip(
                                selected = true,
                                onClick = { selectedTags = selectedTags - tag },
                                label = { Text(tag, fontSize = 12.sp) },
                                trailingIcon = {
                                    Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF4CAF50)
                                )
                            )
                        }
                    }
                }

                // Loading state
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFF4CAF50))
                    }
                }
                // Empty state
                else if (filteredDishes.isEmpty() && !isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBox,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(80.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                if (selectedCategory != null || selectedTags.isNotEmpty())
                                    "No hay platillos con estos filtros"
                                else
                                    "No hay platillos disponibles",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                if (selectedCategory != null || selectedTags.isNotEmpty())
                                    "Intenta cambiar los filtros"
                                else
                                    "Vuelve más tarde",
                                fontSize = 14.sp,
                                color = Color(0xFFB7BDC9)
                            )
                        }
                    }
                }
                // Lista de platillos
                else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        items(filteredDishes) { dish ->
                            ImprovedDishCard(
                                dish = dish,
                                onClick = {
                                    navController.navigate(AppScreen.DishDetail.createRoute(dish.dishId))
                                }
                            )
                        }
                    }
                }
            }

            // Filter Dialog
            if (showFilterDialog) {
                FilterDialog(
                    selectedCategory = selectedCategory,
                    selectedTags = selectedTags,
                    onCategorySelect = { selectedCategory = it },
                    onTagToggle = { tag ->
                        selectedTags = if (selectedTags.contains(tag)) {
                            selectedTags - tag
                        } else {
                            selectedTags + tag
                        }
                    },
                    onClearAll = {
                        selectedCategory = null
                        selectedTags = emptySet()
                    },
                    onDismiss = { showFilterDialog = false }
                )
            }

            // Logout Confirmation Dialog
            if (showLogoutDialog) {
                AlertDialog(
                    onDismissRequest = { showLogoutDialog = false },
                    title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
                    text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                navController.navigate(AppScreen.Blank.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Cerrar sesión", color = Color(0xFFE53935))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showLogoutDialog = false }) {
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
private fun ImprovedDishCard(
    dish: DishModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1219)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = dish.imageUrl,
                    contentDescription = dish.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Badge de categoría
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1A1F2E).copy(alpha = 0.9f)
                ) {
                    Text(
                        text = dish.category,
                        fontSize = 11.sp,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Info del platillo
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Nombre del platillo
                Text(
                    text = dish.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                // Restaurante con mejor contraste
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = dish.restaurantName,
                        fontSize = 14.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Tags dietéticos mejorados
                if (dish.dietaryTags.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(dish.dietaryTags.take(3)) { tag ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1A2F2A),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFF4CAF50).copy(alpha = 0.3f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    Text(
                                        text = tag,
                                        fontSize = 11.sp,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        if (dish.dietaryTags.size > 3) {
                            item {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1A2F2A)
                                ) {
                                    Text(
                                        text = "+${dish.dietaryTags.size - 3}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF4CAF50),
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Precio destacado
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$${String.format("%.2f", dish.price)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )

                    // Botón de ver más
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF4CAF50)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Ver más",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterDialog(
    selectedCategory: String?,
    selectedTags: Set<String>,
    onCategorySelect: (String?) -> Unit,
    onTagToggle: (String) -> Unit,
    onClearAll: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtros", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp)
            ) {
                // Categorías
                item {
                    Text(
                        "Categoría",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(DishCategories.categories) { category ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onCategorySelect(if (selectedCategory == category) null else category)
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedCategory == category,
                            onClick = {
                                onCategorySelect(if (selectedCategory == category) null else category)
                            }
                        )
                        Text(category, modifier = Modifier.padding(start = 8.dp))
                    }
                }

                // Tags dietéticos
                item {
                    Text(
                        "Tags dietéticos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 0.dp)
                    )
                }
                items(DietaryTags.tags) { tag ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTagToggle(tag) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedTags.contains(tag),
                            onCheckedChange = { onTagToggle(tag) }
                        )
                        Text(tag, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Aplicar")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onClearAll()
                onDismiss()
            }) {
                Text("Limpiar")
            }
        },
        containerColor = Color(0xFF0F1219),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}