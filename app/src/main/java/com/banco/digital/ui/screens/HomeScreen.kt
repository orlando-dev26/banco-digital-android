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
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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

@Composable
fun HomeScreenContent(
    userName: String = "Orlando",
    cardNumber: String = "•••• •••• •••• 3279",
    expDate: String = "08/28",
    balance: String = "S/ 4,580.50",
    onNavigateToTransfer: () -> Unit = {}, // <--- AGREGA ESTO
    onNavigateToTransactionDetail: () -> Unit = {} // <--- AGREGA ESTO
) {
    var selectedTab by remember { mutableStateOf("Gastos") }
    val backgroundColor = Color(0xFFF4F5F7)

    val sampleTransactions = remember {
        listOf(
            TransactionItemModel("1", "Supermercado Metro", "Tarjeta Débito • Alimentación", "Hoy, 14:20", "- S/ 85.50", true, Icons.Default.ShoppingCart),
            TransactionItemModel("2", "Restaurante Central", "Tarjeta Débito • Gastronomía", "Ayer, 20:15", "- S/ 140.00", true, Icons.Default.Restaurant),
            TransactionItemModel("3", "Servicio de Internet", "Transferencia • Hogar", "21 Ago, 09:30", "- S/ 119.90", true, Icons.Default.Wifi)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        HeaderSection(userName = userName)

        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            CompactCreditCard(userName, cardNumber, expDate, balance)
        }

        Spacer(modifier = Modifier.height(12.dp))

        QuickActionsSection(onNavigateToTransfer = onNavigateToTransfer) // <--- ACTUALIZA ESTO

        Spacer(modifier = Modifier.height(14.dp))

        TransactionsPanel(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            transactions = sampleTransactions,
            onItemClick = onNavigateToTransactionDetail, // <--- AGREGA ESTO AQUÍ
            modifier = Modifier.weight(1f)
        )
    }
}

// Conservamos los sub-componentes idénticos (HeaderSection, CompactCreditCard, etc.)
@Composable
private fun HeaderSection(userName: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f, fill = false)) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFA7F3D0)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = userName.firstOrNull()?.toString() ?: "U", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF064E3B))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = "Bienvenido,", fontSize = 11.sp, color = Color(0xFF6B7280))
                Text(text = "¡Hola, $userName!", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF111827), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFFFFF), modifier = Modifier.size(38.dp)) {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFF374151), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun CompactCreditCard(userName: String, cardNumber: String, expDate: String, balance: String) {
    val mintGradient = Brush.linearGradient(colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7)))
    val darkGreenColor = Color(0xFF064E3B)
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Color.Transparent) {
        Column(modifier = Modifier.background(brush = mintGradient).padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Cuenta Débito Principal", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = darkGreenColor)
                Text("Vence: $expDate", fontSize = 11.sp, fontWeight = FontWeight.Medium, color = darkGreenColor)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(cardNumber, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF042F2C), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(userName.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = darkGreenColor)
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Text("Saldo Total", fontSize = 10.sp, color = darkGreenColor)
                    Text(balance, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = darkGreenColor)
                }
                Text("BANCO DIGITAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF042F2C).copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
private fun QuickActionsSection(onNavigateToTransfer: () -> Unit = {}) { // <--- RECIBE EL PARÁMETRO AQUÍ
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        QuickActionButton("Enviar", Icons.Default.NorthEast, Modifier.weight(1f)) {
            onNavigateToTransfer() // <--- LE DAMOS LA ACCIÓN AQUÍ
        }
        QuickActionButton("Solicitar", Icons.Default.SouthWest, Modifier.weight(1f)) { }
        QuickActionButton("Más", Icons.Default.MoreHoriz, Modifier.weight(1f)) { }
    }
}

@Composable
private fun QuickActionButton(title: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(modifier = modifier.clip(RoundedCornerShape(16.dp)).clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = Color(0xFFFFFFFF)) {
        Column(modifier = Modifier.padding(vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(34.dp).clip(CircleShape).background(Color(0xFFF4F5F7)), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = Color(0xFF111827), modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
        }
    }
}

@Composable
private fun TransactionsPanel(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    transactions: List<TransactionItemModel>,
    onItemClick: () -> Unit = {}, // <--- AGREGA ESTE PARÁMETRO
    modifier: Modifier = Modifier
    ) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), color = Color(0xFFFFFFFF)) {
        Column(modifier = Modifier.fillMaxSize().padding(top = 14.dp, start = 16.dp, end = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Movimientos", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Row(modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(Color(0xFFF4F5F7)).padding(2.dp)) {
                    FlatTabPill("Gastos", selectedTab == "Gastos") { onTabSelected("Gastos") }
                    FlatTabPill("Ingresos", selectedTab == "Ingresos") { onTabSelected("Ingresos") }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(transactions, key = { it.id }) { item ->
                    TransactionRow(item = item, onClick = onItemClick) //
                }
            }
        }
    }
}

@Composable
private fun FlatTabPill(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(if (isSelected) Color(0xFFFFFFFF) else Color.Transparent).clickable { onClick() }.padding(horizontal = 8.dp, vertical = 4.dp), contentAlignment = Alignment.Center) {
        Text(text, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, color = if (isSelected) Color(0xFF111827) else Color(0xFF6B7280))
    }
}

@Composable
private fun TransactionRow(
    item: TransactionItemModel,
    onClick: () -> Unit = {} // <--- AGREGO ESTE PARÁMETRO
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1F5F9))
            .clickable { onClick() } // <--- AGREGA ESTA LÍNEA PARA QUE SEA CLICKEABLE
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(modifier = Modifier.size(42.dp).clip(CircleShape).background(Color(0xFFE2E8F0)), contentAlignment = Alignment.Center) {
                Icon(item.icon, contentDescription = null, tint = Color(0xFF1E293B), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.category, fontSize = 11.sp, color = Color(0xFF64748B), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.dateTime, fontSize = 10.sp, color = Color(0xFF94A3B8))
            }
        }
        Text(item.amount, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = if (item.isExpense) Color(0xFFDC2626) else Color(0xFF16A34A))
    }
}