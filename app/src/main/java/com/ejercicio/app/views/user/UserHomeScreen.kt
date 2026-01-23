package com.ejercicio.app.views.user

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ejercicio.app.model.DishModel
import com.ejercicio.app.navigation.AppScreen
import com.ejercicio.app.utils.DietaryTags
import com.ejercicio.app.utils.DishCategories
import com.ejercicio.app.viewModel.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserHomeScreen(
    navController: NavHostController,
    loginVM: LoginViewModel
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

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
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Cargar platillos
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

    // Obtener info del usuario
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userName = currentUser?.displayName ?: "Usuario"
    val userEmail = currentUser?.email ?: ""
    val userPhoto = currentUser?.photoUrl?.toString()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModernDrawerContent(
                userName = userName,
                userEmail = userEmail,
                userPhoto = userPhoto,
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    // TODO: Navegar a perfil
                },
                onOrdersClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate(AppScreen.OrderHistory.route)
                },
                onAddressesClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("user_addresses")
                },
                onFavoritesClick = {
                    scope.launch { drawerState.close() }
                    // TODO: Implementar favoritos
                },
                onSettingsClick = {
                    scope.launch { drawerState.close() }
                    // TODO: Implementar configuración
                },
                onHelpClick = {
                    scope.launch { drawerState.close() }
                    // TODO: Implementar ayuda
                },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    showLogoutDialog = true
                },
                onClose = {
                    scope.launch { drawerState.close() }
                }
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Restaurant,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(28.dp)
                                )
                                Text(
                                    "Nutrideli",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            val activeFilters =
                                (if (selectedCategory != null) 1 else 0) +
                                        selectedTags.size

                            if (activeFilters > 0) {
                                Text(
                                    "$activeFilters ${if (activeFilters == 1) "filtro" else "filtros"} activo${if (activeFilters == 1) "" else "s"}",
                                    fontSize = 12.sp,
                                    color = Color(0xFF4CAF50)
                                )
                            }
                        }
                    },
                    actions = {
                        // Filter button
                        BadgedBox(
                            badge = {
                                val count = (if (selectedCategory != null) 1 else 0) + selectedTags.size
                                if (count > 0) {
                                    Badge(
                                        containerColor = Color(0xFF4CAF50)
                                    ) {
                                        Text(
                                            "$count",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        ) {
                            IconButton(onClick = { showFilterDialog = true }) {
                                Icon(
                                    Icons.Filled.Tune,
                                    contentDescription = "Filtros",
                                    tint = Color.White
                                )
                            }
                        }

                        // Menu button
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                Icons.Filled.Menu,
                                contentDescription = "Menú",
                                tint = Color.White
                            )
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
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF4CAF50)
                        )
                    }
                } else if (filteredDishes.isEmpty()) {
                    EmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredDishes) { dish ->
                            DishCard(
                                dish = dish,
                                onClick = {
                                    navController.navigate("${AppScreen.DishDetail.route}/${dish.dishId}")
                                }
                            )
                        }

                        item {
                            Spacer(Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            selectedCategory = selectedCategory,
            selectedTags = selectedTags,
            onDismiss = { showFilterDialog = false },
            onApply = { category, tags ->
                selectedCategory = category
                selectedTags = tags
                showFilterDialog = false
            },
            onClear = {
                selectedCategory = null
                selectedTags = emptySet()
                showFilterDialog = false
            }
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onConfirm = {
                loginVM.signOut()
                navController.navigate(AppScreen.UserLogin.route) {
                    popUpTo(0) { inclusive = true }
                }
            },
            onDismiss = { showLogoutDialog = false }
        )
    }
}

@Composable
private fun ModernDrawerContent(
    userName: String,
    userEmail: String,
    userPhoto: String?,
    onProfileClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onHelpClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onClose: () -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.width(320.dp),
        drawerContainerColor = Color(0xFF0F1219),
        drawerContentColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF1A2F2A),
                            Color(0xFF0F1219),
                            Color(0xFF0F1219)
                        )
                    )
                )
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF1A2F2A))
                    .padding(24.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // User Avatar
                        Surface(
                            modifier = Modifier.size(70.dp),
                            shape = CircleShape,
                            color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                            border = androidx.compose.foundation.BorderStroke(
                                3.dp,
                                Color(0xFF4CAF50)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (userPhoto != null) {
                                    AsyncImage(
                                        model = userPhoto,
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(35.dp)
                                    )
                                }
                            }
                        }

                        // Close button
                        IconButton(onClick = onClose) {
                            Icon(
                                Icons.Filled.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // User Info
                    Text(
                        text = userName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = userEmail,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(16.dp))

                    // Edit Profile Button
                    Surface(
                        onClick = onProfileClick,
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFF4CAF50).copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.Edit,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Editar perfil",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Menu Items
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                DrawerMenuItem(
                    icon = Icons.Filled.Receipt,
                    title = "Mis pedidos",
                    subtitle = "Historial y seguimiento",
                    onClick = onOrdersClick
                )

                DrawerMenuItem(
                    icon = Icons.Filled.Home,
                    title = "Mis direcciones",
                    subtitle = "Administrar ubicaciones",
                    onClick = onAddressesClick
                )

                DrawerMenuItem(
                    icon = Icons.Filled.FavoriteBorder,
                    title = "Favoritos",
                    subtitle = "Platillos guardados",
                    onClick = onFavoritesClick
                )

                Divider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color(0xFF232838)
                )

                DrawerMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "Configuración",
                    subtitle = "Preferencias y ajustes",
                    onClick = onSettingsClick
                )

                DrawerMenuItem(
                    icon = Icons.Outlined.Help,
                    title = "Ayuda y soporte",
                    subtitle = "¿Necesitas asistencia?",
                    onClick = onHelpClick
                )
            }

            // Logout Button
            Surface(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFE53935).copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFFE53935).copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = null,
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Cerrar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE53935)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF232838),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF8B92A1)
                )
            }

            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = Color(0xFF8B92A1),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// Los demás componentes permanecen igual (DishCard, EmptyState, FilterDialog, LogoutDialog)
