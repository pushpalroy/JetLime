# Animation in Jetpack Compose

Reference: `androidx/compose/animation/animation/src/commonMain/kotlin/androidx/compose/animation/`

## State-Based Animations

### animate*AsState

Animate individual properties by targeting a value. The animation starts when the value changes.

```kotlin
val size by animateDpAsState(
    targetValue = if (isExpanded) 200.dp else 100.dp,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
    label = "size"
)

Box(modifier = Modifier.size(size))
```

Common variants:

```kotlin
animateColorAsState(targetValue = Color.Blue)
animateFloatAsState(targetValue = 1f)
animateIntAsState(targetValue = 100)
animateOffsetAsState(targetValue = Offset(10f, 20f))
```

Each automatically handles coroutines and recomposition. Use the `label` parameter for debugging.

## AnimatedVisibility

Controls appear/disappear animations with enter and exit transitions.

```kotlin
var visible by remember { mutableStateOf(true) }

AnimatedVisibility(visible = visible) {
    Text("Hello!")
}

// Trigger
Button(onClick = { visible = !visible }) { Text("Toggle") }
```

### Enter/Exit Transitions

```kotlin
AnimatedVisibility(
    visible = visible,
    enter = slideInHorizontally(initialOffsetX = { -it }) + fadeIn(),
    exit = slideOutHorizontally(targetOffsetX = { -it }) + fadeOut()
) {
    Text("Animated!")
}
```

Built-in transitions:
- `slideInVertically`, `slideOutVertically`
- `slideInHorizontally`, `slideOutHorizontally`
- `expandVertically`, `shrinkVertically`
- `expandHorizontally`, `shrinkHorizontally`
- `fadeIn`, `fadeOut`
- `scaleIn`, `scaleOut`
- Combine with `+`: `slideInVertically() + fadeIn()`

### Advanced: Custom animation specs

```kotlin
AnimatedVisibility(
    visible = visible,
    enter = slideInVertically(
        initialOffsetY = { fullHeight -> fullHeight },
        animationSpec = spring()
    ),
    exit = slideOutVertically(
        targetOffsetY = { fullHeight -> fullHeight },
        animationSpec = tween(durationMillis = 300)
    )
) {
    Box(Modifier.fillMaxWidth().height(100.dp).background(Color.Blue))
}
```

## AnimatedContent

Replace content with smooth transitions.

```kotlin
var count by remember { mutableStateOf(0) }

AnimatedContent(targetState = count) { target ->
    Text(text = "Count: $target")
}

Button(onClick = { count++ }) { Text("Increment") }
```

### Custom transitionSpec

```kotlin
AnimatedContent(
    targetState = count,
    transitionSpec = {
        slideInVertically(initialOffsetY = { it }) with slideOutVertically(targetOffsetY = { -it })
    }
) { target ->
    Text("$target")
}
```

Use `with` to specify exit and enter together. This runs exits and entries simultaneously.

### Sequencing transitions

```kotlin
AnimatedContent(
    targetState = count,
    transitionSpec = {
        slideInVertically(initialOffsetY = { it }) with slideOutVertically(targetOffsetY = { -it }) using SizeTransform(clip = false)
    }
) { target ->
    Text(
        "Count: $target",
        modifier = Modifier.fillMaxWidth()
    )
}
```

`SizeTransform` animates container size smoothly during content changes.

## Crossfade

Simple content swap with fade effect.

```kotlin
var showFirst by remember { mutableStateOf(true) }

Crossfade(targetState = showFirst) { state ->
    if (state) {
        Text("First")
    } else {
        Text("Second")
    }
}
```

Lightweight alternative to `AnimatedContent` for simple visibility toggles.

## updateTransition

Coordinate multiple animated values with a single state.

```kotlin
var expanded by remember { mutableStateOf(false) }
val transition = updateTransition(targetState = expanded)

val size by transition.animateDp { if (it) 200.dp else 100.dp }
val color by transition.animateColor { if (it) Color.Blue else Color.Red }

Box(
    modifier = Modifier
        .size(size)
        .background(color)
        .clickable { expanded = !expanded }
)
```

All animations run in sync, controlled by a single state change. Useful for complex components with multiple animated properties.

## rememberInfiniteTransition

Create looping animations.

