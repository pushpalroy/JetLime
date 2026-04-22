# Design-to-Compose Translation Reference

Translating visual designs (Figma mockups, screenshots, wireframes) into production Compose code. This guide provides a systematic decomposition algorithm, property mapping tables, and patterns that produce clean, theme-aware, accessible composables on the first pass.

---

## 1. Composable Decomposition Algorithm

A divide-and-conquer approach for breaking any visual design into composable functions. Work top-down, outside-in.

### Step 1: Identify Root Layout Structure

Look at the full screen first. What is the outermost structural pattern?

| Visual Pattern | Compose Root |
|---|---|
| Top bar + content + bottom bar | `Scaffold` |
| Scrollable vertical content | `Column` + `verticalScroll()` or `LazyColumn` |
| Tabbed sections | `Scaffold` + `TabRow` + `HorizontalPager` |
| Full-bleed background with overlays | `Box` |
| Side drawer + content | `ModalNavigationDrawer` + `Scaffold` |
| Bottom sheet over content | `ModalBottomSheet` or `Scaffold` + `BottomSheetScaffold` |

### Step 2: Decompose into Visual Sections (Top-Down)

Scan the design from top to bottom. Draw horizontal lines between visually distinct sections. Each section becomes a composable or a block within the parent layout.

```
+---------------------------+
|  Top App Bar              |  -> TopAppBar()
+---------------------------+
|  Hero Image               |  -> HeroSection()
+---------------------------+
|  Title + Subtitle         |  -> HeaderSection()
+---------------------------+
|  Horizontal card list     |  -> FeaturedCardsRow()
+---------------------------+
|  Vertical item list       |  -> ItemList()
+---------------------------+
|  Bottom navigation        |  -> NavigationBar()
+---------------------------+
```

**Do:** Name sections by their purpose (`FeaturedCardsRow`), not their layout (`HorizontalScrollRow`).

**Don't:** Create a composable for every Figma frame. Flatten where possible.

### Step 3: For Each Section, Identify Layout Type

```
Is content stacked vertically?
  └─ Yes → Column
       └─ Is list dynamic/long? → LazyColumn

Is content arranged horizontally?
  └─ Yes → Row
       └─ Does it scroll? → LazyRow
       └─ Does it wrap to next line? → FlowRow

Is content overlapping/layered?
  └─ Yes → Box

Is it a grid?
  └─ Fixed columns → LazyVerticalGrid
  └─ Fixed item size → LazyVerticalStaggeredGrid
  └─ Wrapping chips/tags → FlowRow
```

**Decision tree for layout selection:**

```
                    ┌─ Overlapping layers? ──→ Box
                    │
  Visual section ───┼─ Single axis? ──→ Vertical? ──→ Column / LazyColumn
                    │                  └─ Horizontal? ──→ Row / LazyRow
                    │
                    └─ Grid / wrap? ──→ Fixed columns? ──→ LazyVerticalGrid
                                       └─ Flowing tags? ──→ FlowRow
```

### Step 4: Extract Visual Properties

For each element, read these from the design:

- **Colors** — map to `MaterialTheme.colorScheme.*` tokens, not hex values
- **Typography** — map to `MaterialTheme.typography.*` text styles
- **Spacing** — padding and gaps in dp, map to theme spacing tokens
- **Elevation** — shadow depth, map to `tonalElevation` or `shadowElevation`
- **Corner radius** — map to `MaterialTheme.shapes.*`

### Step 5: Identify Interactive Elements and Map to M3 Components

