package com.banco.digital.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Step2CrearPassword(
    password: String,
    confirmPassword: String,
    onPasswordChange: (password: String, confirmPassword: String) -> Unit,
    onContinue: () -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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

    // Validaciones de seguridad de contraseña
    val hasMinLength = password.length >= 8
    val hasUppercase = password.any { it.isUpperCase() }
    val hasLowercase = password.any { it.isLowerCase() }
    val hasNumber = password.any { it.isDigit() }
    val hasSpecialChar = password.any { !it.isLetterOrDigit() }
    val doPasswordsMatch = password.isNotEmpty() && password == confirmPassword

    val metCount = listOf(hasMinLength, hasUppercase, hasLowercase, hasNumber, hasSpecialChar).count { it }

    val strengthLabel = when (metCount) {
        0, 1, 2 -> "Débil"
        3, 4 -> "Media"
        5 -> "Fuerte"
        else -> "Débil"
    }

    val strengthColor by animateColorAsState(
        targetValue = when (metCount) {
            0, 1, 2 -> Color(0xFFEF4444)
            3, 4 -> Color(0xFFF59E0B)
            5 -> Color(0xFF10B981)
            else -> Color(0xFFE2E8F0)
        },
        label = "StrengthColor"
    )

    val isStep2Valid = metCount == 5 && doPasswordsMatch

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Crea tu Contraseña",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Define una clave de acceso segura para entrar a tu banca digital",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Campo de Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { onPasswordChange(it, confirmPassword) },
            placeholder = { Text("Nueva Contraseña", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar contraseña", tint = Color(0xFF64748B))
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        // Barra de fortaleza de contraseña
        if (password.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 1..5) {
                        val isFilled = i <= metCount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(6.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) strengthColor else Color(0xFFE2E8F0))
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = strengthLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = strengthColor
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Confirmar Contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { onPasswordChange(password, it) },
            placeholder = { Text("Confirmar Contraseña", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar contraseña", tint = Color(0xFF64748B))
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        if (confirmPassword.isNotEmpty() && !doPasswordsMatch) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Las contraseñas no coinciden",
                fontSize = 12.sp,
                color = Color(0xFFDC2626),
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Checklist de Requisitos de Seguridad
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF8FAFC),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color(0xFF064E3B),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Requisitos de seguridad",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                PasswordRequirementItem(label = "Mínimo 8 caracteres", isMet = hasMinLength)
                Spacer(modifier = Modifier.height(8.dp))
                PasswordRequirementItem(label = "Al menos una letra mayúscula (A-Z)", isMet = hasUppercase)
                Spacer(modifier = Modifier.height(8.dp))
                PasswordRequirementItem(label = "Al menos una letra minúscula (a-z)", isMet = hasLowercase)
                Spacer(modifier = Modifier.height(8.dp))
                PasswordRequirementItem(label = "Al menos un número (0-9)", isMet = hasNumber)
                Spacer(modifier = Modifier.height(8.dp))
                PasswordRequirementItem(label = "Al menos un símbolo o carácter especial (!@#$%^&*-_)", isMet = hasSpecialChar)
            }
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
                text = "Continuar a Términos",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep2Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }
    }
}

@Composable
fun PasswordRequirementItem(label: String, isMet: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = if (isMet) Color(0xFFDCFCE7) else Color(0xFFF1F5F9),
            modifier = Modifier.size(20.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isMet) Icons.Default.Check else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (isMet) Color(0xFF064E3B) else Color(0xFF94A3B8),
                    modifier = Modifier.size(12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = if (isMet) Color(0xFF0F172A) else Color(0xFF64748B),
            fontWeight = if (isMet) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
