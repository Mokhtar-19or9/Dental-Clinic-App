package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.dentalclinic.ui.components.FunnyToothMascot
import com.example.dentalclinic.ui.components.PrimaryDentalButton
import com.example.dentalclinic.ui.theme.DentalTeal
import com.example.dentalclinic.ui.theme.DentalTealDark
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(onSignUpSuccess: () -> Unit, onLoginClick: () -> Unit) {
    var name by remember { mutableStateOf(value = "") }
    var email by remember { mutableStateOf(value = "") }
    var phoneNumber by remember { mutableStateOf(value = "") }
    var age by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var confirmPassword by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var confirmPasswordVisible by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val allFieldsRequiredMsg = stringResource(R.string.all_fields_required)
    val passwordsDontMatchMsg = stringResource(R.string.passwords_dont_match)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FunnyToothMascot(
                modifier = Modifier.size(100.dp),
                style = AppSettings.mascotStyle
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                stringResource(R.string.create_account),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DentalTealDark
            )
            
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.full_name)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Row(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = { Text(stringResource(R.string.phone)) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    enabled = !isLoading
                )
                Spacer(Modifier.width(12.dp))
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text(stringResource(R.string.age)) },
                    modifier = Modifier.width(80.dp),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isLoading
                )
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = DentalTeal)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text(stringResource(R.string.confirm_password)) },
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (confirmPasswordVisible)
                        Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff

                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = DentalTeal)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = DentalTeal)
            } else {
                PrimaryDentalButton(
                    text = stringResource(R.string.sign_up),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (name.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || phoneNumber.isBlank() || age.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar(allFieldsRequiredMsg) }
                    } else if (password != confirmPassword) {
                        scope.launch { snackbarHostState.showSnackbar(passwordsDontMatchMsg) }
                    } else {
                        scope.launch {
                            isLoading = true
                            try {
                                val request = PatientRegisterRequest(
                                    fullName = name,
                                    email = email,
                                    userName = email, // Set username as email
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    phoneNumber = phoneNumber,
                                    age = age.toIntOrNull() ?: 0
                                )
                                val response = RetrofitClient.service.registerPatient(request)
                                if (response.isSuccessful) {
                                    // Save the name we have locally immediately
                                    val names = name.split(" ")
                                    val fallbackPatient = com.example.dentalclinic.data.api.PatientResponse(
                                        id = response.body()?.id ?: "",
                                        firstName = names.firstOrNull(),
                                        lastName = if (names.size > 1) names.asSequence().drop(1).joinToString(" ") else null,
                                        fullName = name,
                                        email = email,
                                        age = age.toIntOrNull(),
                                        phone = phoneNumber
                                    )
                                    AppSettings.savePatient(fallbackPatient)

                                    // After successful registration, try to fetch the full patient details
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
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            TextButton(onClick = onLoginClick) {
                Text(stringResource(R.string.already_have_account), color = DentalTeal)
            }
        }
    }
}