| Visual Element | Compose M3 Component |
|---|---|
| Rounded rectangle with text + click | `Button` / `OutlinedButton` / `TextButton` |
| Card with image, title, subtitle | `Card` / `ElevatedCard` / `OutlinedCard` |
| Text input field | `TextField` / `OutlinedTextField` |
| Toggle switch | `Switch` |
| Checkbox | `Checkbox` / `TriStateCheckbox` |
| Chips / tags | `FilterChip` / `AssistChip` / `InputChip` / `SuggestionChip` |
| Floating action button | `FloatingActionButton` / `ExtendedFloatingActionButton` |
| Bottom navigation | `NavigationBar` + `NavigationBarItem` |
| Side navigation | `NavigationRail` / `NavigationDrawer` |
| Top bar | `TopAppBar` / `CenterAlignedTopAppBar` |
| Dialog / modal | `AlertDialog` / `BasicAlertDialog` |
| Progress indicator | `CircularProgressIndicator` / `LinearProgressIndicator` |
| Divider line | `HorizontalDivider` |
| Image with rounded corners | `Image` + `Modifier.clip()` |
| Dropdown menu | `ExposedDropdownMenuBox` |
| Slider | `Slider` / `RangeSlider` |

---

## 2. Figma-to-Compose Property Mapping Tables

### Layout Containers

| Figma Concept | Compose Equivalent |
|---|---|
| Frame (no auto-layout) | `Box` |
| Auto-layout Vertical | `Column` |
| Auto-layout Horizontal | `Row` |
| Auto-layout Wrap | `FlowRow` / `FlowColumn` |
| Grid (fixed columns) | `LazyVerticalGrid(columns = GridCells.Fixed(n))` |
| Absolute-positioned child | `Box` + `Modifier.offset(x, y)` or `Modifier.align()` |
| Component with variants | Composable function with parameters |
| Component instance | Function call site |
| Section / Group (organizational) | No composable needed; flatten into parent |

### Sizing Modes

| Figma Sizing | Compose Modifier |
|---|---|
| Fixed width/height | `Modifier.size(w.dp, h.dp)` or `.width(w.dp).height(h.dp)` |
| Hug contents | Default behavior (wrap content) -- no modifier needed |
| Fill container (horizontal) | `Modifier.fillMaxWidth()` |
| Fill container (vertical) | `Modifier.fillMaxHeight()` |
| Fill container (both) | `Modifier.fillMaxSize()` |
| Fill with min width | `Modifier.fillMaxWidth().widthIn(min = minW.dp)` |
| Fill with max width | `Modifier.fillMaxWidth().widthIn(max = maxW.dp)` |
| Fill with min/max height | `Modifier.fillMaxHeight().heightIn(min = ..., max = ...)` |
| Aspect ratio constraint | `Modifier.aspectRatio(ratio)` |

### Spacing Model

**Key principle: the parent owns spacing.**

| Figma Spacing | Compose Equivalent |
|---|---|
| Padding (all sides) | `Modifier.padding(all.dp)` on the container |
| Padding (per side) | `Modifier.padding(start = ..., top = ..., end = ..., bottom = ...)` |
| Gap between children (vertical auto-layout) | `Column(verticalArrangement = Arrangement.spacedBy(gap.dp))` |
| Gap between children (horizontal auto-layout) | `Row(horizontalArrangement = Arrangement.spacedBy(gap.dp))` |
| Space between (distribute) | `Arrangement.SpaceBetween` |
| Space around | `Arrangement.SpaceAround` |

**Do:** Use `start`/`end` instead of `left`/`right` for RTL language support.

```kotlin
// Correct: RTL-aware
Modifier.padding(start = 16.dp, end = 8.dp)

// Wrong: breaks in RTL locales
Modifier.padding(left = 16.dp, right = 8.dp)  // Avoid
```

**Do:** Use `Arrangement.spacedBy()` for uniform gaps. Avoid inserting `Spacer` between every child.

```kotlin
// Do: clean and uniform
Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
    Text("First")
    Text("Second")
    Text("Third")
}

// Don't: manual spacers everywhere
Column {
    Text("First")
    Spacer(Modifier.height(12.dp))
    Text("Second")
    Spacer(Modifier.height(12.dp))
    Text("Third")
}
```

### Shadow Mapping (Compose 1.9+)

Compose Foundation 1.9 introduced `dropShadow()` and `innerShadow()` as modifier extensions, replacing the legacy `shadow()` modifier for fine-grained control.

