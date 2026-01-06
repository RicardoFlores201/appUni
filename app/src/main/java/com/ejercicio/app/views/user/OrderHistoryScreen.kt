package com.ejercicio.app.views.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ejercicio.app.model.OrderModel
import com.ejercicio.app.model.OrderStatus
import com.ejercicio.app.navigation.AppScreen
import com.ejercicio.app.viewModel.OrderViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    navController: NavHostController,
    orderVM: OrderViewModel
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    // Cargar pedidos al iniciar
    LaunchedEffect(Unit) {
        orderVM.getMyOrders()
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Mis pedidos",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            "${orderVM.myOrders.size} pedidos realizados",
                            fontSize = 12.sp,
                            color = Color(0xFFB7BDC9)
                        )
                    }
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
            if (orderVM.isLoading && orderVM.myOrders.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else if (orderVM.myOrders.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No tienes pedidos aún",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Explora el menú y realiza tu primer pedido",
                            fontSize = 14.sp,
                            color = Color(0xFFB7BDC9),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(Modifier.height(24.dp))
                        Button(
                            onClick = {
                                navController.navigate(AppScreen.UserHome.route) {
                                    popUpTo(AppScreen.UserHome.route) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text("Ver platillos")
                        }
                    }
                }
            } else {
                // Lista de pedidos
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(orderVM.myOrders) { order ->
                        OrderCard(
                            order = order,
                            onClick = {
                                navController.navigate(AppScreen.OrderDetail.createRoute(order.orderId))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrderCard(
    order: OrderModel,
    onClick: () -> Unit
) {
    val statusColor = Color(OrderStatus.getStatusColor(order.status))
    val statusText = OrderStatus.getStatusText(order.status)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF0F1219)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Restaurante y estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        order.restaurantName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        statusText,
                        fontSize = 12.sp,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Fecha y hora
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color(0xFF8B92A1),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    formatDate(order.createdAt),
                    fontSize = 13.sp,
                    color = Color(0xFF8B92A1)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Items
            Text(
                "${order.items.size} ${if (order.items.size == 1) "platillo" else "platillos"}",
                fontSize = 14.sp,
                color = Color(0xFFB7BDC9)
            )

            // Muestra primeros 2 items
            order.items.take(2).forEach { item ->
                Text(
                    "• ${item.quantity}x ${item.dishName}",
                    fontSize = 13.sp,
                    color = Color(0xFF8B92A1),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }

            if (order.items.size > 2) {
                Text(
                    "• +${order.items.size - 2} más...",
                    fontSize = 13.sp,
                    color = Color(0xFF8B92A1),
                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            Divider(color = Color(0xFF232838))

            Spacer(Modifier.height(12.dp))

            // Footer: Total y botón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Total",
                        fontSize = 12.sp,
                        color = Color(0xFF8B92A1)
                    )
                    Text(
                        "$${String.format("%.2f", order.total)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }

                TextButton(
                    onClick = onClick,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFF4CAF50)
                    )
                ) {
                    Text("Ver detalles")
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
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