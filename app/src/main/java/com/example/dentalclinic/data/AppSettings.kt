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
    var jwtToken by mutableStateOf<String?>(value = null)

    fun init(context: Context) {
        val p = context.getSharedPreferences("dental_prefs_v2", Context.MODE_PRIVATE)
        prefs = p
        isDarkMode = p.getBoolean("dark_mode", false)
        currentLanguage = p.getString("lang", "en") ?: "en"
        jwtToken = p.getString("jwt_token", null)
        
        val patientJson = p.getString("patient_data", null)
        if (patientJson != null) {
            try {
                val patient = gson.fromJson(patientJson, PatientResponse::class.java)
                if (patient.id.isNotBlank() || patient.fullName?.isNotBlank() == true) {
                    loggedInPatient = patient
                } else {
                    loggedInPatient = null
                }
            } catch (_: Exception) {
                loggedInPatient = null
            }
        }
    }

    fun saveToken(token: String?) {
        jwtToken = token
        prefs?.edit()?.apply {
            putString("jwt_token", token)
            apply()
        }
    }

    /**
     * Saves patient data, merging with existing data to prevent loss of fields (like email or age)
     * if the new response is partial.
     */
    fun savePatient(patient: PatientResponse?) {
        if (patient == null) {
            loggedInPatient = null
        } else {
            val current = loggedInPatient
            if (current == null) {
                loggedInPatient = patient
            } else {
                // Merge logic: prefer new values but keep current if new is null/blank
                loggedInPatient = patient.copy(
                    id = if (patient.id.isNotBlank()) patient.id else current.id,
                    fullName = if (!patient.fullName.isNullOrBlank()) patient.fullName else current.fullName,
                    email = if (!patient.email.isNullOrBlank()) patient.email else current.email,
                    phone = if (!patient.phone.isNullOrBlank()) patient.phone else current.phone,
                    age = if (patient.age != null && patient.age != 0) patient.age else current.age,
                    userId = if (!patient.userId.isNullOrBlank()) patient.userId else current.userId,
                    firstName = if (!patient.firstName.isNullOrBlank()) patient.firstName else current.firstName,
                    lastName = if (!patient.lastName.isNullOrBlank()) patient.lastName else current.lastName,
                    image = if (!patient.image.isNullOrBlank()) patient.image else current.image,
                    birthday = if (!patient.birthday.isNullOrBlank()) patient.birthday else current.birthday,
                    chronicDiseases = if (!patient.chronicDiseases.isNullOrBlank()) patient.chronicDiseases else current.chronicDiseases,
                    medicines = if (!patient.medicines.isNullOrBlank()) patient.medicines else current.medicines,
                    surgeries = if (!patient.surgeries.isNullOrBlank()) patient.surgeries else current.surgeries,
                    bloodType = if (!patient.bloodType.isNullOrBlank()) patient.bloodType else current.bloodType
                )
            }
        }
        
        prefs?.edit()?.apply {
            putString("patient_data", loggedInPatient?.let { gson.toJson(it) })
            apply()
        }
    }

    fun logout() {
        jwtToken = null
        loggedInPatient = null
        bookedAppointment = null
        prefs?.edit()?.clear()?.apply()
    }
}