```kotlin
val infiniteTransition = rememberInfiniteTransition(label = "infinite")

val alpha by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
        animation = tween(1000),
        repeatMode = RepeatMode.Reverse
    ),
    label = "alpha"
)

Text("Pulsing", modifier = Modifier.alpha(alpha))
```

Runs continuously until the composable is removed. Perfect for loading states, pulsing indicators.

## Animatable

Imperative animation control in coroutines. Use for fine-grained control.

```kotlin
val animatable = remember { Animatable(0f) }

LaunchedEffect(trigger) {
    animatable.animateTo(
        targetValue = 100f,
        animationSpec = spring()
    )
}

Box(Modifier.graphicsLayer(translationX = animatable.value))
```

Useful for responding to gestures or complex conditions:

```kotlin
val animatable = remember { Animatable(0f) }

LaunchedEffect(Unit) {
    animatable.animateTo(targetValue = 360f, animationSpec = tween(2000))
}

Box(
    Modifier
        .size(100.dp)
        .background(Color.Blue)
        .graphicsLayer(rotationZ = animatable.value)
)
```

## Animation Specifications

### spring — Realistic, physics-based

```kotlin
val size by animateDpAsState(
    targetValue = 200.dp,
    animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
)
```

- `dampingRatio`: `NoBouncy` (1f), `LowBouncy` (0.75f), `MediumBouncy` (0.5f), `HighBouncy` (0.2f)
- `stiffness`: `Low`, `Medium`, `High`

Use for interactive feedback, familiar to users.

### tween — Time-based

```kotlin
val color by animateColorAsState(
    targetValue = Color.Blue,
    animationSpec = tween(durationMillis = 500, easing = EaseInOutCubic)
)
```

Easing functions: `EaseInQuad`, `EaseOutQuad`, `EaseInOutQuad`, `LinearEasing`, `FastOutSlowInEasing`.

Predictable timing, good for sequential animations.

### keyframes — Frame-by-frame control

```kotlin
val position by animateFloatAsState(
    targetValue = 100f,
    animationSpec = keyframes {
        0f at 0 using EaseInQuad
        50f at 150 using EaseOutQuad
        100f at 300
    }
)
```

Define exact values at specific timestamps. Use for complex choreography.

## Automatic Size Animation

### animateContentSize

Smoothly animate Box size when content changes.

```kotlin
var expanded by remember { mutableStateOf(false) }

Box(
    modifier = Modifier
        .animateContentSize()
        .background(Color.Blue)
        .clickable { expanded = !expanded }
) {
    Column {
        Text("Header")
        if (expanded) {
            Text("Expanded content...")
        }
    }
}
```

No need for explicit `AnimatedVisibility` or layout transitions. Handles the container automatically.

## Layout Animation in LazyLists

### animateItem — Replaces animateItemPlacement

Animate item appearance, removal, and reordering.

```kotlin
LazyColumn {
    items(items, key = { it.id }) { item ->
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .animateItem()
                .padding(8.dp)
                .background(Color.Gray)
        ) {
            Text(item.name)
        }
    }
}
```

Automatically animates:
- New items sliding in
- Removed items sliding out
- Reordered items moving to new positions

Called on items in Lazy layouts (LazyColumn, LazyRow, LazyVerticalGrid).

## Shared Element Transitions

Animate elements seamlessly across screen boundaries using `SharedTransitionLayout` and Navigation Compose.

### sharedElement() vs sharedBounds()

| Aspect | `sharedElement()` | `sharedBounds()` |
|---|---|---|
| **Content** | Identical on both screens (same image, same icon) | Different content in source and target (e.g., card expands to detail) |
| **Use case** | Hero image, avatar, thumbnail | Container transform, card-to-page |
| **During transition** | Only the target composable is rendered | Both source and target are visible and crossfade |

### Complete Working Example

