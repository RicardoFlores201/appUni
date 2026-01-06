package com.ejercicio.app.viewModel

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejercicio.app.R
import com.ejercicio.app.model.UserModel
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    var email by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var showAlert by mutableStateOf(false)
        private set

    var alertMessage by mutableStateOf("")
        private set

    fun onEmailChange(v: String) { email = v }
    fun onPasswordChange(v: String) { password = v }

    fun closeAlert() { showAlert = false }

    fun openAlert(msg: String) {
        alertMessage = msg
        showAlert = true
    }

    fun loginWithEmailPassword(onSuccess: () -> Unit) {
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
                    verifyIsClient(onSuccess)
                } else {
                    openAlert(task.exception?.localizedMessage ?: "No se pudo iniciar sesión.")
                }
            }
    }

    fun createUser(
        email: String,
        password: String,
        username: String,
        onSuccess: () -> Unit
    ) {
        if (email.isBlank() || password.isBlank() || username.isBlank()) {
            openAlert("Por favor completa todos los campos.")
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

        isLoading = true
        auth.createUserWithEmailAndPassword(email.trim(), password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Guardar usuario en Firestore
                    savedUser(username = username) {
                        isLoading = false
                        onSuccess()
                    }
                } else {
                    isLoading = false
                    openAlert(task.exception?.localizedMessage ?: "Error al crear el usuario.")
                }
            }
    }

    private fun savedUser(username: String, onSaved: () -> Unit = {}) {
        val uid = auth.currentUser?.uid
        val userEmail = auth.currentUser?.email  // <-- CORRECTO (antes tenías uid)

        if (uid.isNullOrBlank()) {
            openAlert("No se pudo obtener el ID del usuario.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val user = UserModel(
                userId = uid,
                email = userEmail ?: "",
                username = username
            )

            firestore.collection("users")
                .document(uid)          // <-- evita duplicados
                .set(user)
                .addOnSuccessListener { onSaved() }
                .addOnFailureListener { e ->
                    openAlert(e.localizedMessage ?: "Error al guardar en Firestore.")
                }
        }
    }

    fun loginWithGoogle(context: Context, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                isLoading = true

                val serverClientId = context.getString(R.string.default_web_client_id)

                val googleIdOption = GetGoogleIdOption.Builder()
                    .setServerClientId(serverClientId)
                    .setFilterByAuthorizedAccounts(false)
                    .setAutoSelectEnabled(false)
                    .build()

                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

                val credentialManager = CredentialManager.create(context)
                val result = credentialManager.getCredential(context, request)

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)

                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        isLoading = false
                        if (task.isSuccessful) {
                            onSuccess()
                        } else {
                            openAlert(task.exception?.localizedMessage ?: "No se pudo iniciar sesión con Google.")
                        }
                    }

            } catch (e: Exception) {
                isLoading = false
                openAlert(e.localizedMessage ?: "Error al iniciar sesión con Google.")
            }
        }
    }

    fun loginWithGithub(activity: ComponentActivity, onSuccess: () -> Unit) {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.scopes = listOf("user:email")

        isLoading = true

        val pendingResultTask = auth.pendingAuthResult
        if (pendingResultTask != null) {
            pendingResultTask.addOnSuccessListener {
                isLoading = false
                onSuccess()
            }.addOnFailureListener { e ->
                isLoading = false
                openAlert(e.localizedMessage ?: "Error en autenticación pendiente.")
            }
        } else {

            auth.startActivityForSignInWithProvider(activity, provider.build())
                .addOnSuccessListener {
                    isLoading = false
                    onSuccess()
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    // Tip: Si el usuario cancela, no siempre es un error que requiera alerta
                    if (e.message?.contains("canceled") == false) {
                        openAlert(e.localizedMessage ?: "Error al conectar con GitHub.")
                    }
                }
        }
    }

    private fun verifyIsClient(onSuccess: () -> Unit) {
        val uid = auth.currentUser?.uid

        if (uid.isNullOrBlank()) {
            isLoading = false
            openAlert("Error al obtener el usuario.")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val doc = firestore.collection("Users")
                    .document(uid)
                    .get()
                    .await()

                launch(Dispatchers.Main) {
                    if (doc.exists()) {
                        // Es un cliente válido
                        isLoading = false
                        onSuccess()
                    } else {
                        auth.signOut()
                        isLoading = false
                        openAlert("Esta cuenta es de restaurante. Por favor usa el acceso de restaurantes.")
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

}
