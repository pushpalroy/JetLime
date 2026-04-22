# Production Crash Playbook for Jetpack Compose

Real-world crash patterns observed in Compose applications at scale. Each section documents the root cause, the failing pattern, the fix, and the rule to prevent recurrence.

---

## 1. remember {} Without Configuration-Derived Key

### Root Cause

`remember {}` without keys caches the initial computation and never recalculates. When the remembered value derives from configuration state (screen dimensions, font scale, density), a configuration change (rotation, foldable posture, window resize) leaves the cached value stale.

### Crash Pattern

```kotlin
// BAD: shimmerCount is cached from initial screenHeightDp, stale after rotation
@Composable
fun ShimmerList() {
    val config = LocalConfiguration.current
    val screenHeightDp = config.screenHeightDp
    val itemHeightDp = 80

    val shimmerCount = remember { (screenHeightDp / itemHeightDp).toInt() }

    LazyColumn {
        items(shimmerCount) { ShimmerItem() }
    }
}
```

After rotation, `screenHeightDp` changes but `shimmerCount` still holds the portrait value. The list renders the wrong number of shimmer placeholders. In extreme cases (foldable unfolding), the stale count causes layout overflow or zero items.

### Fix

```kotlin
// GOOD: screenHeightDp is a key, so remember recalculates on config change
@Composable
fun ShimmerList() {
    val config = LocalConfiguration.current
    val screenHeightDp = config.screenHeightDp
    val itemHeightDp = 80

    val shimmerCount = remember(screenHeightDp) {
        (screenHeightDp / itemHeightDp).toInt().coerceAtLeast(1)
    }

    LazyColumn {
        items(shimmerCount) { ShimmerItem() }
    }
}
```

**Rule:** Any value derived from `LocalConfiguration`, `LocalDensity`, or `LocalLayoutDirection` MUST include that configuration source in `remember`'s key parameters. Audit all `remember {}` calls that reference `screenHeightDp`, `screenWidthDp`, `fontScale`, or `densityDpi`.

---

## 2. indexOf() Inside items {}

### Root Cause

Using `list.indexOf(item)` inside a LazyColumn's `items {}` block is O(n) per item, making the overall list O(n^2). Worse, `indexOf` uses structural equality (`equals`). If the list contains recreated objects (new instances from a network response mapped to data classes), `indexOf` may return `-1`, which when passed as an index triggers `IndexOutOfBoundsException`.

### Crash Pattern

```kotlin
// BAD: O(n^2) and crashes when indexOf returns -1
@Composable
fun MessageList(messages: List<Message>) {
    LazyColumn {
        items(messages) { message ->
            val index = messages.indexOf(message)  // O(n) per item
            MessageRow(
                message = message,
                isEven = index % 2 == 0  // -1 % 2 == -1, not a crash here
            )
        }
    }
}

// Worse: using indexOf result as a direct index
items(messages) { message ->
    val index = messages.indexOf(message)
    val nextMessage = messages[index + 1]  // IndexOutOfBoundsException
}
```

### Fix

```kotlin
// GOOD: Use itemsIndexed to get the index directly
@Composable
fun MessageList(messages: List<Message>) {
    LazyColumn {
        itemsIndexed(
            items = messages,
            key = { _, message -> message.id }
        ) { index, message ->
            MessageRow(
                message = message,
                isEven = index % 2 == 0
            )
        }
    }
}
```

If you need `items` with a key but also need the index, use `items` with `key` and derive the index from the item itself if possible, or switch to `itemsIndexed`.

**Rule:** Never call `indexOf()`, `lastIndexOf()`, or `indexOfFirst {}` inside a `LazyListScope` item factory. Use `itemsIndexed` for index access. Use `items(key = { ... })` for stable identity.

---

## 3. DrawScope Without Zero-Size Guard

### Root Cause

During initial composition, a `Canvas` composable may receive a `Size.Zero` (or `Size.Unspecified`) before layout completes, especially when using `fillMaxSize()` inside a parent that hasn't been measured yet. Dividing by zero-dimension values or computing radii from `size.minDimension` when it's zero produces `NaN` or `Infinity`, which crashes the Skia rendering pipeline.

### Crash Pattern

```kotlin
// BAD: size.minDimension is 0 during initial composition
Canvas(modifier = Modifier.fillMaxSize()) {
    val radius = size.minDimension / 2
    drawCircle(color = Color.Blue, radius = radius)
}
```

On certain devices or inside `SubcomposeLayout`, the first draw call fires with `size = Size(0, 0)`. The `drawCircle` call with `radius = 0f` may not crash, but derived math like `360f / size.width` produces `Infinity` and corrupts the draw path.

