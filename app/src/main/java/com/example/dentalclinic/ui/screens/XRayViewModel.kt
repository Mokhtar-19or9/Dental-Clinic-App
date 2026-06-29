package com.example.dentalclinic.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.data.model.XRayRecord
import kotlinx.coroutines.launch

class XRayViewModel : ViewModel() {
    var isLoading by mutableStateOf(false)
    val xrayRecords = mutableStateListOf<XRayRecord>()
    var errorMessage by mutableStateOf<String?>(null)

    init {
        fetchXrays()
    }

    private fun fetchXrays() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                // Fetch only verified data from API
                val response = RetrofitClient.service.getAllRays()
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        xrayRecords.clear()
                        // Map only actual API data
                        xrayRecords.addAll(list.map { 
                            XRayRecord(
                                title = it.name ?: "Untitled Scan",
                                date = it.createdAt ?: "Date unavailable",
                                finding = it.description ?: "No findings reported by doctor."
                            )
                        })
                    }
                } else {
                    errorMessage = "Unable to fetch verified scans."
                }
            } catch (e: Exception) {
                errorMessage = "Connection error. Please try again."
            } finally {
                isLoading = false
            }
        }
    }
}
