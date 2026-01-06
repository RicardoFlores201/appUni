package com.ejercicio.app.viewModel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejercicio.app.model.OrderItem
import com.ejercicio.app.model.OrderModel
import com.ejercicio.app.model.OrderStatus
import com.ejercicio.app.viewModel.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class OrderViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ordersRef = database.getReference("orders")

    // ===== UI State =====
    var myOrders by mutableStateOf<List<OrderModel>>(emptyList())
        private set

    var restaurantOrders by mutableStateOf<List<OrderModel>>(emptyList())
        private set

    var currentOrder by mutableStateOf<OrderModel?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var showAlert by mutableStateOf(false)
        private set

    var alertMessage by mutableStateOf("")
        private set

    // ===== Alerts =====
    fun closeAlert() { showAlert = false }

    fun openAlert(msg: String) {
        alertMessage = msg
        showAlert = true
    }

    // ============================================================
    // CREATE ORDER
    // ============================================================
    fun createOrder(
        cartItems: List<CartItem>,
        deliveryAddress: String,
        deliveryInstructions: String,
        paymentMethod: String,
        onSuccess: (String) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            openAlert("Debes iniciar sesión para realizar un pedido.")
            return
        }

        if (cartItems.isEmpty()) {
            openAlert("El carrito está vacío.")
            return
        }

        if (deliveryAddress.isBlank()) {
            openAlert("Por favor ingresa una dirección de entrega.")
            return
        }

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Generar ID único para el pedido
                val orderId = UUID.randomUUID().toString()

                // Convertir CartItems a OrderItems
                val orderItems = cartItems.map { cartItem ->
                    OrderItem(
                        dishId = cartItem.dish.dishId,
                        dishName = cartItem.dish.name,
                        dishImageUrl = cartItem.dish.imageUrl,
                        quantity = cartItem.quantity,
                        price = cartItem.dish.price,
                        subtotal = cartItem.subtotal
                    )
                }

                // Calcular totales
                val subtotal = orderItems.sumOf { it.subtotal }
                val deliveryFee = 30.0 // Puedes hacerlo dinámico después
                val total = subtotal + deliveryFee

                // Obtener información del restaurante
                val restaurantId = cartItems.first().dish.restaurantId
                val restaurantName = cartItems.first().dish.restaurantName

                // Crear el pedido
                val order = OrderModel(
                    orderId = orderId,
                    userId = currentUser.uid,
                    userName = currentUser.displayName ?: "Usuario",
                    userEmail = currentUser.email ?: "",
                    restaurantId = restaurantId,
                    restaurantName = restaurantName,
                    items = orderItems,
                    subtotal = subtotal,
                    deliveryFee = deliveryFee,
                    total = total,
                    deliveryAddress = deliveryAddress,
                    deliveryInstructions = deliveryInstructions,
                    paymentMethod = paymentMethod,
                    status = OrderStatus.PENDING
                )

                // Guardar en Realtime Database
                ordersRef.child(orderId).setValue(order.toMap()).await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess(orderId)
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al crear el pedido.")
                }
            }
        }
    }

    // ============================================================
    // GET MY ORDERS (USUARIO)
    // ============================================================
    fun getMyOrders() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            myOrders = emptyList()
            return
        }

        isLoading = true

        // Listener en tiempo real
        ordersRef.orderByChild("userId").equalTo(currentUser.uid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<OrderModel>()

                    for (orderSnapshot in snapshot.children) {
                        try {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orders.add(it) }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Ordenar por fecha (más recientes primero)
                    myOrders = orders.sortedByDescending {
                        when (it.createdAt) {
                            is Long -> it.createdAt
                            else -> 0L
                        }
                    }
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                    openAlert(error.message)
                }
            })
    }

    // ============================================================
    // GET RESTAURANT ORDERS (RESTAURANTE)
    // ============================================================
    fun getRestaurantOrders(restaurantId: String) {
        isLoading = true

        // Listener en tiempo real
        ordersRef.orderByChild("restaurantId").equalTo(restaurantId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<OrderModel>()

                    for (orderSnapshot in snapshot.children) {
                        try {
                            val order = orderSnapshot.getValue(OrderModel::class.java)
                            order?.let { orders.add(it) }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // Ordenar: pendientes primero, luego por fecha
                    restaurantOrders = orders.sortedWith(
                        compareBy<OrderModel> {
                            when (it.status) {
                                OrderStatus.PENDING -> 0
                                OrderStatus.CONFIRMED -> 1
                                OrderStatus.PREPARING -> 2
                                OrderStatus.ON_DELIVERY -> 3
                                OrderStatus.DELIVERED -> 4
                                OrderStatus.CANCELLED -> 5
                                else -> 6
                            }
                        }.thenByDescending {
                            when (it.createdAt) {
                                is Long -> it.createdAt
                                else -> 0L
                            }
                        }
                    )
                    isLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                    openAlert(error.message)
                }
            })
    }

    // ============================================================
    // GET ORDER DETAIL
    // ============================================================
    fun getOrderDetail(orderId: String) {
        isLoading = true

        ordersRef.child(orderId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    currentOrder = snapshot.getValue(OrderModel::class.java)
                } catch (e: Exception) {
                    openAlert(e.localizedMessage ?: "Error al cargar el pedido.")
                }
                isLoading = false
            }

            override fun onCancelled(error: DatabaseError) {
                isLoading = false
                openAlert(error.message)
            }
        })
    }

    // ============================================================
    // UPDATE ORDER STATUS (RESTAURANTE)
    // ============================================================
    fun updateOrderStatus(orderId: String, newStatus: String, onSuccess: () -> Unit) {
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updates = mapOf(
                    "status" to newStatus,
                    "updatedAt" to ServerValue.TIMESTAMP
                )

                ordersRef.child(orderId).updateChildren(updates).await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al actualizar el estado.")
                }
            }
        }
    }

    // ============================================================
    // CANCEL ORDER (USUARIO)
    // ============================================================
    fun cancelOrder(orderId: String, onSuccess: () -> Unit) {
        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updates = mapOf(
                    "status" to OrderStatus.CANCELLED,
                    "updatedAt" to ServerValue.TIMESTAMP
                )

                ordersRef.child(orderId).updateChildren(updates).await()

                withContext(Dispatchers.Main) {
                    isLoading = false
                    onSuccess()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    openAlert(e.localizedMessage ?: "Error al cancelar el pedido.")
                }
            }
        }
    }

    // ============================================================
    // CLEANUP
    // ============================================================
    override fun onCleared() {
        super.onCleared()
        // Remover listeners si es necesario
    }
}