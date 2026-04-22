# PR Review Mode

Activate when: input contains a GitHub PR URL (`github.com/.+/pull/\d+`) or explicit review
phrases: "review this PR", "review this diff", "check this code", "what's wrong with this".

When Review Mode activates:
1. Do **not** follow the generation workflow in `SKILL.md`
2. Follow only this document
3. Output a structured local report — do not post to GitHub

---

## Review Workflow

### Step 1 — Fetch the diff

```bash
gh pr diff <PR_URL>
```

Note all changed `.kt` files. Store the output.

### Step 2 — Fetch full file contents

For each changed `.kt` file, fetch the **complete file** — not just the diff lines.

```bash
# Get PR metadata
gh pr view <PR_URL> --json headRefName,headRepository \
  --jq '{branch: .headRefName, repo: .headRepository.nameWithOwner}'

# Fetch full file (replace {owner}, {repo}, {path}, {branch})
gh api "repos/{owner}/{repo}/contents/{path}?ref={branch}" \
  --jq '.content' | base64 -d
```

**Why full files matter:** The diff shows what changed. The full file shows what the composable
actually looks like — including whether a `modifier` parameter exists at all, and how modifier
chains are structured across multiple lines. Single-line modifier patterns like
`Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {` are invisible in a diff-only view
when that line itself was not modified.

### Step 3 — Scan project settings

Run in priority order. Always run step d regardless of what lint config is found.

**a. `.editorconfig`:**
```bash
cat .editorconfig 2>/dev/null || echo "not found"
```
Note: indent size, max line length, trailing comma rules under `[*.kt]`.

**b. ktlint config:**
```bash
grep -A 20 "\[\*\.kt\]" .editorconfig 2>/dev/null
cat .ktlint 2>/dev/null || echo "not found"
```

**c. detekt:**
```bash
find . -name "detekt.yml" -o -name "detekt-config.yml" 2>/dev/null | head -3
```
If found, read it and note complexity, naming, and style rules.

**d. Infer codebase conventions (always run):**
```bash
# Find 3–5 existing composable files NOT in the diff
find . -name "*.kt" -not -path "*/build/*" | xargs grep -l "@Composable" | \
  grep -v "Test" | head -5
```
For each file, note:
- Modifier chaining: one per line vs. inline on constructor line
- Modifier parameter name: `modifier` vs `Modifier` (both are valid — note which team uses)
- Trailing lambda vs. named `content =` on single-slot composables
- Named parameter usage on single-arg calls

Build a **project profile**. Use it to suppress false positives — flag deviations from the
team's own conventions, not deviations from an external style guide.

### Step 4 — Run the checklist

Evaluate every changed composable against all 6 categories below.
Use the **full file** from Step 2, not the diff from Step 1.

### Step 5 — Output the report

Use the format at the end of this document.

---

## Compose Review Checklist

### Category 1: Modifier Hygiene

Scan the full file for each changed `@Composable` function.

- [ ] **Modifier parameter present.** Every `@Composable` function that renders UI must have
  a `modifier: Modifier = Modifier` parameter. Flag if absent.
  Exception: private composables used only as internal implementation detail with no layout impact.

- [ ] **Modifier passed to root element.** The `modifier` parameter must be applied to the
  outermost layout composable in the function body — not ignored, not applied to an inner element,
  not used on a sibling.

- [ ] **Modifier not duplicated.** `modifier` must not be split across two sibling elements.
  One root element receives it.

- [ ] **Modifier ordering follows the paint model.** Work outward-in:
  `size / fillMaxWidth` → `padding` → `background / border` → `clickable / pointerInput`.
  Flag these specific reversals:
  - `background()` before `padding()` when the intent is background-wraps-content
    (`Modifier.padding(16.dp).background(Color.Red)` = background wraps the padding area;
    `Modifier.background(Color.Red).padding(16.dp)` = background does NOT include the padding area)
  - `clickable()` before `padding()` — shrinks the effective touch target
  - `size()` or `fillMaxWidth()` after `padding()` — the size constraint no longer includes the padding

- [ ] **Single-line modifier check.** Read the full constructor line even when unchanged in the diff.
  Verify ordering is correct even when the entire modifier chain is written inline:
  `Row(modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp))` — this has wrong ordering
  (`clickable` before `padding` shrinks touch target).

