package com.banco.digital.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Step1DatosPersonales(
    nombre: String,
    apellidos: String,
    tipoDocumento: String,
    numeroDocumento: String,
    fechaNacimiento: String,
    correo: String,
    celular: String,
    onDataChange: (
        nombre: String,
        apellidos: String,
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
            android.R.style.Theme_Holo_Light_Dialog_MinWidth,
            { _, selectedYear, month, dayOfMonth ->
                val selectedCal = java.util.Calendar.getInstance()
                selectedCal.set(selectedYear, month, dayOfMonth)
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val dateStr = formatter.format(selectedCal.time)
                onDataChange(nombre, apellidos, tipoDocumento, numeroDocumento, dateStr, "", "")
            },
            year - 18,
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
    val isNameValid = nombre.trim().length >= 2 && apellidos.trim().length >= 2
    val isDniValid = if (tipoDocumento == "DNI") numeroDocumento.length == 8 else numeroDocumento.length >= 4

    val isStep1Valid = isNameValid &&
            isDniValid &&
            fechaNacimiento.isNotEmpty()

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

        // Nombres
        OutlinedTextField(
            value = nombre,
            onValueChange = { onDataChange(it, apellidos, tipoDocumento, numeroDocumento, fechaNacimiento, "", "") },
            placeholder = { Text("Nombres", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Apellidos
        OutlinedTextField(
            value = apellidos,
            onValueChange = { onDataChange(nombre, it, tipoDocumento, numeroDocumento, fechaNacimiento, "", "") },
            placeholder = { Text("Apellidos", color = Color(0xFF94A3B8)) },
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
                                onDataChange(nombre, apellidos, selectionOption, "", fechaNacimiento, "", "")
                                expandedDocType = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = numeroDocumento,
                onValueChange = {
                    val maxLength = if (tipoDocumento == "DNI") 8 else 12
                    if (it.length <= maxLength && (tipoDocumento != "DNI" || it.all { char -> char.isDigit() })) {
                        onDataChange(nombre, apellidos, tipoDocumento, it.uppercase(), fechaNacimiento, "", "")
                    }
                },
                placeholder = { Text(if (tipoDocumento == "DNI") "00000000" else "Número", color = Color(0xFF94A3B8)) },
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

        // Fecha de Nacimiento
        OutlinedTextField(
            value = fechaNacimiento,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text("Fecha de Nacimiento (DD/MM/AAAA)", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = Color(0xFF64748B)) },
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onShowDatePicker() },
            enabled = false // Para que reciba el click de la fila en lugar del teclado
        )

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
                text = "Continuar a Datos de Contacto",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep1Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }
    }
}
