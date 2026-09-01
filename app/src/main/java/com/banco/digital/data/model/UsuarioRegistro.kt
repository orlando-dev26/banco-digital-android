package com.banco.digital.data.model

import java.security.MessageDigest
import java.util.UUID

enum class EstadoKYC {
    PENDIENTE,
    VERIFICADO,
    RECHAZADO
}

data class UsuarioRegistro(
    // Identidad
    val id: String = UUID.randomUUID().toString(),
    val nombreCompleto: String = "",
    val tipoDocumento: String = "DNI",
    val numeroDocumento: String = "",
    val fechaNacimiento: String = "",

    // Contacto
    val correo: String = "",
    val celular: String = "",

    // Credenciales (almacenado siempre en hash SHA-256)
    val passwordHash: String = "",

    // Verificación KYC
    val fotoDniFrontalUri: String = "",
    val fotoDniReversoUri: String = "",
    val fotoSelfieUri: String = "",
    val estadoVerificacion: EstadoKYC = EstadoKYC.PENDIENTE,
    val fechaVerificacion: Long? = null,

    // Generados por el sistema
    val numeroCuenta: String = generarNumeroCuentaAleatorio(),
    val fechaCreacionCuenta: Long = System.currentTimeMillis(),
    val saldoInicial: Double = 0.0,

    // Legal
    val aceptoTerminos: Boolean = false,
    val aceptoTratamientoDatos: Boolean = false,
    val fechaAceptacionTerminos: Long = System.currentTimeMillis()
) {
    companion object {
        fun hashPassword(password: String): String {
            val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun generarNumeroCuentaAleatorio(): String {
            val banco = "0011"
            val plaza = (100..999).random()
            val cuenta = (1000000000L..9999999999L).random()
            return "$banco-$plaza-$cuenta"
        }
    }
}
