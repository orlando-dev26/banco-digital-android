package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class TransactionItemModel(
    val id: String,
    val title: String,
    val category: String,
    val dateTime: String,
    val amount: String,
    val isExpense: Boolean,
    val icon: ImageVector
)

data class NavItem(
    val label: String,
    val icon: ImageVector
)

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    userName: String = "Orlando",
    cardNumber: String = "•••• •••• •••• 3279",
    expDate: String = "08/28",
    balance: String = "S/ 4,580.50"
) {
    var selectedTab by remember { mutableStateOf("Gastos") }
    var selectedNavIndex by remember { mutableIntStateOf(0) }

    val navItems = remember {
        listOf(
            NavItem("Inicio", Icons.Default.Home),
            NavItem("Tarjetas", Icons.Default.CreditCard),
            NavItem("Notificaciones", Icons.Default.Notifications),
            NavItem("Perfil", Icons.Default.Person)
        )
    }

    val sampleTransactions = remember {
        listOf(
            TransactionItemModel(
                id = "1",
                title = "Supermercado Metro",
                category = "Tarjeta Débito • Alimentación",
                dateTime = "Hoy, 14:20",
                amount = "- S/ 85.50",
                isExpense = true,
                icon = Icons.Default.ShoppingCart
            ),
            TransactionItemModel(
                id = "2",
                title = "Restaurante Central",
                category = "Tarjeta Débito • Gastronomía",
                dateTime = "Ayer, 20:15",
                amount = "- S/ 140.00",
                isExpense = true,
                icon = Icons.Default.Restaurant
            ),
            TransactionItemModel(
                id = "3",
                title = "Servicio de Internet y Cable",
                category = "Transferencia • Hogar",
                dateTime = "21 Ago, 09:30",
                amount = "- S/ 119.90",
                isExpense = true,
                icon = Icons.Default.Wifi
            )
        )
    }

    val backgroundColor = Color(0xFFF4F5F7)

    Scaffold(
        modifier = modifier,
        containerColor = backgroundColor,
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
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                fontSize = 11.sp,
                                maxLines = 1,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(backgroundColor)
        ) {
            HeaderSection(userName = userName)

            Spacer(modifier = Modifier.height(10.dp))

            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                CompactCreditCard(
                    userName = userName,
                    cardNumber = cardNumber,
                    expDate = expDate,
                    balance = balance
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            QuickActionsSection()

            Spacer(modifier = Modifier.height(16.dp))

            TransactionsPanel(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                transactions = sampleTransactions,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HeaderSection(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFA7F3D0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userName.firstOrNull()?.toString() ?: "U",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF064E3B)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "Bienvenido,",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
                Text(
                    text = "¡Hola, $userName!",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFFFFFFF),
            shadowElevation = 0.dp,
            tonalElevation = 0.dp,
            modifier = Modifier.size(40.dp)
        ) {
            IconButton(onClick = { /* Ajustes */ }) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Configuración",
                    tint = Color(0xFF374151)
                )
            }
        }
    }
}

@Composable
private fun CompactCreditCard(
    userName: String,
    cardNumber: String,
    expDate: String,
    balance: String
) {
    val mintGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFDCFCE7),
            Color(0xFFA7F3D0),
            Color(0xFF6EE7B7)
        )
    )

    val primaryTextColor = Color(0xFF042F2C)
    val darkGreenColor = Color(0xFF064E3B)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent,
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .background(brush = mintGradient)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cuenta Débito Principal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = darkGreenColor,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Vence: $expDate",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = darkGreenColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = cardNumber,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = primaryTextColor,
                maxLines = 1,
                softWrap = false
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = userName.uppercase(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = darkGreenColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Saldo Total",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = darkGreenColor
                    )
                    Text(
                        text = balance,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = darkGreenColor
                    )
                }

                Text(
                    text = "BANCO DIGITAL",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = primaryTextColor.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun QuickActionsSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionButton(
            title = "Enviar",
            icon = Icons.Default.NorthEast,
            modifier = Modifier.weight(1f),
            onClick = { /* Enviar */ }
        )
        QuickActionButton(
            title = "Solicitar",
            icon = Icons.Default.SouthWest,
            modifier = Modifier.weight(1f),
            onClick = { /* Solicitar */ }
        )
        QuickActionButton(
            title = "Más",
            icon = Icons.Default.MoreHoriz,
            modifier = Modifier.weight(1f),
            onClick = { /* Más */ }
        )
    }
}

@Composable
private fun QuickActionButton(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFFFFFFF),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF4F5F7)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color(0xFF111827),
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF374151),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TransactionsPanel(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    transactions: List<TransactionItemModel>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = Color(0xFFFFFFFF),
        shadowElevation = 0.dp,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Movimientos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.width(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF4F5F7))
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FlatTabPill(
                        text = "Gastos",
                        isSelected = selectedTab == "Gastos",
                        onClick = { onTabSelected("Gastos") }
                    )
                    FlatTabPill(
                        text = "Ingresos",
                        isSelected = selectedTab == "Ingresos",
                        onClick = { onTabSelected("Ingresos") }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions, key = { it.id }) { item ->
                    TransactionRow(item = item)
                }
            }
        }
    }
}

@Composable
private fun FlatTabPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFFFFFFFF) else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFF111827) else Color(0xFF6B7280)
        )
    }
}


@Composable
private fun TransactionRow(item: TransactionItemModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF3F4F6)) // Gris visible de alto contraste (Slate 100)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            // Círculo blanco para que el icono resalte sobre el fondo gris de la tarjeta
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.category,
                    tint = Color(0xFF1E293B),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B),
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.dateTime,
                    fontSize = 10.sp,
                    color = Color(0xFF94A3B8),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = item.amount,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (item.isExpense) Color(0xFFDC2626) else Color(0xFF16A34A),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}