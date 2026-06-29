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
| 2.1 | Define package layers: `ui`, `domain`, `data`, `di` | ✅ |
| 2.2 | Add Navigation Compose + Hilt ViewModel | ✅ |
| 2.3 | Create `DetectionUiState` sealed class / data class | ✅ |
| 2.4 | Create `DetectionViewModel` with `StateFlow` | ✅ |
| 2.5 | Add `DetectionRepository` interface + impl stub | ✅ |
| 2.6 | Add domain model `DetectedObject` (label, confidence, bounds) | ✅ |

### Phase 3 — Camera (CameraX)

| # | Task | Status |
|---|------|--------|
| 3.1 | Add CameraX dependencies | ✅ |
| 3.2 | Add `CAMERA` permission to manifest | ✅ |
| 3.3 | Implement runtime permission composable / flow | ✅ |
| 3.4 | Create `CameraPreview` Composable (`PreviewView`) | ✅ |
| 3.5 | Bind `Preview` + `ImageAnalysis` use cases | ✅ |
| 3.6 | Configure `ImageAnalysis` (YUV, keep-latest strategy) | ✅ |
| 3.7 | Handle lifecycle (bind/unbind on resume/pause) | ✅ |
| 3.8 | Add camera switch (rear default) | ✅ |

### Phase 4 — Machine learning (TensorFlow Lite)

| # | Task | Status |
|---|------|--------|
| 4.1 | Add TFLite dependencies (`tensorflow-lite`, `support`) | ✅ |
| 4.2 | Choose model: **EfficientDet-Lite0** or **MobileNet SSD** | ✅ |
| 4.3 | Bundle `.tflite` model in `assets/` | ✅ |
| 4.4 | Bundle `labels.txt` (COCO 80 classes) | ✅ |
| 4.5 | Create `ObjectDetector` class wrapping `Interpreter` | ✅ |
| 4.6 | Implement image preprocessing (resize, normalize) | ✅ |
| 4.7 | Implement post-processing (NMS, score threshold) | ✅ |
| 4.8 | Provide Hilt `@Singleton` binding for detector | ✅ |
| 4.9 | Benchmark inference time on real device | ⬜ |
| 4.10 | Optional: enable NNAPI / GPU delegate | ⬜ |

### Phase 5 — Detection pipeline

| # | Task | Status |
|---|------|--------|
| 5.1 | Create `ImageAnalyzer` implementing `ImageAnalysis.Analyzer` | ✅ |
| 5.2 | Convert `ImageProxy` → `Bitmap` / `TensorImage` | ✅ |
| 5.3 | Run detector on background thread (coroutines) | ✅ |
| 5.4 | Map detection coordinates to preview aspect ratio | ✅ |
| 5.5 | Throttle analysis to target FPS (skip frames if busy) | ✅ |
| 5.6 | Emit results to ViewModel via callback / Flow | ✅ |
| 5.7 | Handle rotation (sensor orientation) | ✅ |

### Phase 6 — UI

| # | Task | Status |
|---|------|--------|
| 6.1 | Design `DetectionScreen` (camera + overlay) | ✅ |
| 6.2 | Create `BoundingBoxOverlay` Composable (Canvas) | ✅ |
| 6.3 | Draw label + confidence above each box | ✅ |
| 6.4 | Add permission-denied empty state UI | ✅ |
| 6.5 | Add loading state (model init) | ✅ |
| 6.6 | Add top bar (app title, settings icon) | ✅ |
| 6.7 | Material 3 theme polish (colors, typography) | ✅ |
| 6.8 | Dark mode support | ✅ |

### Phase 7 — Settings & polish

| # | Task | Status |
|---|------|--------|
| 7.1 | Settings screen: confidence threshold slider | ✅ |
| 7.2 | Settings screen: max detections limit | ✅ |
| 7.3 | Settings screen: show/hide confidence % | ✅ |
| 7.4 | Persist settings with DataStore | ✅ |
| 7.5 | About screen (offline / privacy message) | ✅ |
| 7.6 | Splash / onboarding (one-time camera permission explain) | ✅ |

### Phase 8 — Testing

