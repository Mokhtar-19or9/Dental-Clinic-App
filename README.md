# Dental Clinic App

Native Android app scaffold for a dental clinic management experience inspired by the provided Figma screenshot.

## Tech

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Fake local data

## How To Run

1. Open this folder in Android Studio:
   `C:\Users\K.HP\Documents\Codex\2026-05-22\android-studio-https-www-figma-com`
2. Let Android Studio sync Gradle.
3. If Android Studio asks to install Android SDK / Gradle dependencies, accept.
4. Run the `app` configuration on an emulator such as Pixel 6 or Pixel 7.

## If Sync Says "Unable To Download File"

This project is pinned to dependency versions already found in the local Gradle cache on this machine:

- Android Gradle Plugin `9.2.1`
- Kotlin `2.1.0`
- Compose BOM `2026.02.01`
- Activity Compose `1.13.0`

In Android Studio:

1. Open **Settings > Build, Execution, Deployment > Gradle**.
2. Enable **Offline work** only if Gradle still tries to download dependencies.
3. Open **File > Sync Project with Gradle Files**.
4. If it asks for Android SDK Platform 35, install it from **Tools > SDK Manager**.

## Implemented Screens

- Home Dashboard
- Appointments
- X-Ray Records
- Profile
- Settings
- Diagnosis
- Medical History
- Notifications
- Chat

## Gemini Prompts For Iteration

### Prompt 1: Polish Figma Matching

```text
You are working in an existing Kotlin Jetpack Compose Android project.
Improve the UI so it matches the provided Dental Clinic Figma screenshot more closely.

Focus only on visual polish:
- teal/blue gradient header
- white rounded cards
- clean health-dashboard spacing
- soft shadows
- bottom navigation style
- compact mobile layout

Do not change package names or app architecture.
Do not add backend or Firebase.
Keep fake data.
```

### Prompt 2: Improve Home Screen

```text
Update HomeScreen.kt and reusable components only.
Make the dashboard look closer to the Figma:
- top greeting header
- patient avatar
- notification icon
- 3 stat cards
- upcoming appointment card
- quick action grid

Keep all navigation callbacks working.
```

### Prompt 3: Add More Real Features

```text
Add realistic local UI behavior without backend:
- appointment search/filter
- notification read/unread local state
- chat message sending in local state
- settings switches

Use Compose state only.
Keep the app simple and buildable.
```

### Prompt 4: Add Room Later

```text
Convert fake appointment and patient data into local Room database storage.
Add repository classes and simple ViewModels.
Keep the same UI screens and navigation.
Do not add Firebase yet.
```
