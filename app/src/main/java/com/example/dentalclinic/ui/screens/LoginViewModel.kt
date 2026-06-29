package com.example.dentalclinic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.PatientParser
import com.example.dentalclinic.data.api.PatientResponse
import com.example.dentalclinic.data.api.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            isLoading = true
            loginError = null
            try {
                val gson = Gson()

                // Save email from form as fallback patient data immediately
                val nameFromEmail = email.split("@")[0]
                AppSettings.savePatient(PatientResponse(
                    id = "",
                    fullName = nameFromEmail,
                    email = email
                ))

                // Try Account/Login with userName+password (same as website)
                val loginPayload = mapOf("userName" to email, "password" to password)
                val accountLoginResponse = RetrofitClient.service.loginRaw(loginPayload)
                var loginSucceeded = accountLoginResponse.isSuccessful
                var successfulLoginBody: String? = null

                if (loginSucceeded) {
                    // 1) Check response headers for JWT
                    val headers = accountLoginResponse.headers().toMultimap()
                    var token = PatientParser.parseTokenFromHeaders(headers)
                    // 2) Fall back to body parsing
                    if (token == null) {
                        successfulLoginBody = accountLoginResponse.body()?.string()
                        token = PatientParser.parseToken(successfulLoginBody, gson)
                        // 3) If body is [UserResponse] array, extract patient info
                        if (successfulLoginBody != null) {
                            PatientParser.parseUserResponseBody(successfulLoginBody, gson)?.let { p ->
                                AppSettings.savePatient(p)
                            }
                        }
                    }
                    if (token != null) AppSettings.saveToken(token)
                }

                // Fallback: try Patient/login with email+password (patient endpoint)
                if (!loginSucceeded) {
                    val loginReq = mapOf("email" to email, "password" to password)
                    val patientLoginResponse = RetrofitClient.service.patientLoginRaw(loginReq)
                    loginSucceeded = patientLoginResponse.isSuccessful
                    if (loginSucceeded) {
                        successfulLoginBody = patientLoginResponse.body()?.string()
                        val headers = patientLoginResponse.headers().toMultimap()
                        var token = PatientParser.parseTokenFromHeaders(headers)
                        if (token == null) {
                            token = PatientParser.parseToken(successfulLoginBody, gson)
                        }
                        if (token != null) AppSettings.saveToken(token)
                    } else {
                        val errorBody = if (!accountLoginResponse.isSuccessful) {
                            accountLoginResponse.errorBody()?.string()
                        } else {
                            patientLoginResponse.errorBody()?.string()
                        }
                        loginError = parseLoginError(errorBody, gson)
                        isLoading = false
                        return@launch
                    }
                }

                // 1. Try to extract patient from login response (some APIs return user info here)
                successfulLoginBody?.let { body ->
                    PatientParser.parsePatient(body, gson)?.let { p ->
                        AppSettings.savePatient(p)
                    }
                }

                // 2. Fetch current patient from dedicated endpoint
                try {
                    val patientResponse = RetrofitClient.service.getCurrentPatient()
                    if (patientResponse.isSuccessful) {
                        val raw = patientResponse.body()?.string()
                        println("DEBUG_PATIENT_RAW: $raw")
                        raw?.let {
                            PatientParser.parsePatient(it, gson)?.let { p ->
                                AppSettings.savePatient(p)
                            }
                        }
                    }
                } catch (e: Exception) {
                    println("DEBUG_PATIENT_ERROR: ${e.message}")
                }

                loginSuccess = true
            } catch (e: Exception) {
                loginError = "Connection error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }

    private fun parseLoginError(errorBody: String?, gson: Gson): String {
        if (errorBody.isNullOrBlank()) return "Invalid email or password"

        // Try extracting message from common JSON error formats
        try {
            val map = gson.fromJson(errorBody, Map::class.java)
            for (key in listOf("message", "Message", "title", "Title", "error", "Error", "detail", "Detail")) {
                val value = map?.get(key)?.toString()
                if (!value.isNullOrBlank()) return value
            }
            // Nested errors object (e.g. ASP.NET errors)
            val errors = map?.get("errors")
            if (errors is Map<*, *>) {
                for ((_, v) in errors) {
                    if (v is List<*>) {
                        val first = v.firstOrNull()?.toString()
                        if (!first.isNullOrBlank()) return first
                    }
                }
            }
        } catch (_: Exception) {}

        return errorBody.replace("\"", "").trim()
    }
}
