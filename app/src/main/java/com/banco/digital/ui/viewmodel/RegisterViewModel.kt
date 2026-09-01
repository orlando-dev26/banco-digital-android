package com.banco.digital.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.banco.digital.data.model.EstadoKYC
import com.banco.digital.data.model.UsuarioRegistro
import com.banco.digital.data.repository.RegisterRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class LivenessStep {
    CENTER_FACE,
    BLINK,
    SMILE,
    COMPLETED
}

data class RegisterUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 8,

    // Paso 1: Datos Personales
    val nombreCompleto: String = "",
    val tipoDocumento: String = "DNI",
    val numeroDocumento: String = "",
    val fechaNacimiento: String = "",
    val correo: String = "",
    val celular: String = "",

    // Paso 2: Contraseña
    val password: String = "",
    val confirmPassword: String = "",

    // Paso 3: Legal
    val aceptoTerminos: Boolean = false,
    val aceptoTratamientoDatos: Boolean = false,
    val fechaAceptacionTerminos: Long = 0L,

    // Paso 4 & 5: Captura DNI
    val fotoDniFrontalUri: String = "",
    val fotoDniReversoUri: String = "",

    // Paso 6: KYC Facial / Liveness
    val livenessStep: LivenessStep = LivenessStep.CENTER_FACE,
    val fotoSelfieUri: String = "",
    val livenessFeedback: String = "Centra tu rostro dentro del óvalo",

    // Paso 7 & 8: Verificación y Resultado
    val isVerifying: Boolean = false,
    val verificationSuccess: Boolean = false,
    val usuarioRegistrado: UsuarioRegistro? = null
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RegisterRepository(application)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    // --- Navegación entre pasos ---
    fun nextStep() {
        if (_uiState.value.currentStep < _uiState.value.totalSteps) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
            if (_uiState.value.currentStep == 7) {
                ejecutarVerificacionFinal()
            }
        }
    }

    fun previousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }

    fun setStep(step: Int) {
        _uiState.update { it.copy(currentStep = step) }
    }

    // --- Paso 1: Datos Personales ---
    fun updatePersonalData(
        nombreCompleto: String,
        tipoDocumento: String,
        numeroDocumento: String,
        fechaNacimiento: String,
        correo: String,
        celular: String
    ) {
        _uiState.update {
            it.copy(
                nombreCompleto = nombreCompleto,
                tipoDocumento = tipoDocumento,
                numeroDocumento = numeroDocumento,
                fechaNacimiento = fechaNacimiento,
                correo = correo,
                celular = celular
            )
        }
    }

    // --- Paso 2: Contraseña ---
    fun updatePassword(password: String, confirmPassword: String) {
        _uiState.update {
            it.copy(
                password = password,
                confirmPassword = confirmPassword
            )
        }
    }

    // --- Paso 3: Términos y Condiciones ---
    fun updateTerminos(aceptoTerminos: Boolean, aceptoTratamiento: Boolean) {
        _uiState.update {
            it.copy(
                aceptoTerminos = aceptoTerminos,
                aceptoTratamientoDatos = aceptoTratamiento,
                fechaAceptacionTerminos = System.currentTimeMillis()
            )
        }
    }

    // --- Paso 4 & 5: Captura de DNI ---
    fun setFotoDniFrontal(uri: String) {
        _uiState.update { it.copy(fotoDniFrontalUri = uri) }
    }

    fun setFotoDniReverso(uri: String) {
        _uiState.update { it.copy(fotoDniReversoUri = uri) }
    }

    // --- Paso 6: Escaneo Facial y Liveness Check ---
    fun onFaceDetected(
        isCentered: Boolean,
        leftEyeOpenProb: Float?,
        rightEyeOpenProb: Float?,
        smilingProb: Float?
    ) {
        when (_uiState.value.livenessStep) {
            LivenessStep.CENTER_FACE -> {
                if (isCentered) {
                    _uiState.update {
                        it.copy(
                            livenessStep = LivenessStep.BLINK,
                            livenessFeedback = "¡Bien! Ahora parpadea suavemente"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(livenessFeedback = "Centra tu rostro dentro del óvalo")
                    }
                }
            }

            LivenessStep.BLINK -> {
                val isBlinking = (leftEyeOpenProb != null && leftEyeOpenProb < 0.25f) ||
                        (rightEyeOpenProb != null && rightEyeOpenProb < 0.25f)
                if (isBlinking) {
                    _uiState.update {
                        it.copy(
                            livenessStep = LivenessStep.SMILE,
                            livenessFeedback = "¡Excelente! Ahora sonríe para la cámara"
                        )
                    }
                }
            }

            LivenessStep.SMILE -> {
                val isSmiling = smilingProb != null && smilingProb > 0.65f
                if (isSmiling) {
                    _uiState.update {
                        it.copy(
                            livenessStep = LivenessStep.COMPLETED,
                            livenessFeedback = "¡Verificación de vida completada con éxito!"
                        )
                    }
                }
            }

            LivenessStep.COMPLETED -> {
                // Ya completado
            }
        }
    }

    fun setFotoSelfie(uri: String) {
        _uiState.update { it.copy(fotoSelfieUri = uri) }
    }

    fun resetLiveness() {
        _uiState.update {
            it.copy(
                livenessStep = LivenessStep.CENTER_FACE,
                livenessFeedback = "Centra tu rostro dentro del óvalo"
            )
        }
    }

    // --- Paso 7: Verificación Final y Registro en SQLite local ---
    fun ejecutarVerificacionFinal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true) }

            // Simulación de proceso KYC (2.5 segundos)
            delay(2500)

            val currentState = _uiState.value
            val nuevoUsuario = UsuarioRegistro(
                nombreCompleto = currentState.nombreCompleto.trim(),
                tipoDocumento = currentState.tipoDocumento,
                numeroDocumento = currentState.numeroDocumento.trim(),
                fechaNacimiento = currentState.fechaNacimiento,
                correo = currentState.correo.trim(),
                celular = currentState.celular.trim(),
                passwordHash = UsuarioRegistro.hashPassword(currentState.password),
                fotoDniFrontalUri = currentState.fotoDniFrontalUri,
                fotoDniReversoUri = currentState.fotoDniReversoUri,
                fotoSelfieUri = currentState.fotoSelfieUri,
                estadoVerificacion = EstadoKYC.VERIFICADO,
                fechaVerificacion = System.currentTimeMillis(),
                aceptoTerminos = currentState.aceptoTerminos,
                aceptoTratamientoDatos = currentState.aceptoTratamientoDatos,
                fechaAceptacionTerminos = currentState.fechaAceptacionTerminos
            )

            // Guardar directamente en la base de datos SQLite del teléfono
            repository.guardarRegistro(nuevoUsuario)

            _uiState.update {
                it.copy(
                    isVerifying = false,
                    verificationSuccess = true,
                    usuarioRegistrado = nuevoUsuario,
                    currentStep = 8 // Avanzar a Paso 8: Bienvenida
                )
            }
        }
    }
}