```kotlin
@Composable
fun App() {
    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = "list") {
            composable("list") {
                ListScreen(
                    onItemClick = { id -> navController.navigate("detail/$id") },
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
            composable("detail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: return@composable
                DetailScreen(
                    itemId = id,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable
                )
            }
        }
    }
}

@Composable
fun ListScreen(
    onItemClick: (String) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Row(
            modifier = Modifier
                .clickable { onItemClick(item.id) }
                // sharedBounds wraps the entire card container (different content at source/target)
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "card-${item.id}"),
                    animatedVisibilityScope = animatedVisibilityScope,
                    boundsTransform = BoundsTransform { initialBounds, targetBounds ->
                        keyframes {
                            durationMillis = 500
                            initialBounds at 0 using ArcMode.ArcBelow
                            targetBounds at 500
                        }
                    }
                )
        ) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    // sharedElement for the identical image across screens
                    .sharedElement(
                        state = rememberSharedContentState(key = "image-${item.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
            Text(
                text = item.title,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "title-${item.id}"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    // Prevent text reflow during transition by snapping to final size
                    .skipToLookaheadSize()
            )
        }
    }
}

@Composable
fun DetailScreen(
    itemId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "card-$itemId"),
                    animatedVisibilityScope = animatedVisibilityScope
                )
        ) {
            Image(
                painter = painterResource(item.imageRes),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .sharedElement(
                        state = rememberSharedContentState(key = "image-$itemId"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
            )
            Text(
                text = item.title,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .sharedElement(
                        state = rememberSharedContentState(key = "title-$itemId"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .skipToLookaheadSize()
            )
            // Non-shared content fades in
            Text(
                text = item.description,
                modifier = Modifier.animateEnterExit(
                    enter = fadeIn() + slideInVertically { it / 3 },
                    exit = fadeOut()
                )
            )
        }
    }
}
```

### BoundsTransform for Arc Motion

Control the animation path between source and target bounds:

```kotlin
val arcBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
    keyframes {
        durationMillis = 500
        initialBounds at 0 using ArcMode.ArcBelow
        targetBounds at 500
    }
}

// Apply to sharedElement or sharedBounds
Modifier.sharedElement(
    state = rememberSharedContentState(key = "hero"),
    animatedVisibilityScope = animatedVisibilityScope,
    boundsTransform = arcBoundsTransform
)
```

### Overlay Rendering

Keep shared elements above all other content during the transition:

```kotlin
Modifier.sharedElement(
    state = rememberSharedContentState(key = "fab"),
    animatedVisibilityScope = animatedVisibilityScope,
    renderInSharedTransitionScopeOverlay = true // Renders above navigation transitions
)
```

### Preventing Text Reflow

Use `skipToLookaheadSize()` so text composables snap to their final size immediately, avoiding awkward line-break changes mid-transition:

```kotlin
Text(
    text = item.title,
    modifier = Modifier
        .sharedElement(
            state = rememberSharedContentState(key = "title-${item.id}"),
            animatedVisibilityScope = animatedVisibilityScope
        )
        .skipToLookaheadSize() // Text uses target size immediately, no reflow
)
```

## Performance: graphicsLayer for Transforms

Animate transforms using `graphicsLayer` instead of layout changes.

```kotlin
// ✅ Correct: Uses GPU-accelerated graphicsLayer
val offset by animateFloatAsState(targetValue = 100f)
Box(modifier = Modifier.graphicsLayer(translationX = offset))

// ❌ Avoid: Causes recomposition and relayout
val offset by animateFloatAsState(targetValue = 100f)
Box(modifier = Modifier.offset(x = offset.dp))
```

Use `graphicsLayer` for:
- Translation (`translationX`, `translationY`)
- Rotation (`rotationX`, `rotationY`, `rotationZ`)
- Scale (`scaleX`, `scaleY`)
- Alpha (opacity)

## Anti-Patterns

### Don't: Animate visibility with if

```kotlin
// ❌ Anti-pattern
@Composable
fun MyScreen() {
    if (visible) {
        Text("Content") // Jumps in/out without animation
    }
}

// ✅ Correct
@Composable
fun MyScreen() {
    AnimatedVisibility(visible = visible) {
        Text("Content")
    }
}
```

### Don't: Create Animatable in composition

```kotlin
// ❌ Anti-pattern
@Composable
fun MyScreen() {
    val animatable = Animatable(0f) // Recreated every recomposition!

    LaunchedEffect(Unit) {
        animatable.animateTo(100f)
    }
}

// ✅ Correct
@Composable
fun MyScreen() {
    val animatable = remember { Animatable(0f) } // Preserved across recompositions

    LaunchedEffect(Unit) {
        animatable.animateTo(100f)
    }
}
```

### Don't: Animate in composition phase

