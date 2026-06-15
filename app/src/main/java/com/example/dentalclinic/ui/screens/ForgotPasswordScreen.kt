package com.example.dentalclinic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.dentalclinic.ui.components.FunnyToothMascot
import com.example.dentalclinic.ui.components.PrimaryDentalButton
import com.example.dentalclinic.ui.theme.DentalTeal
import com.example.dentalclinic.ui.theme.DentalTealDark
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(onBack: () -> Unit, onVerifySuccess: () -> Unit) {
    var password by remember { mutableStateOf(value = "") }
    var confirmPassword by remember { mutableStateOf(value = "") }
    var passwordVisible by remember { mutableStateOf(value = false) }
    var confirmPasswordVisible by remember { mutableStateOf(value = false) }
    var isLoading by remember { mutableStateOf(value = false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val passwordsDontMatchMsg = stringResource(R.string.passwords_dont_match)
    val allFieldsRequiredMsg = stringResource(R.string.all_fields_required)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.forgot_password)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))

            FunnyToothMascot(
                modifier = Modifier.size(100.dp),
                style = AppSettings.mascotStyle
            )

            Spacer(Modifier.height(24.dp))

            Text(
                "Reset Password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = DentalTealDark
            )
            
            Text(
                "Create a strong new password for your account",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
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
                    val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = null, tint = DentalTeal)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                enabled = !isLoading
            )

            Spacer(Modifier.height(40.dp))

            if (isLoading) {
                CircularProgressIndicator(color = DentalTeal)
            } else {
                PrimaryDentalButton(
                    text = "Verify",
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                }
            }
        }
    }
}
