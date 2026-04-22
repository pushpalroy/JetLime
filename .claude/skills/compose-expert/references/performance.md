# Performance Optimization Reference

## Three Phases: Composition, Layout, Drawing

Every frame consists of three phases. Understanding state reads in each phase prevents unnecessary recompositions.

### Composition Phase
- Executes composable functions, evaluates state reads
- Generates lambda and instance allocations
- **State reads here trigger recomposition** of the entire scope

### Layout Phase
- Calculates size and position, runs `measure` and `layout` blocks
- Can read state without triggering composition recomposition
- Mutable state reads OK; prefer `Modifier.offset { }` over `Modifier.offset()`

### Drawing Phase
- Emits draw operations, runs `Canvas` and custom `DrawScope`
- Cannot read mutable state without stability warnings

**Source**: `androidx/compose/runtime/Composer.kt`

---

## Recomposition Skipping with Compiler Reports

The Compose compiler generates `$changed` bitmasks to detect state changes. Enable compiler reports to inspect stability and skippability:

```kotlin
// build.gradle.kts
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_reports")
    metricsDestination = layout.buildDirectory.dir("compose_metrics")
}
```

After building (`./gradlew assembleRelease`), check the generated files in `build/compose_reports/`:

- **`*_composables.txt`** — shows each composable's restartability and skippability:
  ```
  restartable skippable fun MyComponent(name: String, onClick: Function0<Unit>)
  restartable fun UnstableComponent(items: List<Item>)  // NOT skippable — unstable param
  ```

- **`*_classes.txt`** — shows stability inference for each class:
  ```
  stable class User { stable val name: String }
  unstable class ScreenState { unstable val items: List<Item> }
  ```

A composable missing `skippable` means the compiler cannot skip it during recomposition, even when inputs haven't changed. Fix by stabilizing its parameters (see Stability section below).

### Stability — @Stable and @Immutable

A type is **stable** if:
- Its public properties are stable
- Overrides to `equals()` and `hashCode()` are based on stable properties
- Recomposition is skipped when the same instance is passed

Mark stable types explicitly:

```kotlin
@Immutable
data class Person(val name: String, val age: Int)

@Stable
class UserViewModel : ViewModel {
    private val _state = MutableState(UserState())
    val state: State<UserState> = _state
}

// Composable receiving stable types can skip recomposition
@Composable
fun PersonCard(person: Person) {
    Text(person.name)  // Skips if person unchanged
}
```

**Avoid**: `@Stable` on data classes with mutable fields or non-final properties.

---

## Strong Skipping Mode (Default)

Android Gradle Plugin 8.0+ and Compose compiler 1.5.0+ enable **strong skipping mode**. This changes how lambdas are treated:

Without strong skipping, every lambda is unstable. With it enabled:
- Lambdas become stable if all captured variables are stable
- Fewer unnecessary recompositions

```kotlin
// With strong skipping: lambda is stable if count is stable
@Composable
fun Counter(count: Int) {
    Button(onClick = { println(count) }) {  // Stable lambda
        Text("Count: $count")
    }
}
```

Check `build.gradle.kts`:
```kotlin
android {
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.0"
    }
}
```

---

## Defer State Reads to Layout/Draw Phase

Reading state in composition triggers recomposition. Push reads to later phases:

### Bad: Recomposition on Every Offset Change
```kotlin
@Composable
fun Box(offsetX: State<Float>) {
    val x = offsetX.value  // Reads in composition, triggers recomposition
    Box(modifier = Modifier.offset(x.dp, 0.dp))
}
```

### Good: Deferred Read in Layout Phase
```kotlin
@Composable
fun Box(offsetX: State<Float>) {
    Box(
        modifier = Modifier.offset {
            IntOffset(offsetX.value.toInt(), 0)  // Read in layout phase
        }
    )
}
```

Use `Modifier.offset { }` (lambda) instead of `Modifier.offset()` (parameter) for state-dependent positioning.