```kotlin
// ❌ Anti-pattern
@Composable
fun MyScreen() {
    var position by remember { mutableStateOf(0f) }
    position = position + 10f // Infinite recomposition loop!
}

// ✅ Correct
@Composable
fun MyScreen() {
    var position by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        repeat(10) {
            position += 10f
            delay(16)
        }
    }
}
```

### Don't: Forget label parameter

```kotlin
// ❌ Anti-pattern (harder to debug)
val size by animateDpAsState(targetValue = 100.dp)

// ✅ Correct
val size by animateDpAsState(
    targetValue = 100.dp,
    label = "box_size"
)
```

Labels help with debugging layout inspector and animation inspection tools.

---

## Animation Decision Tree

### When to Use Which API

| API | Use When |
|---|---|
| `animate*AsState` | Animating a single property (size, color, alpha) driven by state |
| `AnimatedVisibility` | Showing or hiding a composable with enter/exit transitions |
| `AnimatedContent` / `Crossfade` | Switching between different composables (content swap) |
| `updateTransition` | Multiple properties that must animate in sync from the same state |
| `Animatable` | Gesture-driven or imperative control (coroutine-based, supports `snapTo`, `animateDecay`) |
| `rememberInfiniteTransition` | Infinite looping animations (pulsing, rotating, shimmer) |
| `animateContentSize` | Smoothly animating a container's size when its content changes |
| `animateItem` | List item appearance, disappearance, and reordering in Lazy layouts |

### Which Phase Each Animation Affects

Compose rendering has three phases: **Composition** (what to show), **Layout** (where to place), **Draw** (how to render). Animations should read state in the latest possible phase to minimize work.

```kotlin
// BEST: Draw phase only — no relayout, no recomposition
val alpha by animateFloatAsState(targetValue = if (visible) 1f else 0f, label = "alpha")
Box(
    modifier = Modifier.graphicsLayer { this.alpha = alpha }
)

// GOOD: Layout phase only — relayout but no recomposition
val offsetPx by animateIntAsState(targetValue = if (moved) 300 else 0, label = "offset")
Box(
    modifier = Modifier.offset { IntOffset(offsetPx, 0) }
)

// MODERATE: Composition + Layout — triggers recomposition on every frame
val offsetDp by animateDpAsState(targetValue = if (moved) 100.dp else 0.dp, label = "offset")
Box(
    modifier = Modifier.offset(x = offsetDp)
)
```

**Rule:** Defer state reads to the latest possible phase. Use lambda-based modifiers (`graphicsLayer { }`, `offset { }`) instead of parameter-based modifiers (`graphicsLayer(alpha = ...)`, `offset(x = ...)`).

---

## Design-to-Animation Translation

### Figma Easing Curves to Compose

| Figma Easing | Compose Equivalent |
|---|---|
| Linear | `LinearEasing` |
| Ease In | `FastOutLinearInEasing` |
| Ease Out | `LinearOutSlowInEasing` |
| Ease In and Out | `FastOutSlowInEasing` |
| Custom Bezier (x1, y1, x2, y2) | `CubicBezierEasing(x1, y1, x2, y2)` |

### M3 Motion Duration Tokens

| Token | Duration |
|---|---|
| Short1 | 50ms |
| Short2 | 100ms |
| Short3 | 150ms |
| Short4 | 200ms |
| Medium1 | 250ms |
| Medium2 | 300ms |
| Medium3 | 350ms |
| Medium4 | 400ms |
| Long1 | 450ms |
| Long2 | 500ms |
| Long3 | 550ms |
| Long4 | 600ms |
| ExtraLong1 | 700ms |
| ExtraLong2 | 800ms |
| ExtraLong3 | 900ms |
| ExtraLong4 | 1000ms |

### M3 Easing Tokens

| Token | Compose Value |
|---|---|
| Emphasized | `CubicBezierEasing(0.2f, 0f, 0f, 1f)` |
| EmphasizedDecelerate | `CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)` |
| EmphasizedAccelerate | `CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)` |
| Standard | `FastOutSlowInEasing` |
| StandardDecelerate | `LinearOutSlowInEasing` |
| StandardAccelerate | `FastOutLinearInEasing` |

### Spring Parameter Intuition

**Stiffness** (how fast the animation moves toward its target):