### Fix

```kotlin
// GOOD: guard against zero size, and give Canvas an explicit size
Canvas(
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
) {
    if (size.minDimension <= 0f) return@Canvas
    val radius = size.minDimension / 2
    drawCircle(color = Color.Blue, radius = radius)
}
```

For dynamic sizing, use `Modifier.aspectRatio()` or `BoxWithConstraints` to guarantee non-zero dimensions before drawing:

```kotlin
BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
    if (maxWidth > 0.dp && maxHeight > 0.dp) {
        Canvas(modifier = Modifier.size(maxWidth, maxWidth)) {
            val radius = size.minDimension / 2
            drawCircle(color = Color.Blue, radius = radius)
        }
    }
}
```

**Rule:** Always guard `DrawScope` blocks against zero-size conditions. Never use `fillMaxSize()` on `Canvas` without an explicit height constraint. Prefer `Modifier.size()`, `Modifier.height()`, or `Modifier.aspectRatio()` so Canvas always enters draw with known dimensions.

---

## 4. Duplicate LazyColumn Keys

### Root Cause

`LazyColumn` requires unique keys across all items. When the backend sends items without unique IDs, or when a WebSocket reconnection delivers duplicate messages before deduplication, duplicate keys cause `IllegalArgumentException: Key ... was already used`.

### Crash Pattern

```kotlin
// BAD: backend sends duplicate IDs after WebSocket reconnect
data class Notification(val id: String, val text: String)

LazyColumn {
    items(
        items = notifications,
        key = { it.id }  // Crashes if two items share the same id
    ) { notification ->
        NotificationRow(notification)
    }
}
```

### Fix: Dedup Index Pattern

Add a `dedupIndex` field that is excluded from `equals`/`hashCode` but included in the key. This handles duplicates gracefully without losing data:

```kotlin
data class Notification(
    val id: String,
    val text: String,
    val timestamp: Long
) {
    // dedupIndex is NOT in the primary constructor, excluded from equals/hashCode
    var dedupIndex: Int = 0
}

fun List<Notification>.withDedupIndex(): List<Notification> {
    val seen = mutableMapOf<String, Int>()
    return map { item ->
        val count = seen.getOrDefault(item.id, 0)
        seen[item.id] = count + 1
        item.also { it.dedupIndex = count }
    }
}

@Composable
fun NotificationList(notifications: List<Notification>) {
    val deduped = remember(notifications) { notifications.withDedupIndex() }

    LazyColumn {
        items(
            items = deduped,
            key = { "${it.id}_${it.dedupIndex}" }  // Guaranteed unique
        ) { notification ->
            NotificationRow(notification)
        }
    }
}
```

An alternative defensive approach using `distinctBy` when true duplicates should be dropped:

```kotlin
val uniqueNotifications = remember(notifications) {
    notifications.distinctBy { it.id }
}
```

**Rule:** Never trust backend data to provide unique keys. Either deduplicate with `distinctBy` or use the dedup-index pattern to make keys unique. Wrap key construction in a function that can be unit-tested.

---

## 5. derivedStateOf Driving Collection Size

### Root Cause

`derivedStateOf` recalculates lazily when its inputs change, but the recomposition that reads the derived value and the recomposition that reads the source collection may occur in different frames. When `derivedStateOf` exposes only a count, the `items(count)` call may use a stale count that is out of sync with the actual collection, causing `IndexOutOfBoundsException`.

### Crash Pattern

```kotlin
// BAD: derive only the count; items {} reads allItems directly with stale count
@Composable
fun FilteredList(allItems: List<Item>, filter: String) {
    val itemCount by remember {
        derivedStateOf { allItems.count { it.name.contains(filter) } }
    }

    LazyColumn {
        items(itemCount) { index ->
            val item = allItems.filter { it.name.contains(filter) }[index]
            // itemCount may be stale relative to allItems, IOOB crash
            ItemRow(item)
        }
    }
}
```

### Fix

```kotlin
// GOOD: derive the full filtered list; count and access are always consistent
@Composable
fun FilteredList(allItems: List<Item>, filter: String) {
    val filteredItems by remember {
        derivedStateOf { allItems.filter { it.name.contains(filter) } }
    }

    LazyColumn {
        items(
            items = filteredItems,
            key = { it.id }
        ) { item ->
            ItemRow(item)
        }
    }
}
```

