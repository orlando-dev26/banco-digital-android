package com.banco.digital.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: (
        docType: String,
        docNum: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        birthDate: String,
        password: String
    ) -> Unit = { _, _, _, _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val totalSteps = 4

    // Step 1: Identidad
    var expandedDocType by remember { mutableStateOf(false) }
    val docTypes = listOf("DNI", "PASSPORT", "CE")
    var selectedDocType by remember { mutableStateOf("DNI") }
    var documentNumber by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }

    // Step 2: Contacto
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Step 3: Nacimiento
    var birthDate by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Step 4: Seguridad (Contraseña)
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        birthDate = formatter.format(Date(millis))
                    }
                }) {
                    Text("Aceptar", color = Color(0xFF064E3B), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Botón de Retroceso (si está en paso > 1 vuelve al paso anterior)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            modifier = Modifier
                .size(48.dp)
                .clickable {
                    if (currentStep > 1) {
                        currentStep--
                    } else {
                        onNavigateBack()
                    }
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás", tint = Color(0xFF111827))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Títulos de Encabezado
        Text(
            text = "Crear Cuenta",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Completa los pasos para activar tu cuenta digital",
            fontSize = 15.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // =====================================================================
        // LINEAR STEPPER CON "ENDOWED PROGRESS EFFECT"
        // =====================================================================
        EndowedLinearStepper(
            currentStep = currentStep,
            totalSteps = totalSteps,
            stepLabels = listOf("Identidad", "Contacto", "Nacimiento", "Seguridad")
        )

        Spacer(modifier = Modifier.height(32.dp))

        // =====================================================================
        // CONTENIDO DINÁMICO POR PASO CON TRANSICIÓN ANIMADA
        // =====================================================================
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut()
                    )
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut()
                    )
                }
            },
            label = "StepAnimation"
        ) { step ->
            when (step) {
                1 -> Step1IdentidadContent(
                    selectedDocType = selectedDocType,
                    expandedDocType = expandedDocType,
                    docTypes = docTypes,
                    documentNumber = documentNumber,
                    firstName = firstName,
                    lastName = lastName,
                    textFieldColors = textFieldColors,
                    onDocTypeChange = { selectedDocType = it; expandedDocType = false; documentNumber = "" },
                    onExpandedChange = { expandedDocType = it },
                    onDocNumberChange = { documentNumber = it },
                    onFirstNameChange = { firstName = it },
                    onLastNameChange = { lastName = it },
                    onContinue = { currentStep = 2 },
                    mintGradient = mintGradient,
                    primaryDarkText = primaryDarkText
                )

                2 -> Step2ContactoContent(
                    email = email,
                    phone = phone,
                    textFieldColors = textFieldColors,
                    onEmailChange = { email = it },
                    onPhoneChange = { phone = it },
                    onContinue = { currentStep = 3 },
                    mintGradient = mintGradient,
                    primaryDarkText = primaryDarkText
                )

                3 -> Step3NacimientoContent(
                    birthDate = birthDate,
                    textFieldColors = textFieldColors,
                    onOpenDatePicker = { showDatePicker = true },
                    onContinue = { currentStep = 4 },
                    mintGradient = mintGradient,
                    primaryDarkText = primaryDarkText
                )

                4 -> Step4SeguridadContent(
                    password = password,
                    confirmPassword = confirmPassword,
                    passwordVisible = passwordVisible,
                    confirmPasswordVisible = confirmPasswordVisible,
                    textFieldColors = textFieldColors,
                    onPasswordChange = { password = it },
                    onConfirmPasswordChange = { confirmPassword = it },
                    onTogglePasswordVisible = { passwordVisible = !passwordVisible },
                    onToggleConfirmPasswordVisible = { confirmPasswordVisible = !confirmPasswordVisible },
                    onSubmit = {
                        onRegisterSuccess(
                            selectedDocType,
                            documentNumber,
                            firstName,
                            lastName,
                            email,
                            phone,
                            birthDate,
                            password
                        )
                    },
                    mintGradient = mintGradient,
                    primaryDarkText = primaryDarkText
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// =====================================================================
// COMPONENTE: LINEAR STEPPER (4 PASOS CON ENDOWED PROGRESS)
// =====================================================================
@Composable
fun EndowedLinearStepper(
    currentStep: Int,
    totalSteps: Int = 4,
    stepLabels: List<String>
) {
    // Endowed Progress: Paso 1 inicia con un avance inicial (~25%), Paso 2 = 50%, Paso 3 = 75%, Paso 4 = 100%
    val targetProgress = when (currentStep) {
        1 -> 0.25f
        2 -> 0.50f
        3 -> 0.75f
        4 -> 1.00f
        else -> 0.25f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "StepperProgress"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Barra de fondo inactiva
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )

            // Barra de progreso activa (Endowed Progress con degradado menta/esmeralda)
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFA7F3D0), Color(0xFF34D399), Color(0xFF059669))
                        )
                    )
            )

            // Nodos circulares
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (step in 1..totalSteps) {
                    val isCompleted = step < currentStep
                    val isCurrent = step == currentStep

                    val nodeBg = when {
                        isCompleted -> Color(0xFF064E3B)
                        isCurrent -> Color(0xFFDCFCE7)
                        else -> Color.White
                    }

                    val nodeBorder = when {
                        isCompleted -> Color(0xFF064E3B)
                        isCurrent -> Color(0xFF064E3B)
                        else -> Color(0xFFE2E8F0)
                    }

                    val textColor = when {
                        isCompleted -> Color.White
                        isCurrent -> Color(0xFF042F2C)
                        else -> Color(0xFF94A3B8)
                    }

                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(nodeBg)
                            .border(width = if (isCurrent) 2.5.dp else 1.5.dp, color = nodeBorder, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        } else {
                            Text(
                                text = step.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium,
                                color = textColor
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Etiquetas debajo de cada nodo
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stepLabels.forEachIndexed { index, label ->
                val stepNum = index + 1
                val isActiveOrDone = stepNum <= currentStep

                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (stepNum == currentStep) FontWeight.Bold else FontWeight.Medium,
                    color = if (isActiveOrDone) Color(0xFF064E3B) else Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(72.dp)
                )
            }
        }
    }
}

