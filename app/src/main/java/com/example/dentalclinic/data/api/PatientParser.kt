package com.example.dentalclinic.data.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PatientParser {
    private val FIELD_MAP = mapOf(
        "patientId" to listOf("patientId", "PatientId", "id", "Id", "ID", "patient_id", "Patient_Id"),
        "fullName" to listOf("fullName", "FullName", "full_name", "Full_Name", "name", "Name", "patientName", "PatientName"),
        "firstName" to listOf("firstName", "FirstName", "first_name", "First_Name"),
        "lastName" to listOf("lastName", "LastName", "last_name", "Last_Name"),
        "email" to listOf("email", "Email", "emailAddress", "EmailAddress", "email_address", "Email_Address"),
        "phoneNumber" to listOf("phoneNumber", "PhoneNumber", "phone", "Phone", "mobile", "Mobile", "mobileNumber", "MobileNumber", "telephone", "Telephone", "phone_number", "Phone_Number"),
        "age" to listOf("age", "Age", "patientAge", "PatientAge"),
        "userId" to listOf("userId", "UserId", "user_id", "User_Id"),
        "birthday" to listOf("birthday", "Birthday", "dateOfBirth", "DateOfBirth", "dob", "Dob", "DOB", "birth_date", "Birth_Date"),
        "image" to listOf("image", "Image", "avatar", "Avatar", "profileImage", "ProfileImage", "photo", "Photo"),
        "chronicDiseases" to listOf("chronicDiseases", "ChronicDiseases", "chronic_diseases", "Chronic_Diseases", "chronicDisease", "ChronicDisease"),
        "medicines" to listOf("medicines", "Medicines", "medicine", "Medicine", "medications", "Medications"),
        "surgeries" to listOf("surgeries", "Surgeries", "surgery", "Surgery"),
        "bloodType" to listOf("bloodType", "BloodType", "blood_type", "Blood_Type", "blood", "Blood", "bloodGroup", "BloodGroup"),
    )

    private val tokenFieldNames = listOf("token", "Token", "accessToken", "AccessToken", "access_token", "Access_Token", "jwt", "Jwt", "jwtToken", "JwtToken")

    fun parseToken(bodyString: String?, gson: Gson): String? {
        if (bodyString.isNullOrBlank()) return null

        // Try LoginResponse
        try {
            val loginResp = gson.fromJson(bodyString, LoginResponse::class.java)
            if (loginResp.token != null) return loginResp.token
        } catch (_: Exception) {}

        // Try Map
        try {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(bodyString, mapType)
            val source = extractInnerSource(map)
            for (fieldName in tokenFieldNames) {
                val token = source[fieldName]?.toString()
                if (!token.isNullOrBlank()) return token
            }
        } catch (_: Exception) {}

        // Try plain string (could be raw JWT)
        try {
            val raw = bodyString.trim().removeSurrounding("\"")
            if (raw.startsWith("eyJ") || raw.length > 20) return raw
        } catch (_: Exception) {}

        return null
    }

    fun parsePatient(bodyString: String?, gson: Gson): PatientResponse? {
        if (bodyString.isNullOrBlank()) return null

        // Try direct PatientResponse first (fast path for exact match)
        try {
            val patient = gson.fromJson(bodyString, PatientResponse::class.java)
            if (patient.id.isNotBlank() || patient.fullName?.isNotBlank() == true) {
                val hasRealData = patient.age != null || patient.phone != null || patient.email != null || patient.userId != null || patient.bloodType != null
                if (hasRealData) return patient
            }
        } catch (_: Exception) {}

        // Fallback: extract from map with flexible field name matching
        try {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val outerMap: Map<String, Any> = gson.fromJson(bodyString, mapType)
            val source = extractInnerSource(outerMap)

            val patientId = getField(source, "patientId") ?: return null
            return PatientResponse(
                id = patientId,
                userId = getField(source, "userId"),
                fullName = getField(source, "fullName"),
                firstName = getField(source, "firstName"),
                lastName = getField(source, "lastName"),
                email = getField(source, "email"),
                age = getIntField(source, "age"),
                phone = getField(source, "phoneNumber"),
                image = getField(source, "image"),
                birthday = getField(source, "birthday"),
                chronicDiseases = getField(source, "chronicDiseases"),
                medicines = getField(source, "medicines"),
                surgeries = getField(source, "surgeries"),
                bloodType = getField(source, "bloodType")
            )
        } catch (_: Exception) {}

        return null
    }

    private fun extractInnerSource(map: Map<String, Any>): Map<String, Any> {
        for (key in listOf("data", "Data", "result", "Result", "patient", "Patient", "user", "User", "record", "Record", "records", "Records", "list", "List", "items", "Items", "dto", "Dto")) {
            val value = map[key]
            if (value is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                return value as Map<String, Any>
            }
            if (value is List<*> && value.isNotEmpty() && value[0] is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                return value[0] as Map<String, Any>
            }
        }
        return map
    }

    private fun getField(map: Map<String, Any>, fieldName: String): String? {
        val alternatives = FIELD_MAP[fieldName] ?: return null
        for (alt in alternatives) {
            val value = map[alt]
            if (value != null) {
                return value.toString()
            }
        }
        // Try case-insensitive match
        for ((key, value) in map) {
            if (value == null) continue
            if (key.equals(fieldName, ignoreCase = true)) return value.toString()
        }
        return null
    }

    private fun getIntField(map: Map<String, Any>, fieldName: String): Int? {
        val alternatives = FIELD_MAP[fieldName] ?: return null
        for (alt in alternatives) {
            val value = map[alt]
            if (value != null) {
                when (value) {
                    is Number -> return value.toInt()
                    is String -> return value.toIntOrNull()
                }
            }
        }
        // Try case-insensitive match
        for ((key, value) in map) {
            if (value == null) continue
            if (key.equals(fieldName, ignoreCase = true)) {
                return when (value) {
                    is Number -> value.toInt()
                    is String -> value.toIntOrNull()
                    else -> null
                }
            }
        }
        return null
    }
}
