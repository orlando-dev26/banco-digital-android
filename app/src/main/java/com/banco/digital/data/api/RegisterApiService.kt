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

interface RegisterApiService {

    @POST("api/register")
    suspend fun registrarUsuario(@Body usuario: UsuarioRegistro): Response<RegisterResponse>

    @GET("api/users")
    suspend fun listarUsuarios(): Response<List<UsuarioRegistro>>

    companion object {
        fun create(baseUrl: String = "http://127.0.0.1:3000/"): RegisterApiService {
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
