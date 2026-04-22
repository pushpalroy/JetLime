# Atomic Design System Reference

Building reusable, hierarchical component systems in Jetpack Compose and Compose Multiplatform.
Based on Brad Frost's atomic design methodology, mapped to Compose primitives.

---

## 1. The 5-Level Hierarchy Mapped to Compose

| Level | Compose Equivalent | Examples |
|-------|-------------------|----------|
| **Tokens** | `MaterialTheme.colorScheme.*`, `MaterialTheme.typography.*`, custom `CompositionLocal` tokens (spacing, elevation, brand colors) | `AppTheme.spacing.medium`, `AppTheme.colors.brandPrimary` |
| **Atoms** | Single-purpose composables with one responsibility, slot API, modifier param. Either wrap M3 or build custom. | `AppButton`, `AppTextField`, `AppAvatar`, `AppIcon` |
| **Molecules** | Composables that combine 2+ atoms into a functional unit | `SearchBar` (icon + text field), `MovieCard` (image + text), `UserListItem` |
| **Organisms** | Screen sections combining molecules into a UI region | `MovieCatalogRow` (header + LazyRow of MovieCards), `NavigationDrawerWithContent` |
| **Templates** | Screen layouts defining content areas without data — `Scaffold` + slot composition | `MainScreenTemplate(topBar, content, bottomBar)`, `DetailScreenTemplate(hero, body, actions)` |

**Dependency rule:** each level depends only on levels below it. An organism should not use
raw `Text()` — it should use an atom. A molecule should not hardcode colors — it should use
tokens via `MaterialTheme` or custom `CompositionLocal`.

```
Template
  └── Organism
        └── Molecule
              └── Atom
                    └── Token (MaterialTheme / CompositionLocal)
```

---

## 2. Token Layer

Tokens are the foundation. Every visual property (color, typography, spacing, shape, motion)
should come from a token — never hardcoded in a composable body.

### M3 tokens (use directly)

These are already provided by `MaterialTheme`:

- `MaterialTheme.colorScheme` — primary, secondary, surface, error, etc.
- `MaterialTheme.typography` — displayLarge through labelSmall
- `MaterialTheme.shapes` — extraSmall through extraLarge
- `MaterialTheme.motionScheme` — `defaultSpatialSpec()`, `defaultEffectsSpec()`

### App-level custom tokens

Create when M3 doesn't cover your need. Use `CompositionLocal` + a wrapper theme.

**Spacing scale:**
```kotlin
object AppSpacing {
    val xxs = 2.dp
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
}
val LocalAppSpacing = staticCompositionLocalOf { AppSpacing }
```

**Brand colors (beyond M3 colorScheme):**
```kotlin
data class AppBrandColors(
    val accent: Color,
    val onAccent: Color,
    val surface: Color,
)
val LocalAppBrandColors = staticCompositionLocalOf {
    AppBrandColors(
        accent = Color.Unspecified,
        onAccent = Color.Unspecified,
        surface = Color.Unspecified,
    )
}
```

**Access pattern — wrap in `AppTheme`:**
```kotlin
@Composable
fun AppTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalAppSpacing provides AppSpacing,
        LocalAppBrandColors provides AppBrandColors(
            accent = Color(0xFF1A73E8),
            onAccent = Color.White,
            surface = Color(0xFFF5F5F5),
        )
    ) {
        MaterialTheme(
            colorScheme = /* your color scheme */,
            typography = /* your typography */,
            shapes = /* your shapes */,
        ) {
            content()
        }
    }
}

// Usage anywhere in the tree:
val spacing = LocalAppSpacing.current
val brandColors = LocalAppBrandColors.current
```

**When to create a custom token vs. use M3 directly:**
- M3 covers it → use `MaterialTheme.*` directly
- App-specific concept (brand accent, spacing scale, elevation scale) → custom `CompositionLocal`
- One-off value needed in a single component → not a token, just a local constant

---

## 3. Atom Patterns

Atoms are the smallest reusable UI units. Every atom must satisfy the **atom contract**.

### Atom Contract

Every atom (public composable that renders UI) must satisfy:

1. **`modifier: Modifier = Modifier` parameter** — caller controls layout
2. **Slot APIs for variable content** — `@Composable () -> Unit` or scoped like `@Composable RowScope.() -> Unit`
3. **Token-based styling** — no hardcoded `Color(0xFF...)`, `14.sp`, `FontWeight.Bold`
4. **Sensible defaults** — works without configuration
5. **Preview composable** — `@Preview` function for visual verification

### Two atom types

**1. M3 wrapper atoms** — wrap an M3 component with brand defaults:

```kotlin
@Composable
fun AppButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = LocalAppBrandColors.current.accent,
            contentColor = LocalAppBrandColors.current.onAccent,
        ),
        content = content,
    )
}
```

**2. Custom atoms** — when no M3 equivalent exists:

```kotlin
@Composable
fun AppAvatar(
    imageUrl: String,
    size: AvatarSize = AvatarSize.Medium,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
    )
}

enum class AvatarSize(val dp: Dp) {
    Small(24.dp), Medium(40.dp), Large(56.dp)
}
```

