package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TransactionDetailScreen(
    title: String = "Supermercado Metro",
    category: String = "Tarjeta Débito • Alimentación",
    dateTime: String = "24 Ago 2026, 14:20",
    amount: String = "- S/ 85.50",
    operationId: String = "OP-10293847",
    referenceAccount: String = "•••• 4321",
    isExpense: Boolean = true,
    onNavigateBack: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(24.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Cabecera con botón de retroceso
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(shape = RoundedCornerShape(12.dp), color = Color.White, modifier = Modifier.size(40.dp)) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar", tint = Color(0xFF111827))
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text("Detalle del Movimiento", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono Central
                Box(
                    modifier = Modifier.size(64.dp).clip(CircleShape).background(Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                Text(text = category, fontSize = 12.sp, color = Color(0xFF64748B))

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = amount,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isExpense) Color(0xFFDC2626) else Color(0xFF16A34A)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Fila de datos
                DetailDataRow(label = "Fecha y Hora", value = dateTime)
                Spacer(modifier = Modifier.height(16.dp))
                DetailDataRow(label = "Cuenta Origen/Destino", value = referenceAccount)
                Spacer(modifier = Modifier.height(16.dp))
                DetailDataRow(label = "Nro. de Operación", value = operationId)
            }
        }
    }
}

@Composable
private fun DetailDataRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransactionDetailScreenPreview() {
    TransactionDetailScreen()
}