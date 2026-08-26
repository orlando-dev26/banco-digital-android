package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BiometricSetupScreen(
    onEnableBiometricsClick: () -> Unit = {},
    onSkipClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // Icono representativo de Biometría
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color(0xFFE0F2FE), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint, // Puede usarse un recurso visual (rostro/huella) aquí
                contentDescription = "Biometría",
                modifier = Modifier.size(64.dp),
                tint = Color(0xFF0369A1)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Acceso rápido y seguro",
            fontSize = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF111827),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Activa el reconocimiento facial o huella digital para no tener que ingresar tu PIN web en cada inicio de sesión.",
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        val mintGradient = Brush.linearGradient(
            colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
        )
        val primaryDarkText = Color(0xFF042F2C)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(brush = mintGradient)
                .clickable { onEnableBiometricsClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("Activar Biometría", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryDarkText)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Omitir por ahora",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier
                .clickable { onSkipClick() }
                .padding(8.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun BiometricSetupScreenPreview() {
    BiometricSetupScreen()
}
