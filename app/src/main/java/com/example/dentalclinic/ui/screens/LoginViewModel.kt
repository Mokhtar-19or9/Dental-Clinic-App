package com.example.dentalclinic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.data.api.UserLoginRequest
import com.example.dentalclinic.data.AppSettings
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
                val loginRequest = UserLoginRequest(
                    email = email,
                    userName = email,
                    password = password
                )
                
                // Try Patient Login first
                val patientLoginResponse = RetrofitClient.service.patientLogin(loginRequest)
                
                val loginSuccessful = if (patientLoginResponse.isSuccessful) {
                    true
                } else {
                    // Fallback to Account Login
                    val accountLoginResponse = RetrofitClient.service.login(loginRequest)
                    accountLoginResponse.isSuccessful
                }

                if (loginSuccessful) {
                    // Fetch full patient details
                    val patientResponse = RetrofitClient.service.getCurrentPatient()
                    if (patientResponse.isSuccessful) {
                        AppSettings.savePatient(patientResponse.body())
                    }
                    loginSuccess = true
                } else {
                    loginError = "Invalid email or password"
                }
            } catch (e: Exception) {
                loginError = "Connection error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
