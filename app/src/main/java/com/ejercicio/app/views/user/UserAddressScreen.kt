package com.ejercicio.app.views.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

data class Address(
    val id: String = "",
    val name: String = "",
    val street: String = "",
    val city: String = "",
    val state: String = "",
    val zipCode: String = "",
    val phone: String = "",
    val instructions: String = "",
    val isDefault: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddressesScreen(
    navController: NavHostController
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF05060A),
            Color(0xFF0A0D14),
            Color(0xFF05060A)
        )
    )

    // TODO: Cargar direcciones desde Firebase
    var addresses by remember { mutableStateOf<List<Address>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Mis direcciones",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
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
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    // TODO: Navegar a agregar dirección
                },
                containerColor = Color(0xFF4CAF50),
                contentColor = Color.White
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Agregar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
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
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            } else if (addresses.isEmpty()) {
                // Estado vacío
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier.size(120.dp),
                            shape = RoundedCornerShape(60.dp),
                            color = Color(0xFF1A2F2A)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF4CAF50),
                                    modifier = Modifier.size(64.dp)
                                )
                            }
                        }

                        Spacer(Modifier.height(32.dp))

                        Text(
                            "No tienes direcciones guardadas",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Agrega tu primera dirección de entrega\npara recibir tus pedidos",
                            fontSize = 15.sp,
                            color = Color(0xFFB7BDC9),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )

                        Spacer(Modifier.height(32.dp))

                        Button(
                            onClick = {
                                // TODO: Navegar a agregar dirección
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Agregar dirección",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            } else {
                // Lista de direcciones
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(addresses) { address ->
                        AddressCard(
                            address = address,
                            onEdit = {
                                // TODO: Editar dirección
                            },
                            onDelete = {
                                // TODO: Eliminar dirección
                            },
                            onSetDefault = {
                                // TODO: Establecer como predeterminada
                            }
                        )
                    }

                    // Espaciado para el FAB
                    item {
                        Spacer(Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSetDefault: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            // Header con nombre y badge de default
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Home,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        address.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (address.isDefault) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF1A2F2A)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                "Predeterminada",
                                fontSize = 11.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Dirección
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF8B92A1),
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        address.street,
                        fontSize = 14.sp,
                        color = Color(0xFFB7BDC9)
                    )
                    Text(
                        "${address.city}, ${address.state} ${address.zipCode}",
                        fontSize = 14.sp,
                        color = Color(0xFF8B92A1)
                    )
                }
            }

            if (address.phone.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Phone,
                        contentDescription = null,
                        tint = Color(0xFF8B92A1),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        address.phone,
                        fontSize = 14.sp,
                        color = Color(0xFFB7BDC9)
                    )
                }
            }

            if (address.instructions.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.Info,
                        contentDescription = null,
                        tint = Color(0xFF8B92A1),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        address.instructions,
                        fontSize = 13.sp,
                        color = Color(0xFF8B92A1),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!address.isDefault) {
                    OutlinedButton(
                        onClick = onSetDefault,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4CAF50)
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFF4CAF50)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Predeterminada", fontSize = 13.sp)
                    }
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2196F3)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("Editar", fontSize = 13.sp)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = null,
                        tint = Color(0xFFE53935)
                    )
                }
            }
        }
    }
}