- [ ] **No `padding` + `offset` for the same adjustment.** `offset` does not affect layout;
  `padding` does. They are not interchangeable.

### Category 2: Recomposition

- [ ] **No unstable parameter types** without stability annotations:
  - Plain `List<T>` — compiler infers as unstable. Use `@Immutable` data class wrapper,
    `ImmutableList<T>` (kotlinx-collections-immutable), or annotate the call site
  - `HashMap`, `MutableMap`, any mutable collection — not stable
  - Non-`data` class without `@Stable` — compiler cannot infer stability

- [ ] **Lambdas not created inline without `remember`.** A new lambda instance on every
  recomposition of the parent prevents the child from being skipped.
  ```kotlin
  // BAD — new lambda every parent recomposition
  MyComposable(onClick = { doSomething() })

  // OK — stable reference
  val onClick = remember { { doSomething() } }
  MyComposable(onClick = onClick)
  ```
  Exception: if strong skipping mode is enabled in the Compose compiler (`freeCompilerArgs +=
  "-P", "plugin:androidx.compose.compiler.plugins.kotlin:experimentalStrongSkipping=true"`),
  lambdas are stable by default.

- [ ] **`derivedStateOf {}` used for computed values.** Flag values computed from state reads
  inside a composable body without `remember { derivedStateOf { ... } }`:
  ```kotlin
  // BAD — recomputes and recomposes every time any state changes
  val isValid = username.isNotEmpty() && password.length > 8

  // OK
  val isValid by remember { derivedStateOf { username.isNotEmpty() && password.length > 8 } }
  ```

- [ ] **`remember {}` keys are correct.**
  - Missing keys but referencing an input variable inside — stale value bug
  - Key that never changes (`remember(Unit)` or `remember(constant)`) — effectively `remember {}` with no recalculation

### Category 3: M3 Motion

Cross-reference with `references/material3-motion.md` for token values and easing names.

- [ ] **No hardcoded integer durations** in `tween()`, `spring()`, or `keyframes {}`. Flag any
  `tween(N)` where N is a plain integer literal. Suggest nearest `MotionTokens.Duration*` token.

- [ ] **No pre-M3 easing constants:**
  - `FastOutSlowInEasing` → `MotionTokens.EasingEmphasizedCubicBezier`
  - `LinearOutSlowInEasing` → `MotionTokens.EasingEmphasizedDecelerateCubicBezier`
  - `FastOutLinearInEasing` → `MotionTokens.EasingEmphasizedAccelerateCubicBezier`

- [ ] **`animateColorAsState` has an `animationSpec`.** No-spec `animateColorAsState(target)`
  uses the default spring which is inappropriate for color transitions.
  Suggest: `animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec()`

- [ ] **`AnimatedVisibility` enter/exit easing is asymmetric.** Enter must use Decelerate easing;
  exit must use Accelerate easing. Same easing for both is incorrect.

- [ ] **No non-shared-element animation > 600ms.** Flag durations above `DurationLong4` (600ms)
  unless the animation is a shared element or full-screen transition.

- [ ] **New components use `MotionScheme` not raw `tween()`.** A new component accepting no
  `AnimationSpec` parameter should use `MaterialTheme.motionScheme.defaultSpatialSpec()` /
  `defaultEffectsSpec()` to be theme-motion-aware.

### Category 4: CMP Compatibility

Apply only to files whose path contains `commonMain` or has no platform-specific path segment.

- [ ] No `android.*` imports (`android.content.Context`, `android.util.Log`, etc.)
- [ ] No `androidx.*` imports that are Android-only. Check `references/multiplatform.md`
  API availability matrix for what is available in `commonMain`.
- [ ] No `LocalContext.current` — not available in CMP `commonMain`
- [ ] No `Activity`, `Context`, or `Application` references
- [ ] Resources via `Res.drawable.*` / `Res.string.*` — not `R.drawable.*` / `R.string.*`

### Category 5: Lists & Keys

- [ ] **Every `items()` call has a `key = {}`** in `LazyColumn`, `LazyRow`, `LazyVerticalGrid`,
  `LazyHorizontalGrid`. Missing keys cause incorrect animations and item reuse bugs.
  ```kotlin
  // BAD
  items(movies) { movie -> MovieCard(movie) }

  // OK
  items(movies, key = { it.id }) { movie -> MovieCard(movie) }
  ```

