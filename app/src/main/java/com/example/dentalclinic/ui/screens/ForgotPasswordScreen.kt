package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dentalclinic.R
import com.example.dentalclinic.data.AppSettings
import com.example.dentalclinic.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun ForgotPasswordScreen(onBack: () -> Unit, onVerifySuccess: () -> Unit) {
    val isAr = AppSettings.currentLanguage == "ar"
    var password by remember { mutableStateOf(value = "") }
    var confirmPassword by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var confirmPasswordVisible by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val passwordsDontMatchMsg = if (isAr) "كلمات المرور غير متطابقة" else stringResource(R.string.passwords_dont_match)
    val allFieldsRequiredMsg = if (isAr) "جميع الحقول مطلوبة" else stringResource(R.string.all_fields_required)

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
                // Background decorative circles
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
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

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
                                if (isAr) "إعادة تعيين كلمة المرور" else "Reset Password",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = DentalText
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                if (isAr) "قم بإنشاء كلمة مرور جديدة لحسابك" else "Create a new password for your account",
                                color = DentalText.copy(alpha = 0.5f),
                                fontSize = 14.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )

                            Spacer(Modifier.height(32.dp))

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text(if (isAr) "كلمة المرور الجديدة" else stringResource(R.string.password)) },
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

                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = { Text(if (isAr) "تأكيد كلمة المرور" else stringResource(R.string.confirm_password)) },
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

                            Spacer(Modifier.height(40.dp))

                            if (isLoading) {
                                CircularProgressIndicator(color = DentalTeal, modifier = Modifier.size(48.dp))
                            } else {
                                Button(
                                    onClick = {
                                        if (password.isBlank() || confirmPassword.isBlank()) {
                                            scope.launch { snackbarHostState.showSnackbar(allFieldsRequiredMsg) }
                                        } else if (password != confirmPassword) {
                                            scope.launch { snackbarHostState.showSnackbar(passwordsDontMatchMsg) }
                                        } else {
                                            isLoading = true
                                            scope.launch {
                                                // Simulate API call
                                                kotlinx.coroutines.delay(1500.milliseconds)
                                                isLoading = false
                                                onVerifySuccess()
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
                                    )
                                ) {
                                    Text(
                                        if (isAr) "تأكيد" else "Verify",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}
