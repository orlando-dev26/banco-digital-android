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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockReset
//import androidx.compose.material.icons.filled.ShieldAlert
import androidx.compose.material.icons.filled.Warning

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class NotificationType {
    TRANSFER_IN,
    TRANSFER_OUT,
    SECURITY_ALERT,
    SYSTEM_INFO
}

data class NotificationItemModel(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val isRead: Boolean,
    val type: NotificationType
)

@Composable
fun NotificationsScreenContent() {
    var selectedFilter by remember { mutableStateOf("Todas") }

    val notifications = remember {
        listOf(
            NotificationItemModel(
                id = "n-1",
                title = "Alerta de Seguridad",
                message = "Intento de inicio de sesión detectado desde una nueva IP. Se validó con biometría.",
                timestamp = "Hace 10 min",
                isRead = false,
                type = NotificationType.SECURITY_ALERT
            ),
            NotificationItemModel(
                id = "n-2",
                title = "Transferencia Recibida",
                message = "Has recibido S/ 850.00 de Juan Pérez a tu cuenta principal.",
                timestamp = "Hoy, 11:30",
                isRead = false,
                type = NotificationType.TRANSFER_IN
            ),
            NotificationItemModel(
                id = "n-3",
                title = "Transferencia Exitosa",
                message = "Se completó el pago de S/ 119.90 al servicio de Cable e Internet.",
                timestamp = "21 Ago, 09:30",
                isRead = true,
                type = NotificationType.TRANSFER_OUT
            ),
            NotificationItemModel(
                id = "n-4",
                title = "Políticas de Cuenta Actualizadas",
                message = "Revisa los límites operativos actualizados para transferencias interbancarias en PEN.",
                timestamp = "19 Ago, 16:00",
                isRead = true,
                type = NotificationType.SYSTEM_INFO
            )
        )
    }

    val filteredNotifications = remember(selectedFilter, notifications) {
        when (selectedFilter) {
            "Seguridad" -> notifications.filter { it.type == NotificationType.SECURITY_ALERT }
            "Movimientos" -> notifications.filter {
                it.type == NotificationType.TRANSFER_IN || it.type == NotificationType.TRANSFER_OUT
            }
            else -> notifications
        }
    }

    val backgroundColor = Color(0xFFF4F5F7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        NotificationsHeader()

        Spacer(modifier = Modifier.height(10.dp))

        // Barra de Filtros
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterTabPill(
                text = "Todas",
                isSelected = selectedFilter == "Todas",
                onClick = { selectedFilter = "Todas" }
            )
            FilterTabPill(
                text = "Seguridad",
                isSelected = selectedFilter == "Seguridad",
                onClick = { selectedFilter = "Seguridad" }
            )
            FilterTabPill(
                text = "Movimientos",
                isSelected = selectedFilter == "Movimientos",
                onClick = { selectedFilter = "Movimientos" }
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Lista de Notificaciones
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredNotifications, key = { it.id }) { item ->
                NotificationCardTile(notification = item)
            }
        }
    }
}

@Composable
private fun NotificationsHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Avisos y Seguridad",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF111827)
            )
            Text(
                text = "Eventos del sistema y transacciones",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 11.sp,
                color = Color(0xFF6B7280)
            )
        }

        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFFFFFFF),
            modifier = Modifier.size(38.dp)
        ) {
            IconButton(onClick = { /* Marcar todo como leído */ }) {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Marcar leídas",
                    tint = Color(0xFF064E3B),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun FilterTabPill(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) Color(0xFF064E3B) else Color(0xFFFFFFFF),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = text,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color(0xFFFFFFFF) else Color(0xFF4B5563),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun NotificationCardTile(notification: NotificationItemModel) {
    val (icon, iconTint, iconBg) = when (notification.type) {
        NotificationType.SECURITY_ALERT -> Triple(
            Icons.Default.Warning, // <-- Cambiado aquí
            Color(0xFFDC2626),
            Color(0xFFFEE2E2)
        )
        NotificationType.TRANSFER_IN -> Triple(
            Icons.Default.ArrowDownward,
            Color(0xFF16A34A),
            Color(0xFFDCFCE7)
        )
        NotificationType.TRANSFER_OUT -> Triple(
            Icons.Default.ArrowUpward,
            Color(0xFF064E3B),
            Color(0xFFE2E8F0)
        )
        NotificationType.SYSTEM_INFO -> Triple(
            Icons.Default.Info,
            Color(0xFF2563EB),
            Color(0xFFDBEAFE)
        )
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (notification.isRead) Color(0xFFFFFFFF) else Color(0xFFF8FAFC),
        shadowElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        fontSize = 13.sp,
                        fontWeight = if (notification.isRead) FontWeight.SemiBold else FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = notification.timestamp,
                            fontSize = 10.sp,
                            color = Color(0xFF94A3B8),
                            maxLines = 1,
                            softWrap = false
                        )
                        if (!notification.isRead) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF10B981))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 16.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationsScreenPreview() {
    NotificationsScreenContent()
}