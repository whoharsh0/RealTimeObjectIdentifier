# Manual QA Checklist — Object Identifier

Use this before each release. Test on **at least 2 devices** (one mid-range, one flagship if possible).

## Device info

| Field | Device 1 | Device 2 |
|-------|----------|----------|
| Model | | |
| Android version | | |
| Tester | | |
| Build version | | |
| Date | | |

## Install & first launch

- [ ] App installs without errors
- [ ] Adaptive icon displays correctly on home screen
- [ ] Welcome onboarding dialog appears on first launch
- [ ] "Got it" dismisses onboarding permanently (relaunch app to verify)

## Camera permission

- [ ] Permission card shows with gradient + camera icon
- [ ] Denying permission shows error message
- [ ] Granting permission proceeds to idle screen

## Detection flow

- [ ] "Start Scanning" opens full-screen camera
- [ ] Live preview is smooth (no frozen frame > 2s)
- [ ] Objects show bounding boxes + labels
- [ ] Confidence % toggles correctly from Settings
- [ ] "Stop Scanning" returns to idle screen
- [ ] Flip camera switches front/rear

## Settings

- [ ] Confidence slider changes detection sensitivity
- [ ] Max detections slider limits visible boxes
- [ ] Show confidence toggle works on overlay labels
- [ ] Settings persist after app restart
- [ ] Privacy cards display correctly

## Edge cases

- [ ] App survives rotation during scan (if supported)
- [ ] App handles low light (may detect less — note behavior)
- [ ] Airplane mode ON — detection still works
- [ ] No crashes after 5 minutes continuous scanning

## Release build (before Play Store upload)

- [ ] `bundleRelease` succeeds with release keystore
- [ ] Release AAB installs via internal testing track
- [ ] ProGuard/minify build runs detection correctly

## Sign-off

- [ ] All critical items passed
- [ ] Known issues documented below

### Known issues / notes

```
(write here)
```
