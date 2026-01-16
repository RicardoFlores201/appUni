package com.ejercicio.app.views.restaurant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ejercicio.app.components.Alert
import com.ejercicio.app.utils.DietaryTags
import com.ejercicio.app.utils.DishCategories
import com.ejercicio.app.viewModel.DishViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDishScreen(
    navController: NavHostController,
    dishVM: DishViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    var showCategoryDialog by remember { mutableStateOf(false) }
    var showIngredientsDialog by remember { mutableStateOf(false) }
    var showTagsDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        dishVM.onImageUriChange(uri)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Agregar platillo",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 20.dp)
        ) {
            // Imagen del platillo
            Text(
                "Imagen del platillo *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { imagePickerLauncher.launch("image/*") },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2E),
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (dishVM.imageUri != null) Color(0xFF4CAF50) else Color(0xFF232838)
                )
            ) {
                if (dishVM.imageUri != null) {
                    AsyncImage(
                        model = dishVM.imageUri,
                        contentDescription = "Imagen seleccionada",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Outlined.AddPhotoAlternate,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Toca para seleccionar imagen",
                            fontSize = 14.sp,
                            color = Color(0xFF8B92A1)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Nombre del platillo
            Text(
                "Nombre del platillo *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = dishVM.name,
                onValueChange = { dishVM.onNameChange(it) },
                placeholder = { Text("Ej: Hamburguesa vegana", color = Color(0xFF666666)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF232838),
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedContainerColor = Color(0xFF1A1F2E),
                    focusedContainerColor = Color(0xFF1A1F2E),
                    cursorColor = Color(0xFF4CAF50),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            // Descripción
            Text(
                "Descripción *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = dishVM.description,
                onValueChange = { dishVM.onDescriptionChange(it) },
                placeholder = { Text("Describe el platillo...", color = Color(0xFF666666)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF232838),
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedContainerColor = Color(0xFF1A1F2E),
                    focusedContainerColor = Color(0xFF1A1F2E),
                    cursorColor = Color(0xFF4CAF50),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                minLines = 3,
                maxLines = 5
            )

            Spacer(Modifier.height(20.dp))

            // Categoría
            Text(
                "Categoría *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                onClick = { showCategoryDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2E),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (dishVM.category.isNotBlank()) Color(0xFF4CAF50) else Color(0xFF232838)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (dishVM.category.isBlank()) "Selecciona categoría" else dishVM.category,
                        fontSize = 16.sp,
                        color = if (dishVM.category.isBlank()) Color(0xFF666666) else Color.White
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFF8B92A1)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Precio
            Text(
                "Precio *",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = dishVM.price,
                onValueChange = { dishVM.onPriceChange(it) },
                placeholder = { Text("0.00", color = Color(0xFF666666)) },
                leadingIcon = {
                    Text(
                        "$",
                        fontSize = 16.sp,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFF232838),
                    focusedBorderColor = Color(0xFF4CAF50),
                    unfocusedContainerColor = Color(0xFF1A1F2E),
                    focusedContainerColor = Color(0xFF1A1F2E),
                    cursorColor = Color(0xFF4CAF50),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            // Ingredientes
            Text(
                "Ingredientes * (${dishVM.selectedIngredients.size} seleccionados)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                onClick = { showIngredientsDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2E),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (dishVM.selectedIngredients.isNotEmpty()) Color(0xFF4CAF50) else Color(0xFF232838)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Restaurant,
                        contentDescription = null,
                        tint = if (dishVM.selectedIngredients.isEmpty()) Color(0xFF8B92A1) else Color(0xFF4CAF50)
                    )
                    Text(
                        text = if (dishVM.selectedIngredients.isEmpty())
                            "Seleccionar ingredientes"
                        else
                            dishVM.selectedIngredients.take(3).joinToString(", ") +
                                    if (dishVM.selectedIngredients.size > 3) "..." else "",
                        fontSize = 16.sp,
                        color = if (dishVM.selectedIngredients.isEmpty()) Color(0xFF666666) else Color.White
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Tags dietéticos
            Text(
                "Tags dietéticos (${dishVM.selectedDietaryTags.size} seleccionados)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                onClick = { showTagsDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFF1A1F2E),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (dishVM.selectedDietaryTags.isNotEmpty()) Color(0xFF4CAF50) else Color(0xFF232838)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.LocalOffer,
                        contentDescription = null,
                        tint = if (dishVM.selectedDietaryTags.isEmpty()) Color(0xFF8B92A1) else Color(0xFF4CAF50)
                    )
                    Text(
                        text = if (dishVM.selectedDietaryTags.isEmpty())
                            "Seleccionar tags"
                        else
                            dishVM.selectedDietaryTags.take(3).joinToString(", ") +
                                    if (dishVM.selectedDietaryTags.size > 3) "..." else "",
                        fontSize = 16.sp,
                        color = if (dishVM.selectedDietaryTags.isEmpty()) Color(0xFF666666) else Color.White
                    )
                }
            }

            Spacer(Modifier.height(80.dp))
        }

        // Botón flotante en la parte inferior
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1219))
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(20.dp)
            ) {
                Button(
                    onClick = {
                        dishVM.createDish {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = dishVM.name.isNotBlank() &&
                            dishVM.description.isNotBlank() &&
                            dishVM.price.isNotBlank() &&
                            dishVM.category.isNotBlank() &&
                            dishVM.selectedIngredients.isNotEmpty() &&
                            !dishVM.isLoading
                ) {
                    if (dishVM.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Crear platillo",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Diálogos
        if (showCategoryDialog) {
            CategoryDialog(
                onDismiss = { showCategoryDialog = false },
                onSelect = {
                    dishVM.onCategoryChange(it)
                    showCategoryDialog = false
                }
            )
        }

        if (showIngredientsDialog) {
            IngredientsDialog(
                selectedIngredients = dishVM.selectedIngredients.toSet(),
                onDismiss = { showIngredientsDialog = false },
                onToggle = { dishVM.toggleIngredient(it) }
            )
        }

        if (showTagsDialog) {
            TagsDialog(
                selectedTags = dishVM.selectedDietaryTags.toSet(),
                onDismiss = { showTagsDialog = false },
                onToggle = { dishVM.toggleDietaryTag(it) }
            )
        }

        // Alertas
        if (dishVM.showAlert) {
            Alert(
                title = "Alerta",
                message = dishVM.alertMessage,
                confirmText = "Aceptar",
                onConfirmClick = { dishVM.closeAlert() }
            ) {}
        }
    }
}

@Composable
private fun CategoryDialog(
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Outlined.Category,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("Seleccionar categoría", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(DishCategories.categories.size) { index ->
                    val category = DishCategories.categories[index]
                    Surface(
                        onClick = { onSelect(category) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1A1F2E)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                getCategoryIcon(category),
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                category,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        },
        containerColor = Color(0xFF0F1219),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
private fun IngredientsDialog(
    selectedIngredients: Set<String>,
    onDismiss: () -> Unit,
    onToggle: (String) -> Unit
) {
    val commonIngredients = listOf(
        "Lechuga", "Tomate", "Cebolla", "Pepino", "Aguacate",
        "Pollo", "Carne", "Pescado", "Camarón", "Atún",
        "Queso", "Huevo", "Tocino", "Jamón",
        "Arroz", "Pasta", "Pan", "Tortilla",
        "Frijoles", "Lentejas", "Garbanzos",
        "Zanahoria", "Brócoli", "Espinaca", "Calabaza",
        "Manzana", "Plátano", "Fresa", "Mango",
        "Aceite de oliva", "Limón", "Sal", "Pimienta"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Outlined.Restaurant,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("Seleccionar ingredientes", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp)
            ) {
                items(commonIngredients.size) { index ->
                    val ingredient = commonIngredients[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(ingredient) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedIngredients.contains(ingredient),
                            onCheckedChange = { onToggle(ingredient) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF4CAF50),
                                uncheckedColor = Color(0xFF8B92A1)
                            )
                        )
                        Text(
                            ingredient,
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (selectedIngredients.contains(ingredient)) Color.White else Color(0xFFB7BDC9)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Listo", color = Color(0xFF4CAF50))
            }
        },
        containerColor = Color(0xFF0F1219),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
private fun TagsDialog(
    selectedTags: Set<String>,
    onDismiss: () -> Unit,
    onToggle: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Outlined.LocalOffer,
                contentDescription = null,
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(32.dp)
            )
        },
        title = { Text("Tags dietéticos", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier.height(300.dp)
            ) {
                items(DietaryTags.tags.size) { index ->
                    val tag = DietaryTags.tags[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(tag) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedTags.contains(tag),
                            onCheckedChange = { onToggle(tag) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFF4CAF50),
                                uncheckedColor = Color(0xFF8B92A1)
                            )
                        )
                        Text(
                            tag,
                            modifier = Modifier.padding(start = 8.dp),
                            color = if (selectedTags.contains(tag)) Color.White else Color(0xFFB7BDC9)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Listo", color = Color(0xFF4CAF50))
            }
        },
        containerColor = Color(0xFF0F1219),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

private fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.lowercase()) {
        "desayuno" -> Icons.Outlined.BrunchDining
        "comida", "almuerzo" -> Icons.Outlined.LunchDining
        "cena" -> Icons.Outlined.DinnerDining
        "snack", "botana" -> Icons.Outlined.Fastfood
        "bebida" -> Icons.Outlined.LocalCafe
        "postre" -> Icons.Outlined.Cake
        else -> Icons.Outlined.Restaurant
    }
}