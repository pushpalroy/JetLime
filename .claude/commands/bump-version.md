---
description: Upgrade the library version across all files that contain it
argument-hint: "<new-version>"
---

Bump the JetLime library version from its current value to `$ARGUMENTS` (e.g. `4.2.0`) across every file that embeds it.

## Files to update

Update **all** occurrences of the old version string with the new version `$ARGUMENTS` in:

1. `jetlime/build.gradle.kts`
   - `mavenPublishing.coordinates(...)` third argument
   - `cocoapods { version = "..." }`

2. `jetlime/jetlime.podspec`
   - `spec.version = '...'`

3. `scripts/add_git_tag.sh`
   - `TAG="..."`

4. `README.md`
   - The `implementation("io.github.pushpalroy:jetlime:...")` snippet

## Steps

1. Read each file listed above and locate the current version string.
2. Replace every occurrence of the current version with `$ARGUMENTS` using the Edit tool.
3. After all edits, grep the repo for the old version to confirm no stray occurrences remain:
   ```
   grep -r "<old-version>" --include="*.kts" --include="*.kt" --include="*.sh" --include="*.md" --include="*.podspec" .
   ```
   If any hits remain outside `build/`, `docs/`, or `.git/`, report them and ask whether to update them too.
4. Print a summary table — one row per file — showing the old value replaced and the new value written.

## Constraints

- Do NOT commit or tag. The user will review the diff and commit manually (or run `/dokka` and publish first).
- Do NOT modify `CHANGELOG`, `docs/`, or any generated output — those are updated separately.
- If `$ARGUMENTS` is missing or doesn't look like a semver string (`X.Y.Z`), stop and ask the user to provide it.
