package com.example.dentalclinic.navigation

import androidx.annotation.StringRes
import com.example.dentalclinic.R

enum class DentalRoute(val label: String, val iconText: String, @StringRes val labelRes: Int = 0) {
    Splash("Splash", "", R.string.app_name),
    Login("Login", "", R.string.login),
    SignUp("SignUp", "", R.string.sign_up),
    Home("Home", "🏠", R.string.home),
    Appointments("Appointments", "📅", R.string.appointments),
    XRay("X-Ray", "🦷", R.string.xray),
    Profile("Profile", "👤", R.string.profile),
    Settings("Settings", "⚙️", R.string.settings),
    Diagnosis("Diagnosis", "📋", R.string.diagnosis),
    History("History", "📁", R.string.medical_history),
    Notifications("Notifications", "🔔", R.string.notifications),
    Chat("Chat", "💬", R.string.chat),
    ForgotPassword("ForgotPassword", "", R.string.forgot_password)
}

val bottomRoutes = listOf(
    DentalRoute.Home,
    DentalRoute.Appointments,
    DentalRoute.Chat,
    DentalRoute.Profile,
    DentalRoute.Settings
)