**Rule:** `derivedStateOf` is appropriate for scroll direction, visibility thresholds, and validation states -- values that don't drive collection iteration. Never use `derivedStateOf` to expose a count or index that a `LazyList` will use to access a separate collection.

---

## 6. collectAsState vs collectAsStateWithLifecycle

### Root Cause

`collectAsState()` subscribes to a `Flow` and keeps collecting even when the app is in the background. This causes unnecessary work (network calls, database queries, sensor reads), battery drain, and stale state that flashes briefly when the user returns to the app.

### The Difference

```kotlin
// BAD: continues collecting in background
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.uiState.collectAsState()
    ProfileContent(state)
}

// GOOD: stops collecting when lifecycle drops below STARTED
@Composable
fun ProfileScreen(viewModel: ProfileViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileContent(state)
}
```

`collectAsStateWithLifecycle` is part of `androidx.lifecycle:lifecycle-runtime-compose`. It automatically cancels collection when the `Lifecycle` drops below a configurable state (default: `Lifecycle.State.STARTED`).

### Custom Lifecycle Minimum

```kotlin
// Collect only when RESUMED (stricter, for camera/location flows)
val locationState by locationFlow.collectAsStateWithLifecycle(
    minActiveState = Lifecycle.State.RESUMED
)
```

### Compose Multiplatform Consideration

`collectAsStateWithLifecycle` is Android-only because it depends on `androidx.lifecycle`. In Compose Multiplatform (CMP) projects, use `collectAsState()` on non-Android targets and `collectAsStateWithLifecycle()` on Android via an `expect`/`actual` pattern:

```kotlin
// commonMain
@Composable
expect fun <T> Flow<T>.collectAsStateMultiplatform(
    initial: T
): State<T>

// androidMain
@Composable
actual fun <T> Flow<T>.collectAsStateMultiplatform(
    initial: T
): State<T> = collectAsStateWithLifecycle(initialValue = initial)

// iosMain / desktopMain / wasmJsMain
@Composable
actual fun <T> Flow<T>.collectAsStateMultiplatform(
    initial: T
): State<T> = collectAsState(initial = initial)
```

**Rule:** On Android, always use `collectAsStateWithLifecycle` for `StateFlow` and `SharedFlow`. Reserve `collectAsState` for Compose Multiplatform common code or non-lifecycle-aware contexts only.

---

## 7. SafeShimmerItem Pattern

### Root Cause

Shimmer placeholders rendered via `SubcomposeLayout` can crash when the parent layout has zero size (see Section 3) or when the shimmer animation's `DrawScope` receives unexpected dimensions. A defensive wrapper prevents crashes while maintaining the shimmer UX.

### Full Implementation

```kotlin
@Composable
fun SafeShimmerItem(
    modifier: Modifier = Modifier,
    itemHeight: Dp = 80.dp,
    shimmerColor: Color = Color.LightGray.copy(alpha = 0.6f),
    highlightColor: Color = Color.LightGray.copy(alpha = 0.2f),
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(shimmerColor, highlightColor, shimmerColor),
        start = Offset(translateAnim - 500f, 0f),
        end = Offset(translateAnim, 0f)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(itemHeight)
            .clip(RoundedCornerShape(8.dp))
    ) {
        val result = runCatching {
            Canvas(modifier = Modifier.matchParentSize()) {
                if (size.minDimension <= 0f) return@Canvas
                drawRect(brush = brush)
            }
        }
        if (result.isFailure) {
            // Fallback: solid color placeholder when Canvas fails
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(shimmerColor, RoundedCornerShape(8.dp))
            )
        }
    }
}
```

### Usage with Configuration-Aware Count

```kotlin
@Composable
fun ShimmerLoadingList() {
    val config = LocalConfiguration.current
    val itemHeight = 80.dp
    val shimmerCount = remember(config.screenHeightDp) {
        (config.screenHeightDp / itemHeight.value).toInt().coerceAtLeast(1)
    }

    LazyColumn {
        items(shimmerCount) {
            SafeShimmerItem(itemHeight = itemHeight)
        }
    }
}
```

**Rule:** Wrap all shimmer and placeholder drawing in `runCatching` or explicit size guards. Provide a solid-color fallback so the loading state is never invisible and never crashes.

---

## 8. Multi-Field Keys with Collision Prefixes

### Root Cause

When a `LazyColumn` displays items from multiple categories (live, archived, pinned), using the raw ID as the key causes collisions when the same underlying entity appears in multiple sections. For example, a message with `id = 42` could appear in both "pinned" and "live" sections.

### Crash Pattern

