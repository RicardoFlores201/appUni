package com.ejercicio.app.views

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ejercicio.app.navigation.AppScreen
import kotlinx.coroutines.delay

@Composable
fun BlankView(navController: NavHostController) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(200)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1A5F1F),
                        Color(0xFF2D7A34),
                        Color(0xFF4CAF50)
                    )
                )
            )
    ) {
        // Animated background blobs
        BackgroundBlobs()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(60.dp))

            // Logo y título
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800)) + scaleIn(
                    initialScale = 0.8f,
                    animationSpec = tween(800)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo circular con glassmorphism
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        shadowElevation = 0.dp
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.3f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        ) {
                            Icon(
                                Icons.Filled.Restaurant,
                                contentDescription = "Logo",
                                tint = Color.White,
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Text(
                        text = "Nutrideli",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 2.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        text = "Comida saludable a tu puerta",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Light,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            // Pregunta
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 300))
            ) {
                Text(
                    text = "¿Cómo deseas ingresar?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            // Botones con glassmorphism
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 400)) +
                        slideInVertically(
                            initialOffsetY = { 60 },
                            animationSpec = tween(800, delayMillis = 400)
                        )
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GlassButton(
                        icon = Icons.Filled.Person,
                        title = "Soy Cliente",
                        subtitle = "Explora menús saludables",
                        primary = true,
                        onClick = {
                            navController.navigate(AppScreen.UserLogin.route) {
                                popUpTo(AppScreen.Blank.route) { inclusive = true }
                            }
                        }
                    )

                    GlassButton(
                        icon = Icons.Filled.Storefront,
                        title = "Soy Restaurante",
                        subtitle = "Gestiona tu negocio",
                        primary = false,
                        onClick = {
                            navController.navigate(AppScreen.RestaurantLogin.route) {
                                popUpTo(AppScreen.Blank.route) { inclusive = true }
                            }
                        }
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Features badges
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(800, delayMillis = 600))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FeatureBadge(Icons.Filled.Eco, "Vegano")
                    Spacer(Modifier.width(8.dp))
                    Text("•", color = Color.White.copy(0.5f), fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    FeatureBadge(Icons.Filled.HealthAndSafety, "Sin Gluten")
                    Spacer(Modifier.width(8.dp))
                    Text("•", color = Color.White.copy(0.5f), fontSize = 20.sp)
                    Spacer(Modifier.width(8.dp))
                    FeatureBadge(Icons.Filled.LocalFlorist, "Orgánico")
                }
            }

            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun BackgroundBlobs() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Blob 1
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF81C784).copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
                .blur(50.dp)
        )

        // Blob 2
        Box(
            modifier = Modifier
                .size(250.dp)
                .align(Alignment.TopEnd)
                .offset(x = 80.dp, y = 150.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF66BB6A).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
                .blur(60.dp)
        )

        // Blob 3
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomStart)
                .offset(x = 50.dp, y = 100.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        listOf(
                            Color(0xFF4CAF50).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .blur(40.dp)
        )
    }
}

@Composable
private fun GlassButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    primary: Boolean,
    onClick: () -> Unit
) {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Surface(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(24.dp),
        color = if (primary)
            Color.White.copy(alpha = 0.95f)
        else
            Color.White.copy(alpha = 0.15f),
        border = if (!primary)
            androidx.compose.foundation.BorderStroke(1.5.dp, Color.White.copy(alpha = 0.3f))
        else null,
        shadowElevation = if (primary) 12.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon container
            Surface(
                shape = CircleShape,
                color = if (primary)
                    Color(0xFF4CAF50).copy(alpha = 0.15f)
                else
                    Color.White.copy(alpha = 0.2f),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = if (primary) Color(0xFF2E7D32) else Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            // Text
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (primary) Color(0xFF1B5E20) else Color.White
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = if (primary)
                        Color(0xFF1B5E20).copy(alpha = 0.7f)
                    else
                        Color.White.copy(alpha = 0.8f),
                    lineHeight = 18.sp
                )
            }

            // Arrow
            Icon(
                Icons.Filled.ArrowForward,
                contentDescription = null,
                tint = if (primary)
                    Color(0xFF2E7D32).copy(alpha = 0.6f)
                else
                    Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun FeatureBadge(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text,
            fontSize = 13.sp,
            color = Color.White.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
    }
}