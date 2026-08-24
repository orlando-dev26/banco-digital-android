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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
fun TransferResultScreen(
    destinationAccount: String = "00110123456789",
    amount: String = "150.00",
    description: String = "Pago de servicios",
    idempotencyKey: String = "txn-8f7a-4b2c",
    onNavigateHome: () -> Unit = {}
) {
    val currentDateTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES")).format(Date())

    // 1. Degradado mucho más suave y ligero para el botón
    val softMintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5), Color(0xFFA7F3D0))
    )
    val primaryDarkText = Color(0xFF042F2C)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7)) // Fondo claro consistente
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // 2. Icono de Éxito con verde "Punto Medio"
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF10B981)), // Verde esmeralda medio, fácil de identificar
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Éxito",
                tint = Color.White, // Check en blanco para que resalte perfecto
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Transferencia Exitosa!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = "El dinero ya fue enviado al destinatario.",
            fontSize = 14.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Tarjeta tipo "Voucher"
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
                Text(text = "Importe Transferido", fontSize = 13.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "S/ $amount",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF111827)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Línea punteada divisoria gris suave
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .drawBehind {
                            drawLine(
                                color = Color(0xFFE2E8F0),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 3f,
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 15f), 0f)
                            )
                        }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detalles de la operación
                VoucherDetailRow(label = "Para", value = "Cuenta terminada en ${destinationAccount.takeLast(4)}")
                Spacer(modifier = Modifier.height(16.dp))
                VoucherDetailRow(label = "Concepto", value = description)
                Spacer(modifier = Modifier.height(16.dp))
                VoucherDetailRow(label = "Fecha y hora", value = currentDateTime)
                Spacer(modifier = Modifier.height(16.dp))
                VoucherDetailRow(label = "Nro. Operación", value = idempotencyKey.take(8).uppercase())
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Botón con el nuevo degradado suavizado
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush = softMintGradient)
                .clickable { onNavigateHome() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Volver al Inicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = primaryDarkText
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun VoucherDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.sp, color = Color(0xFF64748B))
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF111827),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TransferResultScreenPreview() {
    TransferResultScreen()
}