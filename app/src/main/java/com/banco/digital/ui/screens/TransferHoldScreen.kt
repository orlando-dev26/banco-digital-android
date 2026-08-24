package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransferHoldScreen(
    amount: String = "4,500.00",
    idempotencyKey: String = "txn-9x8y-7z6w",
    onNavigateHome: () -> Unit = {}
) {
    val currentDateTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES")).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Icono de Advertencia (Ámbar)
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFFFDE68A)), // Amarillo suave
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.WarningAmber,
                contentDescription = "En revisión",
                tint = Color(0xFFD97706), // Naranja oscuro
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Operación en Revisión",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = "Por tu seguridad, estamos validando esta transferencia. Te notificaremos en breve.",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

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
                Text(text = "Importe Retenido", fontSize = 13.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/ $amount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFD97706) // Importe en naranja para destacar el estado
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                    modifier = Modifier.fillMaxWidth().height(1.dp).drawBehind {
                        drawLine(
                            color = Color(0xFFE2E8F0), start = Offset(0f, 0f), end = Offset(size.width, 0f),
                            strokeWidth = 3f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                HoldDetailRow(label = "Estado", value = "Validación de Seguridad")
                Spacer(modifier = Modifier.height(16.dp))
                HoldDetailRow(label = "Fecha y hora", value = currentDateTime)
                Spacer(modifier = Modifier.height(16.dp))
                HoldDetailRow(label = "Nro. Operación", value = idempotencyKey.take(8).uppercase())
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón gris oscuro (Neutro)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E293B))
                .clickable { onNavigateHome() },
            contentAlignment = Alignment.Center
        ) {
            Text("Entendido", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun HoldDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF64748B))
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF111827))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransferHoldScreenPreview() {
    TransferHoldScreen()
}