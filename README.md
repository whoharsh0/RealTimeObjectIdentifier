# Real-Time Object Identifier

A **completely free**, **offline-capable** Android app that identifies objects in real time through your phone camera — powered by on-device machine learning. No API keys, no subscriptions, no usage limits.

---

## Progress legend

| Emoji | Status |
|-------|--------|
| ✅ | **Done** — completed and verified |
| 🔄 | **Ongoing** — currently in progress |
| ⬜ | **Untouched** — not started yet |
| ⏸️ | **Paused** — blocked or deferred |
| ❌ | **Cancelled** — dropped from scope |

> **How to use this file:** Update the emoji at the start of each task as you work. This README is our single source of truth for project progress.

---

## Project overview

| Item | Value |
|------|-------|
| **Platform** | Android (min SDK 26) |
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt (Dagger) |
| **Camera** | CameraX |
| **ML** | TensorFlow Lite (on-device) |
| **Package** | `com.rudra.objectidentifier` |

---

## Requirements (product spec)

### Core features

1. **Live camera preview** — full-screen rear-camera feed with low latency.
2. **Real-time object detection** — bounding boxes drawn over detected objects at ≥ 15 FPS on mid-range devices.
3. **Object labels** — class name + confidence score (e.g. `Cup 87%`).
4. **100% on-device** — all inference runs locally; works offline after first install.
5. **No accounts** — no login, no cloud, no data leaves the device.
6. **Camera permission flow** — clear rationale + graceful denial handling.

### Non-functional requirements

| Requirement | Target |
|-------------|--------|
| Cold start to camera | < 2 seconds |
| Inference latency | < 100 ms per frame (mid-range phone) |
| APK size (with bundled model) | < 50 MB |
| Min Android version | API 26 (Android 8.0) |
| Target Android version | API 35 |

### Out of scope (v1)

- Cloud / API-based detection (Google Vision, OpenAI, etc.)
- Custom model training inside the app
- Video recording with detections burned in
- AR 3D object placement

### Stretch goals (post-v1)

- Toggle front / rear camera
- Freeze frame + inspect detections
- Detection history (Room database)
- GPU delegate (NNAPI / GPU) for faster inference
- Export snapshot with bounding boxes

---

## Tech stack

```
┌─────────────────────────────────────────────┐
│              Jetpack Compose UI              │
│     CameraPreview + DetectionOverlay         │
├─────────────────────────────────────────────┤
│           ViewModel (StateFlow)              │
├─────────────────────────────────────────────┤
│   CameraX          │   TFLite Interpreter    │
│   ImageAnalysis    │   ObjectDetector        │
├─────────────────────────────────────────────┤
│              Hilt (DI)                       │
└─────────────────────────────────────────────┘
```

---

## Master task sheet

### Phase 0 — Project bootstrap

| # | Task | Status |
|---|------|--------|
| 0.1 | Create GitHub repository | ✅ |
| 0.2 | Initialize Android project (Kotlin + Compose) | ✅ |
| 0.3 | Configure Gradle version catalog (`libs.versions.toml`) | ✅ |
| 0.4 | Add `.gitignore` for Android | ✅ |
| 0.5 | Set `applicationId` and package structure | ✅ |
| 0.6 | Verify project builds in Android Studio | ✅ |
| 0.7 | Add CI workflow (GitHub Actions — assemble debug) | ✅ |

### Phase 1 — Dependency injection (Hilt + Dagger)

| # | Task | Status |
|---|------|--------|
| 1.1 | Add Hilt Gradle plugins (root + app module) | ✅ |
| 1.2 | Add Hilt + KSP dependencies | ✅ |
| 1.3 | Create `@HiltAndroidApp` Application class | ✅ |
| 1.4 | Annotate `MainActivity` with `@AndroidEntryPoint` | ✅ |
| 1.5 | Create `AppModule` (`@Module` + `@InstallIn`) | ✅ |
| 1.6 | Add `DispatcherModule` (IO / Main / Default) | ✅ |
| 1.7 | Wire first `@Inject` constructor (smoke test) | ✅ |
| 1.8 | Verify Hilt code generation (`./gradlew assembleDebug`) | ✅ |

### Phase 2 — Architecture & navigation

