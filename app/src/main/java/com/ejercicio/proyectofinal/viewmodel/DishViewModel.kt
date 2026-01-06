package com.ejercicio.proyectofinal.viewModel

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejercicio.proyectofinal.model.DishModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class DishViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // ===== UI State =====
    var name by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var selectedIngredients by mutableStateOf<List<String>>(emptyList())
        private set

    var selectedDietaryTags by mutableStateOf<List<String>>(emptyList())
        private set

    var category by mutableStateOf("")
        private set

    var price by mutableStateOf("")
        private set

    var imageUri by mutableStateOf<Uri?>(null)
        private set

    var dishes by mutableStateOf<List<DishModel>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var showAlert by mutableStateOf(false)
        private set

    var alertMessage by mutableStateOf("")
        private set

    var uploadProgress by mutableStateOf(0f)
        private set

    // ===== UI Intents =====
    fun onNameChange(v: String) { name = v }
    fun onDescriptionChange(v: String) { description = v }
    fun onCategoryChange(v: String) { category = v }
    fun onPriceChange(v: String) {
        // Solo permite números y punto decimal
        if (v.isEmpty() || v.matches(Regex("^\\d*\\.?\\d*$"))) {
            price = v
        }
    }
    fun onImageUriChange(uri: Uri?) { imageUri = uri }

    fun toggleIngredient(ingredient: String) {
        selectedIngredients = if (selectedIngredients.contains(ingredient)) {
            selectedIngredients - ingredient
        } else {
            selectedIngredients + ingredient
        }
    }

    fun toggleDietaryTag(tag: String) {
        selectedDietaryTags = if (selectedDietaryTags.contains(tag)) {
            selectedDietaryTags - tag
        } else {
            selectedDietaryTags + tag
        }
    }

    // ===== Alerts =====
    fun closeAlert() { showAlert = false }

    fun openAlert(msg: String) {
        alertMessage = msg
        showAlert = true
    }

    // ============================================================
    // CREATE DISH
    // ============================================================
    fun createDish(onSuccess: () -> Unit) {
        // Validaciones
        if (name.isBlank() || description.isBlank() || category.isBlank() || price.isBlank()) {
            openAlert("Por favor completa todos los campos obligatorios.")
            return
        }

        if (selectedIngredients.isEmpty()) {
            openAlert("Debes seleccionar al menos un ingrediente.")
            return
        }

        if (price.toDoubleOrNull() == null || price.toDouble() <= 0) {
            openAlert("Por favor ingresa un precio válido.")
            return
        }

        if (imageUri == null) {
            openAlert("Por favor selecciona una imagen del platillo.")
            return
        }

        val restaurantId = auth.currentUser?.uid
        if (restaurantId.isNullOrBlank()) {
            openAlert("Error: No se pudo obtener el ID del restaurante.")
            return
        }

        isLoading = true
        uploadProgress = 0f

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Obtener nombre del restaurante
                val restaurantDoc = firestore.collection("restaurants")
                    .document(restaurantId)
                    .get()
                    .await()

                val restaurantName = restaurantDoc.getString("restaurantName") ?: "Sin nombre"

                // 2. Subir imagen a Storage
                val imageUrl = uploadImageToStorage(imageUri!!, restaurantId)

                // 3. Crear platillo en Firestore
                val dishId = UUID.randomUUID().toString()
                val dish = DishModel(
                    dishId = dishId,
                    restaurantId = restaurantId,
                    restaurantName = restaurantName,
                    name = name.trim(),
                    description = description.trim(),
                    ingredients = selectedIngredients,
                    dietaryTags = selectedDietaryTags,
                    category = category,
                    price = price.toDouble(),
                    imageUrl = imageUrl
                )

                firestore.collection("dishes")
                    .document(dishId)
                    .set(dish)
                    .await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    clearForm()
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al crear el platillo.")
                }
            }
        }
    }

    // ============================================================
    // READ DISHES
    // ============================================================
    fun getRestaurantDishes() {
        val restaurantId = auth.currentUser?.uid ?: return

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val snapshot = firestore.collection("dishes")
                    .whereEqualTo("restaurantId", restaurantId)
                    .get()
                    .await()

                val dishesList = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(DishModel::class.java)
                }

                withContext(Dispatchers.Main) {
                    dishes = dishesList
                    isLoading = false
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al cargar los platillos.")
                }
            }
        }
    }

    // ============================================================
    // UPDATE DISH
    // ============================================================
    fun updateDish(dishId: String, newImageUri: Uri?, onSuccess: () -> Unit) {
        // Validaciones
        if (name.isBlank() || description.isBlank() || category.isBlank() || price.isBlank()) {
            openAlert("Por favor completa todos los campos obligatorios.")
            return
        }

        if (selectedIngredients.isEmpty()) {
            openAlert("Debes seleccionar al menos un ingrediente.")
            return
        }

        if (price.toDoubleOrNull() == null || price.toDouble() <= 0) {
            openAlert("Por favor ingresa un precio válido.")
            return
        }

        val restaurantId = auth.currentUser?.uid
        if (restaurantId.isNullOrBlank()) {
            openAlert("Error: No se pudo obtener el ID del restaurante.")
            return
        }

        isLoading = true
        uploadProgress = 0f

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Si hay nueva imagen, subirla
                val imageUrl = if (newImageUri != null) {
                    uploadImageToStorage(newImageUri, restaurantId)
                } else {
                    // Mantener la imagen existente
                    val existingDish = firestore.collection("dishes")
                        .document(dishId)
                        .get()
                        .await()
                    existingDish.getString("imageUrl") ?: ""
                }

                // Obtener nombre del restaurante
                val restaurantDoc = firestore.collection("restaurants")
                    .document(restaurantId)
                    .get()
                    .await()

                val restaurantName = restaurantDoc.getString("restaurantName") ?: "Sin nombre"

                // Actualizar platillo
                val updates = hashMapOf<String, Any>(
                    "name" to name.trim(),
                    "description" to description.trim(),
                    "ingredients" to selectedIngredients,
                    "dietaryTags" to selectedDietaryTags,
                    "category" to category,
                    "price" to price.toDouble(),
                    "imageUrl" to imageUrl,
                    "restaurantName" to restaurantName
                )

                firestore.collection("dishes")
                    .document(dishId)
                    .update(updates)
                    .await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    clearForm()
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al actualizar el platillo.")
                }
            }
        }
    }

    // ============================================================
    // DELETE DISH
    // ============================================================
    fun deleteDish(dishId: String, onSuccess: () -> Unit) {
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Obtener el platillo para eliminar su imagen
                val dish = firestore.collection("dishes")
                    .document(dishId)
                    .get()
                    .await()
                    .toObject(DishModel::class.java)

                // Eliminar imagen de Storage si existe
                dish?.imageUrl?.let { url ->
                    try {
                        val imageRef = storage.getReferenceFromUrl(url)
                        imageRef.delete().await()
                    } catch (e: Exception) {
                        // Si falla al eliminar la imagen, continuar de todos modos
                    }
                }

                // Eliminar documento de Firestore
                firestore.collection("dishes")
                    .document(dishId)
                    .delete()
                    .await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al eliminar el platillo.")
                }
            }
        }
    }

    // ============================================================
    // UPLOAD IMAGE TO STORAGE
    // ============================================================
    private suspend fun uploadImageToStorage(uri: Uri, restaurantId: String): String {
        val dishId = UUID.randomUUID().toString()
        val storageRef = storage.reference
            .child("dishes/$restaurantId/$dishId.jpg")

        return withContext(Dispatchers.IO) {
            storageRef.putFile(uri)
                .addOnProgressListener { taskSnapshot ->
                    val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toFloat()
                    uploadProgress = progress / 100f
                }
                .await()

            storageRef.downloadUrl.await().toString()
        }
    }

    // ============================================================
    // LOAD DISH FOR EDITING
    // ============================================================
    fun loadDishForEdit(dishId: String) {
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val doc = firestore.collection("dishes")
                    .document(dishId)
                    .get()
                    .await()

                val dish = doc.toObject(DishModel::class.java)

                withContext(Dispatchers.Main) {
                    dish?.let {
                        name = it.name
                        description = it.description
                        selectedIngredients = it.ingredients
                        selectedDietaryTags = it.dietaryTags
                        category = it.category
                        price = it.price.toString()
                        // imageUri se mantiene null (se mostrará la URL existente)
                    }
                    isLoading = false
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al cargar el platillo.")
                }
            }
        }
    }

    // ============================================================
    // CLEAR FORM
    // ============================================================
    fun clearForm() {
        name = ""
        description = ""
        selectedIngredients = emptyList()
        selectedDietaryTags = emptyList()
        category = ""
        price = ""
        imageUri = null
        uploadProgress = 0f
    }
}