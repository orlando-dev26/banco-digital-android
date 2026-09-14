package com.banco.digital.data.repository

import android.content.Context
import android.util.Log
import com.banco.digital.data.api.RegisterApiService
import com.banco.digital.data.local.DatabaseHelper
import com.banco.digital.data.model.UsuarioRegistro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterRepository(private val context: Context? = null) {

    // Lista de IPs a intentar para conectar con tu backend Spring Boot en la PC
    private val endpoints = listOf(
        "http://192.168.18.7:8092/",  // Conexión Wi-Fi directa a tu PC
        "http://10.0.2.2:8092/",      // Conexión desde emulador
        "http://127.0.0.1:8092/"      // Conexión si usas ADB port forwarding
    )

    suspend fun guardarRegistro(usuario: UsuarioRegistro, rawPassword: String = ""): Result<UsuarioRegistro> = withContext(Dispatchers.IO) {
        Log.d("RegisterRepository", "🚀 Enviando registro a Spring Boot: ${usuario.nombreCompleto} (DNI: ${usuario.numeroDocumento})")

        // 1. Guardar primero en base de datos SQLite local del teléfono
        if (context != null) {
            try {
                DatabaseHelper.getInstance(context).guardarUsuario(usuario)
            } catch (e: Exception) {
                Log.e("RegisterRepository", "Error en SQLite local", e)
            }
        }

        // Dividir el nombre completo para el backend si es necesario
        val splitNames = usuario.nombreCompleto.split(" ", limit = 2)
        val name = splitNames.getOrNull(0) ?: ""
        val lastName = splitNames.getOrNull(1) ?: ""

        val request = com.banco.digital.data.api.SpringUserRequest(
            dni = usuario.numeroDocumento,
            password = if (rawPassword.isNotEmpty()) rawPassword else usuario.passwordHash,
            celular = usuario.celular,
            name = name,
            lastName = lastName,
            email = usuario.correo
        )

        // 2. Enviar por red a Spring Boot
        var guardadoEnSpring = false
        for (url in endpoints) {
            try {
                Log.d("RegisterRepository", "📡 Intentando conectar con Spring Boot en: $url")
                val api = RegisterApiService.create(url)
                val response = api.registrarUsuarioSpring(request)
                if (response.isSuccessful) {
                    Log.i("RegisterRepository", "✅ ¡ÉXITO! Usuario guardado en backend de Spring Boot en $url")
                    guardadoEnSpring = true
                    break
                }
            } catch (e: Exception) {
                Log.w("RegisterRepository", "No se pudo conectar con $url (${e.message})")
            }
        }

        if (!guardadoEnSpring) {
            Log.w("RegisterRepository", "⚠️ No se pudo enviar al servidor Spring Boot, pero quedó guardado en el teléfono.")
        }

        Result.success(usuario)
    }

    fun obtenerUsuarioPorDocumento(numDoc: String): UsuarioRegistro? {
        return context?.let { DatabaseHelper.getInstance(it).obtenerUsuarioPorDocumento(numDoc) }
    }

    fun obtenerTodos(): List<UsuarioRegistro> {
        return context?.let { DatabaseHelper.getInstance(it).obtenerTodosLosUsuarios() } ?: emptyList()
    }
}
