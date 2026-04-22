# Compose for TV (Android TV / Google TV)

Reference for building Android TV apps using Jetpack Compose with `androidx.tv:tv-material`
and `androidx.tv:tv-foundation`. This is the modern replacement for Leanback — do **not** use
Leanback for new TV projects.

Source: `tv/tv-material/`, `tv/tv-foundation/` in `androidx/androidx` (branch: `androidx-main`)

---

## 1. Setup & Dependencies

### Gradle

```kotlin
dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2026.03.00")
    implementation(composeBom)

    // General Compose
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Compose for TV — use INSTEAD of androidx.compose.material3:material3
    implementation("androidx.tv:tv-material:1.1.0-rc01")

    // TV Foundation — only needed if using BringIntoViewSpec customization
    // Standard LazyRow/LazyColumn from compose.foundation work out-of-the-box since 1.7.0
    implementation("androidx.tv:tv-foundation:1.0.0-rc01")

    // Optional: TVProvider for home screen channels
    implementation("androidx.tvprovider:tvprovider:1.1.0")
}
```

### Compatibility

| Requirement | Value |
|-------------|-------|
| Min API level | 21 (Android 5.0) — library minimum; practical Google TV / Android TV device minimum is API 23–28 in production |
| Compose BOM | 2026.03.00+ |
| Kotlin | 2.0+ (KGP 2.0.0+ required for consumption) |
| tv-material stable | 1.0.0 (first stable August 2024) |
| tv-material latest | 1.1.0-rc01 |
| tv-foundation latest | 1.0.0-rc01 |

### AndroidManifest.xml

```xml
<application>
    <activity
        android:name=".MainActivity"
        android:exported="true"
        android:banner="@drawable/banner">
        <intent-filter>
            <action android:name="android.intent.action.MAIN" />
            <category android:name="android.intent.category.LEANBACK_LAUNCHER" />
        </intent-filter>
    </activity>
</application>

<!-- Declare it's a TV app -->
<uses-feature android:name="android.software.leanback" android:required="true" />
<!-- TV has no touchscreen -->
<uses-feature android:name="android.hardware.touchscreen" android:required="false" />
```

---

## 2. TV Material3 vs Mobile Material3

**Use `androidx.tv.material3` instead of `androidx.compose.material3` for TV apps.**

| TV (`androidx.tv.*`) | Mobile (`androidx.compose.*`) | Notes |
|----------------------|------------------------------|-------|
| `androidx.tv:tv-material` | `androidx.compose.material3:material3` | TV variant has focus-aware indications |
| `androidx.tv.material3.Surface` | `androidx.compose.material3.Surface` | TV Surface supports Border, Glow, Scale per state |
| `androidx.tv.material3.Button` | `androidx.compose.material3.Button` | TV Button has focus scale/glow/border |
| `androidx.tv.material3.Card` | `androidx.compose.material3.Card` | TV Card has 5+ variants for media |
| `androidx.tv.material3.MaterialTheme` | `androidx.compose.material3.MaterialTheme` | Separate theming — don't mix |

> **Never mix** mobile `MaterialTheme` with TV `MaterialTheme`. Each library defines its own
> `MaterialTheme` object — using both causes inconsistent colors, typography, and shapes.

---

## 3. Component Catalog

### Surfaces (Building Blocks)

`Surface` is the foundational TV composable — all interactive components build on it.
TV Surface supports per-state customization of four visual indications:

| Indication | Type | Purpose |
|------------|------|---------|
| **Scale** | `Float` | Enlarges element on focus (default: 1.0 → 1.1x) |
| **Border** | `Border` | Draws a border around the element on focus |
| **Glow** | `Glow` | Adds a diffused shadow/glow (API 28+ only) |
| **Shape** | `Shape` | Changes shape on focus/press |

Three Surface variants:

```kotlin
// 1. Non-interactive (display only)
Surface(
    modifier = Modifier.size(200.dp),
    colors = SurfaceDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surface
    ),
    shape = RoundedCornerShape(8.dp)
) {
    Text("Static content")
}

// 2. Clickable (buttons, cards, etc.)
Surface(
    onClick = { /* handle click */ },
    modifier = Modifier.size(200.dp),
    scale = ClickableSurfaceDefaults.scale(
        focusedScale = 1.05f
    ),
    border = ClickableSurfaceDefaults.border(
        focusedBorder = Border(
            border = BorderStroke(2.dp, Color.White),
            shape = RoundedCornerShape(8.dp)
        )
    ),
    glow = ClickableSurfaceDefaults.glow(
        focusedGlow = Glow(
            elevationColor = Color.White.copy(alpha = 0.5f),
            elevation = 8.dp
        )
    )
) {
    Text("Clickable")
}

// 3. Selectable (toggles, radio-like selection)
var selected by remember { mutableStateOf(false) }
Surface(
    selected = selected,
    onClick = { selected = !selected },
    modifier = Modifier.size(200.dp),
    scale = SelectableSurfaceDefaults.scale(
        focusedScale = 1.05f,
        selectedScale = 1.02f
    )
) {
    Text(if (selected) "Selected" else "Unselected")
}
```

### Cards

TV provides five card variants for media content:

| Component | Layout | Use Case |
|-----------|--------|----------|
| `Card` | Basic container with click | Simple content card |
| `ClassicCard` | Image + title + subtitle + description | Standard media card |
| `CompactCard` | Image with overlay text | Space-efficient card |
| `WideClassicCard` | Horizontal image + text | Landscape media card |
| `StandardCardContainer` | Image above + content below | Catalog grid items |
| `WideCardContainer` | Image left + content right | List detail items |

```kotlin
// Basic Card
Card(
    onClick = { /* navigate to detail */ },
    modifier = Modifier.size(width = 180.dp, height = 100.dp)
) {
    AsyncImage(
        model = movie.thumbnailUrl,
        contentDescription = movie.title,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}

// ClassicCard — image + metadata slots
ClassicCard(
    onClick = { /* navigate */ },
    image = {
        AsyncImage(
            model = movie.posterUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 150.dp, height = 200.dp)
        )
    },
    title = { Text(movie.title) },
    subtitle = { Text(movie.year.toString()) },
    description = { Text(movie.description, maxLines = 2) }
)

// StandardCardContainer — layout wrapper (image top, content below)
// Note: as of tv-material 1.0.0 stable, interactionSource is managed internally by the card.
// The imageCard slot is a plain @Composable lambda with no interactionSource parameter.
StandardCardContainer(
    imageCard = {
        Card(
            onClick = { /* navigate */ }
        ) {
            AsyncImage(
                model = movie.thumbnailUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width = 180.dp, height = 100.dp)
            )
        }
    },
    title = { Text(movie.title, maxLines = 1) }
)
```

### Carousel

Displays featured content in an auto-scrolling rotator with D-pad navigation.

```kotlin
@Composable
fun FeaturedCarousel(
    featuredContent: List<Movie>,
    modifier: Modifier = Modifier,
) {
    Carousel(
        itemCount = featuredContent.size,
        modifier = modifier
            .fillMaxWidth()
            .height(376.dp),
        // Optional: remember state to control or observe active item
        // carouselState = rememberCarouselState(),
        // autoScrollDurationMillis = CarouselDefaults.TimeToDisplayItemMillis (5000ms)
    ) { index -> // this: AnimatedContentScope — use Modifier.animateEnterExit() inside here
        val movie = featuredContent[index]
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.backgroundImageUrl,
                contentDescription = movie.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(32.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineLarge
                )
                Text(
                    text = movie.description,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )
                // Animate foreground content entry with AnimatedContentScope
                Button(
                    onClick = { /* play */ },
                    modifier = Modifier
                        .animateEnterExit(
                            enter = fadeIn() + slideInHorizontally(),
                            exit = fadeOut() + slideOutHorizontally()
                        )
                ) {
                    Text("Watch Now")
                }
            }
        }
    }
}
```

Key points:
- `CarouselState` controls which item is shown; use `rememberCarouselState()` to persist
- Auto-scroll pauses automatically when the user interacts via D-pad — there is no public API to pause/resume it programmatically (`ScrollPauseHandle` is internal)
- Content inside the carousel lambda receives an `AnimatedContentScope` — use `Modifier.animateEnterExit()` for slide/fade effects
- Custom slide transitions via `contentTransformStartToEnd` and `contentTransformEndToStart` parameters

### Buttons

