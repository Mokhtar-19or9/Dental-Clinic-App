package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.data.api.PatientResponse
import com.example.dentalclinic.data.api.RetrofitClient
import com.example.dentalclinic.ui.theme.DentalCyan
import com.example.dentalclinic.ui.theme.DentalMint
import com.example.dentalclinic.ui.theme.DentalTeal
import com.example.dentalclinic.ui.theme.DentalTealDark
import com.example.dentalclinic.ui.theme.DentalText
import com.google.gson.Gson
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    val isAr = AppSettings.currentLanguage == "ar"
    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val focusManager = LocalFocusManager.current
    val passwordErrorMsg = if (isAr == true) "كلمة المرور مطلوبة" else stringResource(R.string.password_required)
    val emailErrorMsg = if (isAr == true) "البريد الإلكتروني مطلوب" else stringResource(R.string.email_required)

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
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.image_logo),
                                contentDescription = null,
                                modifier = Modifier.size(80.dp)
                            )

                            Spacer(Modifier.height(24.dp))

                            Text(
                                if (isAr == true) "مرحباً بك مجدداً" else stringResource(R.string.welcome_back),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = DentalText
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                if (isAr == true) "سجل الدخول للمتابعة" else stringResource(R.string.login_to_account),
                                color = DentalText.copy(alpha = 0.5f),
                                fontSize = 14.sp
                            )

                            Spacer(Modifier.height(28.dp))

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text(if (isAr == true) "البريد الإلكتروني" else stringResource(R.string.email)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Email,
                                        contentDescription = null,
                                        tint = DentalTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                enabled = !isLoading,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Email,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DentalTeal,
                                    focusedLabelColor = DentalTeal,
                                    cursorColor = DentalTeal
                                )
                            )

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(if (isAr == true) "كلمة المرور" else stringResource(R.string.password)) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Lock,
                                        contentDescription = null,
                                        tint = DentalTeal,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val image = if (passwordVisible)
                                        Icons.Filled.Visibility
                                    else Icons.Filled.VisibilityOff

                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            imageVector = image,
                                            contentDescription = null,
                                            tint = DentalTeal
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                singleLine = true,
                                enabled = !isLoading,
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = { focusManager.clearFocus() }
                                ),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = DentalTeal,
                                    focusedLabelColor = DentalTeal,
                                    cursorColor = DentalTeal
                                )
                            )

                            Spacer(Modifier.height(8.dp))

                            TextButton(
                                onClick = onForgotPasswordClick,
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text(
                                    if (isAr == true) "نسيت كلمة المرور؟" else stringResource(R.string.forgot_password),
                                    color = DentalTeal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(Modifier.height(8.dp))

                            if (isLoading == true) {
                                CircularProgressIndicator(
                                    color = DentalTeal,
                                    modifier = Modifier.size(48.dp))
                            } else {
                                Button(
                                    onClick = {
                                        if (email.isEmpty()) {
                                            scope.launch { snackbarHostState.showSnackbar(emailErrorMsg) }
                                        } else if (password.isEmpty()) {
                                            scope.launch { snackbarHostState.showSnackbar(passwordErrorMsg) }
                                        } else {
                                            scope.launch {
                                                isLoading = true
                                                try {
                                                    val gson = Gson()

                                                    val nameFromEmail = email.split("@")[0]
                                                    AppSettings.savePatient(PatientResponse(
                                                        id = "",
                                                        fullName = nameFromEmail,
                                                        email = email
                                                    ))

                                                    val loginPayload = mapOf("userName" to email, "password" to password)
                                                    val accountLoginResponse = RetrofitClient.service.loginRaw(loginPayload)
                                                    var loginSucceeded = accountLoginResponse.isSuccessful

                                                    if (loginSucceeded == true) {
                                                        val parser = com.example.dentalclinic.data.api.PatientParser
                                                        val headers = accountLoginResponse.headers().toMultimap()
                                                        var token = parser.parseTokenFromHeaders(headers)
                                                        val rawBody = accountLoginResponse.body()?.string()
                                                        
                                                        if (token == null) {
                                                            token = parser.parseToken(rawBody, gson)
                                                        }
                                                        
                                                        if (rawBody != null) {
                                                            parser.parsePatient(rawBody, gson)?.let { p ->
                                                                AppSettings.savePatient(p)
                                                            }
                                                        }
                                                        
                                                        if (token != null) AppSettings.saveToken(token)
                                                    }

                                                    if (loginSucceeded == false) {
                                                        val loginReq = mapOf("email" to email, "password" to password)
                                                        val patientLoginResponse = RetrofitClient.service.patientLoginRaw(loginReq)
                                                        loginSucceeded = patientLoginResponse.isSuccessful
                                                        if (loginSucceeded == true) {
                                                            val parser = com.example.dentalclinic.data.api.PatientParser
                                                            val headers = patientLoginResponse.headers().toMultimap()
                                                            var token = parser.parseTokenFromHeaders(headers)
                                                            val rawBody = patientLoginResponse.body()?.string()
                                                            
                                                            if (token == null) {
                                                                token = parser.parseToken(rawBody, gson)
                                                            }
                                                            
                                                            if (rawBody != null) {
                                                                parser.parsePatient(rawBody, gson)?.let { p ->
                                                                    AppSettings.savePatient(p)
                                                                }
                                                            }

                                                            if (token != null) AppSettings.saveToken(token)
                                                        } else {
                                                            val errorBody = if (accountLoginResponse.isSuccessful == false) {
                                                                accountLoginResponse.errorBody()?.string()
                                                            } else {
                                                                patientLoginResponse.errorBody()?.string()
                                                            }
                                                            val parsed = parseLoginError(errorBody, gson)
                                                            snackbarHostState.showSnackbar(parsed)
                                                            isLoading = false
                                                            return@launch
                                                        }
                                                    }

                                                    try {
                                                        val patientResponse = RetrofitClient.service.getCurrentPatient()
                                                        if (patientResponse.isSuccessful == true) {
                                                            patientResponse.body()?.string()?.let { raw ->
                                                                val p = com.example.dentalclinic.data.api.PatientParser.parsePatient(raw, gson)
                                                                if (p != null) AppSettings.savePatient(p)
                                                            }
                                                        }
                                                    } catch (_: Exception) {}

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
                                                    snackbarHostState.showSnackbar(if (isAr == true) "خطأ في الاتصال: ${e.localizedMessage}" else "Connection error: ${e.localizedMessage}")
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
                                    enabled = isLoading == false
                                ) {
                                    Text(
                                        if (isAr == true) "تسجيل الدخول" else stringResource(R.string.login),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            if (isAr == true) "ليس لديك حساب؟" else stringResource(R.string.dont_have_account),
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 14.sp
                        )
                        TextButton(onClick = onSignUpClick) {
                            Text(
                                if (isAr == true) "إنشاء حساب" else stringResource(R.string.sign_up),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun parseLoginError(errorBody: String?, gson: Gson): String {
    if (errorBody.isNullOrBlank()) return "Invalid email or password"
    try {
        val map = gson.fromJson(errorBody, Map::class.java)
        for (key in listOf("message", "Message", "title", "Title", "error", "Error", "detail", "Detail")) {
            val value = map?.get(key)?.toString()
            if (!value.isNullOrBlank()) return value
        }
        val errors = map?.get("errors")
        if (errors is Map<*, *>) {
            for ((_, v) in errors) {
                if (v is List<*>) {
                    val first = v.firstOrNull()?.toString()
                    if (!first.isNullOrBlank()) return first
                }
            }
        }
    } catch (_: Exception) {}
    return errorBody.replace("\"", "").trim()
}

