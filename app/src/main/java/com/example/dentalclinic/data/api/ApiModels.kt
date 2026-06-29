package com.example.dentalclinic.data.api

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id", alternate = ["Id", "ID", "userId", "UserId", "key", "uId"]) val id: String,
    @SerializedName("firstName", alternate = ["FirstName", "first_name", "fName"]) val firstName: String?,
    @SerializedName("lastName", alternate = ["LastName", "last_name", "lName"]) val lastName: String?,
    @SerializedName("fullName", alternate = ["FullName", "full_name", "name", "Name", "patientName", "PatientName", "displayName"]) val fullName: String?,
    @SerializedName("email", alternate = ["Email", "emailAddress", "EmailAddress", "email_address", "mail"]) val email: String?,
    @SerializedName("userName", alternate = ["UserName", "username"]) val userName: String?,
    @SerializedName("phoneNumber", alternate = ["phone", "Phone", "mobile", "Mobile", "telephone", "tel"]) val phoneNumber: String?
)

data class DoctorResponse(
    @SerializedName("id") val id: String,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("specialization") val specialization: String?,
    @SerializedName("email") val email: String?
)

data class PatientResponse(
    @SerializedName("patientId", alternate = ["id", "Id", "ID", "patient_id", "key"]) val id: String,
    @SerializedName("userId", alternate = ["UserId", "user_id", "uId"]) val userId: String? = null,
    @SerializedName("firstName", alternate = ["FirstName", "first_name", "fName"]) val firstName: String? = null,
    @SerializedName("lastName", alternate = ["LastName", "last_name", "lName"]) val lastName: String? = null,
    @SerializedName("fullName", alternate = ["FullName", "full_name", "name", "Name", "patientName", "PatientName", "displayName"]) val fullName: String? = null,
    @SerializedName("email", alternate = ["Email", "emailAddress", "EmailAddress", "email_address", "mail"]) val email: String? = null,
    @SerializedName("age", alternate = ["Age", "patientAge", "userAge"]) val age: Int? = null,
    @SerializedName("phoneNumber", alternate = ["phone", "Phone", "mobile", "Mobile", "mobileNumber", "telephone", "phone_number", "tel"]) val phone: String? = null,
    @SerializedName("image", alternate = ["Image", "avatar", "Avatar", "profileImage", "photo", "imageUrl"]) val image: String? = null,
    @SerializedName("birthday", alternate = ["Birthday", "dateOfBirth", "dob", "birth_date"]) val birthday: String? = null,
    @SerializedName("chronicDiseases", alternate = ["chronic_diseases", "ChronicDiseases", "chronicDisease", "history", "diseases"]) val chronicDiseases: String? = null,
    @SerializedName("medicines", alternate = ["Medicines", "medicine", "medications", "meds"]) val medicines: String? = null,
    @SerializedName("surgeries", alternate = ["Surgeries", "surgery", "operations"]) val surgeries: String? = null,
    @SerializedName("bloodType", alternate = ["BloodType", "blood_type", "blood", "bloodGroup"]) val bloodType: String? = null
)

data class LoginResponse(
    @SerializedName("token", alternate = ["Token", "accessToken", "access_token", "jwt", "jwtToken"]) val token: String?,
    @SerializedName("expiration") val expiration: String?,
    @SerializedName("user", alternate = ["User", "patient", "Patient", "data", "Data", "record", "Record", "dto", "Dto"]) val user: UserResponse?
)

data class RayResponse(
    @SerializedName("rayId", alternate = ["id", "Id"]) val id: String,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("image", alternate = ["imageUrl", "ImageUrl", "path", "Path", "file", "File"]) val image: String?,
    @SerializedName("patientId") val patientId: String?,
    @SerializedName("createdAt", alternate = ["date", "Date"]) val createdAt: String?,
    @SerializedName("rayType") val rayType: Int? = null,
    @SerializedName("aiAnalysisJson") val aiAnalysisJson: String? = null,
    @SerializedName("totalDetections") val totalDetections: Int? = null
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

data class PatientUpdateRequest(
    @SerializedName("patientId") val patientId: String? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("birthday") val birthday: String? = null,
    @SerializedName("chronicDiseases") val chronicDiseases: String? = null,
    @SerializedName("medicines") val medicines: String? = null,
    @SerializedName("surgeries") val surgeries: String? = null,
    @SerializedName("bloodType") val bloodType: String? = null
)

data class PatientRegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("userName") val userName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("age") val age: Int,
    @SerializedName("birthday") val birthday: String? = null,
    @SerializedName("bloodType") val bloodType: String? = null,
    @SerializedName("chronicDiseases") val chronicDiseases: String? = null,
    @SerializedName("medicines") val medicines: String? = null,
    @SerializedName("surgeries") val surgeries: String? = null
)

data class ChatMessageDto(
    val id: String,
    @SerializedName("content", alternate = ["text", "message"]) val text: String,
    val senderId: String,
    @SerializedName("timestamp", alternate = ["createdAt", "date"]) val createdAt: String
)