| Value | Constant | Feel |
|---|---|---|
| ~26f | — | Slow, heavy, lethargic |
| 200f | `Spring.StiffnessLow` | Gentle, relaxed |
| 400f | `Spring.StiffnessMediumLow` | Casual, comfortable |
| 1500f | `Spring.StiffnessMedium` | Responsive, default |
| 10000f | `Spring.StiffnessHigh` | Snappy, immediate |

**Damping Ratio** (how much bounce):

| Value | Constant | Feel |
|---|---|---|
| 1.0f | `Spring.DampingRatioNoBouncy` | No overshoot, settles directly |
| 0.75f | `Spring.DampingRatioLowBouncy` | Subtle bounce, professional |
| 0.5f | `Spring.DampingRatioMediumBouncy` | Playful, noticeable bounce |
| 0.2f | `Spring.DampingRatioHighBouncy` | Exaggerated, cartoonish bounce |

### Figma Spring to Compose Conversion

```kotlin
fun figmaSpringToCompose(mass: Float, stiffness: Float, damping: Float): SpringSpec<Float> {
    val dampingRatio = damping / (2f * sqrt(stiffness * mass))
    return spring(dampingRatio = dampingRatio, stiffness = stiffness)
}
```

### Production-Validated Spring Specs

```kotlin
val figmaMatchedSpring = spring<Float>(dampingRatio = 0.444f, stiffness = 26.5f)
val responsiveSpring = spring<Float>(dampingRatio = 0.7f, stiffness = 800f)
val snappySpring = spring<Float>(dampingRatio = 0.6f, stiffness = 1000f)
```

---

## Gesture-Driven Animations

### Swipe-to-Dismiss with Animatable

```kotlin
fun Modifier.swipeToDismiss(onDismiss: () -> Unit): Modifier = composed {
    val offsetX = remember { Animatable(0f) }
    val decay = rememberSplineBasedDecay<Float>()

    pointerInput(Unit) {
        coroutineScope {
            while (true) {
                val velocityTracker = VelocityTracker()
                // Wait for touch down
                val pointerId = awaitPointerEventScope {
                    awaitFirstDown().id
                }
                // Cancel any ongoing animation
                offsetX.stop()

                awaitPointerEventScope {
                    horizontalDrag(pointerId) { change ->
                        val horizontalDragOffset = offsetX.value + change.positionChange().x
                        launch { offsetX.snapTo(horizontalDragOffset) }
                        velocityTracker.addPosition(change.uptimeMillis, change.position)
                        change.consume()
                    }
                }

                val velocity = velocityTracker.calculateVelocity().x
                val targetOffsetX = decay.calculateTargetValue(offsetX.value, velocity)

                offsetX.updateBounds(
                    lowerBound = -size.width.toFloat(),
                    upperBound = size.width.toFloat()
                )

                launch {
                    if (abs(targetOffsetX) >= size.width * 0.5f) {
                        // Fling far enough — dismiss
                        offsetX.animateDecay(velocity, decay)
                        onDismiss()
                    } else {
                        // Snap back
                        offsetX.animateTo(
                            targetValue = 0f,
                            initialVelocity = velocity
                        )
                    }
                }
            }
        }
    }.offset { IntOffset(offsetX.value.roundToInt(), 0) }
}
```

### AnchoredDraggable Snap Points

```kotlin
enum class DragValue { Start, Center, End }

@Composable
fun AnchoredDraggableExample() {
    val density = LocalDensity.current
    val anchors = with(density) {
        DraggableAnchors {
            DragValue.Start at -200.dp.toPx()
            DragValue.Center at 0f
            DragValue.End at 200.dp.toPx()
        }
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragValue.Center,
            anchors = anchors,
            positionalThreshold = { totalDistance -> totalDistance * 0.5f },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            animationSpec = spring()
        )
    }

    Box(
        modifier = Modifier
            .offset { IntOffset(state.requireOffset().roundToInt(), 0) }
            .anchoredDraggable(state, Orientation.Horizontal)
            .size(80.dp)
            .background(Color.Blue, RoundedCornerShape(16.dp))
    )
}
```

### Transformable: Pinch, Zoom, Rotate

