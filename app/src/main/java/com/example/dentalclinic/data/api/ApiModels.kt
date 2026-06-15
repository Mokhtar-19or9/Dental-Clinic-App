package com.example.dentalclinic.data.api

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("userName") val userName: String?,
    @SerializedName("phoneNumber") val phoneNumber: String?
)

data class UserLoginRequest(
    @SerializedName("Email") val email: String? = null,
    @SerializedName("UserName") val userName: String? = null,
    @SerializedName("Password") val password: String
)

data class UserCreateRequest(
    @SerializedName("FullName") val fullName: String,
    @SerializedName("Email") val email: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("Password") val password: String,
    @SerializedName("ConfirmPassword") val confirmPassword: String,
    @SerializedName("PhoneNumber") val phoneNumber: String
)

data class PatientRegisterRequest(
    @SerializedName("FullName") val fullName: String,
    @SerializedName("Email") val email: String,
    @SerializedName("UserName") val userName: String,
    @SerializedName("Password") val password: String,
    @SerializedName("ConfirmPassword") val confirmPassword: String,
    @SerializedName("PhoneNumber") val phoneNumber: String,
    @SerializedName("Age") val age: Int
)

data class DoctorResponse(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("email") val email: String?
)

data class PatientResponse(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("fullName") val fullName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("age") val age: Int?,
    @SerializedName("phone") val phone: String?
)

data class RayResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("createdAt") val createdAt: String?
)
