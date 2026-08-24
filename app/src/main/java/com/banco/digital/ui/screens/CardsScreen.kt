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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AccountCardModel(
    val id: String,
    val alias: String,
    val accountNumber14: String,
    val balance: String,
    val currency: String = "PEN",
    val isBlocked: Boolean = false,
    val isPrimary: Boolean = true
)

@Composable
fun CardsScreenContent(
    userName: String = "Orlando"
) {
    var isBalanceVisible by remember { mutableStateOf(true) }
    var isCardLocked by remember { mutableStateOf(false) }
    var onlinePurchasesEnabled by remember { mutableStateOf(true) }

    val primaryAccount = remember(isCardLocked) {
        AccountCardModel("acc-001", "Cuenta Digital Principal", "00110123456789", "S/ 4,580.50", "PEN", isCardLocked, true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
    ) {
        CardsHeaderSection()
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { MainDebitCardView(primaryAccount, userName, isBalanceVisible) { isBalanceVisible = !isBalanceVisible } }
            item { AccountNumberCopyTile(primaryAccount.accountNumber14) }
            item { CardSecurityManagementPanel(isCardLocked, { isCardLocked = it }, onlinePurchasesEnabled, { onlinePurchasesEnabled = it }) }
            item { AccountDetailsInfoCard(primaryAccount.currency, if (primaryAccount.isBlocked) "Bloqueada" else "Activa") }
        }
    }
}

@Composable
private fun CardsHeaderSection() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Mis Cuentas y Tarjetas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFF111827))
            Text("Gestión y seguridad de fondos", fontSize = 11.sp, color = Color(0xFF6B7280))
        }
        Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFFFFF), modifier = Modifier.size(38.dp)) {
            IconButton(onClick = { }) { Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF064E3B), modifier = Modifier.size(20.dp)) }
        }
    }
}

@Composable
private fun MainDebitCardView(account: AccountCardModel, userName: String, isBalanceVisible: Boolean, onToggleVisibility: () -> Unit) {
    val mintGradient = Brush.linearGradient(colors = if (account.isBlocked) listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1), Color(0xFF94A3B8)) else listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7)))
    val textColor = if (account.isBlocked) Color(0xFF334155) else Color(0xFF064E3B)

    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp), color = Color.Transparent) {
        Column(modifier = Modifier.background(brush = mintGradient).padding(18.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (account.isBlocked) Icons.Default.Lock else Icons.Default.CreditCard, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (account.isBlocked) "Tarjeta Bloqueada" else account.alias, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = textColor)
                }
                Text("PEN", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor)
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(account.accountNumber14.chunked(4).joinToString(" "), fontSize = 17.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.2.sp, color = textColor)
            Spacer(modifier = Modifier.height(4.dp))
            Text(userName.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.85f))
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onToggleVisibility() }) {
                        Text("Saldo Disponible", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.9f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff, contentDescription = null, tint = textColor, modifier = Modifier.size(13.dp))
                    }
                    Text(if (isBalanceVisible) account.balance else "S/ ••••••", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold, color = textColor)
                }
                Text("BANCO DIGITAL", fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp, color = textColor.copy(alpha = 0.65f))
            }
        }
    }
}

@Composable
private fun AccountNumberCopyTile(accountNumber: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color(0xFFFFFFFF)) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(Color(0xFFF1F5F9)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF064E3B), modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Número de Cuenta (14 dígitos)", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(accountNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }
            IconButton(onClick = { }, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color(0xFF064E3B), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun CardSecurityManagementPanel(isCardLocked: Boolean, onLockChanged: (Boolean) -> Unit, onlinePurchasesEnabled: Boolean, onOnlinePurchasesChanged: (Boolean) -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), color = Color(0xFFFFFFFF)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Seguridad y Control Operativo", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(if (isCardLocked) Color(0xFFFEE2E2) else Color(0xFFF1F5F9)), contentAlignment = Alignment.Center) {
                        Icon(if (isCardLocked) Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = null, tint = if (isCardLocked) Color(0xFFDC2626) else Color(0xFF064E3B), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Congelar / Bloquear Cuenta", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                        Text(if (isCardLocked) "Operaciones pausadas" else "Cuenta habilitada", fontSize = 10.sp, color = Color(0xFF6B7280))
                    }
                }
                Switch(checked = isCardLocked, onCheckedChange = onLockChanged, colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFFDC2626), uncheckedTrackColor = Color(0xFFE2E8F0)))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFF1F5F9)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF064E3B), modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Transferencias Salientes", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                        Text("Permitir débitos a terceros", fontSize = 10.sp, color = Color(0xFF6B7280))
                    }
                }
                Switch(checked = onlinePurchasesEnabled, onCheckedChange = onOnlinePurchasesChanged, colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF064E3B), uncheckedTrackColor = Color(0xFFE2E8F0)))
            }
        }
    }
}

@Composable
private fun AccountDetailsInfoCard(currency: String, status: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), color = Color(0xFFF1F5F9)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Información Contable y Operativa", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Moneda del Sistema", fontSize = 11.sp, color = Color(0xFF64748B))
                Text("$currency (Soles Peruanos)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Estado de Cuenta", fontSize = 11.sp, color = Color(0xFF64748B))
                Text(status, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (status == "Activa") Color(0xFF16A34A) else Color(0xFFDC2626))
            }
        }
    }
}