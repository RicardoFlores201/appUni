package com.ejercicio.app.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejercicio.app.model.RestaurantModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RestaurantAuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // ===== UI State =====
    var restaurantName by mutableStateOf("")
        private set

    var ownerName by mutableStateOf("")
        private set

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var phone by mutableStateOf("")
        private set

    var address by mutableStateOf("")
        private set

    var description by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var showAlert by mutableStateOf(false)
        private set

    var alertMessage by mutableStateOf("")
        private set

    // ===== UI Intents =====
    fun onRestaurantNameChange(v: String) { restaurantName = v }
    fun onOwnerNameChange(v: String) { ownerName = v }
    fun onEmailChange(v: String) { email = v }
    fun onPasswordChange(v: String) { password = v }
    fun onPhoneChange(v: String) { phone = v }
    fun onAddressChange(v: String) { address = v }
    fun onDescriptionChange(v: String) { description = v }

    // ===== Alerts =====
    fun closeAlert() { showAlert = false }

    fun openAlert(msg: String) {
        alertMessage = msg
        showAlert = true
    }

    // ============================================================
    // REGISTRO DE RESTAURANTE
    // ============================================================
    fun signUpRestaurant(onSuccess: () -> Unit) {
        // Validaciones
        if (restaurantName.isBlank() || ownerName.isBlank() || email.isBlank() ||
            password.isBlank() || phone.isBlank() || address.isBlank()) {
            openAlert("Por favor completa todos los campos obligatorios.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            openAlert("Por favor ingresa un email válido.")
            return
        }

        if (password.length < 6) {
            openAlert("La contraseña debe tener al menos 6 caracteres.")
            return
        }

        if (phone.length < 10) {
            openAlert("Por favor ingresa un número de teléfono válido.")
            return
        }

        isLoading = true

        // Crear cuenta en Firebase Auth
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Guardar datos del restaurante en Firestore
                    saveRestaurant(onSuccess)
                } else {
                    isLoading = false
                    openAlert(task.exception?.localizedMessage ?: "Error al crear la cuenta.")
                }
            }
    }

    /**
     * Guarda el restaurante en Firestore en la colección "Restaurants"
     */
    private fun saveRestaurant(onSaved: () -> Unit) {
        val restaurantId = auth.currentUser?.uid

        if (restaurantId.isNullOrBlank()) {
            isLoading = false
            openAlert("No se pudo obtener el ID del restaurante.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val restaurant = RestaurantModel(
                restaurantId = restaurantId,
                email = email.trim(),
                restaurantName = restaurantName.trim(),
                ownerName = ownerName.trim(),
                description = description.trim(),
                phone = phone.trim(),
                address = address.trim(),
                logoUrl = "" // Por ahora vacío, luego se podrá subir logo
            )

            firestore.collection("restaurants")
                .document(restaurantId)
                .set(restaurant)
                .addOnSuccessListener {
                    isLoading = false
                    onSaved()
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al guardar el restaurante.")
                }
        }
    }

    // ============================================================
    // LOGIN DE RESTAURANTE
    // ============================================================
    fun loginRestaurant(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            openAlert("Por favor completa todos los campos.")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            openAlert("Por favor ingresa un email válido.")
            return
        }

        isLoading = true

        auth.signInWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Verificar que el usuario sea un restaurante
                    verifyIsRestaurant(onSuccess)
                } else {
                    isLoading = false
                    openAlert(task.exception?.localizedMessage ?: "No se pudo iniciar sesión.")
                }
            }
    }

    /**
     * Verifica que el UID autenticado exista en la colección "Restaurants"
     * Si no existe, cierra sesión y muestra error
     */
    private fun verifyIsRestaurant(onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid.isNullOrBlank()) {
            isLoading = false
            openAlert("Error al obtener el usuario.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val doc = firestore.collection("restaurants")
                    .document(uid)
                    .get()
                    .await()

                // Cambiar al hilo principal para actualizar UI
                launch(Dispatchers.Main) {
                    if (doc.exists()) {
                        // Es un restaurante válido
                        isLoading = false
                        onSuccess()
                    } else {
                        // El UID existe en Auth pero NO en Restaurants
                        // Probablemente sea un usuario cliente
                        auth.signOut()
                        isLoading = false
                        openAlert("Esta cuenta no es un restaurante. Por favor usa la app de clientes.")
                    }
                }
            } catch (e: Exception) {
                launch(Dispatchers.Main) {
                    auth.signOut()
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al verificar la cuenta.")
                }
            }
        }
    }

    // ============================================================
    // LOGOUT
    // ============================================================
    fun logoutRestaurant() {
        auth.signOut()
        // Limpiar estados
        restaurantName = ""
        ownerName = ""
        email = ""
        password = ""
        phone = ""
        address = ""
        description = ""
    }

    // ============================================================
    // VERIFICAR SI HAY SESIÓN ACTIVA
    // ============================================================
    fun isRestaurantLoggedIn(): Boolean {
        return auth.currentUser != null
    }
}