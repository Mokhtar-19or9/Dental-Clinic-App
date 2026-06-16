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
import com.example.dentalclinic.data.api.UserLoginRequest
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
                    successfulLoginBody = accountLoginResponse.body()?.string()
                    val token = PatientParser.parseToken(successfulLoginBody, gson)
                    if (token != null) AppSettings.saveToken(token)
                }

                // Fallback: try Patient/login with the typed request
                if (!loginSucceeded) {
                    val typedRequest = UserLoginRequest(
                        email = email,
                        userName = email,
                        password = password
                    )
                    val patientLoginResponse = RetrofitClient.service.patientLogin(typedRequest)
                    loginSucceeded = patientLoginResponse.isSuccessful
                    if (loginSucceeded) {
                        successfulLoginBody = patientLoginResponse.body()?.string()
                        val token = PatientParser.parseToken(successfulLoginBody, gson)
                        if (token != null) AppSettings.saveToken(token)
                    }
                }

                if (!loginSucceeded) {
                    val errorBody = if (!accountLoginResponse.isSuccessful) {
                        accountLoginResponse.errorBody()?.string()
                    } else null
                    loginError = errorBody ?: "Invalid email or password"
                    isLoading = false
                    return@launch
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
}