---

## derivedStateOf — Reducing Recomposition Frequency

When deriving expensive computations from state, wrap in `derivedStateOf` to dedup recompositions:

```kotlin
// Bad: recomposes on every items change
@Composable
fun SearchResults(items: List<Item>, query: String) {
    val filtered = items.filter { query in it.title }  // Composition phase
    LazyColumn {
        items(filtered) { /* ... */ }
    }
}

// Good: only recomposes if filtered result actually changes
@Composable
fun SearchResults(items: List<Item>, query: String) {
    val filtered = remember(items, query) {
        derivedStateOf { items.filter { query in it.title } }
    }
    LazyColumn {
        items(filtered.value) { /* ... */ }
    }
}
```

`derivedStateOf` deduplicates downstream recompositions — two different filters yielding the same list trigger only one downstream recomposition.

---

## remember with Keys

Avoid unnecessary recalculation:

```kotlin
// Recalculates on every recomposition
@Composable
fun ExpensiveItem(id: Int) {
    val metadata = computeMetadata(id)  // Called every time
    Text(metadata)
}

// Recalculates only when id changes
@Composable
fun ExpensiveItem(id: Int) {
    val metadata = remember(id) { computeMetadata(id) }
    Text(metadata)
}

// Multiple keys
@Composable
fun Item(id: Int, userId: Int) {
    val data = remember(id, userId) { fetchData(id, userId) }
    Text(data.toString())
}
```

Omit `remember` if computation is cheap (string formatting, simple objects). Over-wrapping causes memory leaks.

---

## LazyList Performance — Keys and ContentType

### Always Provide Keys

Keys enable item reuse and animations:

```kotlin
// Bad: no keys, items recreated on every list change
LazyColumn {
    items(users) { user ->
        UserRow(user)
    }
}

// Good: keys enable reuse
LazyColumn {
    items(users, key = { it.id }) { user ->
        UserRow(user)
    }
}
```

### ContentType for Efficient Reuse

```kotlin
sealed class ListItem {
    data class Header(val title: String) : ListItem()
    data class User(val user: User) : ListItem()
}

LazyColumn {
    items(
        items = items,
        key = { it.hashCode() },
        contentType = { item ->
            when (item) {
                is ListItem.Header -> "header"
                is ListItem.User -> "user"
            }
        }
    ) { item ->
        when (item) {
            is ListItem.Header -> HeaderRow(item.title)
            is ListItem.User -> UserRow(item.user)
        }
    }
}
```

Without `contentType`, all items compete for one ViewHolder pool. With it, items reuse efficiently.

### Avoid Allocations in Item Scope

```kotlin
// Bad: allocates on every recomposition
LazyColumn {
    items(users) { user ->
        val userState = remember { mutableStateOf(user) }
        UserRow(userState.value)
    }
}

// Good: allocates once
LazyColumn {
    items(
        items = users,
        key = { it.id }
    ) { user ->
        UserRow(user)
    }
}
```

---

## Baseline Profiles

Baseline profiles instruct R8 to pre-compile hot code paths, reducing startup time and jank.

### Generating Profiles

Use Jetpack Macrobenchmark to record profiles:

```kotlin
@RunWith(AndroidBenchmarkRunner::class)
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupCompilation() = benchmarkRule.measureRepeated(
        packageName = "com.example.app",
        metrics = listOf(StartupTimings.FIRST_FRAME),
        iterations = 10,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        // Interact with app
    }
}
```

Profiles are generated in `baseline-prof.txt`:
```
androidx/compose/runtime/Recomposer;startRecomposition()V
com/example/MyScreen;ComposableFunctionName(ILandroidx/compose/runtime/Composer;I)V
```

---

## R8/ProGuard Compose Rules

Compose includes default ProGuard rules. Ensure `shrinkResources true` and `minifyEnabled true`:

```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }
}
```

Custom rules to preserve stability:

```proguard
-keep @androidx.compose.runtime.Stable class **
-keep @androidx.compose.runtime.Immutable class **
-keepclassmembers class * {
    @androidx.compose.runtime.Stable <methods>;
}
```

---

## Measuring Performance

### Layout Inspector — Recomposition Counts

In Android Studio:
1. Run app on device
2. Open **Layout Inspector** (Tools > Layout Inspector)
3. Select target process
4. Check **Show Composition Counts** (toggle in inspector)

Recomposition counts display how many times each composable was recomposed since inspection started.

### Macrobenchmark — Frame Timing

```kotlin
benchmarkRule.measureRepeated(
    packageName = "com.example.app",
    metrics = listOf(FrameTimingMetric()),
    iterations = 10
) {
    // Interact: scroll, click, etc.
}
```

Reports frame times (ms), jank, jitter. Target <16.67ms for 60 fps.

---

## Common Hot Paths

### String Formatting in Composition
```kotlin
// Bad: allocates string every recomposition
@Composable
fun Counter(count: Int) {
    Text("Count: ${count}")  // String.format called
}

// Still composed, but optimized
@Composable
fun Counter(count: Int) {
    Text(buildString { append("Count: "); append(count) })
}
```

### List Filtering Without derivedStateOf
```kotlin
// Bad: filters every recomposition
@Composable
fun FilteredList(items: List<Item>, predicate: (Item) -> Boolean) {
    LazyColumn {
        items(items.filter(predicate)) { /* ... */ }
    }
}

// Good
@Composable
fun FilteredList(items: List<Item>, predicate: (Item) -> Boolean) {
    val filtered = remember(items, predicate) {
        derivedStateOf { items.filter(predicate) }
    }
    LazyColumn {
        items(filtered.value) { /* ... */ }
    }
}
```

### Creating Objects in Lambdas
```kotlin
// Bad
Button(
    colors = ButtonDefaults.buttonColors(
        containerColor = if (isPressed) Color.Red else Color.Blue
    )
) { }

// Good: compute once
val buttonColors = remember(isPressed) {
    ButtonDefaults.buttonColors(
        containerColor = if (isPressed) Color.Red else Color.Blue
    )
}
Button(colors = buttonColors) { }
```

---

## Anti-Patterns

### Wrapping Everything in remember
```kotlin
// Unnecessary
val text = remember { "Hello" }
val size = remember { 12.sp }
val color = remember { Color.Black }
```

`remember` only for mutable state or expensive calculations.

### Premature Optimization
Profile first. Don't add `derivedStateOf` or `remember` without Layout Inspector data.

### @Stable on Mutable Data Classes
```kotlin
// DON'T
@Stable
data class MutableUser(val name: String, val age: MutableState<Int>)

// DO
@Immutable
data class User(val name: String, val age: Int)
```

---

## Resources

- **Compose Compiler Reports**: https://developer.android.com/develop/ui/compose/performance/stability-report
- **Macrobenchmark**: https://developer.android.com/develop/ui/compose/performance/measurement
- **Baseline Profiles**: https://developer.android.com/develop/ui/compose/performance/baseline-profiles

---

## Zero-Size DrawScope Guard

During initial composition, a composable's size can be zero. This causes crashes in calculations like `size.minDimension / 2`.

```kotlin
// BAD: crashes when size is zero
Canvas(modifier = Modifier.fillMaxSize()) {
    val radius = size.minDimension / 2  // NaN or divide-by-zero
    drawCircle(color = Color.Blue, radius = radius)
}

// GOOD: always guard
Canvas(modifier = Modifier.fillMaxSize()) {
    if (size.minDimension <= 0f) return@Canvas
    val radius = size.minDimension / 2
    drawCircle(color = Color.Blue, radius = radius)
}
```

Rule: Never use `fillMaxSize()` on Canvas without an explicit height constraint. Always guard DrawScope operations.

---

## Composition Tracing

