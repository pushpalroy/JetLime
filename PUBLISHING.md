# Publishing to Maven Central

JetLime is published to Maven Central via a manual GitHub Actions workflow (`publish.yml`).
Publishing is triggered on demand — there is no automatic release on push.

---

## One-time Setup

### 1. GPG Signing Key

If you do not already have a GPG key, generate one:

```bash
gpg --full-generate-key
# Choose RSA, 4096 bits, no expiry, enter your name/email/passphrase
```

Publish your public key to a keyserver so Maven Central can verify it:

```bash
gpg --list-keys --keyid-format=short   # note the 8-char key ID, e.g. 1A2B3C4D
gpg --keyserver keyserver.ubuntu.com --send-keys 1A2B3C4D
```

### 2. Export the Signing Key for CI

Vanniktech Maven Publish 0.36.0 requires the key body only — no armor headers, no newlines:

```bash
gpg --armor --export-secret-keys 1A2B3C4D \
  | tail -n +2 \
  | grep -v "^-----END" \
  | grep -v "^=" \
  | tr -d '\n'
```

Copy the single-line output — this is your `SIGNING_IN_MEMORY_KEY` secret value.

### 3. Maven Central Credentials

Log in to [central.sonatype.com](https://central.sonatype.com), click your avatar in the top-right corner, and choose **Generate User Token**. This gives you a username and password scoped for publishing.

### 4. Add GitHub Secrets

Go to **GitHub → Repository → Settings → Secrets and variables → Actions → New repository secret** and add the following five secrets:

| Secret name | Value |
|---|---|
| `MAVEN_CENTRAL_USERNAME` | Token username from Sonatype |
| `MAVEN_CENTRAL_PASSWORD` | Token password from Sonatype |
| `SIGNING_KEY_ID` | Short 8-char GPG key ID (e.g. `1A2B3C4D`) |
| `SIGNING_IN_MEMORY_KEY` | Single-line output from the export command above |
| `SIGNING_PASSWORD` | Passphrase used when creating the GPG key |

`GITHUB_TOKEN` is provided automatically by GitHub Actions — do not add it manually.

### 5. Branch Protection (if enabled)

The workflow commits the version bump directly to `main`. If branch protection requires pull requests, add `github-actions[bot]` as a bypass actor:

**Settings → Branches → edit rule → Allow specified actors to bypass required pull requests**

---

## Releasing a New Version

### Step 1 — Update the version

The workflow updates all version references automatically. The files it touches are:

- `jetlime/build.gradle.kts` — `coordinates(...)` and `cocoapods { version }`
- `jetlime/jetlime.podspec` — `spec.version`
- `scripts/add_git_tag.sh` — `TAG=`
- `README.md` — installation snippet

### Step 2 — Trigger the workflow

1. Go to **GitHub → Actions → Publish to Maven Central**
2. Click **Run workflow**
3. Enter the new version (e.g. `4.3.0`) and click **Run workflow**

### What the workflow does

| Step | Action |
|---|---|
| Checkout | Fetches `main` with full history |
| Setup | Installs JDK 21 and Gradle |
| Update versions | Runs `sed` across all version-bearing files |
| Publish | Runs `publishAndReleaseToMavenCentral` — signs, uploads, and auto-releases |
| Commit | Commits the version bump files and pushes to `main` |
| Tag | Creates and pushes an annotated git tag (e.g. `4.3.0`) |

### Step 3 — Verify

Once the workflow completes (~5–10 min):

- The new version appears on [central.sonatype.com](https://central.sonatype.com) under `io.github.pushpalroy:jetlime`
- A git tag for the version is visible in the repository
- `main` has a new commit: `Bump version to X.Y.Z`

It can take up to 30 minutes for the artifact to appear in Maven search indexes.

---

## Testing a Publish Locally

To verify signing and upload work before triggering CI:

```bash
# Publish to your local ~/.m2 (no signing required)
./gradlew publishToMavenLocal

# Full signed publish to Maven Central (requires credentials in gradle.properties or env)
./gradlew publishAndReleaseToMavenCentral --no-configuration-cache
```

To consume the locally published artifact in the sample app, uncomment the `mavenLocal()` block in `settings.gradle.kts` and the `maven` coordinate in `sample/composeApp/build.gradle.kts`.