| # | Task | Status |
|---|------|--------|
| 2.1 | Define package layers: `ui`, `domain`, `data`, `di` | ⬜ |
| 2.2 | Add Navigation Compose + Hilt ViewModel | ⬜ |
| 2.3 | Create `DetectionUiState` sealed class / data class | ⬜ |
| 2.4 | Create `DetectionViewModel` with `StateFlow` | ⬜ |
| 2.5 | Add `DetectionRepository` interface + impl stub | ⬜ |
| 2.6 | Add domain model `DetectedObject` (label, confidence, bounds) | ⬜ |

### Phase 3 — Camera (CameraX)

| # | Task | Status |
|---|------|--------|
| 3.1 | Add CameraX dependencies | ⬜ |
| 3.2 | Add `CAMERA` permission to manifest | ✅ |
| 3.3 | Implement runtime permission composable / flow | ⬜ |
| 3.4 | Create `CameraPreview` Composable (`PreviewView`) | ⬜ |
| 3.5 | Bind `Preview` + `ImageAnalysis` use cases | ⬜ |
| 3.6 | Configure `ImageAnalysis` (YUV, keep-latest strategy) | ⬜ |
| 3.7 | Handle lifecycle (bind/unbind on resume/pause) | ⬜ |
| 3.8 | Add camera switch (rear default) | ⬜ |

### Phase 4 — Machine learning (TensorFlow Lite)

| # | Task | Status |
|---|------|--------|
| 4.1 | Add TFLite dependencies (`tensorflow-lite`, `support`) | ⬜ |
| 4.2 | Choose model: **EfficientDet-Lite0** or **MobileNet SSD** | ⬜ |
| 4.3 | Bundle `.tflite` model in `assets/` | ⬜ |
| 4.4 | Bundle `labels.txt` (COCO 80 classes) | ⬜ |
| 4.5 | Create `ObjectDetector` class wrapping `Interpreter` | ⬜ |
| 4.6 | Implement image preprocessing (resize, normalize) | ⬜ |
| 4.7 | Implement post-processing (NMS, score threshold) | ⬜ |
| 4.8 | Provide Hilt `@Singleton` binding for detector | ⬜ |
| 4.9 | Benchmark inference time on real device | ⬜ |
| 4.10 | Optional: enable NNAPI / GPU delegate | ⬜ |

### Phase 5 — Detection pipeline

| # | Task | Status |
|---|------|--------|
| 5.1 | Create `ImageAnalyzer` implementing `ImageAnalysis.Analyzer` | ⬜ |
| 5.2 | Convert `ImageProxy` → `Bitmap` / `TensorImage` | ⬜ |
| 5.3 | Run detector on background thread (coroutines) | ⬜ |
| 5.4 | Map detection coordinates to preview aspect ratio | ⬜ |
| 5.5 | Throttle analysis to target FPS (skip frames if busy) | ⬜ |
| 5.6 | Emit results to ViewModel via callback / Flow | ⬜ |
| 5.7 | Handle rotation (sensor orientation) | ⬜ |

### Phase 6 — UI

| # | Task | Status |
|---|------|--------|
| 6.1 | Design `DetectionScreen` (camera + overlay) | ⬜ |
| 6.2 | Create `BoundingBoxOverlay` Composable (Canvas) | ⬜ |
| 6.3 | Draw label + confidence above each box | ⬜ |
| 6.4 | Add permission-denied empty state UI | ⬜ |
| 6.5 | Add loading state (model init) | ⬜ |
| 6.6 | Add top bar (app title, settings icon) | ⬜ |
| 6.7 | Material 3 theme polish (colors, typography) | 🔄 |
| 6.8 | Dark mode support | ⬜ |

### Phase 7 — Settings & polish

| # | Task | Status |
|---|------|--------|
| 7.1 | Settings screen: confidence threshold slider | ⬜ |
| 7.2 | Settings screen: max detections limit | ⬜ |
| 7.3 | Settings screen: show/hide confidence % | ⬜ |
| 7.4 | Persist settings with DataStore | ⬜ |
| 7.5 | About screen (offline / privacy message) | ⬜ |
| 7.6 | Splash / onboarding (one-time camera permission explain) | ⬜ |

### Phase 8 — Testing

