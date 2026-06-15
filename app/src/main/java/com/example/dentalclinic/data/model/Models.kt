package com.example.dentalclinic.data.model

data class Patient(
    val name: String,
    val age: Int,
    val phone: String,
    val email: String,
    val emergencyContact: String,
    val insurance: String
)

data class Appointment(
    val title: String,
    val date: String,
    val time: String,
    val dentist: String,
    val status: String
)

data class Dentist(
    val name: String,
    val specialty: String,
    val rating: String
)

data class XRayRecord(
    val title: String,
    val date: String,
    val finding: String
)

data class Diagnosis(
    val tooth: String,
    val issue: String,
    val severity: String,
    val treatmentPlan: String
)

data class MedicalHistoryItem(
    val title: String,
    val date: String,
    val description: String
)

data class NotificationItem(
    val title: String,
    val message: String,
    val time: String,
    val unread: Boolean
)

data class ChatMessage(
    val sender: String,
    val text: String,
    val time: String,
    val fromPatient: Boolean
)