```kotlin
// Drop shadow: place BEFORE background in the modifier chain
Box(
    Modifier
        .dropShadow(
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.15f),
            blur = 8.dp,
            offsetX = 0.dp,
            offsetY = 4.dp,
            spread = 0.dp
        )
        .background(Color.White, RoundedCornerShape(12.dp))
        .padding(16.dp)
)

// Inner shadow: place AFTER background in the modifier chain
Box(
    Modifier
        .background(Color.White, RoundedCornerShape(12.dp))
        .innerShadow(
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.1f),
            blur = 4.dp,
            offsetX = 0.dp,
            offsetY = 2.dp,
            spread = 0.dp
        )
        .padding(16.dp)
)
```

**Figma shadow fields to Compose mapping:**

| Figma Shadow Property | Compose Parameter |
|---|---|
| X offset | `offsetX` |
| Y offset | `offsetY` |
| Blur | `blur` |
| Spread | `spread` |
| Color + opacity | `color = Color(hex).copy(alpha = opacity)` |
| Drop Shadow type | `Modifier.dropShadow()` |
| Inner Shadow type | `Modifier.innerShadow()` |

**Legacy approach** (pre-1.9, still valid for simple elevation shadows):

```kotlin
// Simple elevation shadow
Box(
    Modifier
        .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
        .background(Color.White)
)
```

### Gradient Mapping

| Figma Gradient Type | Compose Brush |
|---|---|
| Linear gradient | `Brush.linearGradient(colors, start, end)` |
| Radial gradient | `Brush.radialGradient(colors, center, radius)` |
| Angular/sweep gradient | `Brush.sweepGradient(colors, center)` |

Figma uses normalized coordinates (0.0 to 1.0). Convert to pixel `Offset` values:

```kotlin
// Figma linear gradient: start (0, 0) to end (1, 1), 45-degree diagonal
Box(
    Modifier
        .fillMaxWidth()
        .height(200.dp)
        .background(
            Brush.linearGradient(
                colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC6)),
                start = Offset.Zero,
                end = Offset.Infinite  // diagonal
            )
        )
)

// For precise Figma coordinates, use onSizeChanged or BoxWithConstraints:
BoxWithConstraints(
    Modifier.background(
        Brush.linearGradient(
            colors = listOf(Color(0xFF6200EE), Color(0xFF03DAC6)),
            start = Offset(0f, 0f),
            end = Offset(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        )
    )
) {
    // content
}

// Radial gradient
Box(
    Modifier
        .size(200.dp)
        .background(
            Brush.radialGradient(
                colors = listOf(Color.White, Color.Blue),
                center = Offset(100f, 100f),  // center of 200dp box (approx)
                radius = 150f
            )
        )
)
```

### Corner Radius

| Figma Corner Radius | Compose Shape |
|---|---|
| All corners equal | `RoundedCornerShape(radius.dp)` |
| Per-corner values | `RoundedCornerShape(topStart = ..., topEnd = ..., bottomEnd = ..., bottomStart = ...)` |
| Fully rounded (pill) | `RoundedCornerShape(50)` or `CircleShape` |
| No radius | `RectangleShape` |
| Cut corners | `CutCornerShape(size.dp)` |

### Borders

```kotlin
// Solid border
Modifier.border(width.dp, Color(0xFFCCCCCC), RoundedCornerShape(8.dp))

// Gradient border
Modifier.border(
    width = 2.dp,
    brush = Brush.linearGradient(listOf(Color.Red, Color.Blue)),
    shape = RoundedCornerShape(8.dp)
)
```

### Opacity

| Figma Property | Compose Equivalent |
|---|---|
| Layer opacity | `Modifier.alpha(0.5f)` |
| Fill color opacity | `Color(0xFF000000).copy(alpha = 0.5f)` |
| Blend mode | `Modifier.graphicsLayer { compositingStrategy = ... }` |

### Image Fill Modes

| Figma Image Mode | Compose ContentScale |
|---|---|
| Fill (cover, may crop) | `ContentScale.Crop` |
| Fit (contain, no crop) | `ContentScale.Fit` |
| Stretch (distort) | `ContentScale.FillBounds` |
| Tile | Custom `DrawScope` tiling |
| Fill width | `ContentScale.FillWidth` |
| Fill height | `ContentScale.FillHeight` |