| # | Task | Status |
|---|------|--------|
| 8.1 | Unit tests: `ObjectDetector` post-processing | ✅ |
| 8.2 | Unit tests: coordinate mapping | ✅ |
| 8.3 | ViewModel tests with Turbine | ✅ |
| 8.4 | Instrumented test: permission flow | ✅ |
| 8.5 | Manual QA checklist on 2+ devices | ✅ |

### Phase 9 — Release

| # | Task | Status |
|---|------|--------|
| 9.1 | App icon (adaptive launcher) | ✅ |
| 9.2 | ProGuard rules for TFLite / CameraX | ✅ |
| 9.3 | Generate signed release APK / AAB | ✅ |
| 9.4 | Play Store listing copy | ✅ |
| 9.5 | Privacy policy (no data collected) | ✅ |
| 9.6 | Publish to Google Play (internal track) | ⬜ |

---

## v2 roadmap (Phases 10–14)

> v2 turns the v1 detector into a full-featured, personalizable, accessible scanner.
> Statuses below are marked honestly against what actually ships in this build.
> (versionCode `2`, versionName `2.0.0`.)

### Phase 10 — Performance & ML

| # | Task | Status |
|---|------|--------|
| 10.1 | NNAPI delegate toggle (with safe CPU fallback) | ✅ |
| 10.2 | Model variant selector (EfficientDet-Lite0 / Lite1) | ✅ |
| 10.3 | Detector warm-up on app start (off main thread) | ✅ |
| 10.4 | Adaptive frame skip (min interval between frames) | ✅ |
| 10.5 | Battery-saver mode (longer frame interval) | ✅ |
| 10.6 | Detection smoothing (IoU tracking + box/score lerp) | ✅ |
| 10.7 | Stable track IDs across frames | ✅ |
| 10.8 | Live metrics: FPS, inference ms, delegate, processed/skipped | ✅ |
| 10.9 | FPS / metrics overlay chip (toggleable) | ✅ |
| 10.10 | Overlapping-frame guard (`AtomicBoolean`, `ImageProxy` closed in `finally`) | ✅ |
| 10.11 | GPU delegate option | ⬜ |
| 10.12 | On-device benchmark harness | ⬜ |

### Phase 11 — Detection UX

| # | Task | Status |
|---|------|--------|
| 11.1 | Freeze frame (pause analyzer, keep last boxes) | ✅ |
| 11.2 | Tap-to-highlight a detection / track | ✅ |
| 11.3 | Detection list sheet | ✅ |
| 11.4 | Label filter (text query, coerced + length-capped) | ✅ |
| 11.5 | Scene description summary line | ✅ |
| 11.6 | Torch / flashlight toggle (guarded by flash availability) | ✅ |
| 11.7 | Gallery image scan (still-image detector) | ✅ |
| 11.8 | Box styles (Full / Corners / Filled) | ✅ |
| 11.9 | Label scale slider | ✅ |
| 11.10 | Front/rear camera toggle | ✅ |
| 11.11 | Export snapshot with boxes burned in | ⬜ |

### Phase 12 — History & personalization

| # | Task | Status |
|---|------|--------|
| 12.1 | Room database for scan history | ✅ |
| 12.2 | Record scans on stop (top labels + count + scan mode) | ✅ |
| 12.3 | History screen (newest first; per-row timestamp) | ✅ |
| 12.4 | Clear-history action | ✅ |
| 12.5 | Toggle history persistence in settings | ✅ |
| 12.6 | Scan modes (General / Indoor / Outdoor / Food / People) | ✅ |
| 12.7 | Theme system (Dark / AMOLED / Light / High Contrast) | ✅ |
| 12.8 | Default camera lens preference | ✅ |
| 12.9 | Grouped settings sections | ✅ |
| 12.10 | Reset-to-defaults | ✅ |

### Phase 13 — Validation, reliability & accessibility