Use `trace()` for Perfetto/systrace integration:

```kotlin
@Composable
fun ExpensiveScreen() {
    trace("ExpensiveScreen") {
        // composable body — visible in system traces
    }
}
```

Enables identifying slow composables in production profiling without adding logging.

---

## movableContentOf

Avoid recomposition when moving content between layout positions:

```kotlin
val movableContent = remember {
    movableContentOf {
        ExpensiveChild() // Only composed once, moved without recomposition
    }
}

if (isExpanded) {
    ExpandedLayout { movableContent() }
} else {
    CollapsedLayout { movableContent() }
}
```

Without `movableContentOf`, switching between layouts would dispose and recompose `ExpensiveChild`. With it, the content is moved, preserving state and avoiding recomposition.

---

## Compiler Reports (Expanded)

Enable compiler reports to see which composables are skippable and which types are stable:

```kotlin
// build.gradle.kts
composeCompiler {
    reportsDestination = layout.buildDirectory.dir("compose_reports")
    metricsDestination = layout.buildDirectory.dir("compose_metrics")
}
```

After building, check the output files:

- `*_composables.txt` — shows each composable's status:
  ```
  restartable skippable fun MyComponent(name: String, onClick: Function0<Unit>)
  restartable fun UnstableComponent(items: List<Item>)  // NOT skippable!
  ```

- `*_classes.txt` — shows type stability:
  ```
  stable class User { stable val name: String }
  unstable class ScreenState { unstable val items: List<Item> }
  ```

Fix unstable types by:
1. Using `@Stable` annotation on the class
2. Using `kotlinx.collections.immutable.ImmutableList` instead of `List`
3. Adding the class to `compose-stability-config.txt` for multi-module projects

---

## Production Performance Rules

1. **R8: strip previews + semantics in release** — add to `proguard-rules.pro`:
```
-assumenosideeffects class androidx.compose.ui.tooling.preview.** { *; }
```

2. **`@Suppress("ComposeUnstableCollections")`** — pragmatic skipping when stability isn't worth the complexity:
```kotlin
@Suppress("ComposeUnstableCollections")
@Composable
fun ItemList(items: List<Item>) { // List is unstable but acceptable here
    // ...
}
```

3. **ImmutableList for stability** — Guava `ImmutableList` or `kotlinx.collections.immutable`:
```kotlin
// Makes the parameter stable, enabling recomposition skipping
@Composable
fun StableList(items: ImmutableList<Item>) { ... }
```

4. **ReportDrawnWhen** for startup performance:
```kotlin
ReportDrawnWhen { items.isNotEmpty() }
```

5. **Canvas always explicitly sized** — never `fillMaxSize()` without a height constraint:
```kotlin
// BAD
Canvas(Modifier.fillMaxSize()) { ... }

// GOOD
Canvas(Modifier.fillMaxWidth().height(200.dp)) { ... }
```

---

## Compose Multiplatform Performance

Performance tooling differs across platforms:

| Tool | Android | Desktop | iOS | Web |
|------|---------|---------|-----|-----|
| Baseline Profiles | Yes | No | No | No |
| Macrobenchmark | Yes | No | No | No |
| Layout Inspector | Yes (AS) | No | No | No |
| Profiling | Android Studio | JMH | Instruments | Browser DevTools |
| R8/ProGuard | Yes | ProGuard separately | N/A (Kotlin/Native) | N/A |

iOS-specific:
- Kotlin/Native has different GC behavior than Android ART
- Enable ProMotion with `CADisableMinimumFrameDurationOnPhone = true` in Info.plist
- Use configurable frame rate API: increase for animations, decrease for static content

Desktop JVM:
- Different GC characteristics (G1GC vs ART)
- JIT compilation warms up differently

Web/WASM:
- Renders entire canvas per frame (unlike DOM partial repaints)
- WASM GC behavior differs from JVM
- Bundle size impacts initial load time
