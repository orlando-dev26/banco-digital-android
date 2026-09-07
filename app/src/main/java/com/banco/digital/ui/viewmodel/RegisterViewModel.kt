package com.banco.digital.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.banco.digital.data.api.KycApiService
import com.banco.digital.data.model.EstadoKYC
import com.banco.digital.data.model.UsuarioRegistro
import com.banco.digital.data.repository.RegisterRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

sealed class KycState {
    object Idle : KycState()
    object Loading : KycState()
    data class Success(val message: String) : KycState()
    data class Error(val message: String) : KycState()
}

data class RegisterUiState(
    val currentStep: Int = 1,
    val totalSteps: Int = 8,

    // Paso 1: Datos Personales
    val nombre: String = "",
    val apellidos: String = "",
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

    val kycDniState: KycState = KycState.Idle,
    val dniFaceBase64: String = "",

    // Paso 6: KYC Facial / Liveness
    val fotoSelfieUri: String = "",
    val kycLivenessState: KycState = KycState.Idle,
    val livenessFrames: List<ByteArray> = emptyList(),

    // Paso 7 & 8: Verificación y Resultado
    val isVerifying: Boolean = false,
    val verificationSuccess: Boolean = false,
    val usuarioRegistrado: UsuarioRegistro? = null
)

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RegisterRepository(application)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val kycEndpoints = listOf(
        "http://192.168.18.7:8000/",
        "http://127.0.0.1:8000/",
        "http://10.0.2.2:8000/"
    )

    // --- Navegación entre pasos ---
    fun nextStep() {
        if (_uiState.value.currentStep < _uiState.value.totalSteps) {
            _uiState.update { it.copy(currentStep = it.currentStep + 1) }
            if (_uiState.value.currentStep == 8) {
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
        nombre: String,
        apellidos: String,
        tipoDocumento: String,
        numeroDocumento: String,
        fechaNacimiento: String,
        correo: String,
        celular: String
    ) {
        _uiState.update {
            it.copy(
                nombre = nombre,
                apellidos = apellidos,
                tipoDocumento = tipoDocumento,
                numeroDocumento = numeroDocumento,
                fechaNacimiento = fechaNacimiento,
                correo = correo,
                celular = celular
            )
        }
    }

    // --- Paso 2: Datos de Contacto ---
    fun updateContactData(correo: String, celular: String) {
        _uiState.update {
            it.copy(correo = correo, celular = celular)
        }
    }

    // --- Paso 3: Contraseña ---
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

    fun validarDniFrontal(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(fotoDniFrontalUri = uri, kycDniState = KycState.Loading) }

            try {
                val file = File(Uri.parse(uri).path ?: "")
                if (!file.exists()) {
                    _uiState.update { it.copy(kycDniState = KycState.Error("Archivo no encontrado")) }
                    return@launch
                }

                val reqFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("imagen", file.name, reqFile)

                var success = false
                for (url in kycEndpoints) {
                    try {
                        val api = KycApiService.create(url)
                        val response = api.validarDniIndividual(part)
                        
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.is_valid_document) {
                                _uiState.update { it.copy(kycDniState = KycState.Success("DNI Frontal Validado")) }
                                success = true
                                nextStep() // Pasar al reverso
                                break
                            } else {
                                _uiState.update { it.copy(kycDniState = KycState.Error(body?.message ?: "Documento no válido")) }
                                success = true
                                break
                            }
                        }
                    } catch (e: Exception) { }
                }
                if (!success) _uiState.update { it.copy(kycDniState = KycState.Error("Error de conexión")) }
            } catch (e: Exception) {
                _uiState.update { it.copy(kycDniState = KycState.Error("Error: ${e.message}")) }
            }
        }
    }

    fun validarDniReverso(uri: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(fotoDniReversoUri = uri, kycDniState = KycState.Loading) }

            try {
                val file = File(Uri.parse(uri).path ?: "")
                if (!file.exists()) {
                    _uiState.update { it.copy(kycDniState = KycState.Error("Archivo no encontrado")) }
                    return@launch
                }

                val reqFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("imagen", file.name, reqFile)

                var success = false
                for (url in kycEndpoints) {
                    try {
                        val api = KycApiService.create(url)
                        val response = api.validarDniIndividual(part)
                        
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.is_valid_document) {
                                _uiState.update { 
                                    it.copy(
                                        dniFaceBase64 = body.face_image_base64 ?: "",
                                        kycDniState = KycState.Success("DNI Reverso Validado")
                                    )
                                }
                                success = true
                                nextStep() // Pasar a Liveness
                                break
                            } else {
                                _uiState.update { it.copy(kycDniState = KycState.Error(body?.message ?: "Documento no válido")) }
                                success = true
                                break
                            }
                        }
                    } catch (e: Exception) { }
                }
                if (!success) _uiState.update { it.copy(kycDniState = KycState.Error("Error de conexión")) }
            } catch (e: Exception) {
                _uiState.update { it.copy(kycDniState = KycState.Error("Error: ${e.message}")) }
            }
        }
    }

    // --- Paso 6: Escaneo Facial y Liveness Check ---
    fun addLivenessFrame(frame: ByteArray) {
        _uiState.update { 
            it.copy(livenessFrames = it.livenessFrames + frame)
        }
    }

    fun enviarFramesLiveness() {
        viewModelScope.launch {
            _uiState.update { it.copy(kycLivenessState = KycState.Loading) }

            val frames = _uiState.value.livenessFrames
            val faceBase64 = _uiState.value.dniFaceBase64

            if (frames.isEmpty()) {
                _uiState.update { it.copy(kycLivenessState = KycState.Error("No hay frames capturados")) }
                return@launch
            }

            if (faceBase64.isEmpty()) {
                _uiState.update { it.copy(kycLivenessState = KycState.Error("Falta la foto base del DNI")) }
                return@launch
            }

            try {
                val parts = frames.mapIndexed { index, frame ->
                    val reqBody = frame.toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("frames", "frame_$index.jpg", reqBody)
                }
                
                val dniFaceBody = faceBase64.toRequestBody("text/plain".toMediaTypeOrNull())

                var success = false
                for (url in kycEndpoints) {
                    try {
                        val api = KycApiService.create(url)
                        val response = api.verificarLiveness(parts, dniFaceBody)
                        
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.is_alive && body.match) {
                                _uiState.update { 
                                    it.copy(kycLivenessState = KycState.Success("Liveness y match exitosos"))
                                }
                                success = true
                                nextStep()
                                break
                            } else {
                                _uiState.update { it.copy(kycLivenessState = KycState.Error(body?.message ?: "Prueba de vida o coincidencia fallida")) }
                                success = true
                                break
                            }
                        }
                    } catch (e: Exception) {
                        // Try next endpoint
                    }
                }

                if (!success) {
                    _uiState.update { it.copy(kycLivenessState = KycState.Error("Error conectando al servidor Liveness")) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(kycLivenessState = KycState.Error("Error en prueba liveness: ${e.message}")) }
            }
        }
    }

    fun setFotoSelfie(uri: String) {
        _uiState.update { it.copy(fotoSelfieUri = uri) }
    }

    fun resetLivenessState() {
        _uiState.update { 
            it.copy(
                kycLivenessState = KycState.Idle, 
                livenessFrames = emptyList()
            ) 
        }
    }

    // --- Paso 7: Verificación Final y Registro en SQLite local ---
    fun ejecutarVerificacionFinal() {
        if (_uiState.value.kycLivenessState !is KycState.Success) {
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isVerifying = true) }

            // Simulación de proceso KYC (2.5 segundos)
            delay(2500)

            val currentState = _uiState.value
            val fullName = "${currentState.nombre.trim()} ${currentState.apellidos.trim()}".trim()
            
            val nuevoUsuario = UsuarioRegistro(
                nombreCompleto = fullName,
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
