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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banco.digital.ui.viewmodel.RegisterViewModel

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
    viewModel: RegisterViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    val stepLabels = listOf(
        "Datos",
        "Clave",
        "Legal",
        "DNI Fr.",
        "DNI Rev.",
        "Facial",
        "Validar",
        "Listo"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .padding(horizontal = 20.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // Encabezado con Botón de Retroceso e Indicador de Progreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.currentStep < 7) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    shadowElevation = 1.dp,
                    modifier = Modifier
                        .size(44.dp)
                        .clickable {
                            if (uiState.currentStep > 1) {
                                viewModel.previousStep()
                            } else {
                                onNavigateBack()
                            }
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color(0xFF111827)
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(44.dp))
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        // Títulos Principales
        Text(
            text = "Registro Digital",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = "Onboarding y verificación bancaria KYC",
            fontSize = 14.sp,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // =====================================================================
        // STEPPER LINEAL CON "ENDOWED PROGRESS EFFECT" (8 PASOS)
        // =====================================================================
        EndowedLinearStepper8(
            currentStep = uiState.currentStep,
            totalSteps = uiState.totalSteps,
            stepLabels = stepLabels
        )

        Spacer(modifier = Modifier.height(28.dp))

        // =====================================================================
        // CONTENIDO DINÁMICO DE LOS 8 PASOS CON TRANSICIÓN ANIMADA
        // =====================================================================
        AnimatedContent(
            targetState = uiState.currentStep,
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
            label = "StepAnimation8"
        ) { step ->
            when (step) {
                // Paso 1: Datos Personales
                1 -> Step1DatosPersonales(
                    nombreCompleto = uiState.nombreCompleto,
                    tipoDocumento = uiState.tipoDocumento,
                    numeroDocumento = uiState.numeroDocumento,
                    fechaNacimiento = uiState.fechaNacimiento,
                    correo = uiState.correo,
                    celular = uiState.celular,
                    onDataChange = { nombre, tipoDoc, numDoc, fechaNac, correo, celular ->
                        viewModel.updatePersonalData(nombre, tipoDoc, numDoc, fechaNac, correo, celular)
                    },
                    onContinue = { viewModel.nextStep() }
                )

                // Paso 2: Crear Contraseña (Vista dedicada con checklist de seguridad)
                2 -> Step2CrearPassword(
                    password = uiState.password,
                    confirmPassword = uiState.confirmPassword,
                    onPasswordChange = { pass, confirm ->
                        viewModel.updatePassword(pass, confirm)
                    },
                    onContinue = { viewModel.nextStep() }
                )

                // Paso 3: Términos y Condiciones
                3 -> Step3TerminosYCondiciones(
                    aceptoTerminos = uiState.aceptoTerminos,
                    aceptoTratamientoDatos = uiState.aceptoTratamientoDatos,
                    onTerminosChange = { terminos, datos ->
                        viewModel.updateTerminos(terminos, datos)
                    },
                    onContinue = { viewModel.nextStep() }
                )

                // Paso 4: Captura DNI Frontal
                4 -> Step4DniFrontal(
                    fotoDniFrontalUri = uiState.fotoDniFrontalUri,
                    onPhotoCaptured = { uri -> viewModel.setFotoDniFrontal(uri) },
                    onContinue = { viewModel.nextStep() }
                )

                // Paso 5: Captura DNI Reverso
                5 -> Step5DniReverso(
                    fotoDniReversoUri = uiState.fotoDniReversoUri,
                    onPhotoCaptured = { uri -> viewModel.setFotoDniReverso(uri) },
                    onContinue = { viewModel.nextStep() }
                )

                // Paso 6: Escaneo Facial y Liveness Check
                6 -> Step6EscaneoFacial(
                    livenessStep = uiState.livenessStep,
                    feedbackText = uiState.livenessFeedback,
                    onFaceDetected = { isCentered, leftEye, rightEye, smile ->
                        viewModel.onFaceDetected(isCentered, leftEye, rightEye, smile)
                    },
                    onSelfieCaptured = { uri -> viewModel.setFotoSelfie(uri) },
                    onContinue = { viewModel.nextStep() }
                )

                // Paso 7: Verificando Identidad
                7 -> Step7VerificandoIdentidad()

                // Paso 8: Bienvenida y Activación de Cuenta
                8 -> Step8Bienvenida(
                    usuario = uiState.usuarioRegistrado,
                    onFinishRegistration = {
                        val user = uiState.usuarioRegistrado
                        if (user != null) {
                            onRegisterSuccess(
                                user.tipoDocumento,
                                user.numeroDocumento,
                                user.nombreCompleto,
                                "",
                                user.correo,
                                user.celular,
                                user.fechaNacimiento,
                                user.passwordHash
                            )
                        } else {
                            onNavigateBack()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// =====================================================================
// COMPONENTE: LINEAR STEPPER CON "ENDOWED PROGRESS EFFECT" (8 PASOS)
// =====================================================================
@Composable
fun EndowedLinearStepper8(
    currentStep: Int,
    totalSteps: Int = 8,
    stepLabels: List<String>
) {
    // Endowed Progress: Paso 1 inicia con avance inicial (~16%), avanzando proporcionalmente hasta 100% en el paso 8
    val targetProgress = when (currentStep) {
        1 -> 0.16f
        2 -> 0.28f
        3 -> 0.40f
        4 -> 0.52f
        5 -> 0.64f
        6 -> 0.76f
        7 -> 0.88f
        8 -> 1.00f
        else -> 0.16f
    }

    val animatedProgress by animateFloatAsState(
        targetValue = targetProgress,
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "StepperProgress8"
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Barra de fondo inactiva
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0))
            )

            // Barra de progreso activa con degradado menta/esmeralda
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = animatedProgress)
                    .height(8.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFFA7F3D0), Color(0xFF34D399), Color(0xFF059669))
                        )
                    )
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenFullPreview() {
    RegisterScreen()
}