# Material 3 Motion

Source: `compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/tokens/MotionTokens.kt`
and `compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3/MotionScheme.kt`
in `androidx/androidx` (branch: `androidx-main`)

CMP compatibility: `MotionTokens`, `MotionScheme`, and all easing constants are in
`androidx.compose.material3` — available on all CMP targets (Android, Desktop, iOS, Web)
since M3 1.2.0. No platform guards needed.

---

## 1. Two APIs, One System

M3 provides two ways to apply motion:

| API | When to Use |
|-----|------------|
| **`MotionScheme`** (preferred) | Inside components that should adapt to the app's motion scheme — the theme controls whether spring-based or tween-based specs are used |
| **`MotionTokens` + `tween()`** | When you need explicit `tween()` / `keyframes {}` control and the component is not theme-motion-aware |

Use `MotionScheme` for new components. Use `MotionTokens` when the caller explicitly provides `AnimationSpec` parameters or when working with `AnimatedVisibility`, `Crossfade`, or shared elements.

---

## 2. MotionScheme API (Preferred for Components)

`MotionScheme` is part of `MaterialTheme` alongside `colorScheme`, `typography`, and `shapes`.

```kotlin
// Access via MaterialTheme
val motionScheme = MaterialTheme.motionScheme

// Two built-in schemes
MaterialTheme(motionScheme = MotionScheme.standard())    // utilitarian UI
MaterialTheme(motionScheme = MotionScheme.expressive())  // prominent UI (M3 recommended default)
```

### Spec Functions

| Function | Use Case |
|----------|---------|
| `defaultSpatialSpec<T>()` | Layout changes, position/size transitions (spatial) |
| `fastSpatialSpec<T>()` | Quick spatial transitions |
| `slowSpatialSpec<T>()` | Deliberate spatial transitions |
| `defaultEffectsSpec<T>()` | Opacity, color, non-spatial changes |
| `fastEffectsSpec<T>()` | Quick opacity/color transitions |
| `slowEffectsSpec<T>()` | Deliberate opacity/color transitions |

```kotlin
@Composable
fun AnimatedCard(expanded: Boolean) {
    val motionScheme = MaterialTheme.motionScheme

    // Size change = spatial
    val size by animateDpAsState(
        targetValue = if (expanded) 200.dp else 100.dp,
        animationSpec = motionScheme.defaultSpatialSpec(),
        label = "card-size"
    )

    // Color change = effects
    val color by animateColorAsState(
        targetValue = if (expanded) MaterialTheme.colorScheme.primaryContainer
                      else MaterialTheme.colorScheme.surface,
        animationSpec = motionScheme.defaultEffectsSpec(),
        label = "card-color"
    )
}
```

> **Key difference from `tween()`**: `MotionScheme` specs are spring-based by default in
> `expressive()`. The actual spec type (spring vs. tween) is controlled by the theme, not
> the component. This means motion adapts to the app's motion scheme without code changes.

---

## 3. Duration Tokens

Use when explicit `tween()` control is needed. All values sourced from `MotionTokens.kt`
(generated from Material Design spec v0_103). Durations are `Float` — use `.toInt()` for
`tween(durationMillis = ...)`.

| Token | Value (ms) | Use Case |
|-------|-----------|---------|
| `MotionTokens.DurationShort1` | 50ms | Micro interactions — ripple spread, checkbox tick |
| `MotionTokens.DurationShort2` | 100ms | Small element appear/disappear |
| `MotionTokens.DurationShort3` | 150ms | Icon transitions, selection indicators |
| `MotionTokens.DurationShort4` | 200ms | Tooltip appear, chip selection |
| `MotionTokens.DurationMedium1` | 250ms | FAB expand, card state change |
| `MotionTokens.DurationMedium2` | 300ms | **Most common** — dialog, bottom sheet, nav drawer |
| `MotionTokens.DurationMedium3` | 350ms | Expanded component transitions |
| `MotionTokens.DurationMedium4` | 400ms | Page-level panel transitions |
| `MotionTokens.DurationLong1` | 450ms | Complex layout changes |
| `MotionTokens.DurationLong2` | 500ms | Shared element enter |
| `MotionTokens.DurationLong3` | 550ms | Shared element — large content |
| `MotionTokens.DurationLong4` | 600ms | Full container morphs |
| `MotionTokens.DurationExtraLong1` | 700ms | Full-screen transitions only |
| `MotionTokens.DurationExtraLong2` | 800ms | Full-screen transitions only |
| `MotionTokens.DurationExtraLong3` | 900ms | Full-screen transitions only |
| `MotionTokens.DurationExtraLong4` | 1000ms | Full-screen transitions only |