| # | Task | Status |
|---|------|--------|
| 13.1 | `SettingsValidator` clamps every numeric setting on read + write | ✅ |
| 13.2 | Safe enum parsing from DataStore (never trust strings) | ✅ |
| 13.3 | Gallery bitmap validation (null / zero-dim / oversized → recycle) | ✅ |
| 13.4 | Division-by-zero guards (IoU, FPS, normalization) | ✅ |
| 13.5 | `runCatching` around detector load, inference, camera, torch, Room, DataStore, decode | ✅ |
| 13.6 | Bitmap recycling across the pipeline | ✅ |
| 13.7 | High-contrast overlay colors | ✅ |
| 13.8 | Reduce-motion respected (static scanning indicator) | ✅ |
| 13.9 | Content descriptions on icon controls | ✅ |
| 13.10 | Speak-labels (TalkBack/TTS) — preference persisted | 🔄 |
| 13.11 | Region-of-interest cropping — preference persisted | ⬜ |

### Phase 14 — Trust, privacy & quality gates

| # | Task | Status |
|---|------|--------|
| 14.1 | About screen (model info, how-it-works, privacy, licenses) | ✅ |
| 14.2 | 100% on-device / offline messaging | ✅ |
| 14.3 | Release minify + resource shrink + ProGuard (incl. Room rules) | ✅ |
| 14.4 | Unit tests: detection mapper (JVM, no Android RectF) | ✅ |
| 14.5 | Unit tests: smoother (filter / track / highlight) | ✅ |
| 14.6 | Unit tests: settings validator | ✅ |
| 14.7 | ViewModel tests with Turbine (settings + detection) | ✅ |
| 14.8 | Hand-written fakes (no mockk dependency) | ✅ |
| 14.9 | Instrumented test: permission flow | ✅ |

---

## v2 plan (Phases A–E)

The user-enumerated plan, annotated with what actually shipped in this build.

### Phase A — Detection screen controls

- ✅ Dedicated `ui/components/GlassControlBar.kt` (extracted from `CameraPermissionContent.kt`).
- ✅ Overlay consumes `displayDetections` (smoothed + tracked), not raw detections.
- ✅ `MetricsChip` shown only when `showFpsOverlay` is enabled.
- ✅ `sceneDescription` text rendered in the top bar.
- ✅ Tap on a box → `onHighlightTrack(trackId)`.
- ✅ Freeze, torch, gallery-pick, and detection-list toggle all reachable from the UI (top bar + control bar).

### Phase B — Settings screen (grouped sections)

- ✅ **Scanning/Detection:** ScanMode chips, confidence + max-detections sliders, label filter, ModelVariant toggle.
- ✅ **Display:** BoxStyle picker, label-scale slider, show-FPS toggle, scene-description toggle.
- ✅ **Behavior:** auto-start, keep-screen-on, haptic, battery saver, default camera, save history.
- ✅ **Accessibility:** high contrast, reduce motion, speak labels.
- ✅ **Advanced:** NNAPI, smoothing, frame skip, filter label, min box-size slider, min label-confidence slider.
- ✅ Reset-to-defaults button. All handlers wired to `SettingsViewModel`.

### Phase C — History screen

- ✅ Reads `ScanHistoryRepository` (Room), shown in the nav graph.
- ✅ Newest-first list with top labels + detection count + scan mode per scan.
- ✅ Clear-history button.
- 🔄 Visual date-section headers are flattened into a single time-sorted list (timestamp shown per row).

### Phase D — Theme system

- ✅ `appTheme` flows from settings into `RealTimeObjectIdentifierTheme(appTheme, content)`.
- ✅ Dark, AMOLED, Light, and High-Contrast schemes.
- ✅ High-contrast also flows to the overlay via `highContrastMode`.

### Phase E — Freeze frame + gallery flow

- ✅ When frozen: analyzer stops, last boxes stay, a "Frozen" badge shows.
- ✅ Gallery image detection result is shown with bounding boxes over the idle-screen overlay, plus a scene-description snackbar.
- ✅ Bitmaps validated and recycled on failure.

---

## v3 UI/UX polish roadmap

> A focused polish pass over the working v2 app: smoother animation, glass/depth, adaptive
> layout, richer theming, and detection-overlay refinements. Every animation honors the existing
> `reduceMotion` setting and every haptic honors the existing `hapticFeedback` setting. All blur /
> dynamic-color paths are API-gated (31+) with graceful fallbacks.

### A — Smoother animations

