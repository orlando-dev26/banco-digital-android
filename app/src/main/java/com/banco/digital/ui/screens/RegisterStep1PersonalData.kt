package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1DatosPersonales(
    nombreCompleto: String,
    tipoDocumento: String,
    numeroDocumento: String,
    fechaNacimiento: String,
    correo: String,
    celular: String,
    onDataChange: (
        nombre: String,
        tipoDoc: String,
        numDoc: String,
        fechaNac: String,
        correo: String,
        celular: String
    ) -> Unit,
    onContinue: () -> Unit
) {
    var expandedDocType by remember { mutableStateOf(false) }
    val docTypes = listOf("DNI", "PASSPORT", "CE")

    val context = androidx.compose.ui.platform.LocalContext.current
    val onShowDatePicker = {
        val calendar = java.util.Calendar.getInstance()
        val year = calendar.get(java.util.Calendar.YEAR)
        val dialog = android.app.DatePickerDialog(
            context,
            android.R.style.Theme_Holo_Light_Dialog_MinWidth, // Estilo de ruleta (wheel)
            { _, selectedYear, month, dayOfMonth ->
                val selectedCal = java.util.Calendar.getInstance()
                selectedCal.set(selectedYear, month, dayOfMonth)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateStr = formatter.format(selectedCal.time)
                onDataChange(nombreCompleto, tipoDocumento, numeroDocumento, dateStr, correo, celular)
            },
            year - 18, // Por defecto muestra 18 años atrás
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
    }

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
    val isDniValid = if (tipoDocumento == "DNI") numeroDocumento.length == 8 else numeroDocumento.length >= 4
    val isEmailValid = correo.trim().contains("@") && correo.trim().contains(".")
    val isPhoneValid = celular.trim().length >= 9

    val isStep1Valid = nombreCompleto.trim().isNotEmpty() &&
            isDniValid &&
            fechaNacimiento.isNotEmpty() &&
            isEmailValid &&
            isPhoneValid

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Datos Personales",
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Ingresa tu información oficial para crear tu cuenta",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre Completo
        OutlinedTextField(
            value = nombreCompleto,
            onValueChange = { onDataChange(it, tipoDocumento, numeroDocumento, fechaNacimiento, correo, celular) },
            placeholder = { Text("Nombres y Apellidos Completos", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tipo y Número de Documento
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ExposedDropdownMenuBox(
                expanded = expandedDocType,
                onExpandedChange = { expandedDocType = it },
                modifier = Modifier.weight(0.38f)
            ) {
                OutlinedTextField(
                    value = tipoDocumento,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDocType) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedDocType,
                    onDismissRequest = { expandedDocType = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    docTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, fontWeight = FontWeight.Medium) },
                            onClick = {
                                onDataChange(nombreCompleto, selectionOption, "", fechaNacimiento, correo, celular)
                                expandedDocType = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = numeroDocumento,
                onValueChange = {
                    if (tipoDocumento == "DNI" && it.length <= 8 && it.all { char -> char.isDigit() }) {
                        onDataChange(nombreCompleto, tipoDocumento, it, fechaNacimiento, correo, celular)
                    } else if (tipoDocumento != "DNI" && it.length <= 12) {
                        onDataChange(nombreCompleto, tipoDocumento, it.uppercase(), fechaNacimiento, correo, celular)
                    }
                },
                placeholder = { Text("Número ($tipoDocumento)", color = Color(0xFF94A3B8)) },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF64748B)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (tipoDocumento == "DNI") KeyboardType.Number else KeyboardType.Text
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                modifier = Modifier.weight(0.62f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Selector de Fecha de Nacimiento
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDatePicker() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (fechaNacimiento.isNotEmpty()) "Fecha de Nacimiento" else "Seleccionar Fecha de Nacimiento",
                        fontSize = if (fechaNacimiento.isNotEmpty()) 11.sp else 14.sp,
                        color = Color(0xFF64748B)
                    )
                    if (fechaNacimiento.isNotEmpty()) {
                        Text(
                            text = fechaNacimiento,
                            fontSize = 15.sp,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Correo Electrónico
        OutlinedTextField(
            value = correo,
            onValueChange = { onDataChange(nombreCompleto, tipoDocumento, numeroDocumento, fechaNacimiento, it, celular) },
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
                        onDataChange(nombreCompleto, tipoDocumento, numeroDocumento, fechaNacimiento, correo, it)
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
                    brush = if (isStep1Valid) mintGradient else Brush.linearGradient(
                        listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                    )
                )
                .clickable(enabled = isStep1Valid) {
                    if (isStep1Valid) onContinue()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continuar a Crear Contraseña",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep1Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }

        // Espaciado extra para que el teclado no tape el botón
        Spacer(modifier = Modifier.height(120.dp))
    }
}
