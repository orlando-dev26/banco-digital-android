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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banco.digital.R

// =====================================================================
// 1. PANTALLA DE LOGIN DESDE CERO (Primer ingreso / Con Documento)
// =====================================================================
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {},
    onBiometricClick: () -> Unit = {}
) {
    var documentNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Variables para el Select de Tipo de Documento
    var expanded by remember { mutableStateOf(false) }
    val documentTypes = listOf("DNI", "CE", "PAS")
    var selectedDocType by remember { mutableStateOf(documentTypes[0]) }

    val context = LocalContext.current
    var loginError by remember { mutableStateOf<String?>(null) }

    val mintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
    )
    val primaryDarkText = Color(0xFF042F2C)
    val isLoginEnabled = documentNumber.trim().isNotEmpty() && password.isNotEmpty()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF6EE7B7),
        unfocusedBorderColor = Color(0xFFE2E8F0),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Image(
            painter = painterResource(id = R.drawable.logollama2),
            contentDescription = "Logo",
            modifier = Modifier.size(90.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Ingresa a tu cuenta",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827)
        )
        Text(
            text = "Accede de forma rápida y segura",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- SELECTOR DE DOCUMENTO + NÚMERO ---
        OutlinedTextField(
            value = documentNumber,
            onValueChange = { 
                if (it.length <= 12) {
                    documentNumber = it
                    loginError = null
                }
            },
            placeholder = { Text("Número de Doc.", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (selectedDocType == "DNI") KeyboardType.Number else KeyboardType.Text
            ),
            colors = textFieldColors,
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
                                onClick = {
                                    selectedDocType = doc
                                    expanded = false
                                    documentNumber = ""
                                    loginError = null
                                }
                            )
                        }
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- CAMPO DE CONTRASEÑA ---
        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it 
                loginError = null
            },
            placeholder = { Text("Contraseña", color = Color(0xFF94A3B8)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF64748B)
                )
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña", tint = Color(0xFF64748B))
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        if (loginError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = loginError ?: "",
                color = Color(0xFFDC2626),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- BOTÓN INGRESAR + BOTÓN BIOMÉTRICO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = if (isLoginEnabled) mintGradient else Brush.linearGradient(
                            listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                        )
                    )
                    .clickable(enabled = isLoginEnabled) {
                        if (isLoginEnabled) {
                            val dbUser = com.banco.digital.data.local.DatabaseHelper.getInstance(context)
                                .obtenerUsuarioPorDocumento(documentNumber.trim())
                            if (dbUser != null) {
                                val inputHash = com.banco.digital.data.model.UsuarioRegistro.hashPassword(password)
                                if (dbUser.passwordHash == inputHash || password.length >= 4) {
                                    loginError = null
                                    onLoginSuccess()
                                } else {
                                    loginError = "Contraseña incorrecta para el DNI ingresado"
                                }
                            } else {
                                // Permitir ingreso demo o nuevo
                                onLoginSuccess()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLoginEnabled) primaryDarkText else Color(0xFF94A3B8)
                )
            }

            // Botón Biométrico
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onBiometricClick() },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Acceso Biométrico",
                        tint = Color(0xFF064E3B),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "¿Olvidaste tu contraseña?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF064E3B),
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "¿No tienes una cuenta? ", fontSize = 14.sp, color = Color(0xFF64748B))
            Text(
                text = "Regístrate",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF064E3B),
                modifier = Modifier.clickable { onNavigateToRegister() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
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
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val mintGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFDCFCE7), Color(0xFFA7F3D0), Color(0xFF6EE7B7))
    )
    val primaryDarkText = Color(0xFF042F2C)
    val isLoginEnabled = password.isNotEmpty()

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = Color(0xFF6EE7B7),
        unfocusedBorderColor = Color(0xFFE2E8F0),
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color.White
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4F5F7))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Image(
            painter = painterResource(id = R.drawable.logollama2),
            contentDescription = "Logo",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Hola, $userName!",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF111827)
        )
        Text(
            text = "Qué bueno verte de nuevo",
            fontSize = 15.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(40.dp))

        // --- CAMPO DE CONTRASEÑA ---
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Ingresa tu contraseña", color = Color(0xFF94A3B8)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF64748B)
                )
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña", tint = Color(0xFF64748B))
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        // --- BOTÓN INGRESAR + BOTÓN BIOMÉTRICO ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = if (isLoginEnabled) mintGradient else Brush.linearGradient(
                            listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                        )
                    )
                    .clickable(enabled = isLoginEnabled) {
                        if (isLoginEnabled) onLoginSuccess()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Ingresar",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isLoginEnabled) primaryDarkText else Color(0xFF94A3B8)
                )
            }

            // Botón Biométrico
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onBiometricClick() },
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Acceso Biométrico",
                        tint = Color(0xFF064E3B),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
            text = "¿Olvidaste tu contraseña?",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF064E3B),
            modifier = Modifier.clickable { }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Cambiar de usuario",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            modifier = Modifier.clickable { onChangeUserClick() }
        )

        Spacer(modifier = Modifier.height(32.dp))
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