```kotlin
@Composable
fun TransformableExample() {
    var scale by remember { mutableFloatStateOf(1f) }
    var rotation by remember { mutableFloatStateOf(0f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformableState = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
        scale = (scale * zoomChange).coerceIn(0.5f, 5f)
        rotation += rotationChange
        offset += offsetChange
    }

    Box(
        modifier = Modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                rotationZ = rotation
                translationX = offset.x
                translationY = offset.y
            }
            .transformable(state = transformableState)
            .size(200.dp)
            .background(Color.Blue)
    )
}
```

---

## Animation Recipes

### Shimmer / Skeleton Loading

```kotlin
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -1000f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f)
        ),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + 500f, 0f)
    )

    background(shimmerBrush)
}

@Composable
fun SkeletonCard() {
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .shimmerEffect()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(20.dp)
                .clip(RoundedCornerShape(4.dp))
                .shimmerEffect()
        )
    }
}

@Composable
fun ContentWithLoading(isLoading: Boolean, content: @Composable () -> Unit) {
    Crossfade(targetState = isLoading, label = "loading_crossfade") { loading ->
        if (loading) {
            SkeletonCard()
        } else {
            content()
        }
    }
}
```

### Staggered List Entrance

```kotlin
@Composable
fun StaggeredListEntrance(items: List<String>) {
    Column {
        items.forEachIndexed { index, item ->
            val animatable = remember { Animatable(0f) }
            LaunchedEffect(Unit) {
                delay(index * 100L)
                animatable.animateTo(
                    targetValue = 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
            }
            Text(
                text = item,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = animatable.value
                        translationX = (1f - animatable.value) * 100f
                    }
                    .padding(8.dp)
            )
        }
    }
}
```

### Swipe-to-Dismiss (Material 3)

```kotlin
@Composable
fun SwipeToDismissItem(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value != SwipeToDismissBoxValue.Settled) {
                onDismiss()
                true
            } else false
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Color.Green
                    SwipeToDismissBoxValue.EndToStart -> Color.Red
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "dismiss_bg"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                    else -> Alignment.CenterEnd
                }
            ) {
                Icon(
                    imageVector = when (dismissState.targetValue) {
                        SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Done
                        else -> Icons.Default.Delete
                    },
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        content()
    }
}
```

### Expandable Card

```kotlin
@Composable
fun ExpandableCard(title: String, description: String) {
    var expanded by remember { mutableStateOf(false) }
    val arrowRotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "arrow_rotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
            .clickable { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = title, style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    modifier = Modifier.graphicsLayer { rotationZ = arrowRotation }
                )
            }
            AnimatedVisibility(visible = expanded) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
```

### Pull-to-Refresh Custom

```kotlin
@Composable
fun CustomPullToRefresh(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        indicator = { state ->
            val distanceFraction = state.distanceFraction.coerceIn(0f, 1f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refreshing",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer {
                            scaleX = distanceFraction
                            scaleY = distanceFraction
                            rotationZ = distanceFraction * 360f
                        }
                )
            }
        }
    ) {
        content()
    }
}
```

### FAB Morph

**Pattern 1: ExtendedFloatingActionButton with scroll-driven expand/collapse**

```kotlin
@Composable
fun CollapsibleFab(listState: LazyListState) {
    val expandedFab by remember {
        derivedStateOf { listState.firstVisibleItemIndex == 0 }
    }

    ExtendedFloatingActionButton(
        onClick = { /* action */ },
        expanded = expandedFab,
        icon = { Icon(Icons.Default.Add, contentDescription = "Add") },
        text = { Text("New Item") }
    )
}
```

**Pattern 2: Exploding FAB with updateTransition**

```kotlin
@Composable
fun ExplodingFab(isExpanded: Boolean, onClick: () -> Unit) {
    val transition = updateTransition(targetState = isExpanded, label = "fab_explode")

    val size by transition.animateDp(label = "size") { if (it) 200.dp else 56.dp }
    val cornerRadius by transition.animateDp(label = "corner") { if (it) 16.dp else 28.dp }
    val color by transition.animateColor(label = "color") {
        if (it) MaterialTheme.colorScheme.secondaryContainer
        else MaterialTheme.colorScheme.primaryContainer
    }
    val contentAlpha by transition.animateFloat(label = "alpha") { if (it) 1f else 0f }

    Surface(
        modifier = Modifier.size(size).clickable { onClick() },
        shape = RoundedCornerShape(cornerRadius),
        color = color
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (!isExpanded) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
            Column(
                modifier = Modifier.graphicsLayer { alpha = contentAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Expanded content
                Text("Option 1")
                Text("Option 2")
                Text("Option 3")
            }
        }
    }
}
```