| # | Task | Status |
|---|------|--------|
| 8.1 | Unit tests: `ObjectDetector` post-processing | ⬜ |
| 8.2 | Unit tests: coordinate mapping | ⬜ |
| 8.3 | ViewModel tests with Turbine | ⬜ |
| 8.4 | Instrumented test: permission flow | ⬜ |
| 8.5 | Manual QA checklist on 2+ devices | ⬜ |

### Phase 9 — Release

| # | Task | Status |
|---|------|--------|
| 9.1 | App icon (adaptive launcher) | 🔄 |
| 9.2 | ProGuard rules for TFLite / CameraX | ⬜ |
| 9.3 | Generate signed release APK / AAB | ⬜ |
| 9.4 | Play Store listing copy | ⬜ |
| 9.5 | Privacy policy (no data collected) | ⬜ |
| 9.6 | Publish to Google Play (internal track) | ⬜ |

---

## Current sprint focus

**Sprint 2 — Phase 2 (architecture)**

1. ✅ Phase 0 complete (bootstrap + CI)
2. ✅ Phase 1 complete (Hilt + DispatcherModule + inject smoke test)
3. ⬜ Define package layers and Navigation Compose
4. ⬜ `DetectionViewModel` + `DetectedObject` domain model
5. ⬜ CameraX preview on screen

---

### Push to GitHub (one-time setup)

Your GitHub account: **[@whoharsh0](https://github.com/whoharsh0)**

The local repo is ready. Run these commands once to authenticate and publish:

```powershell
# 1. Install GitHub CLI (if needed) — or use the one in %TEMP%\gh-cli\bin\gh.exe
gh auth login

# 2. Create the remote repo and push
cd D:\Cursor\RealTimeObjectIdentifier
gh repo create RealTimeObjectIdentifier --public --source=. --remote=origin --push
```

If the repo already exists on GitHub:

```powershell
git remote add origin https://github.com/whoharsh0/RealTimeObjectIdentifier.git
git branch -M main
git push -u origin main
```

After a successful push, update task **0.1** in this README from 🔄 to ✅.

---

### Prerequisites

- Android Studio Ladybug (2024.2+) or newer
- JDK 17+
- Android SDK 35
- A physical Android device (recommended for camera + ML testing)

### Clone and run

```bash
git clone https://github.com/whoharsh0/RealTimeObjectIdentifier.git
cd RealTimeObjectIdentifier
```

Open the project in Android Studio, sync Gradle, then run on a device.

### Build from command line

```bash
# Windows
gradlew.bat assembleDebug

# macOS / Linux
./gradlew assembleDebug
```

---

## Project structure (target)

```
app/src/main/java/com/rudra/objectidentifier/
├── ObjectIdentifierApp.kt          # @HiltAndroidApp
├── MainActivity.kt                 # @AndroidEntryPoint
├── di/
│   ├── AppModule.kt                # App-wide bindings
│   └── DispatcherModule.kt         # Coroutine dispatchers
├── ui/
│   ├── theme/
│   ├── screen/
│   └── components/
├── domain/
├── data/
└── presentation/
```

---

## Model reference

| Model | Size | Speed | Accuracy | Recommendation |
|-------|------|-------|----------|----------------|
| EfficientDet-Lite0 | ~4 MB | Fast | Good | **Default choice** |
| EfficientDet-Lite1 | ~6 MB | Medium | Better | Flagship phones |
| MobileNet SSD v1 | ~4 MB | Very fast | OK | Low-end devices |

Download from [TensorFlow Lite Model Zoo](https://www.tensorflow.org/lite/models/object_detection/overview).

---

## Changelog

| Date | Change |
|------|--------|
| 2026-06-24 | Project created — Kotlin, Compose, Hilt wired |
| 2026-06-24 | README task sheet initialized |
| 2026-06-24 | ✅ `assembleDebug` build verified — Hilt code generation OK |
| 2026-06-24 | ✅ Phase 0 complete — GitHub Actions CI added |
| 2026-06-24 | ✅ Phase 1 complete — DispatcherModule, AppInfoProvider, MainViewModel |

---

## License

MIT License — free to use, modify, and distribute.

---

## Contributors

- **RUDRA** — sharmarudra095@gmail.com
