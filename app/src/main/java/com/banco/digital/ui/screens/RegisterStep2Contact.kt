package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step2DatosContacto(
    correo: String,
    celular: String,
    onDataChange: (correo: String, celular: String) -> Unit,
    onContinue: () -> Unit
) {
    val mintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
    )
    val primaryDarkText = Color(0xFF042F2C)

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF6EE7B7),
        unfocusedBorderColor = Color(0xFFE2E8F0),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White,
        disabledContainerColor = Color.White,
        disabledBorderColor = Color(0xFFE2E8F0),
        disabledTextColor = Color(0xFF111827)
    )

    // Validaciones
    val isEmailValid = correo.trim().contains("@") && correo.trim().contains(".")
    val isPhoneValid = celular.trim().length >= 9

    val isStep2Valid = isEmailValid && isPhoneValid

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Datos de Contacto",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Ingresa tu correo y número de teléfono para notificaciones",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Correo Electrónico
        OutlinedTextField(
            value = correo,
            onValueChange = { onDataChange(it, celular) },
            placeholder = { Text("Correo electrónico (ej: usuario@correo.com)", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Celular con código de país
        var expandedCountryCode by remember { mutableStateOf(false) }
        var selectedCountryCode by remember { mutableStateOf("🇵🇪 +51") }
        val countryCodes = listOf(
            "🇵🇪 +51",   // Perú
            "🇨🇴 +57",   // Colombia
            "🇲🇽 +52",   // México
            "🇦🇷 +54",   // Argentina
            "🇨🇱 +56",   // Chile
            "🇪🇨 +593",  // Ecuador
            "🇧🇴 +591",  // Bolivia
            "🇧🇷 +55",   // Brasil
            "🇪🇸 +34",   // España
            "🇺🇸 +1"     // Estados Unidos
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ExposedDropdownMenuBox(
                expanded = expandedCountryCode,
                onExpandedChange = { expandedCountryCode = it },
                modifier = Modifier.weight(0.40f)
            ) {
                OutlinedTextField(
                    value = selectedCountryCode,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCountryCode) },
                    modifier = Modifier.menuAnchor(),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp)
                )
                ExposedDropdownMenu(
                    expanded = expandedCountryCode,
                    onDismissRequest = { expandedCountryCode = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    countryCodes.forEach { code ->
                        DropdownMenuItem(
                            text = { Text(code, fontWeight = FontWeight.Medium) },
                            onClick = {
                                selectedCountryCode = code
                                expandedCountryCode = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = celular,
                onValueChange = {
                    if (it.all { char -> char.isDigit() } && it.length <= 9) {
                        onDataChange(correo, it)
                    }
                },
                placeholder = { Text("987654321", color = Color(0xFF94A3B8)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                modifier = Modifier.weight(0.60f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Botón Continuar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = if (isStep2Valid) mintGradient else Brush.linearGradient(
                        listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                    )
                )
                .clickable(enabled = isStep2Valid) {
                    if (isStep2Valid) onContinue()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continuar a Crear Contraseña",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep2Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }

        // Espaciado extra para que el teclado no tape el botón
        Spacer(modifier = Modifier.height(120.dp))
    }
}
