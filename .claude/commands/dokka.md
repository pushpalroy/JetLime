---
description: Generate API documentation using Dokka V2 and sync to docs folder
---

Generate HTML API documentation for the JetLime library. This command runs the Dokka V2 generation task and synchronizes the output to the root `docs/` directory, which is served via GitHub Pages.

Behavior:
1. Run `./scripts/run_dokka.sh`. This script executes the `:jetlime:syncDokkaToDocs` Gradle task.
2. The task generates HTML documentation using Dokka V2 from the source files.
3. It incorporates additional documentation from `dokkaModule.md` and `dokkaPackage.md` if present.
4. It clears the existing `docs/` directory and copies the new documentation there.

Notes:
- The output in `docs/` should be reviewed if significant API changes were made.
- Dokka V2 is used, as configured in `jetlime/build.gradle.kts`.
- The synchronization task ensures that the latest documentation is ready for deployment to GitHub Pages.
- If the command fails, check the Gradle output for configuration or source errors.