```kotlin
// BAD: same message ID in pinned and live sections causes key collision
LazyColumn {
    items(pinnedMessages, key = { it.id }) { message ->
        PinnedMessageRow(message)
    }
    items(liveMessages, key = { it.id }) { message ->
        LiveMessageRow(message)
    }
}
// IllegalArgumentException: Key 42 was already used
```

### Fix: Type-Prefixed Keys

```kotlin
// GOOD: prefix keys with section type to avoid collisions
LazyColumn {
    items(
        items = pinnedMessages,
        key = { "pinned_${it.id}" }
    ) { message ->
        PinnedMessageRow(message)
    }
    items(
        items = liveMessages,
        key = { "live_${it.id}" }
    ) { message ->
        LiveMessageRow(message)
    }
    items(
        items = archivedMessages,
        key = { "archived_${it.id}" }
    ) { message ->
        ArchivedMessageRow(message)
    }
}
```

### Sealed Class Alternative

For type safety, use a sealed class for keys:

```kotlin
sealed class ListKey {
    data class Pinned(val id: Long) : ListKey()
    data class Live(val id: Long) : ListKey()
    data class Archived(val id: Long) : ListKey()
}

LazyColumn {
    items(pinnedMessages, key = { ListKey.Pinned(it.id) }) { ... }
    items(liveMessages, key = { ListKey.Live(it.id) }) { ... }
    items(archivedMessages, key = { ListKey.Archived(it.id) }) { ... }
}
```

**Rule:** When mixing item types in a single `LazyList`, always prefix or wrap keys with the item type. Use string prefixes for simplicity or sealed classes for compile-time safety.

---

## 9. Production State Rules

These rules prevent the most common state-related crashes and architectural mistakes in production Compose applications.

### Rule 1: mutableStateOf ONLY in Composables, Never in ViewModels

```kotlin
// BAD: Compose state in ViewModel couples VM to Compose runtime
class ProfileViewModel : ViewModel() {
    var name by mutableStateOf("")  // Don't do this
}

// GOOD: Use coroutine-native state in ViewModel
class ProfileViewModel : ViewModel() {
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
}
```

ViewModels should expose `StateFlow` (via `MutableStateFlow` + `asStateFlow()`). Compose state (`mutableStateOf`) belongs in `@Composable` functions and state holder classes annotated with `@Stable`. This keeps ViewModels testable without the Compose runtime.

### Rule 2: SharedFlow for Events, Not Channel

```kotlin
// BAD: Channel drops events when no collector is active
class OrderViewModel : ViewModel() {
    private val _events = Channel<UiEvent>()
    val events = _events.receiveAsFlow()
}

// GOOD: SharedFlow with buffer handles brief collector gaps
class OrderViewModel : ViewModel() {
    private val _events = MutableSharedFlow<UiEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()
}
```

`Channel` is a hot stream that requires an active collector. During configuration changes or lifecycle transitions, events are silently dropped. `SharedFlow` with `extraBufferCapacity = 1` buffers one event during brief collector gaps.

### Rule 3: rememberSaveable Only at NavGraph Level

```kotlin
// BAD: rememberSaveable deep in a list item (bloats saved state bundle)
@Composable
fun ChatMessageItem(message: Message) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    // Saved for every message in the list -- Bundle size explodes
}

// GOOD: rememberSaveable at screen level, remember inside items
@Composable
fun ChatScreen(viewModel: ChatViewModel) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    LazyColumn {
        items(messages, key = { it.id }) { message ->
            var expanded by remember { mutableStateOf(false) }
            ChatMessageItem(message, expanded)
        }
    }
}
```

`rememberSaveable` serializes to the `Bundle`, which has a ~1MB limit on Android. Using it inside list items for per-item state quickly exceeds this limit and causes `TransactionTooLargeException`.

### Rule 4: snapshotFlow + distinctUntilChanged for Reactive Scroll

```kotlin
// GOOD: efficient reactive scroll position monitoring
@Composable
fun ScrollAwareList(listState: LazyListState) {
    val isScrolledPast = remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    // For side effects based on scroll position
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                // Analytics, FAB visibility, header collapse
            }
    }
}
```

`snapshotFlow` converts Compose snapshot state to a `Flow`, and `distinctUntilChanged()` prevents redundant emissions. Never poll scroll state in a recomposition-driven loop.

### Rule 5: stateIn() with map() for Derived Flows

```kotlin
// GOOD: derive UI state from repository flow
class DashboardViewModel(repository: DashboardRepository) : ViewModel() {
    val uiState: StateFlow<DashboardUiState> = repository.dashboardData
        .map { data ->
            DashboardUiState(
                totalSales = data.sales.sumOf { it.amount },
                topProduct = data.products.maxByOrNull { it.revenue }?.name.orEmpty(),
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DashboardUiState()
        )
}
```

