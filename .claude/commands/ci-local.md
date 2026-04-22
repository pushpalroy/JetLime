---
description: Run the full CI pipeline locally (spotless + all platform builds) to catch failures before pushing
argument-hint: "[fast|full]"
---

Mirror the GitHub Actions `build.yml` pipeline locally so the user can verify everything CI runs before pushing. CI matrix: `spotlessCheck`, `build_android.sh`, `build_web_js.sh`, `build_web_wasm.sh`, `build_macos.sh`, `build_ios.sh`.

## Behavior

Run these in sequence with fail-fast semantics. As soon as one step fails, stop and show the failing step's last ~30 lines of output — do NOT continue to later steps, because cascaded failures hide the real error.

### `$1 == "fast"` (or when user wants a quick pre-push check)
Run only the cheap/fast steps:
1. `./gradlew spotlessCheck`
2. `./scripts/build_android.sh`
3. `./scripts/build_web_js.sh`

Skip wasm, desktop, and iOS.

### `$1 == "full"` or no argument (default — matches CI exactly)
Run every CI step, in order:
1. `./gradlew spotlessCheck`
2. `./scripts/build_android.sh`
3. `./scripts/build_web_js.sh`
4. `./scripts/build_web_wasm.sh`
5. `./scripts/build_macos.sh`
6. `./scripts/build_ios.sh` — only on macOS; skip with a note on other OSes.

## Handling the common failures

- **`spotlessCheck` fails**: tell the user to run `/spotless apply` (or `./gradlew spotlessApply`), review the diff, and re-run. Do NOT auto-apply — they should see what changed.
- **`kotlinStoreYarnLock` fails during a web build** ("Lock file was changed..."): run `./gradlew kotlinUpgradeYarnLock`, then tell the user to commit `kotlin-js-store/yarn.lock` (and `kotlin-js-store/wasm/yarn.lock` if it changed). Do NOT commit automatically.
- **Gradle daemon / class-cast weirdness after a Kotlin or AGP bump**: suggest `./gradlew --stop` and a re-run.
- **iOS build on non-macOS host**: skip with a clear message — CI runs this only on `macos-latest`.

## Final report

After all steps succeed, print a single-line summary naming each step that ran and passed. No celebratory wall of text.

If a step failed, the summary is: which step, which file/task, and the recommended next command — nothing more.