---

## 4. Easing Tokens

All values sourced from `MotionTokens.kt`. Access via `MotionTokens.Easing*CubicBezier`.

| Token | CubicBezierEasing(x1, y1, x2, y2) | Direction | Use Case |
|-------|------------------------------------|-----------|---------|
| `MotionTokens.EasingEmphasizedDecelerateCubicBezier` | `(0.05f, 0.7f, 0.1f, 1.0f)` | Entering | Element arriving on screen — fast start, gentle settle |
| `MotionTokens.EasingEmphasizedAccelerateCubicBezier` | `(0.3f, 0.0f, 0.8f, 0.15f)` | Exiting | Element leaving screen — slow start, fast exit |
| `MotionTokens.EasingEmphasizedCubicBezier` | `(0.2f, 0.0f, 0.0f, 1.0f)` | Both | Default for most M3 component transitions |
| `MotionTokens.EasingStandardDecelerateCubicBezier` | `(0.0f, 0.0f, 0.0f, 1.0f)` | Entering | Simple enter — less expressive than Emphasized |
| `MotionTokens.EasingStandardAccelerateCubicBezier` | `(0.3f, 0.0f, 1.0f, 1.0f)` | Exiting | Simple exit |
| `MotionTokens.EasingStandardCubicBezier` | `(0.2f, 0.0f, 0.0f, 1.0f)` | Both | Simple state changes |
| `MotionTokens.EasingLinearCubicBezier` | `(0.0f, 0.0f, 1.0f, 1.0f)` | — | Looping / repeating animations only |
| `MotionTokens.EasingLegacyCubicBezier` | `(0.4f, 0.0f, 0.2f, 1.0f)` | — | `FastOutSlowInEasing` equivalent — do not use in new code |
| `MotionTokens.EasingLegacyAccelerateCubicBezier` | `(0.4f, 0.0f, 1.0f, 1.0f)` | — | `FastOutLinearInEasing` equivalent — do not use in new code |
| `MotionTokens.EasingLegacyDecelerateCubicBezier` | `(0.0f, 0.0f, 0.2f, 1.0f)` | — | `LinearOutSlowInEasing` equivalent — do not use in new code |

> **Enter/exit rule (always):** Enter = Decelerate easing (fast start, gentle settle).
> Exit = Accelerate easing (slow start, quick departure). Never use the same easing for both.
> The `Legacy*` tokens are equivalent to the pre-M3 named constants — do not use them in new code.

---

## 5. Using Tokens in Compose Animation APIs

### animate*AsState (prefer MotionScheme)

```kotlin
// Color — effects spec
val color by animateColorAsState(
    targetValue = if (selected) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.surface,
    animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
    label = "selection-color"
)

// If explicit tween is required:
val color by animateColorAsState(
    targetValue = targetColor,
    animationSpec = tween(
        durationMillis = MotionTokens.DurationShort4.toInt(),   // 200ms
        easing = MotionTokens.EasingStandardCubicBezier         // state change, not enter/exit
    ),
    label = "color"
)
```

### AnimatedVisibility (asymmetric enter/exit)

Enter and exit must use different durations and easing — exit is always faster.

```kotlin
AnimatedVisibility(
    visible = visible,
    enter = fadeIn(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium2.toInt(),      // 300ms
            easing = MotionTokens.EasingEmphasizedDecelerateCubicBezier // entering
        )
    ) + slideInVertically(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationMedium2.toInt(),
            easing = MotionTokens.EasingEmphasizedDecelerateCubicBezier
        ),
        initialOffsetY = { it / 4 }
    ),
    exit = fadeOut(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort4.toInt(),       // 200ms — exit is faster
            easing = MotionTokens.EasingEmphasizedAccelerateCubicBezier // exiting
        )
    ) + slideOutVertically(
        animationSpec = tween(
            durationMillis = MotionTokens.DurationShort4.toInt(),
            easing = MotionTokens.EasingEmphasizedAccelerateCubicBezier
        ),
        targetOffsetY = { it / 4 }
    )
) {
    content()
}
```

### updateTransition (multi-property, shared spec)

