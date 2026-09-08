package com.banco.digital.data.api

import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface KycApiService {
    @Multipart
    @POST("api/kyc/dni")
    suspend fun validarDni(
        @Part dniFrontal: MultipartBody.Part,
        @Part dniReverso: MultipartBody.Part
    ): Response<KycDniResponse>

    @Multipart
    @POST("api/kyc/dni-single")
    suspend fun validarDniIndividual(
        @Part imagen: MultipartBody.Part,
        @Part nombreEsperado: MultipartBody.Part? = null,
        @Part apellidosEsperados: MultipartBody.Part? = null,
        @Part dniEsperado: MultipartBody.Part? = null
    ): Response<KycDniResponse>

    @Multipart
    @POST("api/kyc/liveness-match")
    suspend fun verificarLiveness(
        @Part frames: List<@JvmSuppressWildcards MultipartBody.Part>,
        @Part("dni_face_base64") dniFaceBase64: RequestBody
    ): Response<KycLivenessResponse>

    companion object {
        fun create(baseUrl: String = "http://192.168.18.7:8000/"): KycApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(KycApiService::class.java)
        }
    }
}

data class KycDniResponse(
    val is_valid_document: Boolean,
    val extracted_text: String?,
    val face_cropped: Boolean,
    val face_image_base64: String?,
    val datos_coinciden: Boolean = false,
    val message: String?
)

data class KycLivenessResponse(
    val is_alive: Boolean,
    val match: Boolean,
    val confidence: Float?,
    val message: String?
)
