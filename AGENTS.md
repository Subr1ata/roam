# Roam Project Guidelines

Roam is a native Android app with Java/XML screens, Clerk email sign-up/sign-in,
and a session-protected stay discovery experience. Property listings are
bundled sample data, not a connected booking service.

## Project Structure

- `app/src/main/java/com/roam/app/`: Java application code.
- `app/src/main/java/com/roam/app/*.kt`: Kotlin Clerk initialization and
  authentication bridge.
- `app/src/main/java/com/roam/app/ui/onboarding/`: Java onboarding slides,
  pager adapter, and slide model.
- `app/src/main/java/com/roam/app/ui/auth/`: native authentication screens and
  form interactions.
- `app/src/main/java/com/roam/app/ui/home/`: local stay data, stay model, and
  protected Home/Search/Trips/Profile tabs.
- `app/src/main/res/layout/`: Android XML layouts.
- `app/src/main/res/values/onboarding_slides.xml`: aligned resource arrays for
  onboarding slide IDs, titles, bodies, image URLs, and locations.
- `app/src/main/res/drawable/`: Android XML drawable backgrounds.
- `app/src/main/AndroidManifest.xml`: application and launcher configuration.
- `app/src/main/assets/`: bundled assets. Check whether an asset is used by
  the Android activity before changing the app architecture.
- `app/build.gradle`: Android SDK levels, app identity, and dependencies.
- `build.gradle`: Android Gradle Plugin and the project-specific build-output
  location.

## Implementation

- Keep screen behavior and UI wiring consistent between `MainActivity.java`
  and `res/layout/activity_main.xml`. Check that view IDs referenced in Java
  exist in the layout. The signed-in container hosts `HomeTabsView`.
- Preserve horizontal slide navigation from both the photo area and the
  onboarding content sheet; swipes beginning on sheet controls should advance
  the pager without breaking taps.
- Use `ClerkAuth` for account creation, email-code sign-in/verification,
  session observation, and sign-out. Never fake successful authentication or
  set signed-in state locally; show protected content only after Clerk reports
  an initialized authenticated session.
- Keep Clerk credentials out of tracked files. The publishable key can be
  supplied through the ignored `.env`, ignored `local.properties`, or the
  `CLERK_PUBLISHABLE_KEY` environment variable. Never embed a Clerk secret key.
- Keep the signed-out onboarding in native Android views and the existing
  ViewPager2 pager. Load its data from the aligned resource arrays and bind
  image URLs with Glide; do not replace the Android screen with React Native.
- Keep Home, Search, Trips, and Profile behind the existing Clerk session
  check. Use the local `StayRepository` for sample listing and search behavior;
  do not imply that sample stays are bookable or connected to a backend.
- Home search should open the Search tab with its query focused, and search
  results should filter locally by query and category without rebuilding the
  input on each keystroke.
- Prefer the Android SDK and dependencies already declared in the app module.
  Ask before introducing additional frameworks, unrelated remote services,
  or backend behavior.
- Keep README feature and technology claims aligned with what is implemented.
- Do not place secrets or user-specific SDK configuration in tracked project
  files. `.env` and `local.properties` are local machine configuration.

## Build and Verification

Use PowerShell from the repository root:

```powershell
.\gradlew.bat assembleDebug
```

The project uses Gradle 8.14.4, Android Gradle Plugin 8.13.2, Kotlin Gradle
Plugin 2.4.21, Clerk Android SDK 1.3.0, Glide 4.16.0, JDK 17, compile SDK 36,
target SDK 35, minimum SDK 24, and AndroidX ViewPager2 1.1.0. Install any
missing Android SDK components through Android Studio or the Android SDK
Manager.

Gradle build output is intentionally redirected to
`%USERPROFILE%\.gradle\roam-build` to avoid OneDrive deletion-permission
problems. The debug APK is at
`%USERPROFILE%\.gradle\roam-build\app\outputs\apk\debug\app-debug.apk`.
Do not redirect build output into `app\build` unless the OneDrive ACL issue has
been resolved.

Run `assembleDebug` after Java, manifest, resource, or build-configuration
changes. Install and launch the APK on an emulator or device when one is
available. Report build and device checks separately; do not claim a launch
was verified without actually launching the app.

## Working Agreements

- Read the affected Java, XML, manifest, and Gradle files before changing
  behavior; make small, focused changes.
- Ask the user when a decision would change the app's screen, behavior, or
  technology stack. Keep the native onboarding, Clerk auth flow, and signed-in
  form as the default.
- Do not delete generated or user files, change machine-level settings, or
  repair workspace permissions without the user's request.
- After code changes, summarize what changed, list the checks actually run and
  their results, and mention any verification blocked by missing tools or
  devices.