```kotlin
val transition = updateTransition(targetState = expanded, label = "card")

val elevation by transition.animateDp(
    transitionSpec = {
        tween(
            durationMillis = MotionTokens.DurationMedium1.toInt(),  // 250ms
            easing = MotionTokens.EasingEmphasizedCubicBezier
        )
    },
    label = "elevation"
) { isExpanded -> if (isExpanded) 8.dp else 0.dp }

val cornerRadius by transition.animateDp(
    transitionSpec = {
        tween(
            durationMillis = MotionTokens.DurationMedium1.toInt(),
            easing = MotionTokens.EasingEmphasizedCubicBezier
        )
    },
    label = "corner-radius"
) { isExpanded -> if (isExpanded) 0.dp else 12.dp }
```

### Shared element transitions

Shared elements cross screen boundaries — use Long range.

```kotlin
Modifier.sharedElement(
    state = rememberSharedContentState(key = "hero-${item.id}"),
    animatedVisibilityScope = animatedVisibilityScope,
    boundsTransform = { _, _ ->
        tween(
            durationMillis = MotionTokens.DurationLong2.toInt(),    // 500ms
            easing = MotionTokens.EasingEmphasizedCubicBezier
        )
    }
)
```

---

## 6. Decision Tree

Pick the right duration by working through these questions in order:

1. **Micro interaction?** (ripple, checkbox tick, toggle thumb snap)
   → `DurationShort1` (50ms) or `DurationShort2` (100ms)

2. **Component state change?** (button press feedback, chip select, icon swap, tab indicator)
   → `DurationShort3` (150ms) or `DurationShort4` (200ms)

3. **Container change?** (card expand, FAB extend/shrink, menu open, tooltip)
   → `DurationMedium1` (250ms) or `DurationMedium2` (300ms) ← most common

4. **Screen-level element?** (dialog enter, bottom sheet slide, search bar expand, nav drawer)
   → `DurationMedium3` (350ms) or `DurationMedium4` (400ms)

5. **Shared element / hero transition?** (image or card expands from list to detail screen)
   → `DurationLong1` (450ms) or `DurationLong2` (500ms)

6. **Full-screen complex morph?** (entire screen layout changes)
   → `DurationLong3`–`DurationExtraLong1` (550ms–700ms)

**Easing rule (always apply):**
- Element arriving → `EasingEmphasizedDecelerateCubicBezier`
- Element departing → `EasingEmphasizedAccelerateCubicBezier`
- Element changing state (stays on screen) → `EasingEmphasizedCubicBezier`
- Looping/infinite → `EasingLinearCubicBezier`
- Prefer `MotionScheme` specs over manual easing for theme-aware components

---

## 7. Review Flags

Patterns to catch in code review. See also `references/pr-review.md` Category 3.

| Pattern in Code | Flag | Fix |
|----------------|------|-----|
| `tween(50)` | Hardcoded duration | `MotionTokens.DurationShort1.toInt()` |
| `tween(100)` | Hardcoded duration | `MotionTokens.DurationShort2.toInt()` |
| `tween(150)` | Hardcoded duration | `MotionTokens.DurationShort3.toInt()` |
| `tween(200)` | Hardcoded duration | `MotionTokens.DurationShort4.toInt()` |
| `tween(250)` | Hardcoded duration | `MotionTokens.DurationMedium1.toInt()` |
| `tween(300)` | Hardcoded duration | `MotionTokens.DurationMedium2.toInt()` |
| `tween(350)` | Hardcoded duration | `MotionTokens.DurationMedium3.toInt()` |
| `tween(400)` | Hardcoded duration | `MotionTokens.DurationMedium4.toInt()` |
| `tween(N)` with any integer literal | Hardcoded duration | Nearest `MotionTokens.Duration*` token |
| `FastOutSlowInEasing` | Pre-M3 easing | `MotionTokens.EasingEmphasizedCubicBezier` |
| `LinearOutSlowInEasing` | Pre-M3 easing | `MotionTokens.EasingEmphasizedDecelerateCubicBezier` |
| `FastOutLinearInEasing` | Pre-M3 easing | `MotionTokens.EasingEmphasizedAccelerateCubicBezier` |
| `animateColorAsState(target)` no `animationSpec` | Missing spec | `MaterialTheme.motionScheme.defaultEffectsSpec()` |
| Same easing on both `enter` and `exit` | Wrong pairing | Decelerate for enter, Accelerate for exit |
| Duration > 600ms on non-shared-element | Too slow | Reduce to `DurationLong1`–`DurationLong2` |
| New component uses explicit `tween()` instead of `MotionScheme` | Not theme-aware | Use `MaterialTheme.motionScheme.defaultSpatialSpec()` / `defaultEffectsSpec()` |
