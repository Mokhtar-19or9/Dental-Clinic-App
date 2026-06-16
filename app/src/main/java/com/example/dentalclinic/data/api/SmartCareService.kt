package com.example.dentalclinic.data.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface SmartCareService {
    @POST("api/v1/Account/Login")
    suspend fun login(@Body request: UserLoginRequest): Response<ResponseBody>

    @POST("api/v1/Account/Login")
    suspend fun loginRaw(@Body request: Map<String, String>): Response<ResponseBody>

    @POST("api/v1/Patients/login")
    suspend fun patientLogin(@Body request: UserLoginRequest): Response<ResponseBody>

    @POST("api/v1/Account/Create")
    suspend fun createAccount(@Body request: UserCreateRequest): Response<UserResponse>

    @POST("api/v1/Patients/register")
    suspend fun registerPatient(@Body request: PatientRegisterRequest): Response<PatientResponse>

    @GET("api/v1/Doctors/GetAll")
    suspend fun getAllDoctors(): Response<List<DoctorResponse>>

    @GET("api/v1/Patients/current")
    suspend fun getCurrentPatient(): Response<ResponseBody>

    @GET("api/v1/Ray/GetAll")
    suspend fun getAllRays(): Response<List<RayResponse>>

    @GET("api/v1/Ray/GetByPatient/{id}")
    suspend fun getRaysByPatient(@Path("id") patientId: String): Response<List<RayResponse>>

    @GET("api/v1/Chat/History")
    suspend fun getChatHistory(@Query("id") conversationId: String, @Query("limit") limit: Int = 20): Response<List<ChatMessageDto>>
}

data class ChatMessageDto(
    val id: String,
    val text: String,
    val senderId: String,
    val createdAt: String
)