```kotlin
// Standard filled button
Button(onClick = { /* action */ }) {
    Text("Play")
}

// Outlined button
OutlinedButton(onClick = { /* action */ }) {
    Text("Add to List")
}

// Icon button
IconButton(onClick = { /* action */ }) {
    Icon(Icons.Default.Search, contentDescription = "Search")
}

// Wide button (full-width with icon + text + subtitle)
// Note: WideButton is available in tv-material 1.1.0-rc01+. Verify availability before use.
WideButton(
    onClick = { /* action */ },
    title = { Text("Continue Watching") },
    subtitle = { Text("Episode 3 — 45 min remaining") },
    icon = { Icon(Icons.Default.PlayArrow, null) }
)
```

### Navigation: Drawer & Tabs

Two patterns for top-level navigation:

**Side navigation with `NavigationDrawer`:**

```kotlin
@Composable
fun TvApp() {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val selectedItem = remember { mutableIntStateOf(0) }
    val items = listOf("Home", "Movies", "Shows", "Settings")

    NavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            items.forEachIndexed { index, label ->
                NavigationDrawerItem(
                    selected = selectedItem.intValue == index,
                    onClick = { selectedItem.intValue = index },
                    leadingContent = {
                        Icon(
                            imageVector = when (index) {
                                0 -> Icons.Default.Home
                                1 -> Icons.Default.Movie
                                2 -> Icons.Default.Tv
                                else -> Icons.Default.Settings
                            },
                            contentDescription = null
                        )
                    }
                ) {
                    Text(label)
                }
            }
        }
    ) {
        // Main content area
        when (selectedItem.intValue) {
            0 -> HomeScreen()
            1 -> MoviesScreen()
            2 -> ShowsScreen()
            3 -> SettingsScreen()
        }
    }
}
```

- `NavigationDrawer` — always visible at screen edge, collapses when focus moves to content
- `ModalNavigationDrawer` — overlays content with a scrim, use for secondary nav
- `NavigationDrawerScope.doesNavigationDrawerHaveFocus` — check if drawer has focus to expand/collapse items

**Top navigation with `TabRow`:**

```kotlin
@Composable
fun TopNavigation() {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home", "Movies", "Shows")

    Column {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onFocus = { selectedTab = index }, // TV tabs activate on focus
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }
        // Content below tabs
        when (selectedTab) {
            0 -> HomeScreen()
            1 -> MoviesScreen()
            2 -> ShowsScreen()
        }
    }
}
```

> **TV-specific behavior**: Tabs typically activate on focus (not click). Use the `onFocus`
> callback to switch content as the user D-pads through tabs.

### Chips

```kotlin
// Assist chip
AssistChip(onClick = { /* action */ }) { Text("Genre: Action") }

// Filter chip (toggleable)
var selected by remember { mutableStateOf(false) }
FilterChip(
    selected = selected,
    onClick = { selected = !selected }
) { Text("4K") }

// Input chip (removable)
InputChip(
    selected = true,
    onClick = { /* remove filter */ }
) { Text("English") }

// Suggestion chip
SuggestionChip(onClick = { /* apply */ }) { Text("Recommended") }
```

### ListItem & DenseListItem

```kotlin
// Standard list item
ListItem(
    selected = false,
    onClick = { /* navigate to setting */ },
    headlineContent = { Text("Audio Language") },
    supportingContent = { Text("English") },
    leadingContent = {
        Icon(Icons.Default.Language, contentDescription = null)
    },
    trailingContent = {
        Icon(Icons.Default.ChevronRight, contentDescription = null)
    }
)

// Dense list item — reduced height
DenseListItem(
    selected = false,
    onClick = { /* action */ },
    headlineContent = { Text("Subtitle Size") }
)
```

### Form Controls

```kotlin
// Checkbox
var checked by remember { mutableStateOf(false) }
Checkbox(checked = checked, onCheckedChange = { checked = it })

// RadioButton
RadioButton(selected = true, onClick = { /* select */ })

// Switch
var enabled by remember { mutableStateOf(true) }
Switch(checked = enabled, onCheckedChange = { enabled = it })
```

---

## 4. Focus & D-pad Navigation

TV UIs are focus-driven — there is no touch. Every interactive element must be focusable and
navigable via the D-pad (up/down/left/right + select + back).

### Focus Indications

TV Material provides four built-in focus indications — configured per component via `Defaults.*` objects:

| Indication | API | When to Use |
|------------|-----|-------------|
| Scale | `scale(focusedScale = 1.05f)` | Most components — clear visual feedback |
| Border | `border(focusedBorder = Border(...))` | Buttons, list items — precise outline |
| Glow | `glow(focusedGlow = Glow(...))` | Cards, media tiles — elevated look |
| Color | `colors(focusedContainerColor = ...)` | Chips, tabs — background color change |

### Focus Management with Modifiers

```kotlin
// Request initial focus
val focusRequester = remember { FocusRequester() }

LazyRow {
    items(movies) { movie ->
        MovieCard(
            modifier = if (movie == movies.first())
                Modifier.focusRequester(focusRequester)
            else Modifier
        )
    }
}

LaunchedEffect(Unit) {
    focusRequester.requestFocus()
}

// Customize D-pad directional focus traversal
// Use up/down/left/right — NOT enter/exit (those control FocusGroup enter/exit, not D-pad directions)
Modifier.focusProperties {
    right = customFocusRequester    // override where right D-pad goes
    left = FocusRequester.Cancel    // block left navigation
    // up / down also available
}

// Focus restoration — remember which child had focus
Modifier.focusRestorer()
```

### D-pad Input Handling

```kotlin
Modifier.onKeyEvent { keyEvent ->
    when {
        keyEvent.key == Key.DirectionCenter && keyEvent.type == KeyEventType.KeyUp -> {
            // Select/enter pressed
            true
        }
        keyEvent.key == Key.Back && keyEvent.type == KeyEventType.KeyUp -> {
            // Back button pressed
            true
        }
        else -> false
    }
}

// Long-press support is built into TV Surface components
Surface(
    onClick = { /* regular click */ },
    onLongClick = { /* long-press menu */ }
) { /* content */ }
```

### Back Button

```kotlin
// Use BackHandler from activity-compose
BackHandler(enabled = isDetailVisible) {
    // Return to previous screen
    isDetailVisible = false
}
```

> **Pattern**: Back button should always navigate to the previous destination. Pressing back
> from the start destination should exit the app. Never gate exit with a confirmation dialog.

---

## 5. Lists & Scrolling

### Standard Lazy Layouts (Recommended)

Since Compose Foundation 1.7.0, standard `LazyRow`/`LazyColumn`/`LazyGrid` support
focus-driven scrolling out of the box. **The deprecated `TvLazyRow`/`TvLazyColumn`
from `tv-foundation` should no longer be used.**

```kotlin
@Composable
fun CatalogBrowser(
    featuredContent: List<Movie>,
    sections: List<Section>,
    modifier: Modifier = Modifier,
    onMovieSelected: (Movie) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Featured carousel at top
        item {
            FeaturedCarousel(featuredContent)
        }
        // Content sections
        items(sections) { section ->
            Text(
                text = section.title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(
                    items = section.movies,
                    key = { it.id }
                ) { movie ->
                    MovieCard(
                        movie = movie,
                        onClick = { onMovieSelected(movie) }
                    )
                }
            }
        }
    }
}
```

### Custom Scroll Positioning with BringIntoViewSpec

Override default scroll behavior to keep focused items at a consistent position (e.g., 30% from
the left edge instead of just scrolling to make the item visible):

```kotlin
// Both the composable function AND the CompositionLocalProvider call inside require
// @OptIn(ExperimentalFoundationApi::class) — annotate the whole function.
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PositionFocusedItemInLazyLayout(
    parentFraction: Float = 0.3f,
    childFraction: Float = 0f,
    content: @Composable () -> Unit,
) {
    val bringIntoViewSpec = remember(parentFraction, childFraction) {
        object : BringIntoViewSpec {
            override fun calculateScrollDistance(
                offset: Float,
                size: Float,
                containerSize: Float
            ): Float {
                val targetForLeadingEdge =
                    parentFraction * containerSize - (childFraction * size)
                val adjustedTarget = if (size <= containerSize &&
                    (containerSize - targetForLeadingEdge) < size) {
                    containerSize - size
                } else {
                    targetForLeadingEdge
                }
                return offset - adjustedTarget
            }
        }
    }
    CompositionLocalProvider(
        LocalBringIntoViewSpec provides bringIntoViewSpec,
        content = content,
    )
}

// Usage:
PositionFocusedItemInLazyLayout(parentFraction = 0.3f) {
    LazyColumn {
        items(sections) { section ->
            LazyRow { /* items */ }
        }
    }
}
```

### Migration from TV Foundation Lazy Layouts

