# CLAUDE.md
 
This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.
 
## Project Overview
 
JetLime is a Kotlin Multiplatform (KMP) Compose Multiplatform library that renders customizable timeline UIs. Targets: Android, iOS (x64/arm64/simulator), Desktop (JVM), JS (IR), WasmJs. The published artifact is `io.github.pushpalroy:jetlime`.
 
The repo has two Gradle modules:
- `:jetlime` — the library (all platform source in `src/commonMain/kotlin/com/pushpal/jetlime/`; Android-specific manifest + instrumented tests in `src/androidMain` and `src/androidTest`).
- `:sample:composeApp` — a sample app that consumes `:jetlime` via `implementation(project(":jetlime"))` and runs on all five targets (Android, iOS via CocoaPods, Desktop, Web-JS, Web-WASM).
JDK 17 is required for builds; CI uses JDK 21 only for the Spotless lint job. Kotlin 2.3.20, Compose Multiplatform 1.10.3, `androidTarget` compileSdk 36 / minSdk 23.
 
## Common Commands
 
Format / lint (required before PR — CI runs `spotlessCheck`):
```
./gradlew spotlessApply     # auto-fix
./gradlew spotlessCheck     # verify
```
Spotless applies ktlint + `io.nlopez.compose.rules:ktlint` with a mandatory MIT license header from `spotless/copyright.kt`. `.editorconfig` enforces 2-space indent, `max_line_length=100`, trailing commas, and allow-lists `LocalJetLimeStyle` for the Compose ktlint rule (`compose_allowed_composition_locals`).
 
Library tests (Compose UI tests — the real ones live in `jetlime/src/androidTest/`, not `src/test/`):
```
./gradlew :jetlime:connectedAndroidTest              # all instrumented tests (needs emulator/device)
./gradlew :jetlime:connectedAndroidTest --tests "com.pushpal.jetlime.JetLimeColumnTest.jetLimeColumn_displaysItems"
```
 
Sample app per-platform builds (wrap the right gradle tasks and copy outputs to `distributions/`):
```
./scripts/build_android.sh      # :sample:composeApp:assembleDebug
./scripts/build_ios.sh          # xcodebuild on sample/iosApp/iosApp.xcworkspace
./scripts/build_macos.sh        # :sample:composeApp:packageUberJarForCurrentOS
./scripts/build_web_js.sh       # :sample:composeApp:jsBrowserDistribution
./scripts/build_web_wasm.sh     # :sample:composeApp:wasmJsBrowserDistribution
```
 
API docs (Dokka V2 — output is synced into the root `docs/` directory that GitHub Pages serves):
```
./scripts/run_dokka.sh          # wraps :jetlime:syncDokkaToDocs --no-configuration-cache
```
 
Publishing (see `gradle.properties` for required credentials — `mavenCentralUsername`, `signing.*`):
```
./gradlew publishToMavenLocal                                     # test locally via ~/.m2
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
```
To test a local publish, uncomment the `maven` coordinate in `sample/composeApp/build.gradle.kts` and add `mavenLocal()` to `settings.gradle.kts`.
 
Compose compiler metrics/reports:
```
./gradlew assembleRelease -PcomposeCompilerReports=true   # outputs under jetlime/build/compose_compiler/
```
 
## Architecture
 
The library is tiny (~11 files in `commonMain`) and built around three layers:
 
1. **List containers** (`JetLimeList.kt`) — `JetLimeColumn` / `JetLimeRow` wrap `LazyColumn` / `LazyRow`. They install a `JetLimeStyle` via the `LocalJetLimeStyle` `CompositionLocal` and compute an `EventPosition` (`START` / `MIDDLE` / `END`) for each index via `EventPosition.dynamic(index, listSize)`. The arrangement (`VERTICAL` vs `HORIZONTAL`) is stamped on the style here and is how `JetLimeEvent` dispatches to `VerticalEvent` vs `HorizontalEvent`.
2. **Events** (`JetLimeEvent.kt`, `JetLimeExtendedEvent.kt`) — `JetLimeEvent` is a single-slot composable that uses `Modifier.drawBehind` to paint the connecting line(s) and the point circle/icon. The per-item `JetLimeEventStyle` carries both the event's `EventPosition` (used to decide whether to draw the up/down or left/right connector segments via `isNotStart()` / `isNotEnd()`) and a `PointPlacement` (`START` / `CENTER` / `END`) that governs where the point sits within the item box and how the connector is split into two segments that meet at the point. `JetLimeExtendedEvent` (vertical-only) adds a second slot (`additionalContent`) rendered on the opposite side of the line; it uses a custom `Layout` and `BoxWithConstraints` capped by `JetLimeEventDefaults.AdditionalContentMaxWidth`.
3. **Style + defaults** — `JetLimeStyle` (list-level: line brush, thickness, `pathEffect`, `contentDistance`, `itemSpacing`, alignment) and `JetLimeEventStyle` (per-event point visuals) are `@Immutable`. `JetLimeDefaults` / `JetLimeEventDefaults` expose the `columnStyle()` / `rowStyle()` / `eventStyle()` / `pointAnimation()` / `lineGradientBrush()` / `lineSolidBrush()` factory helpers — always extend these rather than constructing style classes directly (the constructors are `internal`).
### Key drawing invariants
 
- Line segments are drawn with Compose `drawLine`, branching on `PointPlacement` and `EventPosition`. For `CENTER`/`END` placement, the code draws two separate segments (start→point and point→end) and skips the relevant half at the first/last item. For `START` placement (default), a single segment extends from the point past the item box, with a `pointStartFactor = 1.1f` overdraw so adjacent items' lines visually join.
- **RTL mirroring** is handled explicitly in `HorizontalEvent` and in `JetLimeExtendedEvent`. In horizontal RTL, the `xOffset` is flipped as `size.width - logicalXOffset` and segment start/end Xs swap sides. Extended vertical uses `LocalLayoutDirection` + `absolutePadding` so physical LEFT/RIGHT alignment is preserved regardless of layout direction. Any change to line/point drawing must keep both LTR and RTL visually correct — see the RTL tests in `JetLimeColumnTest` / `JetLimeRowTest`.
- `VerticalAlignment.LEFT/RIGHT` and `HorizontalAlignment.TOP/BOTTOM` are *physical* sides (via `absolutePadding`), not start/end-relative.
## Release Housekeeping
 
The library version appears in several places that must be kept in sync on release:
- `jetlime/build.gradle.kts` — `mavenPublishing.coordinates(..., "X.Y.Z")` and `cocoapods { version = "X.Y.Z" }`
- `jetlime/jetlime.podspec`
- `scripts/add_git_tag.sh` — `TAG="X.Y.Z"`
- `README.md` installation snippet
After publishing, `scripts/add_git_tag.sh` creates and pushes the `X.Y.Z` git tag on `main`.
