---
description: Run Spotless formatting check and auto-fix any violations
argument-hint: "[check|apply]"
---

Run Spotless on this project. CI runs `spotlessCheck`, so formatting violations block merges — the goal of this command is to keep the branch clean.

Behavior based on `$1`:

- **`check`** (or no argument): run `./gradlew spotlessCheck`. If it passes, report "Spotless passes" and stop. If it fails, show the violating files from the output — do NOT auto-apply.
- **`apply`**: run `./gradlew spotlessApply`, then `./gradlew spotlessCheck` to confirm. If files were modified, list them with `git status --short` so the user can review and commit.

Notes:
- Spotless uses ktlint + `io.nlopez.compose.rules:ktlint` and a mandatory MIT license header from `spotless/copyright.kt`. Do not hand-edit formatting — let `spotlessApply` own it.
- After `apply`, do not commit or push automatically. Show the diff summary and let the user decide.
- If the Gradle build itself errors (not just formatting), surface the real error rather than retrying.
