package com.banco.digital.data.repository

import android.content.Context
import android.util.Log
import com.banco.digital.data.api.RegisterApiService
import com.banco.digital.data.local.DatabaseHelper
import com.banco.digital.data.model.UsuarioRegistro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterRepository(private val context: Context? = null) {

    // Lista de IPs a intentar para conectar con tu PostgreSQL en la PC
    private val endpoints = listOf(
        "http://192.168.18.7:3000/",  // Conexión Wi-Fi directa a tu PC
        "http://127.0.0.1:3000/",     // Conexión por cable USB
        "http://10.0.2.2:3000/"       // Conexión si usas emulador
    )

    suspend fun guardarRegistro(usuario: UsuarioRegistro): Result<UsuarioRegistro> = withContext(Dispatchers.IO) {
        Log.d("RegisterRepository", "🚀 Enviando registro a PostgreSQL: ${usuario.nombreCompleto} (DNI: ${usuario.numeroDocumento})")

        // 1. Guardar primero en base de datos SQLite local del teléfono
        if (context != null) {
            try {
                DatabaseHelper.getInstance(context).guardarUsuario(usuario)
            } catch (e: Exception) {
                Log.e("RegisterRepository", "Error en SQLite local", e)
            }
        }

        // 2. Enviar por red a tu base de datos PostgreSQL en Windows
        var guardadoEnPostgres = false
        for (url in endpoints) {
            try {
                Log.d("RegisterRepository", "📡 Intentando conectar con PostgreSQL en: $url")
                val api = RegisterApiService.create(url)
                val response = api.registrarUsuario(usuario)
                if (response.isSuccessful && response.body()?.success == true) {
                    Log.i("RegisterRepository", "✅ ¡ÉXITO! Usuario guardado en PostgreSQL en $url: ${response.body()?.message}")
                    guardadoEnPostgres = true
                    break
                }
            } catch (e: Exception) {
                Log.w("RegisterRepository", "No se pudo conectar con $url (${e.message})")
            }
        }

        if (!guardadoEnPostgres) {
            Log.w("RegisterRepository", "⚠️ No se pudo enviar al servidor PostgreSQL, pero quedó guardado en el teléfono.")
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
