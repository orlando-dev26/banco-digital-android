package com.banco.digital.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Importante: Asegúrate de que esta línea exista para tu imagen
import com.banco.digital.R

// =====================================================================
// 1. PANTALLA DE LOGIN DESDE CERO (Selector de Doc integrado)
// =====================================================================
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onBiometricClick: () -> Unit = {}
) {
    var documentNumber by remember { mutableStateOf("") }
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 6

    // Variables para el Select (Dropdown)
    var expanded by remember { mutableStateOf(false) }
    val documentTypes = listOf("DNI", "CE", "PAS")
    var selectedDocType by remember { mutableStateOf(documentTypes[0]) }

    val keys = remember { (0..9).shuffled() }

    LaunchedEffect(pin) {
        if (pin.length == maxPinLength) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1.2f))

        Image(painter = painterResource(id = R.drawable.logollama2), contentDescription = "Logo", modifier = Modifier.size(90.dp))

        Spacer(modifier = Modifier.height(12.dp))
        Text("Ingresa a tu cuenta", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
        Spacer(modifier = Modifier.height(24.dp))

        // --- SOLUCIÓN APLICADA: UN SOLO RECTÁNGULO CON EL SELECTOR ADENTRO ---
        OutlinedTextField(
            value = documentNumber,
            onValueChange = { if (it.length <= 12) documentNumber = it },
            placeholder = { Text("Número de Doc.") },
            modifier = Modifier.fillMaxWidth(), // Ahora ocupa el 100% de la pantalla sin desbordarse
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF6EE7B7), unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            ),
            // Aquí metemos el botón selector en la parte izquierda del rectángulo
            leadingIcon = {
                Box {
                    Row(
                        modifier = Modifier
                            .clickable { expanded = true }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedDocType, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Cambiar documento", tint = Color(0xFF64748B))

                        // Una pequeña línea separadora visual
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.height(24.dp).width(1.dp).background(Color(0xFFE2E8F0)))
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        documentTypes.forEach { doc ->
                            DropdownMenuItem(
                                text = { Text(doc, color = Color(0xFF111827), fontWeight = FontWeight.Medium) },
                                onClick = { selectedDocType = doc; expanded = false }
                            )
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text("Ingresa tu clave web (6 dígitos)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        PinDots(pinLength = pin.length, maxPinLength = maxPinLength)
        Spacer(modifier = Modifier.height(24.dp))

        RandomKeypad(
            keys = keys,
            onNumberClick = { if (pin.length < maxPinLength) pin += it },
            onBiometricClick = onBiometricClick,
            onDeleteClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
        )

        Spacer(modifier = Modifier.height(24.dp))
        Text("¿Olvidaste tu clave web?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF064E3B), modifier = Modifier.clickable { })
        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "¿No tienes una cuenta? ", fontSize = 14.sp, color = Color(0xFF64748B))
            Text("Regístrate", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF064E3B), modifier = Modifier.clickable { onNavigateToRegister() })
        }
        Spacer(modifier = Modifier.weight(0.8f))
    }
}


// =====================================================================
// 2. PANTALLA DE LOGIN RÁPIDO (Usuario ya registrado en el celular)
// =====================================================================
@Composable
fun QuickLoginScreen(
    userName: String = "Orlando",
    onLoginSuccess: () -> Unit = {},
    onBiometricClick: () -> Unit = {},
    onChangeUserClick: () -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }
    val maxPinLength = 6
    val keys = remember { (0..9).shuffled() }

    LaunchedEffect(pin) {
        if (pin.length == maxPinLength) onLoginSuccess()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(painter = painterResource(id = R.drawable.logollama2), contentDescription = "Logo", modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(16.dp))

        Text("¡Hola, $userName!", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF111827))
        Text("Qué bueno verte de nuevo", fontSize = 15.sp, color = Color(0xFF64748B))

        Spacer(modifier = Modifier.height(40.dp))

        Text("Ingresa tu clave web", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF64748B))
        Spacer(modifier = Modifier.height(16.dp))

        PinDots(pinLength = pin.length, maxPinLength = maxPinLength)
        Spacer(modifier = Modifier.height(32.dp))

        RandomKeypad(
            keys = keys,
            onNumberClick = { if (pin.length < maxPinLength) pin += it },
            onBiometricClick = onBiometricClick,
            onDeleteClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) }
        )

        Spacer(modifier = Modifier.height(32.dp))
        Text("¿Olvidaste tu clave web?", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF064E3B), modifier = Modifier.clickable { })
        Spacer(modifier = Modifier.height(16.dp))

        Text("Cambiar de usuario", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B), modifier = Modifier.clickable { onChangeUserClick() })

        Spacer(modifier = Modifier.weight(1f))
    }
}


// =====================================================================
// COMPONENTES REUTILIZABLES (Teclado y Puntitos)
// =====================================================================
@Composable
fun PinDots(pinLength: Int, maxPinLength: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
        for (i in 0 until maxPinLength) {
            val isFilled = i < pinLength
            Box(modifier = Modifier.size(16.dp).clip(CircleShape).background(if (isFilled) Color(0xFF064E3B) else Color(0xFFCBD5E1)))
        }
    }
}

@Composable
fun RandomKeypad(keys: List<Int>, onNumberClick: (String) -> Unit, onBiometricClick: () -> Unit, onDeleteClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KeypadButton(keys[0].toString()) { onNumberClick(keys[0].toString()) }
            KeypadButton(keys[1].toString()) { onNumberClick(keys[1].toString()) }
            KeypadButton(keys[2].toString()) { onNumberClick(keys[2].toString()) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KeypadButton(keys[3].toString()) { onNumberClick(keys[3].toString()) }
            KeypadButton(keys[4].toString()) { onNumberClick(keys[4].toString()) }
            KeypadButton(keys[5].toString()) { onNumberClick(keys[5].toString()) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KeypadButton(keys[6].toString()) { onNumberClick(keys[6].toString()) }
            KeypadButton(keys[7].toString()) { onNumberClick(keys[7].toString()) }
            KeypadButton(keys[8].toString()) { onNumberClick(keys[8].toString()) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            KeypadIconButton(icon = Icons.Default.Fingerprint, tint = Color(0xFF064E3B)) { onBiometricClick() }
            KeypadButton(keys[9].toString()) { onNumberClick(keys[9].toString()) }
            KeypadIconButton(icon = Icons.Default.Backspace, tint = Color(0xFF64748B)) { onDeleteClick() }
        }
    }
}

@Composable
fun KeypadButton(text: String, onClick: () -> Unit) {
    Surface(modifier = Modifier.height(60.dp).width(76.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = Color.White, shadowElevation = 1.dp) {
        Box(contentAlignment = Alignment.Center) { Text(text = text, fontSize = 26.sp, fontWeight = FontWeight.Medium, color = Color(0xFF111827)) }
    }
}

@Composable
fun KeypadIconButton(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: Color, onClick: () -> Unit) {
    Surface(modifier = Modifier.height(60.dp).width(76.dp).clickable { onClick() }, shape = RoundedCornerShape(16.dp), color = Color.Transparent) {
        Box(contentAlignment = Alignment.Center) { Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(32.dp)) }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun QuickLoginScreenPreview() {
    QuickLoginScreen()
}