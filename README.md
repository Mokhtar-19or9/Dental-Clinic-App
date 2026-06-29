# Smile Scan — Dental Clinic App

Native Android application for dental clinic management. Patients can view their X-rays, medical history, diagnoses, appointments, chat with the clinic, and generate medical reports.

## Features

- **Authentication** — Login / Sign Up / Forgot Password with JWT token management
- **Home Dashboard** — Greeting, stat cards, upcoming appointments, quick actions
- **X-Ray Records** — View X-ray images loaded from the API (base64, data URIs, or server URLs) with AI analysis results
- **Medical History** — Chronological list of past procedures and conditions
- **Diagnosis** — AI-powered diagnosis reports with associated X-rays
- **Appointments** — View and manage upcoming appointments
- **Chat** — Messaging with the clinic
- **Notifications** — Read/unread notification management
- **Profile** — View and update patient information
- **Settings** — Language toggle (English/Arabic), dark mode, mascot style, notification preferences
- **Medical Report** — Generate and share a formatted HTML medical report with patient info, history, and X-ray records (via `ReportGenerator.kt`)
- **Bilingual** — Full Arabic / English support with RTL layout

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Networking | Retrofit 2 + OkHttp 4 |
| Image Loading | Coil 3 |
| JSON | Gson |
| Build | Gradle 9.5 + AGP 9.2.1 |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 |

## Architecture

```
MVC-like (ViewModel + Composable)
├── data/              # Business logic & data layer
│   ├── api/           # Retrofit service, models, auth interceptor
│   ├── model/         # Domain data classes
│   ├── fake/          # Fallback fake data
│   └── ...            # AppSettings, ReportGenerator, LocalRayProvider
├── navigation/        # Routes & NavHost composable
└── ui/
    ├── components/    # Reusable composables (DentalComponents, mascot)
    ├── screens/       # Screen composables + ViewModels
    └── theme/         # Colors, typography, theme
```

### Data Flow

1. `SmartCareService.kt` defines all API endpoints
2. `RetrofitClient.kt` provides the singleton Retrofit instance with auth interceptor
3. `AppSettings.kt` persists data via SharedPreferences (token, patient info, notifications, etc.)
4. Screens call the API via `RetrofitClient.service` inside `LaunchedEffect` or ViewModels
5. `PatientParser.kt` handles flexible JSON parsing (multiple field name variants)
6. Images are loaded via `RayImage()` in `DentalComponents.kt` which handles base64, data URIs, absolute URLs, relative server paths, and `file:///android_asset/` URIs

## Project Structure

```
app/src/main/java/com/example/dentalclinic/
├── DentalApplication.kt          # Coil image loader factory
├── MainActivity.kt               # Entry point, edge-to-edge, RTL
├── data/
│   ├── AppSettings.kt            # SharedPreferences wrapper (Compose-reactive)
│   ├── LocalRayProvider.kt       # Local asset images (unused in current flow)
│   ├── ReportGenerator.kt        # HTML medical report generator + share
│   ├── api/
│   │   ├── ApiModels.kt          # Request/response data classes
│   │   ├── AuthInterceptor.kt    # JWT Bearer token interceptor
│   │   ├── PatientParser.kt      # Flexible patient JSON parser
│   │   ├── RetrofitClient.kt     # Retrofit singleton
│   │   └── SmartCareService.kt   # API endpoint definitions
│   ├── fake/FakeDentalData.kt    # Hardcoded mock data
│   └── model/Models.kt           # Domain models
├── navigation/
│   ├── DentalApp.kt              # NavHost + Scaffold + bottom nav
│   └── DentalRoutes.kt           # Route enum
└── ui/
    ├── components/
    │   ├── DentalComponents.kt   # Reusable UI components + RayImage
    │   └── FunnyToothMascot.kt   # Animated tooth mascot
    ├── screens/                  # 14 screens + 4 ViewModels
    └── theme/                    # Color.kt, Theme.kt, Type.kt
```

## Screens

| Screen | File | Description |
|--------|------|-------------|
| Splash | `SplashScreen.kt` | Animated logo intro, auto-navigates |
| Login | `LoginScreen.kt` | Email/password with error parsing |
| Sign Up | `SignUpScreen.kt` | Registration form |
| Forgot Password | `ForgotPasswordScreen.kt` | Password reset form |
| Home | `HomeScreen.kt` | Dashboard with stats, appointments, medical report button |
| X-Ray | `XRayScreen.kt` | X-ray viewer with AI analysis |
| Medical History | `MedicalHistoryScreen.kt` | Past procedures list |
| Diagnosis | `DiagnosisScreen.kt` | AI diagnosis with images |
| Appointments | `AppointmentsScreen.kt` | Appointment list |
| Chat | `ChatScreen.kt` | Messaging UI |
| Notifications | `NotificationsScreen.kt` | Notification list |
| Profile | `ProfileScreen.kt` | Patient info display |
| Settings | `SettingsScreen.kt` | Language, theme, mascot, notifications |

## API Endpoints

Base URL: `http://smartcare.tryasp.net/`

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `api/v1/Account/Login` | User login |
| POST | `api/v1/Patients/login` | Patient login fallback |
| POST | `api/v1/Account/Create` | Account registration |
| GET | `api/v1/Patients` | Fetch patient list |
| GET | `api/v1/Patients/{id}` | Fetch single patient |
| GET | `api/v1/Rays` | Fetch X-ray records |
| GET | `api/v1/Rays/patient/{patientId}` | Fetch X-rays by patient |
| GET | `api/v1/Diagnosis` | Fetch diagnosis records |
| GET | `api/v1/Diagnosis/patient/{patientId}` | Fetch diagnoses by patient |
| GET | `api/v1/Files/{fileName}` | Download uploaded files |

## Setup & Run

1. Open the project folder in Android Studio
2. Let Gradle sync (AGP 9.2.1, Kotlin 2.1.0)
3. If the SDK platform isn't installed, install **Android SDK Platform 35** from SDK Manager
4. If Gradle tries to download dependencies, enable **Offline Work** in Gradle settings (dependencies are pinned to cached versions)
5. Run the `app` configuration on an emulator (Pixel 6/7 recommended)

## Fallback & Offline Behavior

- New accounts with no X-rays show **empty state** ("nothing to display") — no local images are shown as fallback
- The app uses `usesCleartextTraffic="true"` for HTTP API calls
- A `FileProvider` is configured for sharing generated reports
- Local asset images exist in `assets/rays/` but are **not used** in the current login flow

## Key Components

- **`RayImage()`** in `DentalComponents.kt` — universal image loader that handles base64 strings, `data:` URIs, absolute HTTP URLs, relative server paths, and `file:///android_asset/` URIs; falls back to `AsyncImage` (Coil) and decodes base64 to `Bitmap` via Android's `BitmapFactory`
- **`PatientParser`** — parses patient JSON from any API format, supporting 5+ field name variants per field
- **`AuthInterceptor`** — automatically attaches JWT token to all requests
- **`ReportGenerator`** — generates a clean HTML document with patient info + X-ray images, shares via Android intent as `.htm` file (opens in browser or Word)

## Navigation Routes

```
Splash → Login → Home → {Appointments, XRay, Profile, Settings, Diagnosis, History, Notifications, Chat}
Splash → Login → SignUp → Home
Login → ForgotPassword
```
