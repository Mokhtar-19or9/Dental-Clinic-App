package com.example.dentalclinic.data.api

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id", alternate = ["Id", "ID", "userId", "UserId"]) val id: String,
    @SerializedName("firstName", alternate = ["FirstName", "first_name"]) val firstName: String?,
    @SerializedName("lastName", alternate = ["LastName", "last_name"]) val lastName: String?,
    @SerializedName("fullName", alternate = ["FullName", "full_name", "name", "Name", "patientName", "PatientName"]) val fullName: String?,
    @SerializedName("email", alternate = ["Email", "emailAddress", "EmailAddress", "email_address"]) val email: String?,
    @SerializedName("userName", alternate = ["UserName", "username"]) val userName: String?,
    @SerializedName("phoneNumber", alternate = ["phone", "Phone", "mobile", "Mobile", "telephone"]) val phoneNumber: String?
)

data class DoctorResponse(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("email") val email: String?
)

data class PatientResponse(
    @SerializedName("patientId", alternate = ["id", "Id", "ID", "patient_id"]) val id: String,
    @SerializedName("userId", alternate = ["UserId", "user_id"]) val userId: String? = null,
    @SerializedName("firstName", alternate = ["FirstName", "first_name"]) val firstName: String? = null,
    @SerializedName("lastName", alternate = ["LastName", "last_name"]) val lastName: String? = null,
    @SerializedName("fullName", alternate = ["FullName", "full_name", "name", "Name", "patientName", "PatientName"]) val fullName: String? = null,
    @SerializedName("email", alternate = ["Email", "emailAddress", "EmailAddress", "email_address"]) val email: String? = null,
    @SerializedName("age", alternate = ["Age", "patientAge"]) val age: Int? = null,
    @SerializedName("phoneNumber", alternate = ["phone", "Phone", "mobile", "Mobile", "mobileNumber", "telephone", "phone_number"]) val phone: String? = null,
    @SerializedName("image", alternate = ["Image", "avatar", "Avatar", "profileImage", "photo"]) val image: String? = null,
    @SerializedName("birthday", alternate = ["Birthday", "dateOfBirth", "dob", "birth_date"]) val birthday: String? = null,
    @SerializedName("chronicDiseases", alternate = ["chronic_diseases", "ChronicDiseases", "chronicDisease"]) val chronicDiseases: String? = null,
    @SerializedName("medicines", alternate = ["Medicines", "medicine", "medications"]) val medicines: String? = null,
    @SerializedName("surgeries", alternate = ["Surgeries", "surgery"]) val surgeries: String? = null,
    @SerializedName("bloodType", alternate = ["BloodType", "blood_type", "blood", "bloodGroup"]) val bloodType: String? = null
)

data class LoginResponse(
    @SerializedName("token", alternate = ["Token", "accessToken", "access_token", "jwt", "jwtToken"]) val token: String?,
    @SerializedName("expiration") val expiration: String?,
    @SerializedName("user", alternate = ["User", "patient", "Patient", "data", "Data"]) val user: UserResponse?
)

data class RayResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("createdAt", alternate = ["date", "Date"]) val createdAt: String?
)

data class UserLoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("password") val password: String
)

data class UserCreateRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)

data class PatientRegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("age") val age: Int
)
