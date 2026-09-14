package com.banco.digital.data.api

import com.banco.digital.data.model.UsuarioRegistro
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

// Modelo de datos esperado por el backend Spring Boot
data class SpringUserRequest(
    val dni: String,
    val password: String,
    val celular: String,
    val name: String,
    val lastName: String,
    val email: String
)

data class SpringUserResponse(
    val id: Long?,
    val name: String?,
    val message: String?
)

interface RegisterApiService {

    // Endpoint provisional local
    @POST("api/register")
    suspend fun registrarUsuario(@Body usuario: UsuarioRegistro): Response<RegisterResponse>

    // NUEVO ENDPOINT SPRING BOOT
    @POST("user/insert")
    suspend fun registrarUsuarioSpring(@Body request: SpringUserRequest): Response<Void>

    @GET("api/users")
    suspend fun listarUsuarios(): Response<List<UsuarioRegistro>>

    companion object {
        // Se apunta por defecto a 10.0.2.2 que es el localhost de la PC desde el emulador Android
        fun create(baseUrl: String = "http://10.0.2.2:8092/"): RegisterApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(4, TimeUnit.SECONDS)
                .readTimeout(4, TimeUnit.SECONDS)
                .writeTimeout(4, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RegisterApiService::class.java)
        }
    }
}

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val userId: String? = null,
    val numeroCuenta: String? = null
)
