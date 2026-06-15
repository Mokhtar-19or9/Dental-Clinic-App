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

    init {
        fetchXrays()
    }

    private fun fetchXrays() {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = RetrofitClient.service.getAllRays()
                if (response.isSuccessful) {
                    response.body()?.let { list ->
                        xrayRecords.clear()
                        xrayRecords.addAll(list.map { 
                            XRayRecord(
                                title = it.name ?: "Untitled Scan",
                                date = it.createdAt ?: "Recently",
                                finding = it.description ?: "No findings reported."
                            )
                        })
                    }
                }
            } catch (e: Exception) {
                // Keep existing or empty
            } finally {
                isLoading = false
            }
        }
    }
}
