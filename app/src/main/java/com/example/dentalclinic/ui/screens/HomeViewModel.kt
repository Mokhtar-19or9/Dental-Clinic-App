package com.example.dentalclinic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalclinic.data.api.PatientParser
import com.example.dentalclinic.data.api.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var patientName by mutableStateOf("User")
    var isLoading by mutableStateOf(false)

    init {
        fetchPatientData()
    }

    private fun fetchPatientData() {
        viewModelScope.launch {
            isLoading = true
            try {
                val gson = Gson()
                val response = RetrofitClient.service.getCurrentPatient()
                if (response.isSuccessful) {
                    val rawBody = response.body()?.string()
                    if (!rawBody.isNullOrBlank()) {
                        val p = PatientParser.parsePatient(rawBody, gson)
                        if (p?.fullName?.isNotBlank() == true) patientName = p.fullName
                    }
                }
            } catch (_: Exception) {
            } finally {
                isLoading = false
            }
        }
    }
}
