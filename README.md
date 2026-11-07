# Roam

Roam is a native Android travel app with swipeable onboarding, Clerk-powered
email sign-up and sign-in using verification codes, and a session-protected
home experience with local sample stays, search, and native bottom tabs. Its
screens are built with Java and Android XML; there is no Expo or React Native
project.

## Features

- **Swipeable onboarding:** Browse three destination slides with location
  labels, page indicators, travel copy, and destination photos.
- **Clerk authentication:** Create an account with email verification, or
  request a one-time email code to sign in.
- **Persistent sessions and protected content:** Home and the other signed-in
  tabs are shown only when Clerk restores or establishes an authenticated
  session.
- **Stay discovery:** Browse six locally defined sample properties across
  cabin, beach, city, and villa categories, with locations, descriptions,
  ratings, review counts, nightly prices, and photos.
- **Search and filters:** Open Search from the home search bar, type a
  location or preference, and see matching local stays update as you type.
- **Native bottom navigation:** Switch between Home, Search, Trips, and
  Profile, with subtle screen and card animations.
- **Sign out:** End the Clerk session from Profile and return to onboarding.

The onboarding content follows the sample slides in the
[referenced Airbnb Clone](https://github.com/jsmastery-pro/airbnb-clone/blob/main/src/components/onboarding/slides.ts),
adapted to Android string-array resources and native views.

## Tech stack

- **Java** for the Android activity, onboarding adapter, and slide model.
- **Kotlin** for the existing Android application and authentication bridge
  integration.
- **Android SDK and XML layouts** for the native app screens.
- **[AndroidX ViewPager2](https://developer.android.com/develop/ui/views/animations/screen-slide-2)**
  for horizontally swipeable onboarding pages.
- **[Glide](https://github.com/bumptech/glide)** for loading and displaying
  onboarding and property photos.
- **Android SDK animations and native views** for screen transitions, stay
  cards, search interactions, and bottom navigation.
- **[Clerk Android SDK](https://github.com/clerk/clerk-android)** for native
  email-code authentication and persistent session management.
- **[Gradle](https://gradle.org/)** and Android Gradle Plugin for builds.

## Compatibility

- **Android:** minimum API 24; target SDK 35; compile SDK 36.
- **Java:** JDK 17 or newer.
- **Build tooling:** Gradle 8.14.4, Android Gradle Plugin 8.13.2, Kotlin Gradle
  Plugin 2.4.21, Clerk Android SDK 1.3.0, and Glide 4.16.0.
- **Property data:** bundled sample data; no property API or backend is
  required. Property images are loaded from Unsplash and need an internet
  connection.

## Clerk setup

1. In the [Clerk Dashboard](https://dashboard.clerk.com/), enable Email,
   password sign-up, email-code sign-in, and email-code verification at
   sign-up.
2. Enable Clerk's Native API and register the Android application ID
   `com.roam.app` as a native application.
3. Set `CLERK_PUBLISHABLE_KEY` in the ignored project `.env` file, or use the
   `CLERK_PUBLISHABLE_KEY` environment variable or
   `clerk.publishableKey` in ignored `local.properties`. Environment variables
   take precedence, followed by `local.properties`, then `.env`.
4. Rebuild after changing the key. Never put a Clerk secret key in the Android
   app.

## Build

From PowerShell, run:

```powershell
.\gradlew.bat assembleDebug
```

The debug APK is written to
`%USERPROFILE%\.gradle\roam-build\app\outputs\apk\debug\app-debug.apk`.
Build outputs are kept outside the project directory to avoid OneDrive file
permission issues.