| Deprecated (`tv-foundation`) | Replacement (`compose.foundation`) |
|-------------------------------|-------------------------------------|
| `TvLazyRow` | `LazyRow` |
| `TvLazyColumn` | `LazyColumn` |
| `TvLazyHorizontalGrid` | `LazyHorizontalGrid` |
| `TvLazyVerticalGrid` | `LazyVerticalGrid` |
| `pivotOffsets` | `BringIntoViewSpec` via `LocalBringIntoViewSpec` |

Requires `compose.foundation` 1.7.0+. Update imports and remove the `Tv` prefix.

---

## 6. Theming

TV `MaterialTheme` mirrors the mobile Material3 theming system but is defined in
`androidx.tv.material3`:

```kotlin
@Composable
fun TvAppTheme(content: @Composable () -> Unit) {
    // TV apps typically use dark themes for the living room
    // Import darkColorScheme from androidx.tv.material3 — NOT androidx.compose.material3
    val colorScheme = darkColorScheme(
        primary = Color(0xFFBB86FC),
        onPrimary = Color.Black,
        secondary = Color(0xFF03DAC5),
        surface = Color(0xFF121212),
        onSurface = Color.White,
        background = Color(0xFF000000),
        onBackground = Color.White,
    )

    val typography = Typography(
        // TV typography should be larger for 10-foot viewing
        headlineLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp,
            lineHeight = 40.sp
        ),
        bodyLarge = TextStyle(
            fontSize = 18.sp,
            lineHeight = 24.sp
        ),
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        shapes = Shapes(), // default TV shapes
        content = content
    )
}
```

Key differences from mobile theming:
- Import `MaterialTheme` **and** `darkColorScheme`/`lightColorScheme` from `androidx.tv.material3`, not `androidx.compose.material3`
- TV apps almost always use `darkColorScheme()` — living rooms are dark environments
- Typography sizes should be larger for 10-foot viewing distance (16sp minimum body text)
- `Shapes` from `androidx.tv.material3` — same concept, TV-specific defaults

### CompositionLocals

| Local | Purpose |
|-------|---------|
| `LocalContentColor` | Content color for text/icons at current position |
| `LocalTextStyle` | Default text style (set via `ProvideTextStyle`) |

---

## 7. Building an Immersive List

`ImmersiveList` was removed in tv-material 1.0.0-beta01. Build it manually by changing
the background based on the focused item:

```kotlin
@Composable
fun ImmersiveMovieRow(
    movies: List<Movie>,
    modifier: Modifier = Modifier,
) {
    var focusedMovie by remember { mutableStateOf(movies.firstOrNull()) }

    Box(modifier = modifier.fillMaxWidth().height(400.dp)) {
        // Background — changes based on focused item
        AnimatedContent(
            targetState = focusedMovie,
            transitionSpec = {
                fadeIn(tween(500)) togetherWith fadeOut(tween(500))
            },
            label = "immersive-background"
        ) { movie ->
            movie?.let {
                AsyncImage(
                    model = it.backgroundImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Scrim gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )

        // Foreground row
        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(32.dp)
        ) {
            focusedMovie?.let { movie ->
                Text(movie.title, style = MaterialTheme.typography.headlineLarge)
                Text(movie.description, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(movies, key = { it.id }) { movie ->
                    // Wrap Card in a Box — onFocusChanged on an interactive TV Surface
                    // may not fire reliably. Apply it to a non-interactive wrapper instead.
                    Box(
                        modifier = Modifier
                            .size(width = 160.dp, height = 90.dp)
                            .onFocusChanged { state ->
                                if (state.hasFocus) focusedMovie = movie
                            }
                    ) {
                        Card(
                            onClick = { /* navigate to detail */ },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            AsyncImage(
                                model = movie.thumbnailUrl,
                                contentDescription = movie.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
            }
        }
    }
}
```

---

## 8. Details Screen Pattern

```kotlin
@Composable
fun DetailsScreen(
    movie: Movie,
    modifier: Modifier = Modifier,
    onStartPlayback: (Movie) -> Unit = {},
) {
    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            model = movie.backgroundImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Scrim for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.9f), Color.Transparent)
                    )
                )
        )
        Column(
            modifier = Modifier
                .padding(48.dp)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(movie.title, style = MaterialTheme.typography.displaySmall)
            Spacer(Modifier.height(8.dp))
            Text(movie.description, style = MaterialTheme.typography.bodyLarge, maxLines = 4)
            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { onStartPlayback(movie) }) {
                    Text("Play")
                }
                OutlinedButton(onClick = { /* add to list */ }) {
                    Text("My List")
                }
            }
        }
    }
}
```