`SharingStarted.WhileSubscribed(5_000)` keeps the upstream active for 5 seconds after the last subscriber detaches, surviving configuration changes without restarting the flow. Combine with `.map()` for derived transformations instead of creating separate `derivedStateOf` in the UI.

---

## 10. Production Performance Rules

### Rule 1: @Stable on UI State, @Immutable on Data Models

```kotlin
// Data models from network/database: truly immutable
@Immutable
data class Product(
    val id: Long,
    val name: String,
    val price: Double,
    val imageUrl: String
)

// UI state with observable mutations: stable contract
@Stable
data class CartUiState(
    val items: List<Product> = emptyList(),
    val total: Double = 0.0,
    val isLoading: Boolean = false
)
```

`@Immutable` tells the compiler all properties will never change after construction. `@Stable` promises that changes will be notified to the Compose runtime. Misuse (e.g., `@Immutable` on a class with `var` properties) causes skipped recompositions and stale UI.

### Rule 2: Canvas Always Explicitly Sized

```kotlin
// BAD: Canvas with fillMaxSize and no parent constraints
Canvas(modifier = Modifier.fillMaxSize()) { /* may get zero size */ }

// GOOD: explicit dimensions
Canvas(modifier = Modifier.size(200.dp)) { ... }

// GOOD: explicit height with flexible width
Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) { ... }

// GOOD: aspect ratio
Canvas(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) { ... }
```

See Section 3 for detailed crash scenarios. Explicit sizing prevents zero-size draw calls.

### Rule 3: ReportDrawnWhen for Startup Signals

```kotlin
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ReportDrawnWhen { state.isContentReady }

    when {
        state.isLoading -> ShimmerLoadingList()
        state.isContentReady -> ContentList(state.items)
    }
}
```

`ReportDrawnWhen` signals to the system (and performance monitoring tools) when the screen has meaningful content. This is critical for accurate Time-To-Initial-Display (TTID) and Time-To-Full-Display (TTFD) metrics.

### Rule 4: R8 Strip Previews and Semantics in Release

```kotlin
// In build.gradle.kts (app module)
android {
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}
```

In `proguard-rules.pro`, R8 automatically strips `@Preview` composables from release builds. For semantics stripping in production (accessibility data you don't need in release), use `Modifier.clearAndSetSemantics {}` selectively rather than blanket removal.

### Rule 5: Pragmatic Collection Stability

```kotlin
// When a composable receives a List that you know won't mutate:
@Suppress("ComposeUnstableCollections")
@Composable
fun ProductGrid(
    products: List<Product>,  // Compiler sees List as unstable
    onProductClick: (Product) -> Unit
) {
    LazyVerticalGrid(columns = GridCells.Fixed(2)) {
        items(products, key = { it.id }) { product ->
            ProductCard(product, onProductClick)
        }
    }
}
```

The Compose compiler treats `List`, `Map`, and `Set` as unstable because they are interfaces that could be backed by mutable implementations. Options to handle this:

1. **`@Suppress("ComposeUnstableCollections")`** -- pragmatic, per-function opt-out
2. **Wrap in `@Immutable` holder** -- `@Immutable data class ProductList(val items: List<Product>)`
3. **Use `kotlinx.collections.immutable`** -- `ImmutableList<Product>` is recognized as stable
4. **Compose compiler stability config file** -- declare stable classes globally

Choose based on team convention. Option 1 is fastest for existing codebases. Option 3 is cleanest for new projects.

---

## Quick Reference: Crash Pattern Checklist

| Pattern | Symptom | Section |
|---------|---------|---------|
| `remember {}` without config key | Stale values after rotation | 1 |
| `indexOf()` in `items {}` | O(n^2), IndexOutOfBoundsException | 2 |
| Canvas with zero size | NaN/Infinity in draw, Skia crash | 3 |
| Duplicate LazyColumn keys | IllegalArgumentException | 4 |
| `derivedStateOf` for collection count | IndexOutOfBoundsException | 5 |
| `collectAsState` in background | Battery drain, stale flash | 6 |
| Unguarded shimmer drawing | Crash in SubcomposeLayout | 7 |
| Key collision across item types | IllegalArgumentException | 8 |
| `mutableStateOf` in ViewModel | Untestable, lifecycle mismatch | 9 |
| Canvas with `fillMaxSize()` only | Zero-size draw crash | 10 |