```kotlin
Image(
    painter = painterResource(R.drawable.hero),
    contentDescription = "Hero image",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(12.dp))
)
```

---

## 3. Design Token to MaterialTheme Mapping

| Design System Token | MaterialTheme API |
|---|---|
| Color styles (Primary, Surface, Error...) | `MaterialTheme.colorScheme` |
| Text styles (Heading, Body, Caption...) | `MaterialTheme.typography` |
| Corner radius (Small, Medium, Large...) | `MaterialTheme.shapes` |
| Spacing scale (4, 8, 16, 24...) | Custom `CompositionLocal` (see below) |
| Elevation scale | Custom `CompositionLocal` (see below) |

### Custom Spacing CompositionLocal

Material 3 does not ship a spacing scale. Define one that mirrors your design system:

```kotlin
@Immutable
data class AppSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp
)

val LocalAppSpacing = staticCompositionLocalOf { AppSpacing() }

// Provide in your theme wrapper
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val spacing = AppSpacing()
    CompositionLocalProvider(LocalAppSpacing provides spacing) {
        MaterialTheme(
            colorScheme = lightColorScheme(),
            typography = Typography(),
            shapes = Shapes()
        ) {
            content()
        }
    }
}

// Usage at call site
@Composable
fun ProfileCard() {
    val spacing = LocalAppSpacing.current
    Card(
        modifier = Modifier.padding(spacing.md)
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm)
        ) {
            Text("Name", style = MaterialTheme.typography.titleMedium)
            Text("Bio", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
```

### Custom Elevation CompositionLocal

```kotlin
@Immutable
data class AppElevation(
    val none: Dp = 0.dp,
    val xs: Dp = 1.dp,
    val sm: Dp = 2.dp,
    val md: Dp = 4.dp,
    val lg: Dp = 8.dp,
    val xl: Dp = 16.dp
)

val LocalAppElevation = staticCompositionLocalOf { AppElevation() }
```

### Mapping Figma Text Styles to Typography

```kotlin
// Figma design system:        Compose Typography:
// Heading/H1  36sp Bold    → displaySmall or headlineLarge
// Heading/H2  28sp Bold    → headlineMedium
// Heading/H3  22sp SemiBold→ titleLarge
// Body/Large  16sp Regular → bodyLarge
// Body/Small  14sp Regular → bodyMedium
// Caption     12sp Regular → bodySmall or labelMedium
// Button      14sp Medium  → labelLarge

val AppTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily = yourFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    // ... map each Figma text style
)
```

---

## 4. Modifier Ordering Rules

**Canonical principle:** outer to inner = layout/sizing first, then decoration, then interaction.

Modifiers are applied left-to-right in the chain. Each modifier wraps everything that follows it. Think of it as layers from outside in.

### Correct Ordering Patterns

```kotlin
// Pattern 1: Card-like surface
Modifier
    .fillMaxWidth()                              // 1. Layout sizing
    .padding(horizontal = 16.dp, vertical = 8.dp) // 2. External margin (space from siblings)
    .dropShadow(                                  // 3. Shadow (before background)
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(alpha = 0.1f),
        blur = 8.dp, offsetY = 4.dp
    )
    .background(Color.White, RoundedCornerShape(12.dp)) // 4. Background fill
    .clip(RoundedCornerShape(12.dp))              // 5. Clip content to shape
    .clickable { }                                // 6. Interaction (inside clip for ripple bounds)
    .padding(16.dp)                               // 7. Internal padding (content inset)

// Pattern 2: Clickable with large touch target
Modifier
    .fillMaxWidth()
    .clickable { }           // Clickable AFTER padding = larger touch target
    .padding(16.dp)          // Internal content padding

// Pattern 3: Background extends to edges, padding inside
Modifier
    .fillMaxWidth()
    .background(MaterialTheme.colorScheme.surface)
    .padding(16.dp)
```

### Common Mistakes