### Bottom Sheet Drag

```kotlin
enum class SheetValue { Hidden, Collapsed, Expanded }

@Composable
fun DraggableBottomSheet(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val anchors = with(density) {
        DraggableAnchors {
            SheetValue.Hidden at 0f
            SheetValue.Collapsed at -200.dp.toPx()
            SheetValue.Expanded at -600.dp.toPx()
        }
    }

    val state = remember {
        AnchoredDraggableState(
            initialValue = SheetValue.Hidden,
            anchors = anchors,
            positionalThreshold = { totalDistance -> totalDistance * 0.5f },
            velocityThreshold = { with(density) { 125.dp.toPx() } },
            animationSpec = spring(stiffness = Spring.StiffnessMediumLow)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        content()

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .offset { IntOffset(0, (state.requireOffset()).roundToInt()) }
                .anchoredDraggable(state, Orientation.Vertical),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxWidth().height(600.dp).padding(16.dp)) {
                // Drag handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .width(40.dp)
                        .height(4.dp)
                        .background(Color.Gray, RoundedCornerShape(2.dp))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Sheet Content")
            }
        }
    }
}
```

### Parallax Scroll Header

```kotlin
@Composable
fun ParallaxHeader(scrollState: ScrollState) {
    val scrollOffset = scrollState.value.toFloat()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .graphicsLayer {
                translationY = scrollOffset * 0.6f // Parallax factor
                scaleX = 1f + (scrollOffset * 0.001f).coerceAtLeast(0f)
                scaleY = 1f + (scrollOffset * 0.001f).coerceAtLeast(0f)
                alpha = (1f - (scrollOffset / 600f)).coerceIn(0f, 1f)
            }
    ) {
        Image(
            painter = painterResource(R.drawable.header),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
```

### Animated Tab Switch

```kotlin
@Composable
fun AnimatedTabContent(selectedTabIndex: Int) {
    AnimatedContent(
        targetState = selectedTabIndex,
        transitionSpec = {
            val direction = if (targetState > initialState) 1 else -1
            slideInHorizontally(
                initialOffsetX = { fullWidth -> direction * fullWidth },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)) togetherWith
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -direction * fullWidth },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300)) using
            SizeTransform(clip = false)
        },
        label = "tab_content"
    ) { tabIndex ->
        when (tabIndex) {
            0 -> TabOneContent()
            1 -> TabTwoContent()
            2 -> TabThreeContent()
        }
    }
}
```

---

## Sequential/Parallel Animation Choreography

### Sequential (Coroutine Chaining)

Each `animateTo` suspends until complete, so chaining them creates sequential animation:

```kotlin
val alpha = remember { Animatable(0f) }
val translateY = remember { Animatable(100f) }
val scale = remember { Animatable(0.5f) }

LaunchedEffect(Unit) {
    alpha.animateTo(1f, animationSpec = tween(300))
    translateY.animateTo(0f, animationSpec = spring())
    scale.animateTo(1f, animationSpec = tween(200))
}
```

### Parallel (Multiple launch blocks)

```kotlin
val alpha = remember { Animatable(0f) }
val translateY = remember { Animatable(100f) }

LaunchedEffect(Unit) {
    coroutineScope {
        launch { alpha.animateTo(1f, animationSpec = tween(300)) }
        launch { translateY.animateTo(0f, animationSpec = spring()) }
    }
    // Code here runs after BOTH animations complete
}
```

### Staggered Delays

```kotlin
val items = remember { List(5) { Animatable(0f) } }

LaunchedEffect(Unit) {
    items.forEachIndexed { index, animatable ->
        launch {
            delay(index * 80L)
            animatable.animateTo(1f, animationSpec = spring())
        }
    }
}
```

### Mixed Sequential + Parallel

```kotlin
LaunchedEffect(Unit) {
    // Phase 1: Sequential — fade in first
    alpha.animateTo(1f, animationSpec = tween(200))

    // Phase 2: Parallel — move and scale at the same time
    coroutineScope {
        launch { translateY.animateTo(0f, animationSpec = spring()) }
        launch { scale.animateTo(1f, animationSpec = spring()) }
    }

    // Phase 3: Sequential — final flourish after Phase 2 completes
    rotation.animateTo(360f, animationSpec = tween(400))
}
```

