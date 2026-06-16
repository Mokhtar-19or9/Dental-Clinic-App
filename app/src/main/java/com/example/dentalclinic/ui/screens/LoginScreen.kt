package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.PatientResponse
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.data.api.UserLoginRequest
import com.google.gson.Gson
import com.example.dentalclinic.ui.components.FunnyToothMascot
import com.example.dentalclinic.ui.components.PrimaryDentalButton
import com.example.dentalclinic.ui.theme.DentalTeal
import com.example.dentalclinic.ui.theme.DentalTealDark
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val passwordErrorMsg = stringResource(R.string.password_required)
    val emailErrorMsg = stringResource(R.string.email_required)

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FunnyToothMascot(
                modifier = Modifier.size(120.dp),
                style = AppSettings.mascotStyle
            )
            
            Spacer(Modifier.height(24.dp))
            
            Text(
                stringResource(R.string.welcome_back),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DentalTealDark
            )
            
            Text(
                stringResource(R.string.login_to_account),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 16.sp
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text(stringResource(R.string.email)) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

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

            Spacer(Modifier.height(32.dp))

            if (isLoading) {
                CircularProgressIndicator(color = DentalTeal)
            } else {
                PrimaryDentalButton(
                    text = stringResource(R.string.login),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (email.isEmpty()) {
                        scope.launch { snackbarHostState.showSnackbar(emailErrorMsg) }
                    } else if (password.isEmpty()) {
                        scope.launch { snackbarHostState.showSnackbar(passwordErrorMsg) }
                    } else {
                        scope.launch {
                            isLoading = true
                            try {
                                val gson = Gson()

                                // Save email from form as fallback patient data immediately
                                val nameFromEmail = email.split("@")[0]
                                AppSettings.savePatient(PatientResponse(
                                    id = "",
                                    fullName = nameFromEmail,
                                    email = email
                                ))

                                // Try Account/Login with userName+password (same as website)
                                val loginPayload = mapOf("userName" to email, "password" to password)
                                val accountLoginResponse = RetrofitClient.service.loginRaw(loginPayload)
                                var loginSucceeded = accountLoginResponse.isSuccessful

                                if (loginSucceeded) {
                                    val rawBody = accountLoginResponse.body()?.string()
                                    val token = com.example.dentalclinic.data.api.PatientParser.parseToken(rawBody, gson)
                                    if (token != null) AppSettings.saveToken(token)
                                }

                                // Fallback: try Patient/login with the typed request
                                if (!loginSucceeded) {
                                    val typedRequest = UserLoginRequest(
                                        email = email,
                                        userName = email,
                                        password = password
                                    )
                                    val patientLoginResponse = RetrofitClient.service.patientLogin(typedRequest)
                                    loginSucceeded = patientLoginResponse.isSuccessful
                                    if (loginSucceeded) {
                                        val rawBody = patientLoginResponse.body()?.string()
                                        val token = com.example.dentalclinic.data.api.PatientParser.parseToken(rawBody, gson)
                                        if (token != null) AppSettings.saveToken(token)
                                    }
                                }

                                if (!loginSucceeded) {
                                    val errorBody = if (!accountLoginResponse.isSuccessful) {
                                        accountLoginResponse.errorBody()?.string()
                                    } else null
                                    snackbarHostState.showSnackbar(errorBody ?: "Invalid email or password")
                                    isLoading = false
                                    return@launch
                                }

                                try {
                                    val patientResponse = RetrofitClient.service.getCurrentPatient()
                                    if (patientResponse.isSuccessful) {
                                        patientResponse.body()?.string()?.let { raw ->
                                            val p = com.example.dentalclinic.data.api.PatientParser.parsePatient(raw, gson)
                                            if (p != null) AppSettings.savePatient(p)
                                        }
                                    }
                                } catch (_: Exception) {}

                                // Ensure fallback patient data is always set before navigation
                                if (AppSettings.loggedInPatient?.fullName.isNullOrBlank()) {
                                    val nameFromEmail = email.split("@")[0]
                                    AppSettings.savePatient(PatientResponse(
                                        id = "",
                                        fullName = nameFromEmail,
                                        email = email
                                    ))
                                }

                                onLoginSuccess()
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Connection error: ${e.localizedMessage}")
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(16.dp))
            
            TextButton(onClick = onForgotPasswordClick) {
                Text(stringResource(R.string.forgot_password), color = DentalTeal)
            }

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(R.string.dont_have_account), color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                TextButton(onClick = onSignUpClick) {
                    Text(stringResource(R.string.sign_up), color = DentalTeal, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

