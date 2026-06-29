# Release Guide

## 1. Create a signing keystore (one time)

```powershell
keytool -genkeypair -v `
  -keystore release.keystore `
  -alias objectidentifier `
  -keyalg RSA -keysize 2048 -validity 10000
```

Copy `keystore.properties.example` to `keystore.properties` and fill in your passwords.

## 2. Build release AAB (Play Store)

```powershell
cd D:\Cursor\RealTimeObjectIdentifier
.\gradlew.bat bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

## 3. Build release APK (optional sideload)

```powershell
.\gradlew.bat assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

## 4. Verify release build locally

```powershell
.\gradlew.bat testDebugUnitTest assembleRelease --no-daemon
```

Without `keystore.properties`, release builds use the **debug keystore** for local testing only. Do not upload those builds to Play Store.

## 5. Publish to Google Play (internal track)

1. Open [Google Play Console](https://play.google.com/console)
2. Create app → **Object Identifier**
3. Upload `app-release.aab` to **Internal testing**
4. Paste content from [PLAY_STORE_LISTING.md](PLAY_STORE_LISTING.md)
5. Add privacy policy URL or host [PRIVACY_POLICY.md](PRIVACY_POLICY.md) on GitHub Pages
6. Complete content rating questionnaire (likely **Everyone**)
7. Submit for review

## Version bumps

Update in `app/build.gradle.kts`:

```kotlin
versionCode = 2   // increment every upload
versionName = "1.1.0"
```
