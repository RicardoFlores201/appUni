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
import com.ejercicio.app.model.OrderStatus
import com.ejercicio.app.navigation.AppScreen
import com.ejercicio.app.viewModel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavHostController,
    orderId: String,
    orderVM: OrderViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    var showCancelDialog by remember { mutableStateOf(false) }

    // Cargar pedido al iniciar
    LaunchedEffect(orderId) {
        orderVM.getOrderDetail(orderId)
    }

    val order = orderVM.currentOrder

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detalle del pedido",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .padding(paddingValues)
        ) {
            if (orderVM.isLoading || order == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Estado del pedido con timeline
                    item {
                        OrderStatusCard(order = order)
                    }

                    // Información del restaurante
                    item {
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
                                    modifier = Modifier.size(56.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF1A2F2A)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.padding(14.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Restaurante",
                                        fontSize = 12.sp,
                                        color = Color(0xFF8B92A1)
                                    )
                                    Text(
                                        order.restaurantName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
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
                                Text(
                                    order.deliveryAddress,
                                    fontSize = 14.sp,
                                    color = Color(0xFFB7BDC9),
                                    lineHeight = 20.sp
                                )
                                if (order.deliveryInstructions.isNotBlank()) {
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "Instrucciones: ${order.deliveryInstructions}",
                                        fontSize = 13.sp,
                                        color = Color(0xFF8B92A1),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                    )
                                }
                            }
                        }
                    }

                    // Items del pedido
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
                                        "Platillos (${order.items.size})",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(Modifier.height(16.dp))

                                order.items.forEach { item ->
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
                                                model = item.dishImageUrl,
                                                contentDescription = null,
                                                modifier = Modifier
                                                    .size(56.dp)
                                                    .clip(RoundedCornerShape(10.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                            Spacer(Modifier.width(12.dp))
                                            Column {
                                                Text(
                                                    "${item.quantity}x ${item.dishName}",
                                                    color = Color.White,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    maxLines = 2,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                                Text(
                                                    "$${String.format("%.2f", item.price)} c/u",
                                                    color = Color(0xFF8B92A1),
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                        Text(
                                            "$${String.format("%.2f", item.subtotal)}",
                                            color = Color(0xFF4CAF50),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Resumen de pago
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
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Resumen de pago",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                                Spacer(Modifier.height(16.dp))

                                PaymentRow("Subtotal", order.subtotal)
                                Spacer(Modifier.height(8.dp))
                                PaymentRow("Envío", order.deliveryFee)
                                Spacer(Modifier.height(8.dp))
                                PaymentRow("Método de pago", order.paymentMethod, isMethod = true)

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
                                        "$${String.format("%.2f", order.total)}",
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }
                    }

                    // Botón cancelar (solo si está pendiente)
                    if (order.status == OrderStatus.PENDING) {
                        item {
                            OutlinedButton(
                                onClick = { showCancelDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFFE53935)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.5.dp,
                                    Color(0xFFE53935)
                                )
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Cancelar pedido", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    // Espaciado final
                    item {
                        Spacer(Modifier.height(20.dp))
                    }
                }
            }

            // Cancel Dialog
            if (showCancelDialog) {
                AlertDialog(
                    onDismissRequest = { showCancelDialog = false },
                    title = { Text("Cancelar pedido", fontWeight = FontWeight.Bold) },
                    text = { Text("¿Estás seguro de cancelar este pedido? Esta acción no se puede deshacer.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                orderVM.cancelOrder(orderId) {
                                    showCancelDialog = false
                                    navController.navigate(AppScreen.OrderHistory.route) {
                                        popUpTo(AppScreen.UserHome.route) { inclusive = false }
                                    }
                                }
                            }
                        ) {
                            Text("Cancelar pedido", color = Color(0xFFE53935))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showCancelDialog = false }) {
                            Text("Volver")
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
private fun OrderStatusCard(order: com.ejercicio.app.model.OrderModel) {
    val statusColor = Color(OrderStatus.getStatusColor(order.status))
    val statusText = OrderStatus.getStatusText(order.status)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1219)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Estado actual
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Estado del pedido",
                        fontSize = 14.sp,
                        color = Color(0xFF8B92A1)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        statusText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Icon(
                        getStatusIcon(order.status),
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Fecha
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color(0xFF8B92A1),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    formatDate(order.createdAt),
                    fontSize = 13.sp,
                    color = Color(0xFF8B92A1)
                )
            }

            Spacer(Modifier.height(16.dp))
            Divider(color = Color(0xFF232838))
            Spacer(Modifier.height(16.dp))

            // Timeline de estados
            Text(
                "Seguimiento",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Spacer(Modifier.height(12.dp))

            val statuses = listOf(
                OrderStatus.PENDING to "Pendiente",
                OrderStatus.CONFIRMED to "Confirmado",
                OrderStatus.PREPARING to "En preparación",
                OrderStatus.ON_DELIVERY to "En camino",
                OrderStatus.DELIVERED to "Entregado"
            )

            statuses.forEachIndexed { index, (status, label) ->
                TimelineItem(
                    label = label,
                    isCompleted = getStatusOrder(order.status) >= getStatusOrder(status),
                    isActive = order.status == status,
                    isLast = index == statuses.size - 1
                )
            }
        }
    }
}

@Composable
private fun TimelineItem(
    label: String,
    isCompleted: Boolean,
    isActive: Boolean,
    isLast: Boolean
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.size(20.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isCompleted || isActive) Color(0xFF4CAF50) else Color(0xFF232838)
            ) {
                if (isCompleted && !isActive) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(32.dp)
                        .background(if (isCompleted) Color(0xFF4CAF50) else Color(0xFF232838))
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Text(
            label,
            fontSize = 14.sp,
            color = if (isActive) Color.White else if (isCompleted) Color(0xFFB7BDC9) else Color(0xFF8B92A1),
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
private fun PaymentRow(label: String, value: Any, isMethod: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = Color(0xFFB7BDC9)
        )
        Text(
            if (isMethod) value.toString() else "$${String.format("%.2f", value)}",
            fontSize = 14.sp,
            color = Color.White,
            fontWeight = if (isMethod) FontWeight.Normal else FontWeight.Medium
        )
    }
}

private fun getStatusIcon(status: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (status) {
        OrderStatus.PENDING -> Icons.Default.DateRange
        OrderStatus.CONFIRMED -> Icons.Default.CheckCircle
        OrderStatus.PREPARING -> Icons.Default.PlayArrow
        OrderStatus.ON_DELIVERY -> Icons.Default.Done
        OrderStatus.DELIVERED -> Icons.Default.Done
        OrderStatus.CANCELLED -> Icons.Default.Close
        else -> Icons.Default.Info
    }
}

private fun getStatusOrder(status: String): Int {
    return when (status) {
        OrderStatus.PENDING -> 0
        OrderStatus.CONFIRMED -> 1
        OrderStatus.PREPARING -> 2
        OrderStatus.ON_DELIVERY -> 3
        OrderStatus.DELIVERED -> 4
        OrderStatus.CANCELLED -> -1
        else -> -1
    }
}

private fun formatDate(timestamp: Any): String {
    return try {
        val date = when (timestamp) {
            is Long -> Date(timestamp)
            else -> Date()
        }
        val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "MX"))
        sdf.format(date)
    } catch (e: Exception) {
        "Fecha desconocida"
    }
}