---

## Predictive Back Gesture Animation (Android 14+)

### NavHost Transitions

```kotlin
NavHost(
    navController = navController,
    startDestination = "home",
    enterTransition = {
        slideInHorizontally(initialOffsetX = { it }) + fadeIn(animationSpec = tween(300))
    },
    exitTransition = {
        slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut(animationSpec = tween(300))
    },
    popEnterTransition = {
        slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn(animationSpec = tween(300))
    },
    popExitTransition = {
        slideOutHorizontally(targetOffsetX = { it }) + fadeOut(animationSpec = tween(300))
    }
) {
    composable("home") { HomeScreen() }
    composable("detail") { DetailScreen() }
}
```

### PredictiveBackHandler

```kotlin
@Composable
fun PredictiveBackExample(onBack: () -> Unit) {
    var boxScale by remember { mutableFloatStateOf(1f) }

    PredictiveBackHandler(enabled = true) { progress: Flow<BackEventCompat> ->
        try {
            progress.collect { backEvent ->
                boxScale = 1f - (0.3f * backEvent.progress)
            }
            onBack()
        } catch (e: CancellationException) {
            boxScale = 1f
            throw e
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                scaleX = boxScale
                scaleY = boxScale
            }
    ) {
        Text("Swipe back to see scale animation")
    }
}
```

### M3 Automatic Predictive Back

These Material 3 components animate with predictive back gestures out of the box (no extra code needed):

- `SearchBar` — collapses back on swipe
- `ModalBottomSheet` — slides down with gesture progress
- `ModalNavigationDrawer` — slides closed with gesture progress

---

## Additional Anti-Patterns

### Don't: Read animated state in composition when draw-phase suffices

```kotlin
// BAD: Reads alpha during composition, triggers recomposition every frame
val alpha by animateFloatAsState(targetValue = 0.5f, label = "alpha")
Box(modifier = Modifier.alpha(alpha))

// GOOD: Reads alpha during draw phase only, skips recomposition
val alpha by animateFloatAsState(targetValue = 0.5f, label = "alpha")
Box(modifier = Modifier.graphicsLayer { this.alpha = alpha })
```

### Don't: Use offset(x, y) for animated movement

```kotlin
// BAD: Parameter-based offset triggers recomposition + relayout
val animatedDp by animateDpAsState(targetValue = 100.dp, label = "x")
Box(modifier = Modifier.offset(x = animatedDp))

// BETTER: Lambda offset — layout phase only, no recomposition
val animatedPx by animateIntAsState(targetValue = 300, label = "x")
Box(modifier = Modifier.offset { IntOffset(animatedPx, 0) })

// BEST: graphicsLayer — draw phase only
val animatedPx by animateFloatAsState(targetValue = 300f, label = "x")
Box(modifier = Modifier.graphicsLayer { translationX = animatedPx })
```

### Don't: Use updateTransition for independent properties

```kotlin
// BAD: Properties don't need synchronization but are coupled
val transition = updateTransition(targetState = state, label = "t")
val alpha by transition.animateFloat(label = "a") { if (it) 1f else 0f }
val size by transition.animateDp(label = "s") { if (it) 200.dp else 100.dp }

// GOOD: Independent properties use separate animate*AsState
val alpha by animateFloatAsState(targetValue = if (state) 1f else 0f, label = "alpha")
val size by animateDpAsState(targetValue = if (state) 200.dp else 100.dp, label = "size")
```

### Don't: Hardcode arbitrary durations

```kotlin
// BAD: Arbitrary duration with no design rationale
val anim by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(durationMillis = 347),
    label = "anim"
)

// GOOD: Use M3 motion tokens for consistency
val anim by animateFloatAsState(
    targetValue = 1f,
    animationSpec = tween(durationMillis = MotionTokens.DurationMedium2.toInt()),
    label = "anim"
)

// BETTER: Use spring() for interruptible, natural-feeling animations
val anim by animateFloatAsState(
    targetValue = 1f,
    animationSpec = spring(stiffness = Spring.StiffnessMedium),
    label = "anim"
)
```