```kotlin
// Wrong: padding before fillMaxWidth clips the fill area
Modifier
    .padding(16.dp)
    .fillMaxWidth()  // Fills remaining width AFTER padding is applied

// Correct: fillMaxWidth first, then pad inward
Modifier
    .fillMaxWidth()
    .padding(16.dp)

// Wrong: clickable before padding = small touch target
Modifier
    .clickable { }
    .padding(16.dp)   // Padding is outside the clickable area

// Correct: clickable after padding = padding area is clickable too
Modifier
    .padding(16.dp)
    .clickable { }    // Entire padded region responds to clicks

// Wrong: shadow after background (invisible or clipped)
Modifier
    .background(Color.White, RoundedCornerShape(12.dp))
    .dropShadow(...)  // Shadow drawn inside the background layer

// Correct: shadow before background
Modifier
    .dropShadow(...)
    .background(Color.White, RoundedCornerShape(12.dp))
```

---

## 5. Semantic vs Literal Translation

Figma designs express visual output. Compose code should express semantics. Always prefer Material 3 components over manually reconstructing their appearance with `Box` + modifiers.

### Anti-pattern: Literal Translation

```kotlin
// Figma shows a card: rounded rect, shadow, image, title, subtitle
// Literal translation -- DON'T
Box(
    Modifier
        .shadow(4.dp, RoundedCornerShape(12.dp))
        .background(Color.White, RoundedCornerShape(12.dp))
        .clip(RoundedCornerShape(12.dp))
) {
    Column(Modifier.padding(16.dp)) {
        Image(painter = painterResource(R.drawable.photo), contentDescription = null)
        Text("Title", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("Subtitle", fontSize = 14.sp, color = Color(0xFF666666))
    }
}
```

### Correct: Semantic Translation

```kotlin
// Semantic translation -- DO
ElevatedCard(
    modifier = Modifier.fillMaxWidth(),
    shape = MaterialTheme.shapes.medium
) {
    Column {
        Image(
            painter = painterResource(R.drawable.photo),
            contentDescription = "Photo description",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        )
        Column(Modifier.padding(16.dp)) {
            Text("Title", style = MaterialTheme.typography.titleMedium)
            Text(
                "Subtitle",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
```

### Why This Matters

| Concern | Literal Box+Modifiers | M3 Component |
|---|---|---|
| Dark theme | Breaks (hardcoded colors) | Automatic |
| Elevation overlay | Missing | Built-in tonal elevation |
| Ripple / feedback | Must add manually | Built-in |
| Accessibility | Must add semantics manually | Roles + descriptions built-in |
| Dynamic color (Material You) | Does not respond | Automatic |
| State handling (disabled, focused) | Manual | Built-in styling per state |

**Rule:** If a Material 3 component exists for the visual pattern, use it. Only build custom layouts for genuinely novel UI elements.

### Quick Mapping: Visual Pattern to M3 Component

| "It looks like a..." | Use |
|---|---|
| Rounded card with shadow | `ElevatedCard` |
| Outlined card | `OutlinedCard` |
| Pill-shaped button | `Button(shape = CircleShape)` |
| Icon + label row | `ListItem` |
| Search bar | `SearchBar` / `DockedSearchBar` |
| Segmented control | `SegmentedButton` (M3 1.2+) |
| Banner notification | `Snackbar` |
| Full-width separator | `HorizontalDivider` |
| Pull-to-refresh | `PullToRefreshBox` |

---

## 6. Anti-Patterns

### Over-nesting Layouts

Figma designs often have deep frame hierarchies for organizational reasons. Do not mirror this nesting in Compose -- flatten aggressively.

```kotlin
// Anti-pattern: mirroring Figma's 5-level nesting
Box {
    Column {
        Row {
            Box {
                Column {
                    Text("Title")
                    Text("Subtitle")
                }
            }
        }
    }
}

// Correct: flatten to what layout actually requires
Column {
    Text("Title", style = MaterialTheme.typography.titleMedium)
    Text("Subtitle", style = MaterialTheme.typography.bodyMedium)
}
```

