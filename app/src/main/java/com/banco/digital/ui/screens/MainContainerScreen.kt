package com.banco.digital.ui.screens

import androidx.activity.compose.BackHandler

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class BottomNavItem(val label: String, val icon: ImageVector)

@Composable
fun MainContainerScreen(
    onNavigateToTransfer: () -> Unit = {},
    onNavigateToTransactionDetail: () -> Unit = {} // <--- AGREGA ESTO
) { // <--- AGREGA ESTO
    var selectedNavIndex by rememberSaveable { mutableIntStateOf(0) }

    // ESTO MANEJA EL BOTÓN ATRÁS DENTRO DE LAS PESTAÑAS
    BackHandler(enabled = selectedNavIndex != 0) {
        selectedNavIndex = 0 // Si no estás en el Inicio, te regresa al Inicio
    }

    val navItems = listOf(
        BottomNavItem("Inicio", Icons.Default.Home),
        BottomNavItem("Tarjetas", Icons.Default.CreditCard),
        BottomNavItem("Avisos", Icons.Default.Notifications),
        BottomNavItem("Perfil", Icons.Default.Person)
    )

    Scaffold(
        containerColor = Color(0xFFF4F5F7),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFFFFFFFF),
                tonalElevation = 0.dp
            ) {
                navItems.forEachIndexed { index, item ->
                    val isSelected = selectedNavIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedNavIndex = index },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                modifier = Modifier.size(22.dp)
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 10.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF064E3B),
                            selectedTextColor = Color(0xFF064E3B),
                            indicatorColor = Color(0xFFDCFCE7),
                            unselectedIconColor = Color(0xFF9CA3AF),
                            unselectedTextColor = Color(0xFF9CA3AF)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Aquí es donde ocurre la magia: según el índice, mostramos una pantalla u otra
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedNavIndex) {
                0 -> HomeScreenContent(
                    onNavigateToTransfer = onNavigateToTransfer,
                    onNavigateToTransactionDetail = onNavigateToTransactionDetail // <--- CONÉCTALO AQUÍ
                ) // <--- CONÉCTALO AQUÍ
                1 -> CardsScreenContent()
                2 -> NotificationsScreenContent()
                3 -> ProfileScreenContent() // ¡Navegación 100% completa!
            }
        }
    }
}

@Composable
private fun PlaceholderProfileView(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF64748B)
        )
    }
}