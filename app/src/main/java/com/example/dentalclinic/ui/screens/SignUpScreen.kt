package com.example.dentalclinic.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.PatientParser
import com.example.dentalclinic.data.api.PatientRegisterRequest
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.theme.*
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onLoginClick: () -> Unit) {
    var name by remember { mutableStateOf(value = "") }
    var email by remember { mutableStateOf(value = "") }
    var phoneNumber by remember { mutableStateOf(value = "") }
    var birthday by remember { mutableStateOf(value = "") }
    var bloodType by remember { mutableStateOf(value = "") }
    var chronicDiseases by remember { mutableStateOf(value = "") }
    var surgeries by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var confirmPassword by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var confirmPasswordVisible by remember { mutableStateOf(value = false) }
    var showMedical by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val passwordsDontMatchMsg = stringResource(R.string.passwords_dont_match)

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format(Locale.ENGLISH, "%02d/%02d/%04d", dayOfMonth, month + 1, year)
            birthday = formattedDate
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    fun generateUsernameFromName(fullName: String): String {
        return fullName.lowercase()
            .replace(Regex("[^a-z0-9]"), "")
            .take(20)
            .ifBlank { "user${System.currentTimeMillis() % 10000}" }
    }

    fun calculateAgeFromBirthday(birthdayStr: String): Int {
        if (birthdayStr.isBlank()) return 0
        return try {
            val parts = birthdayStr.split("/")
            if (parts.size == 3) {
                val day = parts[0].toInt()
                val month = parts[1].toInt()
                val year = parts[2].toInt()
                val cal = java.util.Calendar.getInstance()
                val currentYear = cal.get(java.util.Calendar.YEAR)
                val currentMonth = cal.get(java.util.Calendar.MONTH) + 1
                val currentDay = cal.get(java.util.Calendar.DAY_OF_MONTH)
                var age = currentYear - year
                if (month > currentMonth || (month == currentMonth && day > currentDay)) {
                    age--
                }
                age
            } else 0
        } catch (_: Exception) { 0 }
    }

    fun formatBirthdayForServer(birthdayStr: String): String? {
        if (birthdayStr.isBlank()) return null
        return try {
            val parts = birthdayStr.split("/")
            if (parts.size == 3) {
                // Convert DD/MM/YYYY to YYYY-MM-DD
                "${parts[2]}-${parts[1]}-${parts[0]}"
            } else null
        } catch (_: Exception) { null }
    }

    fun getPasswordStrength(pass: String): String? {
        if (pass.isBlank()) return null
        var score = 0
        if (pass.length >= 8) score++
        if (pass.any { it.isUpperCase() }) score++
        if (pass.any { it.isDigit() }) score++
        if (pass.any { !it.isLetterOrDigit() }) score++
        
        return when {
            score < 2 -> "Weak password. Consider using numbers and symbols for better security."
            score < 4 -> "Medium strength password."
            else -> null // Strong
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(DentalTealDark, DentalTeal, DentalCyan),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 80.dp, y = (-60).dp)
                        .size(280.dp)
                        .alpha(0.12f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(DentalMint, Color.Transparent),
                                radius = 280f
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .offset(x = (-80).dp, y = 80.dp)
                        .size(320.dp)
                        .alpha(0.08f)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White, Color.Transparent),
                                radius = 320f
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(40.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.image_logo),
                                contentDescription = null,
                                modifier = Modifier.size(72.dp)
                            )

                            Spacer(Modifier.height(20.dp))

                            Text(
                                stringResource(R.string.create_account),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = DentalText
                            )

                            Spacer(Modifier.height(24.dp))

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text(stringResource(R.string.full_name)) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Person, contentDescription = null, tint = DentalTeal, modifier = Modifier.size(20.dp))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                enabled = !isLoading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DentalTeal,
                                    focusedLabelColor = DentalTeal,
                                    cursorColor = DentalTeal
                                )
                            )

                            Spacer(Modifier.height(14.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(stringResource(R.string.email)) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Email, contentDescription = null, tint = DentalTeal, modifier = Modifier.size(20.dp))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                enabled = !isLoading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DentalTeal,
                                    focusedLabelColor = DentalTeal,
                                    cursorColor = DentalTeal
                                )
                            )

                            Spacer(Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = phoneNumber,
                                    onValueChange = { phoneNumber = it },
                                    label = { Text(stringResource(R.string.phone)) },
                                    leadingIcon = {
                                        Icon(Icons.Filled.Phone, contentDescription = null, tint = DentalTeal, modifier = Modifier.size(20.dp))
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(14.dp),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    enabled = !isLoading,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = DentalTeal,
                                        focusedLabelColor = DentalTeal,
                                        cursorColor = DentalTeal
                                    )
                                )
                                Spacer(Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = birthday,
                                        onValueChange = { },
                                        readOnly = true,
                                        label = { Text("Birthday") },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Filled.CalendarMonth,
                                                contentDescription = null,
                                                tint = DentalTeal,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DentalTeal,
                                            unfocusedBorderColor = DentalLine,
                                            focusedLabelColor = DentalTeal,
                                            cursorColor = DentalTeal
                                        )
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .alpha(0f)
                                            .clickable(enabled = !isLoading) { datePickerDialog.show() }
                                    )
                                }
                            }

                            Spacer(Modifier.height(14.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(stringResource(R.string.password)) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = DentalTeal, modifier = Modifier.size(20.dp))
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(imageVector = image, contentDescription = null, tint = DentalTeal)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                enabled = !isLoading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DentalTeal,
                                    focusedLabelColor = DentalTeal,
                                    cursorColor = DentalTeal
                                )
                            )

                            getPasswordStrength(password)?.let { tip ->
                                Text(
                                    text = "💡 Tip: $tip",
                                    color = if (tip.startsWith("Weak")) Color(0xFFE57373) else DentalTeal,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(top = 4.dp).align(Alignment.Start)
                                )
                            }

                            Spacer(Modifier.height(14.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text(stringResource(R.string.confirm_password)) },
                                leadingIcon = {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = DentalTeal, modifier = Modifier.size(20.dp))
                                },
                                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                        Icon(imageVector = image, contentDescription = null, tint = DentalTeal)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                enabled = !isLoading,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DentalTeal,
                                    focusedLabelColor = DentalTeal,
                                    cursorColor = DentalTeal
                                )
                            )

                            Spacer(Modifier.height(8.dp))
                            TextButton(onClick = { showMedical = !showMedical }) {
                                Text(
                                    if (showMedical) "▲ Medical Info (optional)" else "▼ Medical Info (optional)",
                                    color = DentalTeal,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            AnimatedVisibility(visible = showMedical) {
                                Column {
                                    Spacer(Modifier.height(8.dp))
                                    OutlinedTextField(
                                        value = bloodType,
                                        onValueChange = { bloodType = it },
                                        label = { Text("Blood Type (optional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        singleLine = true,
                                        enabled = !isLoading,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DentalTeal,
                                            focusedLabelColor = DentalTeal,
                                            cursorColor = DentalTeal
                                        )
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(rememberScrollState()),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        bloodTypes.forEach { type ->
                                            FilterChip(
                                                selected = bloodType == type,
                                                onClick = { bloodType = type },
                                                label = { Text(type, fontSize = 12.sp) },
                                                colors = FilterChipDefaults.filterChipColors(
                                                    selectedContainerColor = DentalTeal,
                                                    selectedLabelColor = Color.White,
                                                    labelColor = DentalTeal
                                                ),
                                                border = FilterChipDefaults.filterChipBorder(
                                                    enabled = true,
                                                    selected = bloodType == type,
                                                    borderColor = DentalTeal,
                                                    selectedBorderColor = DentalTeal
                                                )
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(14.dp))
                                    OutlinedTextField(
                                        value = chronicDiseases,
                                        onValueChange = { chronicDiseases = it },
                                        label = { Text("Chronic Diseases (optional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        enabled = !isLoading,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DentalTeal,
                                            focusedLabelColor = DentalTeal,
                                            cursorColor = DentalTeal
                                        )
                                    )
                                    Spacer(Modifier.height(14.dp))
                                    OutlinedTextField(
                                        value = surgeries,
                                        onValueChange = { surgeries = it },
                                        label = { Text("Surgeries (optional)") },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(14.dp),
                                        enabled = !isLoading,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = DentalTeal,
                                            focusedLabelColor = DentalTeal,
                                            cursorColor = DentalTeal
                                        )
                                    )
                                }
                            }

                            Spacer(Modifier.height(20.dp))

                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = DentalTeal,
                                    modifier = Modifier.size(48.dp))
                            } else {
                                Button(
                                    onClick = {
                                        if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || phoneNumber.isBlank() || birthday.isBlank()) {
                                            scope.launch { snackbarHostState.showSnackbar("All fields are required") }
                                        } else if (password != confirmPassword) {
                                            scope.launch { snackbarHostState.showSnackbar(passwordsDontMatchMsg) }
                                        } else {
                                            scope.launch {
                                                isLoading = true
                                                try {
                                                    val trimmedName = name.trim()
                                                    val trimmedEmail = email.trim()
                                                    val generatedUsername = generateUsernameFromName(trimmedName)
                                                    
                                                    val request = PatientRegisterRequest(
                                                        fullName = trimmedName,
                                                        userName = generatedUsername,
                                                        email = trimmedEmail,
                                                        password = password,
                                                        confirmPassword = confirmPassword,
                                                        phoneNumber = phoneNumber.trim(),
                                                        age = calculateAgeFromBirthday(birthday),
                                                        birthday = formatBirthdayForServer(birthday),
                                                        bloodType = bloodType.trim().uppercase().ifBlank { null },
                                                        chronicDiseases = chronicDiseases.ifBlank { null },
                                                        medicines = null,
                                                        surgeries = surgeries.ifBlank { null }
                                                    )
                                                    val response = RetrofitClient.service.registerPatient(request)
                                                    if (response.isSuccessful) {
                                                        val names = name.split(" ")
                                                        val fallbackPatient = com.example.dentalclinic.data.api.PatientResponse(
                                                            id = response.body()?.id ?: "",
                                                            firstName = names.firstOrNull(),
                                                            lastName = if (names.size > 1) names.asSequence().drop(1).joinToString(" ") else null,
                                                            fullName = name,
                                                            email = email,
                                                            age = calculateAgeFromBirthday(birthday),
                                                            phone = phoneNumber,
                                                            birthday = formatBirthdayForServer(birthday),
                                                            bloodType = bloodType.ifBlank { null },
                                                            chronicDiseases = chronicDiseases.ifBlank { null },
                                                            surgeries = surgeries.ifBlank { null }
                                                        )
                                                        AppSettings.savePatient(fallbackPatient)

                                                        try {
                                                            val patientResponse = RetrofitClient.service.getCurrentPatient()
                                                            if (patientResponse.isSuccessful) {
                                                                patientResponse.body()?.string()?.let { raw ->
                                                                    val p = PatientParser.parsePatient(raw, com.google.gson.Gson())
                                                                    if (p != null) AppSettings.savePatient(p)
                                                                }
                                                            }
                                                        } catch (_: Exception) {}
                                                        onSignUpSuccess()
                                                    } else {
                                                        val errorBody = response.errorBody()?.string() ?: "Unknown error"
                                                        val displayMsg = if (errorBody.contains("entity changes")) {
                                                            "Database Error: Possibly duplicate email or phone number."
                                                        } else {
                                                            errorBody
                                                        }
                                                        snackbarHostState.showSnackbar("Registration failed: $displayMsg")
                                                    }
                                                } catch (e: Exception) {
                                                    snackbarHostState.showSnackbar("Error: ${e.localizedMessage}")
                                                } finally {
                                                    isLoading = false
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DentalTeal,
                                        contentColor = Color.White
                                    ),
                                    enabled = !isLoading
                                ) {
                                    Text(
                                        stringResource(R.string.sign_up),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    TextButton(onClick = onLoginClick) {
                        Text(
                            stringResource(R.string.already_have_account),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}


