package com.example.dentalclinic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalclinic.data.api.PatientResponse
import com.example.dentalclinic.data.api.RetrofitClient
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var patientName by mutableStateOf("John Doe")
    var isLoading by mutableStateOf(false)

    init {
        fetchPatientData()
    }

    private fun fetchPatientData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.service.getCurrentPatient()
                if (response.isSuccessful) {
                    val p = response.body()
                    patientName = "${p?.firstName} ${p?.lastName}"
                }
            } catch (e: Exception) {
                // Keep default
            } finally {
                isLoading = false
            }
        }
    }
}