| # | Item | Status |
|---|------|--------|
| A1 | Inter-frame bounding-box smoothing (exp. smoothing keyed by `trackId`, snaps on reduce-motion) | ✅ |
| A2 | Fade label/confidence changes to reduce flicker | ✅ |
| A3 | `Crossfade` between loading / permission / detecting / idle phases | ✅ |
| A4 | Animate the detection-count number (`animateIntAsState`) | ✅ |
| A5 | `AnimatedVisibility` (slide+fade) for control bar & metrics chip | ✅ |
| A6 | Animated freeze state (scale-in hint + Pause/Play crossfade + frosted tint) | ✅ |
| A7 | Press/scale feedback on icon buttons + primary/secondary buttons | ✅ |

### B — Glassmorphism & depth

| # | Item | Status |
|---|------|--------|
| B8 | Frosted blur behind control bar + top bar on API 31+, alpha-fill fallback < 31 | ✅ * |
| B9 | Pulsing accent glow around the highlighted track's box (reduce-motion aware) | ✅ |
| B10 | Soft elevation/shadow on control bar + metrics chip | ✅ |

\* Real `RenderEffect`/`Modifier.blur` frosted panels + soft shadows on API 31+, translucent
fallback below. Note: the camera feed is a `SurfaceView`, so the blur frosts the glass panel
layers (not a true live backdrop blur of the camera, which Compose can't sample from a Surface).

### C — Adaptive / responsive layout

| # | Item | Status |
|---|------|--------|
| C11 | `WindowSizeClass` adaptive layout — side rail on wide/landscape, bottom bar in portrait | ✅ |
| C12 | Edge-to-edge insets — bottom bar respects nav/gesture bars, top respects status bar | ✅ |

### D — Theming & color polish

| # | Item | Status |
|---|------|--------|
| D13 | "System (dynamic)" theme via dynamic color on API 31+ (falls back to Dark < 31) | ✅ |
| D14 | Completed cohesive Light theme (containers, variants, outline, error roles) | ✅ |
| D15 | Animated theme/color changes (`animateColorAsState`, snaps on reduce-motion) | ✅ |
| D16 | Deliberate typography scale (weights + letter-spacing per role) | ✅ |

### E — UX refinements

| # | Item | Status |
|---|------|--------|
| E17 | Real Material 3 `ModalBottomSheet` for the detection list (drag handle, tap-to-highlight) | ✅ |
| E18 | Richer idle state — animated scan-line hero, value prop, clear "Scan from gallery" button | ✅ |
| E19 | Haptics on start/stop, freeze, and first appearance of a new class (gated by setting) | ✅ |
| E20 | Permission polish — animated rationale + "Open app settings" deep link when denied | ✅ |
| E21 | Onboarding is a swipeable `HorizontalPager` carousel with animated page dots | ✅ |
| E22 | Branded loading scan-line viewfinder instead of a bare spinner | ✅ |

### F — Bounding-box details

| # | Item | Status |
|---|------|--------|
| F23 | Clamp labels to the screen (shift left when they'd clip the right edge) | ✅ |
| F24 | Fade boxes by confidence (lower confidence → more transparent) | ✅ |
| F25 | Tap ripple/radial pulse where the user taps (reduce-motion aware) | ✅ |

New tests: `BoundingBoxGeometryTest` (label-clamp / confidence-fade / smoothing math) and a
`SettingsValidator` case for the new `SYSTEM` theme.

New dependency: `androidx.compose.material3:material3-window-size-class` (via the compose BOM).

---

## Detection accuracy improvements (v3.1)

> Gallery recall, live-camera recall, and optional larger ML models. Lite0 remains the default for
> speed; Lite1/Lite2 are selectable in **Settings → Model**.

| # | Item | Status |
|---|------|--------|
| A1 | Gallery decode up to **2048 px** largest side (was 1280) via `GalleryBitmapDecoder` | ✅ |
| A2 | Gallery still-image detection uses a slightly lower score threshold (latency not a concern) | ✅ |
| A3 | Default max detections raised **5 → 10** (cap **20**); default confidence **0.45 → 0.40** | ✅ |
| A4 | Smaller minimum box size (**0.015**) so distant/small objects are not filtered out as early | ✅ |
| A5 | CameraX analysis resolution target **1280×720** (was device default, often lower) | ✅ |
| A6 | Bundle **EfficientDet-Lite2** (`efficientdet_lite2.tflite`) — selectable in Settings | ✅ |
| A7 | Bundle Lite0 + Lite1 assets from official TensorFlow sources | ✅ |

**Model trade-offs**

| Model | Input | Speed | Recall | APK add-on |
|-------|-------|-------|--------|------------|
| Lite0 (default) | 320×320 | Fastest | Lowest | ~4 MB |
| Lite1 | 384×384 | Balanced | Better | ~6 MB |
| Lite2 | 448×448 | Slowest | Best | ~10 MB |

All three models still use the **80 COCO classes** — mis-labels on non-COCO objects (e.g. a
specific gadget mapped to "remote") are a model limitation, not a bug. Lite2 reduces misses but
cannot invent new vocabulary.

---

## Current sprint focus

**Sprint 8 — v2 feature build**

1. ✅ Phases 10–14 — performance/ML, detection UX, history, validation, accessibility, trust
2. ✅ Phases A–E — detection controls, grouped settings, history, theming, freeze/gallery
3. ✅ Green `testDebugUnitTest` + `assembleDebug` (Room + Hilt + KSP wired)
4. ⬜ GPU delegate + on-device benchmark (Phase 10.11–10.12)
5. ⬜ TalkBack/TTS speak-labels engine (Phase 13.10) and ROI cropping (13.11)
6. ⬜ Capture Play Store screenshots + manual QA on 2 devices

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
| 2026-06-24 | ✅ Phase 8/9 — tests, release docs, bundleRelease CI, ProGuard release build |
| 2026-06-25 | 🔄 v2 kickoff — Room history, expanded settings, theming, freeze/torch/gallery (versionCode 2 / 2.0.0) |
| 2026-06-25 | ✅ v2 Phases 10–14 + A–E landed — performance/ML, detection UX, validation, accessibility |
| 2026-06-25 | ✅ v2 verified GREEN — `testDebugUnitTest` + `assembleDebug` pass on fresh Gradle 8.13 / Android Studio (D:) setup |
| 2026-06-26 | ✅ v3 UI/UX polish — animations, glassmorphism/depth, WindowSizeClass adaptive layout, dynamic + light theming, ModalBottomSheet, onboarding carousel, branded loading, bounding-box smoothing/clamp/confidence-fade/tap-pulse (reduce-motion + haptic settings honored) |
| 2026-06-28 | ✅ Diagnostics & reliability — added `core/AppLog` (tag-prefixed, debug-gated, rate-limited helper for hot paths) and replaced silent `runCatching`/`catch` swallows with leveled logging across detectors, camera, repositories & ViewModels; cleaner CameraX teardown (unbind before surface destroyed to curb "BufferQueue abandoned"); gallery decode downsamples safely and recycles bitmaps. Note: per-frame `ImageProxy.toBitmap()` still allocates each frame — see recommended follow-up below |
| 2026-06-28 | ✅ Detection accuracy (v3.1) — gallery up to 2048 px + lower still-image threshold; default max detections 10 (cap 20); 1280×720 analysis resolution; EfficientDet-Lite2 bundled and selectable alongside Lite0/Lite1 |

> **Recommended follow-up (bitmap churn):** the live analysis path allocates a fresh `Bitmap` per
> frame in `DetectionImageAnalyzer.toBitmap()` (plus a rotated copy in `TfliteObjectDetector`),
> which drives the Large-Object-Space / GC churn seen in logcat. A safe fix is a reusable bitmap
> pool keyed by frame dimensions, but it touches the inference pipeline and was intentionally left
> as a follow-up rather than risk a behavior regression.

## Release docs

- [Release guide](docs/RELEASE.md)
- [Play Store listing](docs/PLAY_STORE_LISTING.md)
- [Privacy policy](docs/PRIVACY_POLICY.md)
- [QA checklist](docs/QA_CHECKLIST.md)

---

## License

MIT License — free to use, modify, and distribute.

---

## Contributors

- **RUDRA** — sharmarudra095@gmail.com
