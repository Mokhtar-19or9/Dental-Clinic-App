package com.example.dentalclinic.navigation

import androidx.annotation.StringRes
import com.example.dentalclinic.R

enum class DentalRoute(val label: String, val iconText: String, @StringRes val labelRes: Int = 0) {
    Splash("Splash", "", R.string.app_name),
    Login("Login", "", R.string.login),
    SignUp("SignUp", "", R.string.sign_up),
    Home("Home", "H", R.string.home),
    Appointments("Appointments", "A", R.string.appointments),
    XRay("X-Ray", "X", R.string.xray),
    Profile("Profile", "P", R.string.profile),
    Settings("Settings", "S", R.string.settings),
    Diagnosis("Diagnosis", "D", R.string.diagnosis),
    History("History", "M", R.string.medical_history),
    Notifications("Notifications", "N", R.string.notifications),
    Chat("Chat", "C", R.string.chat),
    ForgotPassword("ForgotPassword", "", R.string.forgot_password)
}

val bottomRoutes = listOf(
    DentalRoute.Home,
    DentalRoute.Appointments,
    DentalRoute.Chat,
    DentalRoute.Profile,
    DentalRoute.Settings
)
