package com.example.dentalclinic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalclinic.data.api.PatientRegisterRequest
import com.example.dentalclinic.data.api.RetrofitClient
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    var signUpError by mutableStateOf<String?>(null)
    var signUpSuccess by mutableStateOf(false)

    fun signUp(fullName: String, email: String, password: String, confirmPassword: String, phoneNumber: String, age: Int) {
        viewModelScope.launch {
            isLoading = true
            signUpError = null
            try {
                val response = RetrofitClient.service.registerPatient(
                    PatientRegisterRequest(
                        fullName = fullName,
                        email = email,
                        userName = email,
                        password = password,
                        confirmPassword = confirmPassword,
                        phoneNumber = phoneNumber,
                        age = age
                    )
                )
                if (response.isSuccessful) {
                    signUpSuccess = true
                } else {
                    val errorBody = response.errorBody()?.string()
                    signUpError = if (!errorBody.isNullOrBlank()) {
                        errorBody
                    } else {
                        "Server error: ${response.code()} ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                signUpError = "Connection error: ${e.localizedMessage}"
            } finally {
                isLoading = false
            }
        }
    }
}