// ... (copiar del archivo original)

@Composable
private fun DishCard(
    dish: DishModel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
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
            // Image
            AsyncImage(
                model = dish.imageUrl,
                contentDescription = dish.name,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp)),
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Storefront,
                        contentDescription = null,
                        tint = Color(0xFF8B92A1),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = dish.restaurantName,
                        fontSize = 12.sp,
                        color = Color(0xFF8B92A1)
                    )
                }

                Spacer(Modifier.height(8.dp))

                // Tags
                if (dish.dietaryTags.isNotEmpty()) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(dish.dietaryTags.take(2)) { tag ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF1A2F2A)
                            ) {
                                Text(
                                    text = tag,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    fontSize = 10.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "$${dish.price}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.SearchOff,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(80.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "No se encontraron platillos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Intenta ajustar los filtros",
                fontSize = 14.sp,
                color = Color(0xFF8B92A1)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FilterDialog(
    selectedCategory: String?,
    selectedTags: Set<String>,
    onDismiss: () -> Unit,
    onApply: (String?, Set<String>) -> Unit,
    onClear: () -> Unit
) {
    var tempCategory by remember { mutableStateOf(selectedCategory) }
    var tempTags by remember { mutableStateOf(selectedTags) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFF0F1219)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF1A2F2A)
                        ) {
                            Icon(
                                Icons.Filled.Tune,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(24.dp)
                            )
                        }

                        Text(
                            "Filtros",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Cerrar",
                            tint = Color(0xFF8B92A1)
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        "Categoría",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DishCategories.categories.forEach { category ->
                            CategoryChip(
                                text = category,
                                icon = getCategoryIcon(category),
                                selected = tempCategory == category,
                                onClick = {
                                    tempCategory = if (tempCategory == category) null else category
                                }
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        "Preferencias dietéticas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    androidx.compose.foundation.layout.FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DietaryTags.tags.forEach { tag ->
                            DietaryChip(
                                text = tag,
                                icon = getDietaryIcon(tag),
                                selected = tag in tempTags,
                                onClick = {
                                    tempTags = if (tag in tempTags) {
                                        tempTags - tag
                                    } else {
                                        tempTags + tag
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                val activeFilters = (if (tempCategory != null) 1 else 0) + tempTags.size
                if (activeFilters > 0) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF1A2F2A)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Filled.FilterAlt,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    "$activeFilters ${if (activeFilters == 1) "filtro activo" else "filtros activos"}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onClear,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFFE53935).copy(alpha = 0.5f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFE53935)
                        )
                    ) {
                        Icon(
                            Icons.Filled.Clear,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Limpiar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { onApply(tempCategory, tempTags) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        )
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Aplicar",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (selected) Color(0xFF4CAF50) else Color(0xFF1A1F2E),
        border = if (selected) null else BorderStroke(1.dp, Color(0xFF2A3142))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) Color.White else Color(0xFF8B92A1),
                modifier = Modifier.size(20.dp)
            )
            Text(
                text,
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) Color.White else Color(0xFFB7BDC9)
            )

            if (selected) {
                Icon(
                    Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun DietaryChip(
    text: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF2E7D32) else Color(0xFF1A1F2E),
        border = if (selected)
            BorderStroke(2.dp, Color(0xFF4CAF50))
        else
            BorderStroke(1.dp, Color(0xFF2A3142))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (selected) Color(0xFF81C784) else Color(0xFF8B92A1),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = if (selected) Color.White else Color(0xFFB7BDC9)
            )
        }
    }
}

@Composable
private fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Outlined.Logout,
                contentDescription = null,
                tint = Color(0xFFE53935),
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                "Cerrar sesión",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text("¿Estás seguro de que deseas cerrar sesión?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE53935)
                )
            ) {
                Text("Cerrar sesión")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = Color(0xFF0F1219),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

private fun getCategoryIcon(category: String): ImageVector {
    return when (category.lowercase()) {
        "desayuno" -> Icons.Filled.BrunchDining
        "comida", "almuerzo" -> Icons.Filled.LunchDining
        "cena" -> Icons.Filled.DinnerDining
        "snack", "botana" -> Icons.Filled.Fastfood
        "bebida" -> Icons.Filled.LocalCafe
        "postre" -> Icons.Filled.Cake
        else -> Icons.Filled.Restaurant
    }
}

private fun getDietaryIcon(tag: String): ImageVector {
    return when (tag.lowercase()) {
        "vegano", "vegan" -> Icons.Filled.Eco
        "vegetariano" -> Icons.Filled.Spa
        "sin gluten", "gluten free" -> Icons.Filled.HealthAndSafety
        "keto", "cetogénico" -> Icons.Filled.Favorite
        "bajo en calorías", "light" -> Icons.Filled.LocalFireDepartment
        "orgánico", "organic" -> Icons.Filled.LocalFlorist
        "sin lactosa" -> Icons.Filled.NoFood
        "alto en proteína" -> Icons.Filled.FitnessCenter
        else -> Icons.Filled.Restaurant
    }
}