// =====================================================================
// PASO 1: IDENTIFICACIÓN Y NOMBRES
// =====================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Step1IdentidadContent(
    selectedDocType: String,
    expandedDocType: Boolean,
    docTypes: List<String>,
    documentNumber: String,
    firstName: String,
    lastName: String,
    textFieldColors: TextFieldColors,
    onDocTypeChange: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onDocNumberChange: (String) -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onContinue: () -> Unit,
    mintGradient: Brush,
    primaryDarkText: Color
) {
    val isStep1Valid = documentNumber.trim().isNotEmpty() &&
            firstName.trim().isNotEmpty() &&
            lastName.trim().isNotEmpty() &&
            (if (selectedDocType == "DNI") documentNumber.length == 8 else documentNumber.length >= 4)

    Column {
        Text(
            text = "Paso 1: Identificación",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Ingresa tus datos personales oficiales",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Selector Tipo y Número de Documento
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ExposedDropdownMenuBox(
                expanded = expandedDocType,
                onExpandedChange = onExpandedChange,
                modifier = Modifier.weight(0.38f)
            ) {
                OutlinedTextField(
                    value = selectedDocType,
                    onValueChange = {},
                    readOnly = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = textFieldColors,
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expandedDocType,
                    onDismissRequest = { onExpandedChange(false) },
                    modifier = Modifier.background(Color.White)
                ) {
                    docTypes.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption, fontWeight = FontWeight.Medium) },
                            onClick = { onDocTypeChange(selectionOption) }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = documentNumber,
                onValueChange = {
                    if (selectedDocType == "DNI" && it.length <= 8 && it.all { char -> char.isDigit() }) {
                        onDocNumberChange(it)
                    } else if (selectedDocType != "DNI" && it.length <= 12) {
                        onDocNumberChange(it.uppercase())
                    }
                },
                placeholder = { Text("Número ($selectedDocType)", color = Color(0xFF94A3B8)) },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFF64748B)) },
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (selectedDocType == "DNI") KeyboardType.Number else KeyboardType.Text
                ),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = textFieldColors,
                modifier = Modifier.weight(0.62f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nombres
        OutlinedTextField(
            value = firstName,
            onValueChange = onFirstNameChange,
            placeholder = { Text("Nombres completos", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Apellidos
        OutlinedTextField(
            value = lastName,
            onValueChange = onLastNameChange,
            placeholder = { Text("Apellidos completos", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF64748B)) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

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
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep1Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }
    }
}

// =====================================================================
// PASO 2: CONTACTO (CORREO Y TELÉFONO)
// =====================================================================
@Composable
private fun Step2ContactoContent(
    email: String,
    phone: String,
    textFieldColors: TextFieldColors,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onContinue: () -> Unit,
    mintGradient: Brush,
    primaryDarkText: Color
) {
    val isStep2Valid = email.trim().contains("@") && email.trim().contains(".") && phone.trim().length >= 9

    Column {
        Text(
            text = "Paso 2: Información de Contacto",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "¿Dónde podemos enviarte notificaciones y códigos de seguridad?",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Correo Electrónico
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("Correo electrónico", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF64748B)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Teléfono Celular
        OutlinedTextField(
            value = phone,
            onValueChange = { if (it.all { char -> char.isDigit() } && it.length <= 12) onPhoneChange(it) },
            placeholder = { Text("Teléfono celular (ej: 987654321)", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = textFieldColors,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

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
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep2Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }
    }
}

// =====================================================================
// PASO 3: FECHA DE NACIMIENTO
// =====================================================================
@Composable
private fun Step3NacimientoContent(
    birthDate: String,
    textFieldColors: TextFieldColors,
    onOpenDatePicker: () -> Unit,
    onContinue: () -> Unit,
    mintGradient: Brush,
    primaryDarkText: Color
) {
    val isStep3Valid = birthDate.isNotEmpty()

    Column {
        Text(
            text = "Paso 3: Fecha de Nacimiento",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Necesitamos verificar tu mayoría de edad para habilitar tu cuenta",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenDatePicker() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (birthDate.isNotEmpty()) birthDate else "Seleccionar Fecha de Nacimiento (DD/MM/AAAA)",
                    fontSize = 15.sp,
                    color = if (birthDate.isNotEmpty()) Color(0xFF111827) else Color(0xFF94A3B8),
                    fontWeight = if (birthDate.isNotEmpty()) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Botón Continuar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = if (isStep3Valid) mintGradient else Brush.linearGradient(
                        listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                    )
                )
                .clickable(enabled = isStep3Valid) {
                    if (isStep3Valid) onContinue()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Continuar",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep3Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }
    }
}

// =====================================================================
// PASO 4: SEGURIDAD (CREAR CONTRASEÑA)
// =====================================================================
@Composable
private fun Step4SeguridadContent(
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    textFieldColors: TextFieldColors,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisible: () -> Unit,
    onToggleConfirmPasswordVisible: () -> Unit,
    onSubmit: () -> Unit,
    mintGradient: Brush,
    primaryDarkText: Color
) {
    val isPasswordValid = password.length >= 6
    val doPasswordsMatch = password.isNotEmpty() && password == confirmPassword
    val isStep4Valid = isPasswordValid && doPasswordsMatch

    Column {
        Text(
            text = "Paso 4: Seguridad y Contraseña",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Crea una contraseña segura (mínimo 6 caracteres) para ingresar a tu banca digital",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("Crear Contraseña", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onTogglePasswordVisible) {
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

        Spacer(modifier = Modifier.height(16.dp))

        // Confirmar Contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = { Text("Confirmar Contraseña", color = Color(0xFF94A3B8)) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B)) },
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = onToggleConfirmPasswordVisible) {
                    Icon(imageVector = image, contentDescription = "Mostrar/Ocultar contraseña", tint = Color(0xFF64748B))
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

        Spacer(modifier = Modifier.height(28.dp))

        // Botón Finalizar Registro
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = if (isStep4Valid) mintGradient else Brush.linearGradient(
                        listOf(Color(0xFFE2E8F0), Color(0xFFCBD5E1))
                    )
                )
                .clickable(enabled = isStep4Valid) {
                    if (isStep4Valid) onSubmit()
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Registrarme",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isStep4Valid) primaryDarkText else Color(0xFF94A3B8)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Al registrarte, aceptas los Términos y Condiciones y la Política de Privacidad del Banco Digital.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            lineHeight = 16.sp
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}