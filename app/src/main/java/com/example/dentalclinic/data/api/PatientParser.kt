package com.example.dentalclinic.data.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Calendar

object PatientParser {
    private val FIELD_MAP = mapOf(
        "patientId" to listOf("patientId", "PatientId", "id", "Id", "ID", "patient_id", "Patient_Id", "key", "Key"),
        "fullName" to listOf("fullName", "FullName", "full_name", "Full_Name", "name", "Name", "patientName", "PatientName", "userName", "UserName", "displayName", "DisplayName"),
        "firstName" to listOf("firstName", "FirstName", "first_name", "First_Name", "fName"),
        "lastName" to listOf("lastName", "LastName", "last_name", "Last_Name", "lName"),
        "email" to listOf("email", "Email", "emailAddress", "EmailAddress", "email_address", "Email_Address", "mail"),
        "phoneNumber" to listOf("phoneNumber", "PhoneNumber", "phone", "Phone", "mobile", "Mobile", "mobileNumber", "MobileNumber", "telephone", "Telephone", "phone_number", "Phone_Number", "tel"),
        "age" to listOf("age", "Age", "patientAge", "PatientAge", "userAge"),
        "userId" to listOf("userId", "UserId", "user_id", "User_Id", "uid", "uId"),
        "birthday" to listOf("birthday", "Birthday", "dateOfBirth", "DateOfBirth", "dob", "Dob", "DOB", "birth_date", "Birth_Date"),
        "image" to listOf("image", "Image", "avatar", "Avatar", "profileImage", "ProfileImage", "photo", "Photo", "imageUrl"),
        "chronicDiseases" to listOf("chronicDiseases", "ChronicDiseases", "chronic_diseases", "Chronic_Diseases", "chronicDisease", "ChronicDisease", "history", "diseases"),
        "medicines" to listOf("medicines", "Medicines", "medicine", "Medicine", "medications", "Medications", "meds"),
        "surgeries" to listOf("surgeries", "Surgeries", "surgery", "Surgery", "operations"),
        "bloodType" to listOf("bloodType", "BloodType", "blood_type", "Blood_Type", "blood", "Blood", "bloodGroup", "BloodGroup"),
    )

    private val tokenFieldNames = listOf("token", "Token", "accessToken", "AccessToken", "access_token", "Access_Token", "jwt", "Jwt", "jwtToken", "JwtToken")

    private val jwtHeaderRegex = Regex("""(eyJ[a-zA-Z0-9_-]+\.eyJ[a-zA-Z0-9_-]+\.[a-zA-Z0-9_-]+)""")

    fun parseTokenFromHeaders(headers: Map<String, List<String>>): String? {
        val headerNames = listOf("Authorization", "Set-Cookie", "X-Auth-Token", "Token", "Access-Token", "access-token")
        for (name in headerNames) {
            val values = headers[name] ?: continue
            for (value in values) {
                val match = jwtHeaderRegex.find(value)
                if (match != null) return match.groupValues[1]
                if (value.trim().startsWith("eyJ")) return value.trim()
                val stripped = value.removePrefix("Bearer ").trim()
                if (stripped.startsWith("eyJ")) return stripped
            }
        }
        // Case-insensitive fallback over ALL headers
        for ((key, values) in headers) {
            for (value in values) {
                val match = jwtHeaderRegex.find(value)
                if (match != null) return match.groupValues[1]
                if (value.trim().startsWith("eyJ")) return value.trim()
                val stripped = value.removePrefix("Bearer ").trim()
                if (stripped.startsWith("eyJ")) return stripped
            }
        }
        return null
    }

    fun parseToken(bodyString: String?, gson: Gson): String? {
        if (bodyString.isNullOrBlank()) return null

        try {
            val loginResp = gson.fromJson(bodyString, LoginResponse::class.java)
            if (loginResp.token != null) return loginResp.token
        } catch (_: Exception) {}

        try {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val map: Map<String, Any> = gson.fromJson(bodyString, mapType)
            val source = extractInnerSource(map)
            for (fieldName in tokenFieldNames) {
                val token = source[fieldName]?.toString()
                if (!token.isNullOrBlank()) return token
            }
        } catch (_: Exception) {}

        try {
            val raw = bodyString.trim().removeSurrounding("\"")
            if (raw.startsWith("eyJ") || raw.length > 20) return raw
        } catch (_: Exception) {}

        return null
    }

