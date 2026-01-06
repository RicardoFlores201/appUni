package com.ejercicio.app.views.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.ejercicio.app.components.Alert
import com.ejercicio.app.navigation.AppScreen
import com.ejercicio.app.viewModel.CartViewModel
import com.ejercicio.app.viewModel.OrderViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    navController: NavHostController,
    cartVM: CartViewModel,
    orderVM: OrderViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    var deliveryAddress by remember { mutableStateOf("") }
    var deliveryInstructions by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("Efectivo") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdOrderId by remember { mutableStateOf("") }

    val deliveryFee = 30.0
    val subtotal = cartVM.total
    val total = subtotal + deliveryFee

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Confirmar pedido",
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
        },
        bottomBar = {
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
                        .padding(20.dp)
                ) {
                    // Resumen de totales
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", color = Color(0xFFB7BDC9), fontSize = 14.sp)
                        Text(
                            "$${String.format("%.2f", subtotal)}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Envío", color = Color(0xFFB7BDC9), fontSize = 14.sp)
                        Text(
                            "$${String.format("%.2f", deliveryFee)}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Divider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFF232838)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "$${String.format("%.2f", total)}",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4CAF50)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Botón realizar pedido
                    Button(
                        onClick = {
                            orderVM.createOrder(
                                cartItems = cartVM.cartItems,
                                deliveryAddress = deliveryAddress,
                                deliveryInstructions = deliveryInstructions,
                                paymentMethod = selectedPaymentMethod
                            ) { orderId ->
                                createdOrderId = orderId
                                showSuccessDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(16.dp),
                        enabled = deliveryAddress.isNotBlank() && !orderVM.isLoading
                    ) {
                        if (orderVM.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(Icons.Default.CheckCircle, contentDescription = null)
                            Spacer(Modifier.width(12.dp))
                            Text(
                                "Realizar pedido",
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Información del restaurante
                item {
                    val restaurantName = cartVM.cartItems.firstOrNull()?.dish?.restaurantName
                    if (restaurantName != null) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF0F1219)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1A2F2A)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Pedido de",
                                        fontSize = 12.sp,
                                        color = Color(0xFF8B92A1)
                                    )
                                    Text(
                                        restaurantName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }

                // Dirección de entrega
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F1219)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Dirección de entrega",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = deliveryAddress,
                                onValueChange = { deliveryAddress = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text("Ej: Calle 123, Colonia Centro, CP 12345")
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFF232838),
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedContainerColor = Color(0xFF1A1F2E),
                                    focusedContainerColor = Color(0xFF1A1F2E),
                                    cursorColor = Color(0xFF4CAF50),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    unfocusedPlaceholderColor = Color(0xFF8B92A1),
                                    focusedPlaceholderColor = Color(0xFF8B92A1)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 2,
                                maxLines = 3
                            )

                            Spacer(Modifier.height(12.dp))

                            OutlinedTextField(
                                value = deliveryInstructions,
                                onValueChange = { deliveryInstructions = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = {
                                    Text("Instrucciones adicionales (opcional)")
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedBorderColor = Color(0xFF232838),
                                    focusedBorderColor = Color(0xFF4CAF50),
                                    unfocusedContainerColor = Color(0xFF1A1F2E),
                                    focusedContainerColor = Color(0xFF1A1F2E),
                                    cursorColor = Color(0xFF4CAF50),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White,
                                    unfocusedPlaceholderColor = Color(0xFF8B92A1),
                                    focusedPlaceholderColor = Color(0xFF8B92A1)
                                ),
                                shape = RoundedCornerShape(12.dp),
                                minLines = 2
                            )
                        }
                    }
                }

                // Método de pago
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F1219)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Método de pago",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            // Opciones de pago
                            PaymentMethodOption(
                                text = "Efectivo",
                                icon = Icons.Default.PlayArrow,
                                selected = selectedPaymentMethod == "Efectivo",
                                onClick = { selectedPaymentMethod = "Efectivo" }
                            )

                            Spacer(Modifier.height(8.dp))

                            PaymentMethodOption(
                                text = "Tarjeta (en desarrollo)",
                                icon = Icons.Default.Check,
                                selected = selectedPaymentMethod == "Tarjeta",
                                onClick = { /* selectedPaymentMethod = "Tarjeta" */ },
                                enabled = false
                            )
                        }
                    }
                }

                // Resumen del pedido
                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF0F1219)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Resumen del pedido",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }

                            Spacer(Modifier.height(12.dp))

                            cartVM.cartItems.forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = item.dish.imageUrl,
                                            contentDescription = null,
                                            modifier = Modifier
                                                .size(48.dp)
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                "${item.quantity}x ${item.dish.name}",
                                                color = Color.White,
                                                fontSize = 14.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                "$${String.format("%.2f", item.dish.price)} c/u",
                                                color = Color(0xFF8B92A1),
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                    Text(
                                        "$${String.format("%.2f", item.subtotal)}",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                // Espaciado final
                item {
                    Spacer(Modifier.height(20.dp))
                }
            }

            // Success Dialog
            if (showSuccessDialog) {
                AlertDialog(
                    onDismissRequest = { },
                    title = {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "¡Pedido realizado!",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    text = {
                        Column {
                            Text(
                                "Tu pedido ha sido enviado al restaurante.",
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Recibirás una notificación cuando sea confirmado.",
                                fontSize = 13.sp,
                                color = Color(0xFF8B92A1),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                cartVM.clearCart()
                                navController.navigate(AppScreen.OrderDetail.createRoute(createdOrderId)) {
                                    popUpTo(AppScreen.UserHome.route) { inclusive = false }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Ver pedido")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                cartVM.clearCart()
                                navController.navigate(AppScreen.UserHome.route) {
                                    popUpTo(AppScreen.UserHome.route) { inclusive = true }
                                }
                            }
                        ) {
                            Text("Ir al inicio")
                        }
                    },
                    containerColor = Color(0xFF0F1219),
                    titleContentColor = Color.White,
                    textContentColor = Color.White
                )
            }

            // Alertas del ViewModel
            if (orderVM.showAlert) {
                Alert(
                    title = "Error",
                    message = orderVM.alertMessage,
                    confirmText = "Aceptar",
                    onConfirmClick = { orderVM.closeAlert() }
                ) {}
            }
        }
    }
}

@Composable
private fun PaymentMethodOption(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color(0xFF1A2F2A) else Color(0xFF1A1F2E),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.5.dp,
            color = if (selected) Color(0xFF4CAF50) else Color(0xFF232838)
        ),
        enabled = enabled
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (enabled) {
                    if (selected) Color(0xFF4CAF50) else Color(0xFF8B92A1)
                } else {
                    Color(0xFF444444)
                },
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text,
                color = if (enabled) Color.White else Color(0xFF666666),
                fontSize = 15.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f)
            )
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}