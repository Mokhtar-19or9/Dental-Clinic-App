package com.example.dentalclinic.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.dentalclinic.ui.components.MascotStyle
import com.example.dentalclinic.data.model.Appointment
import com.example.dentalclinic.data.api.PatientResponse
import com.google.gson.Gson

object AppSettings {
    private var prefs: SharedPreferences? = null
    private val gson = Gson()

    var isDarkMode by mutableStateOf(value = false)
    var currentLanguage by mutableStateOf(value = "en")
    var mascotStyle by mutableStateOf(value = MascotStyle.CASUAL)
    var bookedAppointment by mutableStateOf<Appointment?>(value = null)
    var loggedInPatient by mutableStateOf<PatientResponse?>(value = null)

    fun init(context: Context) {
        val p = context.getSharedPreferences("dental_prefs_v2", Context.MODE_PRIVATE)
        prefs = p
        isDarkMode = p.getBoolean("dark_mode", false)
        currentLanguage = p.getString("lang", "en") ?: "en"
        
        val patientJson = p.getString("patient_data", null)
        if (patientJson != null) {
            loggedInPatient = try {
                gson.fromJson(patientJson, PatientResponse::class.java)
            } catch (_: Exception) {
                null
            }
        }
    }

    fun savePatient(patient: PatientResponse?) {
        loggedInPatient = patient
        prefs?.edit()?.apply {
            putString("patient_data", patient?.let { gson.toJson(it) })
            apply()
        }
    }

    fun logout() {
        savePatient(null)
        bookedAppointment = null
        prefs?.edit()?.clear()?.apply()
    }
}