    fun parsePatient(bodyString: String?, gson: Gson): PatientResponse? {
        if (bodyString.isNullOrBlank()) return null

        try {
            val patient = gson.fromJson(bodyString, PatientResponse::class.java)
            if (patient.id.isNotBlank() || patient.fullName?.isNotBlank() == true) {
                // Calculate age from birthday if age is null
                val enrichedAge = patient.age ?: calculateAgeFromBirthday(patient.birthday)
                val hasRealData = enrichedAge != null || patient.phone != null || patient.email != null || patient.userId != null || patient.bloodType != null
                if (hasRealData) {
                    return if (enrichedAge != patient.age) patient.copy(age = enrichedAge) else patient
                }
            }
        } catch (_: Exception) {}

        try {
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            val outerMap: Map<String, Any> = gson.fromJson(bodyString, mapType)
            val source = extractInnerSource(outerMap)

            val patientId = getField(source, "patientId") ?: return null
            val birthday = getField(source, "birthday")
            var age = getIntField(source, "age")
            if (age == null) age = calculateAgeFromBirthday(birthday)
            return PatientResponse(
                id = patientId,
                userId = getField(source, "userId"),
                fullName = getField(source, "fullName"),
                firstName = getField(source, "firstName"),
                lastName = getField(source, "lastName"),
                email = getField(source, "email"),
                age = age,
                phone = getField(source, "phoneNumber"),
                image = getField(source, "image"),
                birthday = birthday,
                chronicDiseases = getField(source, "chronicDiseases"),
                medicines = getField(source, "medicines"),
                surgeries = getField(source, "surgeries"),
                bloodType = getField(source, "bloodType")
            )
        } catch (_: Exception) {}

        return null
    }

    fun parseUserResponseBody(bodyString: String?, gson: Gson): PatientResponse? {
        if (bodyString.isNullOrBlank()) return null
        try {
            val listType = object : TypeToken<List<Map<String, Any>>>() {}.type
            val list: List<Map<String, Any>> = gson.fromJson(bodyString, listType)
            if (list.isEmpty()) return null
            val user = list[0]
            val id = getField(user, "patientId") ?: getField(user, "id") ?: return null
            val birthday = getField(user, "birthday")
            var age = getIntField(user, "age")
            if (age == null) age = calculateAgeFromBirthday(birthday)
            return PatientResponse(
                id = id,
                userId = getField(user, "userId"),
                fullName = getField(user, "fullName") ?: getField(user, "customName") ?: getField(user, "userName"),
                firstName = getField(user, "firstName"),
                lastName = getField(user, "lastName"),
                email = getField(user, "email"),
                age = age,
                phone = getField(user, "phoneNumber"),
                image = getField(user, "image"),
                birthday = birthday,
                chronicDiseases = getField(user, "chronicDiseases"),
                medicines = getField(user, "medicines"),
                surgeries = getField(user, "surgeries"),
                bloodType = getField(user, "bloodType")
            )
        } catch (_: Exception) {}
        return null
    }

    private fun calculateAgeFromBirthday(birthday: String?): Int? {
        if (birthday.isNullOrBlank()) return null
        return try {
            val datePart = birthday.substringBefore("T").substringBefore(" ")
            val parts = datePart.split("-")
            if (parts.size != 3) return null
            val birthYear = parts[0].toInt()
            val birthMonth = parts[1].toInt()
            val birthDay = parts[2].toInt()
            val now = Calendar.getInstance()
            var age = now.get(Calendar.YEAR) - birthYear
            val currentMonth = now.get(Calendar.MONTH) + 1
            val currentDay = now.get(Calendar.DAY_OF_MONTH)
            if (currentMonth < birthMonth || (currentMonth == birthMonth && currentDay < birthDay)) age--
            age
        } catch (_: Exception) { null }
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