Deep nesting increases measure/layout passes. Each layout node is a measure cost. Flatten to the minimum tree depth that achieves the visual result.

### Hardcoded Values vs Theme Tokens

```kotlin
// Anti-pattern: hardcoded hex colors and font sizes
Text(
    text = "Hello",
    color = Color(0xFF1A1A1A),
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium
)

Box(Modifier.background(Color(0xFFF5F5F5)))

// Correct: theme tokens
Text(
    text = "Hello",
    style = MaterialTheme.typography.bodyLarge,
    color = MaterialTheme.colorScheme.onSurface
)

Box(Modifier.background(MaterialTheme.colorScheme.surfaceVariant))
```

Hardcoded values break dark theme, dynamic color, and design system updates. The only acceptable hardcoded color is inside your theme definition files.

### Ignoring Accessibility

```kotlin
// Anti-pattern: no content description, tiny touch target
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = null,  // Screen readers skip this
    modifier = Modifier
        .size(20.dp)            // Below 48dp minimum touch target
        .clickable { onFavorite() }
)

// Correct: accessible
IconButton(onClick = onFavorite) {  // IconButton enforces 48dp minimum
    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = "Add to favorites"
    )
}
```

**Accessibility checklist for design translation:**

- All interactive elements have minimum 48dp touch targets (use `IconButton`, `TextButton`, or `Modifier.sizeIn(minWidth = 48.dp, minHeight = 48.dp)`)
- All images and icons have meaningful `contentDescription` (or `null` if purely decorative, paired with `Modifier.semantics { }` as needed)
- Color contrast ratios meet WCAG AA (4.5:1 for text, 3:1 for large text)
- Interactive states are visually distinguishable (not just color change)

### Designing for One Screen Width Only

```kotlin
// Anti-pattern: fixed widths that break on tablets
Row(Modifier.width(360.dp)) {
    Column(Modifier.width(180.dp)) { /* left panel */ }
    Column(Modifier.width(180.dp)) { /* right panel */ }
}

// Correct: responsive with WindowSizeClass
val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

when (windowSizeClass.windowWidthSizeClass) {
    WindowWidthSizeClass.COMPACT -> {
        // Single column layout (phones)
        Column { /* all content stacked */ }
    }
    WindowWidthSizeClass.MEDIUM -> {
        // Two-pane list-detail (small tablets, foldables)
        ListDetailPaneScaffold(/* ... */)
    }
    WindowWidthSizeClass.EXPANDED -> {
        // Navigation rail + content (large tablets, desktop)
        Row {
            NavigationRail { /* ... */ }
            Content(Modifier.weight(1f))
        }
    }
}
```

**Do:** Use `fillMaxWidth()`, `weight()`, and `WindowSizeClass` for responsive layouts.

**Don't:** Use fixed pixel/dp widths for containers that should adapt.

### Forgetting Scroll Behavior

```kotlin
// Anti-pattern: content overflows without scrolling
Column(Modifier.fillMaxSize()) {
    // 20 items that exceed screen height -- bottom items invisible
    repeat(20) { Text("Item $it") }
}

// Correct: add scrolling
Column(
    Modifier
        .fillMaxSize()
        .verticalScroll(rememberScrollState())
) {
    repeat(20) { Text("Item $it") }
}

// Or for dynamic lists:
LazyColumn(Modifier.fillMaxSize()) {
    items(20) { Text("Item $it") }
}
```

### Ignoring Content Padding from Scaffold

```kotlin
// Anti-pattern: ignoring innerPadding from Scaffold
Scaffold(topBar = { TopAppBar(title = { Text("App") }) }) { innerPadding ->
    // Content renders BEHIND the top bar
    LazyColumn {
        items(data) { Text(it) }
    }
}

// Correct: apply innerPadding
Scaffold(topBar = { TopAppBar(title = { Text("App") }) }) { innerPadding ->
    LazyColumn(
        modifier = Modifier.padding(innerPadding),  // or contentPadding = innerPadding
    ) {
        items(data) { Text(it) }
    }
}
```