---

## 9. TVProvider Integration

`androidx.tvprovider` lets your app publish channels and programs to the Android TV home
screen. This is not Compose UI — it's a content provider API used alongside your Compose app.

```kotlin
// IMPORTANT: publishChannel / publishPreviewProgram perform I/O — call from a background
// coroutine (Dispatchers.IO) or a WorkManager task, never from the main thread.
// Example using a coroutine:
viewModelScope.launch(Dispatchers.IO) {
    val channel = PreviewChannel.Builder()
        .setDisplayName("Continue Watching")
        .setAppLinkIntentUri(Uri.parse("myapp://home"))
        .build()

    val channelId = PreviewChannelHelper(context).publishChannel(channel)

    val program = PreviewProgram.Builder()
        .setChannelId(channelId)
        .setTitle("Movie Title")
        .setDescription("Episode 3")
        .setPosterArtUri(Uri.parse("https://example.com/poster.jpg"))
        .setIntentUri(Uri.parse("myapp://movie/123"))
        .setType(TvContractCompat.PreviewPrograms.TYPE_MOVIE)
        .build()

    PreviewChannelHelper(context).publishPreviewProgram(program)
}
```

---

## 10. Anti-Patterns & Gotchas

### Don't mix mobile and TV Material

```kotlin
// WRONG — mixing material3 themes
import androidx.compose.material3.MaterialTheme as MobileMaterialTheme
import androidx.tv.material3.MaterialTheme as TvMaterialTheme

// This WILL cause inconsistent colors, typography, and shapes.
// Pick one: use tv.material3 for TV apps.
```

### Don't use Leanback with Compose

Leanback (`androidx.leanback`) is the legacy View-based TV framework. Do not mix it with
Compose for TV. Use `androidx.tv:tv-material` exclusively for new projects.

### Glow disabled below API 28

Glow indication (`Glow(...)`) is silently disabled on devices running Android 8.1 (API 27)
and below. Use Border + Scale as primary indications for broad compatibility.

### Focus traps

Ensure every screen has a clear focus path. Common traps:
- Empty `LazyRow`/`LazyColumn` with no items — focus search crashes or skips over them
- Overlapping focusable elements — D-pad direction becomes unpredictable
- Missing `focusRestorer()` — returning to a list loses the user's position

### TV lazy layouts are deprecated

`TvLazyRow`, `TvLazyColumn`, `TvLazyVerticalGrid`, `TvLazyHorizontalGrid` from
`tv-foundation` are deprecated since alpha11. Migrate to standard Compose Foundation
lazy layouts (requires Foundation 1.7.0+).

### Don't show virtual back button

TV uses the hardware back button on the remote. Never render a back button in your UI —
it wastes screen real estate and confuses the navigation pattern.

### Typography too small

Mobile-sized text (14sp body) is unreadable at TV viewing distances (10 feet). Use 16sp
minimum for body text, 24sp+ for headings.

### Missing content descriptions

TV apps must be accessible via TalkBack. Every interactive element needs a
`contentDescription` — especially image-only cards and icon buttons.

### ExoPlayer rendering on Surface

On lower API versions, `CompositingStrategy.Offscreen` on Surface composables can prevent
ExoPlayer from rendering video. This was fixed in tv-material 1.0.1 by moving the
compositing strategy from Surface to Text.

---

## 11. Sample Apps & Resources

| Resource | URL |
|----------|-----|
| JetStream (full app sample) | `github.com/android/tv-samples/tree/main/JetStreamCompose` |
| TV Material Catalog | `github.com/android/tv-samples/tree/main/TvMaterialCatalog` |
| Compose for TV codelab | `developer.android.com/codelabs/compose-for-tv-introduction` |
| TV design guides | `developer.android.com/design/ui/tv/guides/components` |
| API reference | `developer.android.com/reference/kotlin/androidx/tv/material3/package-summary` |
| ImmersiveList sample (manual) | `cs.android.com/androidx/.../tv/samples/.../ImmersiveListSamples.kt` |
| TV samples repo | `github.com/android/tv-samples` |
