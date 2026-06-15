package com.example.dentalclinic.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.dentalclinic.ui.components.DentalBottomBar
import com.example.dentalclinic.ui.screens.*

@Composable
fun DentalApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRouteName = navBackStackEntry?.destination?.route
    
    val currentDentalRoute = DentalRoute.entries.find { it.name == currentRouteName } ?: DentalRoute.Splash

    Scaffold(
        bottomBar = {
            if (currentDentalRoute in bottomRoutes) {
                DentalBottomBar(
                    routes = bottomRoutes,
                    selectedRoute = currentDentalRoute
                ) { route ->
                    navController.navigate(route.name) {
                        popUpTo(DentalRoute.Home.name) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        }
    ) { padding ->
        val modifier = Modifier.padding(padding)
        
        NavHost(
            navController = navController,
            startDestination = DentalRoute.Splash.name,
            modifier = Modifier,
            enterTransition = {
                fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                slideInHorizontally(initialOffsetX = { 400 }, animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            },
            exitTransition = {
                fadeOut(animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                slideOutHorizontally(targetOffsetX = { -400 }, animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 0.92f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                slideInHorizontally(initialOffsetX = { -400 }, animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                scaleIn(initialScale = 0.92f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                slideOutHorizontally(targetOffsetX = { 400 }, animationSpec = tween(500, easing = FastOutSlowInEasing)) +
                scaleOut(targetScale = 0.92f, animationSpec = tween(500, easing = FastOutSlowInEasing))
            }
        ) {
            composable(DentalRoute.Splash.name) {
                SplashScreen { isLoggedIn ->
                    val destination = if (isLoggedIn) DentalRoute.Home.name else DentalRoute.Login.name
                    navController.navigate(destination) {
                        popUpTo(DentalRoute.Splash.name) { inclusive = true }
                    }
                }
            }
            
            composable(DentalRoute.Login.name) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(DentalRoute.Home.name) {
                            popUpTo(DentalRoute.Login.name) { inclusive = true }
                        }
                    },
                    onSignUpClick = {
                        navController.navigate(DentalRoute.SignUp.name)
                    },
                    onForgotPasswordClick = {
                        navController.navigate(DentalRoute.ForgotPassword.name)
                    }
                )
            }

            composable(DentalRoute.ForgotPassword.name) {
                ForgotPasswordScreen(
                    onBack = { navController.popBackStack() },
                    onVerifySuccess = {
                        navController.navigate(DentalRoute.Login.name) {
                            popUpTo(DentalRoute.ForgotPassword.name) { inclusive = true }
                        }
                    }
                )
            }

            composable(DentalRoute.SignUp.name) {
                SignUpScreen(
                    onSignUpSuccess = {
                        navController.navigate(DentalRoute.Home.name) {
                            popUpTo(DentalRoute.SignUp.name) { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate(DentalRoute.Login.name) {
                            popUpTo(DentalRoute.SignUp.name) { inclusive = true }
                        }
                    }
                )
            }

            composable(DentalRoute.Home.name) {
                HomeScreen(
                    modifier = modifier,
                    onOpenAppointments = { navController.navigate(DentalRoute.Appointments.name) },
                    onOpenXRay = { navController.navigate(DentalRoute.XRay.name) },
                    onOpenDiagnosis = { navController.navigate(DentalRoute.Diagnosis.name) },
                    onOpenHistory = { navController.navigate(DentalRoute.History.name) },
                    onOpenChat = { navController.navigate(DentalRoute.Chat.name) },
                    onOpenNotifications = { navController.navigate(DentalRoute.Notifications.name) }
                )
            }
            
            composable(DentalRoute.Appointments.name) { AppointmentsScreen(modifier) }
            composable(DentalRoute.XRay.name) { XRayScreen(modifier) }
            composable(DentalRoute.Profile.name) { 
                ProfileScreen(
                    modifier = modifier,
                    onLogout = {
                        navController.navigate(DentalRoute.Login.name) {
                            popUpTo(DentalRoute.Home.name) { inclusive = true }
                        }
                    }
                ) 
            }
            
            composable(DentalRoute.Settings.name) { 
                SettingsScreen(
                    modifier = modifier,
                    onNavigateToNotifications = { navController.navigate(DentalRoute.Notifications.name) }
                ) 
            }
            
            composable(
                DentalRoute.Diagnosis.name,
                enterTransition = { slideInVertically(initialOffsetY = { 1000 }) + fadeIn() },
                exitTransition = { slideOutVertically(targetOffsetY = { 1000 }) + fadeOut() }
            ) { 
                DiagnosisScreen(modifier, onBack = { navController.popBackStack() }) 
            }
            
            composable(
                DentalRoute.History.name,
                enterTransition = { slideInVertically(initialOffsetY = { 1000 }) + fadeIn() },
                exitTransition = { slideOutVertically(targetOffsetY = { 1000 }) + fadeOut() }
            ) { 
                MedicalHistoryScreen(modifier, onBack = { navController.popBackStack() }) 
            }
            
            composable(DentalRoute.Notifications.name) { 
                NotificationsScreen(modifier, onBack = { navController.popBackStack() }) 
            }

            composable(DentalRoute.Chat.name) { 
                ChatScreen(modifier, onBack = { navController.popBackStack() }) 
            }
        }
    }
}
