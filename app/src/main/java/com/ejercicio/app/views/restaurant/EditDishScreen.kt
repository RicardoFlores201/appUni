package com.ejercicio.app.views.restaurant

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.clip
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
import com.ejercicio.app.utils.HealthyIngredients
import com.ejercicio.app.viewModel.DishViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDishScreen(
    navController: NavHostController,
    dishVM: DishViewModel,
    dishId: String
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    var showCategoryMenu by remember { mutableStateOf(false) }
    var showIngredientsDialog by remember { mutableStateOf(false) }
    var showTagsDialog by remember { mutableStateOf(false) }

    // Cargar datos del platillo al iniciar
    LaunchedEffect(dishId) {
        dishVM.loadDishForEdit(dishId)
    }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        dishVM.onImageUriChange(uri)
    }

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White
                    )
                }
                Text(
                    text = "Editar platillo",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            // Selector de imagen
            Text(
                text = "Imagen del platillo",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFB7BDC9),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F1219))
                    .border(
                        width = 2.dp,
                        color = Color(0xFF218A85),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                // Mostrar nueva imagen o imagen existente
                AsyncImage(
                    model = dishVM.imageUri ?: "",
                    contentDescription = "Imagen del platillo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Overlay para cambiar imagen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Cambiar imagen",
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            // Nombre
            EditFieldLabel("Nombre del platillo *")
            EditDishTextField(
                value = dishVM.name,
                onValueChange = dishVM::onNameChange,
                placeholder = "Ej: Hamburguesa vegana"
            )

            Spacer(Modifier.height(16.dp))

            // Descripción
            EditFieldLabel("Descripción *")
            EditDishTextField(
                value = dishVM.description,
                onValueChange = dishVM::onDescriptionChange,
                placeholder = "Describe el platillo...",
                maxLines = 3
            )

            Spacer(Modifier.height(16.dp))

            // Categoría
            EditFieldLabel("Categoría *")
            ExposedDropdownMenuBox(
                expanded = showCategoryMenu,
                onExpandedChange = { showCategoryMenu = !showCategoryMenu }
            ) {
                OutlinedTextField(
                    value = dishVM.category,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    placeholder = { Text("Selecciona categoría", color = Color(0xFF6B7280)) },
                    trailingIcon = {
                        Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF218A85))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFF232838),
                        focusedBorderColor = Color(0xFF218A85),
                        unfocusedContainerColor = Color(0xFF0F1219),
                        focusedContainerColor = Color(0xFF0F1219),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                ExposedDropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    DishCategories.categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                dishVM.onCategoryChange(category)
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Precio
            EditFieldLabel("Precio *")
            EditDishTextField(
                value = dishVM.price,
                onValueChange = dishVM::onPriceChange,
                placeholder = "0.00",
                keyboardType = KeyboardType.Decimal,
                leadingIcon = {
                    Text("$", color = Color(0xFF218A85), fontSize = 18.sp)
                }
            )

            Spacer(Modifier.height(16.dp))

            // Ingredientes
            EditFieldLabel("Ingredientes * (${dishVM.selectedIngredients.size} seleccionados)")
            Button(
                onClick = { showIngredientsDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F1219)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232838))
            ) {
                Icon(Icons.Default.Edit, null, tint = Color(0xFF218A85))
                Spacer(Modifier.width(8.dp))
                Text("Editar ingredientes", color = Color.White)
            }

            if (dishVM.selectedIngredients.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dishVM.selectedIngredients) { ingredient ->
                        FilterChip(
                            selected = true,
                            onClick = { dishVM.toggleIngredient(ingredient) },
                            label = { Text(ingredient, fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tags dietéticos
            EditFieldLabel("Tags dietéticos (${dishVM.selectedDietaryTags.size} seleccionados)")
            Button(
                onClick = { showTagsDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F1219)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232838))
            ) {
                Icon(Icons.Default.Edit, null, tint = Color(0xFF218A85))
                Spacer(Modifier.width(8.dp))
                Text("Editar tags", color = Color.White)
            }

            if (dishVM.selectedDietaryTags.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(dishVM.selectedDietaryTags) { tag ->
                        FilterChip(
                            selected = true,
                            onClick = { dishVM.toggleDietaryTag(tag) },
                            label = { Text(tag, fontSize = 12.sp) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, null, modifier = Modifier.size(16.dp))
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // Botón guardar cambios
            Button(
                onClick = {
                    dishVM.updateDish(dishId, dishVM.imageUri) {
                        navController.popBackStack()
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
                enabled = !dishVM.isLoading
            ) {
                if (dishVM.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar cambios", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // Progress de upload
            if (dishVM.isLoading && dishVM.uploadProgress > 0) {
                Spacer(Modifier.height(16.dp))
                LinearProgressIndicator(
                    progress = { dishVM.uploadProgress },
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF218A85),
                )
                Text(
                    text = "Subiendo imagen... ${(dishVM.uploadProgress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color(0xFFB7BDC9),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Dialog ingredientes (reutiliza el mismo de AddDish)
        if (showIngredientsDialog) {
            EditIngredientsDialog(
                selectedIngredients = dishVM.selectedIngredients,
                onToggle = dishVM::toggleIngredient,
                onDismiss = { showIngredientsDialog = false }
            )
        }

        // Dialog tags
        if (showTagsDialog) {
            EditTagsDialog(
                selectedTags = dishVM.selectedDietaryTags,
                onToggle = dishVM::toggleDietaryTag,
                onDismiss = { showTagsDialog = false }
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
private fun EditFieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFFB7BDC9),
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun EditDishTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    maxLines: Int = 1,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp),
        singleLine = maxLines == 1,
        maxLines = maxLines,
        placeholder = { Text(placeholder, color = Color(0xFF6B7280), fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color(0xFF232838),
            focusedBorderColor = Color(0xFF218A85),
            unfocusedContainerColor = Color(0xFF0F1219),
            focusedContainerColor = Color(0xFF0F1219),
            cursorColor = Color(0xFF218A85),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        )
    )
}

@Composable
private fun EditIngredientsDialog(
    selectedIngredients: List<String>,
    onToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val grouped = HealthyIngredients.getGrouped()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar ingredientes") },
        text = {
            LazyColumn(
                modifier = Modifier.height(400.dp)
            ) {
                grouped.forEach { (category, ingredients) ->
                    item {
                        Text(
                            text = category,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    items(ingredients) { ingredient ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggle(ingredient) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = selectedIngredients.contains(ingredient),
                                onCheckedChange = { onToggle(ingredient) }
                            )
                            Text(ingredient, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Listo")
            }
        }
    )
}

@Composable
private fun EditTagsDialog(
    selectedTags: List<String>,
    onToggle: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar tags dietéticos") },
        text = {
            LazyColumn {
                items(DietaryTags.tags) { tag ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggle(tag) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = selectedTags.contains(tag),
                            onCheckedChange = { onToggle(tag) }
                        )
                        Text(tag, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Listo")
            }
        }
    )
}