- [ ] **`contentType = {}` present for heterogeneous lists.** When a lazy list renders more than
  one type of composable (e.g., headers + items), `contentType` must be specified so Compose
  reuses composition nodes correctly.

- [ ] **No `LazyColumn` directly nested inside `LazyColumn`** without a fixed height on the inner
  one. Unbounded nested lazy lists throw `java.lang.IllegalStateException` at runtime.

- [ ] **`TvLazyRow` / `TvLazyColumn` / `TvLazyVerticalGrid` / `TvLazyHorizontalGrid` from
  `tv-foundation` flagged as deprecated.** Replace with standard Foundation equivalents.
  See migration table in `references/tv-compose.md` Section 5.

### Category 6: Atomic Design

Cross-reference with `references/atomic-design.md` for token patterns and naming rules.

- [ ] **No hardcoded `Color(0xFF...)` inside composable bodies.** Colors must come from
  `MaterialTheme.colorScheme.*` or an app-level brand token (`CompositionLocal`).
  Exception: `Color.Transparent`, `Color.Unspecified`, and `Color.White`/`Color.Black` as
  explicit design choices are acceptable.

- [ ] **No hardcoded `fontSize`, `fontWeight`, or `TextStyle(...)`.** Typography must come from
  `MaterialTheme.typography.*`. Flag any inline `TextStyle(fontSize = 14.sp)` or
  `fontWeight = FontWeight.Bold` outside of a theme definition file.

- [ ] **No magic number spacing (`16.dp`) without token.** If the project defines a spacing
  scale (check for `CompositionLocal` with spacing values), flag raw `dp` values that should
  use the scale. If no spacing scale exists, note it as a suggestion — not a critical issue.

- [ ] **Composable names describe function, not context.** Flag composables matching these
  patterns: `*For*` (e.g., `ButtonForSettings`), `*With*` (e.g., `CardWithRedBorder`),
  `*In*` (e.g., `HeaderInHome`). Exception: `*WithDefaults` pattern used for providing
  default parameters is acceptable.

- [ ] **Public composables have `modifier: Modifier = Modifier`.** (Overlaps Category 1 — in
  atomic context, additionally verify the modifier is passed to the root element and not
  consumed by an inner element.)

- [ ] **Composables rendering variable content have slot APIs.** Flag composables that hardcode
  `Text("Submit")`, `Icon(Icons.Default.Close, ...)`, or similar fixed content that should
  be a slot parameter. Exception: internal/private composables with fixed content by design.

- [ ] **Organisms do not directly reference ViewModel.** Any composable that combines multiple
  UI components (organism-level) must accept data and callbacks as parameters. Flag direct
  `viewModel()`, `hiltViewModel()`, or `koinViewModel()` calls inside organisms.
  The screen-level composable is the correct place for ViewModel access.

---

## Output Report Format

```
## PR Review: <PR title> (#NNN)
Branch: <head-branch> → <base-branch>

### Project Profile
- Code style: <inferred — e.g. "modifier chains one per line", "trailing lambdas preferred">
- Lint config: <ktlint / detekt / neither found>
- Conventions from: <files sampled>

---

### Issues

#### Critical
Issues that cause bugs, crashes, or correctness problems.

- `path/File.kt:42` — `MyCard` is missing `modifier: Modifier = Modifier` parameter. All UI
  composables must expose a modifier for caller layout control.
  Fix: add `modifier: Modifier = Modifier` to the signature; pass to the root element.

#### Suggestions
Style, M3 alignment, and performance improvements.

- `path/File.kt:87` — `tween(300)` → `MotionTokens.DurationMedium2.toInt()` (300ms = Medium2)
- `path/File.kt:103` — `FastOutSlowInEasing` → `MotionTokens.EasingEmphasizedCubicBezier`
- `path/File.kt:115` — `items(movies)` missing `key = { it.id }` — add key to prevent reorder bugs

#### Positive Patterns
Good Compose usage — always include at least one.

- `path/File.kt:55` — Correct `derivedStateOf {}` preventing redundant recompositions
- `path/File.kt:71` — `sharedBounds()` used correctly for container-to-page expansion

---

### Summary
<N> critical, <M> suggestions across <K> files reviewed.
```

**Sections policy:**
- Critical and Suggestions are always present (write "None found" if empty)
- Positive Patterns is always present — reviews must not read as a pure hit list