### Naming rule

Name by what the component **IS**, not where it's used.

| Bad | Good | Why |
|-----|------|-----|
| `ButtonWithBoldCTA` | `AppButton` | The boldness is a style variant, not a component |
| `RedBorderCard` | `HighlightCard` or `AppCard` | Named by visual appearance, not function |
| `HomeMovieCard` | `MovieCard` | Named by screen, not reusable |
| `ButtonForSettings` | `AppButton` | Named by context, not function |

---

## 4. Molecule, Organism, and Template Patterns

### Molecule — composes 2+ atoms

A molecule combines atoms into a functional unit. It accepts data and callbacks, not ViewModels.

```kotlin
@Composable
fun MovieCard(
    movie: Movie,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppCard(onClick = onClick, modifier = modifier) {
        AppImage(url = movie.posterUrl, contentDescription = movie.title)
        AppText(text = movie.title, style = MaterialTheme.typography.titleSmall)
        AppText(text = movie.year.toString(), style = MaterialTheme.typography.bodySmall)
    }
}
```

### Organism — composes molecules into a UI region

An organism is a screen section. It still accepts data as parameters — never reads from a ViewModel directly.

```kotlin
@Composable
fun MovieCatalogRow(
    title: String,
    movies: List<Movie>,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        AppText(text = title, style = MaterialTheme.typography.headlineSmall)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(LocalAppSpacing.current.sm)
        ) {
            items(movies, key = { it.id }) { movie ->
                MovieCard(movie = movie, onClick = { onMovieClick(movie) })
            }
        }
    }
}
```

### Template — defines screen layout via slot composition

Templates define where content goes, not what it is. They accept slot parameters, no data.

```kotlin
@Composable
fun CatalogScreenTemplate(
    topBar: @Composable () -> Unit,
    hero: @Composable () -> Unit,
    sections: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(topBar = topBar, modifier = modifier) { padding ->
        LazyColumn(contentPadding = padding) {
            item { hero() }
            item { sections() }
        }
    }
}
```

### Level summary

| Level | Accepts | Composes | ViewModel? |
|-------|---------|----------|-----------|
| Atom | Primitives, slots, modifier | M3 components or raw Compose | No |
| Molecule | Data classes, callbacks, modifier | Atoms | No |
| Organism | Data, callbacks, modifier | Molecules + atoms | No |
| Template | Slots only, modifier | Scaffold + layout | No |
| Screen | ViewModel | Template + organisms + molecules | Yes — this is the only level that touches ViewModel |

---

## 5. The "Ask" Prompt

When the skill detects component-building intent (user asks to "build a card", "create a button",
"implement this component"), **before scaffolding code**, ask:

> "This looks like a **[molecule/organism]**. Should I also scaffold the **[lower-level] atoms**
> it needs, or does your codebase already have them?"

The developer can answer:

| Answer | Skill behavior |
|--------|---------------|
| "Yes, scaffold everything" | Create from token layer up — define spacing/color tokens, atoms, then the requested component |
| "Just build the card" | Build the requested component using atomic principles (slots, modifier, tokens) but don't create lower-level atoms |
| "We already have AppButton, AppImage" | Reuse those atoms, only build the new molecule/organism |

**The skill always applies atomic principles regardless of the answer.** The question is only
about whether to scaffold lower levels. Every component gets:
- `modifier: Modifier = Modifier`
- Slot APIs where appropriate
- Token-based styling (no hardcoded values)
- Sensible defaults

---

## 6. Anti-Patterns

| Anti-Pattern | Why It's Wrong | Fix |
|-------------|---------------|-----|
| `Color(0xFF1A73E8)` inside a composable body | Hardcoded color — not themeable, not dark-mode-safe | Use `MaterialTheme.colorScheme.*` or app brand token |
| `fontSize = 14.sp`, `fontWeight = FontWeight.Bold` | Hardcoded typography breaks consistency | Use `MaterialTheme.typography.*` |
| `Modifier.padding(16.dp)` without spacing token | Magic number spacing — inconsistent across app | Use `LocalAppSpacing.current.md` (or define a spacing scale) |
| Composable named `ButtonForSettings` / `CardWithRedBorder` | Named by context, not by function — not reusable | Name by what it IS: `AppButton`, `HighlightCard` |
| Public composable with no `modifier` parameter | Caller cannot control layout | Add `modifier: Modifier = Modifier`, pass to root element |
| Composable rendering UI with no slot parameters | Content is hardcoded, not composable | Add slot APIs for variable content |
| Raw `Text()` / `Button()` / `Icon()` in an organism | Skips atomic levels — loses theming and consistency | Use app-level atom wrappers |
| Organism that directly reads ViewModel | Couples UI to data layer — not reusable, not previewable | Accept data and callbacks as parameters; let the screen call ViewModel |
| Molecule with more than 3–4 responsibilities | Too much in one component — hard to reuse parts | Decompose into smaller molecules or extract atoms |
