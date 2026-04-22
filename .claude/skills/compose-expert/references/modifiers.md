# Jetpack Compose Modifiers Reference

Modifiers are the primary way to decorate or augment a composable. They apply layout, drawing, gesture, and accessibility behavior. Understanding modifier ordering and the available APIs is critical for correctness and performance.

## Modifier Chain Ordering

Order matters. Modifiers are applied left-to-right in the DSL, but conceptually they wrap bottom-to-top. Each modifier receives a lambda that draws/measures the content below it.

```kotlin
// Example: different results depending on order
Box(
    Modifier
        .background(Color.Red)
        .padding(16.dp)
        .size(100.dp)
)
// Red background wraps the padded content, which wraps the 100x100 box

Box(
    Modifier
        .size(100.dp)
        .padding(16.dp)
        .background(Color.Red)
)
// 100x100 box is padded, then the whole thing (132x132) gets red background
```

**Do:** Order modifiers from outer (layout/sizing) to inner (styling/interaction).
**Don't:** Put `size` after `padding` if you want the padding included in the final size.

Source: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/Modifier.kt`

## Common Modifier Patterns

### Padding and Sizing

```kotlin
// Padding: external spacing around content
Box(Modifier.padding(16.dp)) { }

// Size: exact dimensions (overrides requested size from parent)
Box(Modifier.size(100.dp)) { }
Box(Modifier.size(width = 200.dp, height = 100.dp)) { }

// FillMaxWidth/FillMaxHeight: expand to available space
Box(Modifier.fillMaxWidth(0.8f)) { }  // 80% of parent width
Box(Modifier.fillMaxSize()) { }       // 100% of parent

// Do: use fillMaxWidth before adding padding for alignment clarity
Column(Modifier.fillMaxWidth()) {
    Box(Modifier.padding(16.dp).fillMaxWidth()) { }
}

// Don't: apply fillMaxWidth after background if you want background to expand
// Instead:
Box(Modifier.fillMaxWidth().background(Color.Blue)) { }
```

### Background and Border

```kotlin
// Background applies a color to the surface
Box(Modifier.background(Color.Blue)) { }
Box(Modifier.background(Color.Blue, shape = RoundedCornerShape(8.dp))) { }

// Border draws a stroke (order matters!)
Box(
    Modifier
        .size(100.dp)
        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        .background(Color.White)
)
// The border is drawn AFTER background in visual order (because modifiers below it are drawn first)

// Combine background + border: apply border first in chain
Box(
    Modifier
        .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
        .background(Color.White)
)
```

### Clipping

```kotlin
// Clip content to a shape
Box(Modifier.clip(RoundedCornerShape(8.dp))) {
    Image(painter = painterResource(id = R.drawable.my_image), contentDescription = "")
}

// Do: apply clip before background if you want background inside the shape
Box(
    Modifier
        .clip(RoundedCornerShape(8.dp))
        .background(Color.Blue)
) { }

// Don't: apply background then clip (works but semantically wrong)
Box(
    Modifier
        .background(Color.Blue)
        .clip(RoundedCornerShape(8.dp))
) { }
```

## Clickable and Combined Clickable

```kotlin
// Basic click handling with ripple effect (Material 3 default)
Button(onClick = { }) { Text("Click me") }

// Manual clickable with ripple
Box(
    Modifier
        .size(100.dp)
        .clickable(
            indication = ripple(),  // Material ripple feedback
            interactionSource = remember { MutableInteractionSource() }
        ) { /* handle click */ }
)

// Combined clickable: long press + double click + click
Box(
    Modifier
        .combinedClickable(
            onClick = { },
            onLongClick = { },
            onDoubleClick = { },
            indication = ripple()
        )
) { }

// Do: provide explicit interactionSource for testing/state observation
val interactionSource = remember { MutableInteractionSource() }
Box(
    Modifier.clickable(
        interactionSource = interactionSource,
        indication = ripple()
    ) { }
)

// Don't: forget indication parameter (will have no visual feedback)
Box(Modifier.clickable { }) { }  // No ripple
```

## Modifier.composed vs Modifier.Node

The old API (`composed`) is being phased out in favor of the new `ModifierNodeElement` API. Both work, but new code should use the latter.

### Old API: Modifier.composed

```kotlin
fun Modifier.myCustomModifier(value: String) = composed {
    val state = remember { mutableStateOf(value) }
    this.then(
        Modifier
            .background(Color.Blue)
            .clickable { state.value = "updated" }
    )
}
```

- Creates a new composable scope
- Captures composition locals
- Causes recomposition when remember state changes
- Deprecated but still supported

### New API: Modifier.Node

```kotlin
class MyCustomNode(val value: String) : Modifier.Node {
    override fun onDetach() {
        // Cleanup when removed
    }
}

data class MyCustomElement(val value: String) : ModifierNodeElement<MyCustomNode>() {
    override fun create() = MyCustomNode(value)
    override fun update(node: MyCustomNode) {
        node.value = value
    }
}

fun Modifier.myCustomModifier(value: String) = this.then(MyCustomElement(value))
```

**Do:** Use `Modifier.Node` for new custom modifiers. It's more efficient and doesn't create composition scopes.
**Don't:** Create new `composed` modifiers; migrate existing ones to `Modifier.Node`.

Source: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/modifier/ModifierNodeElement.kt`

## Layout vs Drawing vs Pointer Input Modifiers

Modifiers fall into categories that affect when they execute:

```kotlin
// Layout modifier: affects measurement and layout pass
fun Modifier.customSize(width: Dp, height: Dp) =
    this.then(object : LayoutModifier {
        override fun MeasureScope.measure(measurable: Measurable, constraints: Constraints) =
            measurable.measure(Constraints.fixed(width.roundToPx(), height.roundToPx()))
                .run { layout(width = size.width, height = size.height) { place(0, 0) } }
    })

// Drawing modifier: doesn't affect layout, just draws after content
fun Modifier.customDraw() = drawBehind { drawCircle(Color.Red) }

// Pointer input modifier: handles gestures/events
fun Modifier.detectCustomGesture() = pointerInput(Unit) {
    detectTapGestures { offset -> /* handle */ }
}
```

**Do:** Use layout modifiers for sizing/positioning, drawing modifiers for visual effects, pointer modifiers for input.
**Don't:** Use layout modifiers to create visual effects; use drawing modifiers instead.

## Modifier.graphicsLayer — Performance Implications

`graphicsLayer` applies transformations at the graphics rendering level. It's more efficient than recomposing for animations.

```kotlin
// Efficient: transforms applied on the graphics layer, no recomposition
Box(
    Modifier.graphicsLayer(
        scaleX = 1.2f,
        scaleY = 1.2f,
        translationX = 10f,
        rotationZ = 45f,
        alpha = 0.8f
    )
) { }

// Less efficient: recomposes every frame
var scaleX by remember { mutableStateOf(1f) }
LaunchedEffect(Unit) {
    while (true) {
        scaleX = 1.2f
        delay(16)
    }
}
Box(Modifier.scale(scaleX)) { }
```

**Do:** Use `graphicsLayer` for animations and frequent property changes.
**Don't:** Animate state values that trigger recomposition when `graphicsLayer` would suffice.

Source: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/graphics/GraphicsLayerModifier.kt`

## Modifier.semantics — Accessibility

Semantics describe the meaning of UI elements for screen readers and accessibility tests.

```kotlin
// Add semantic label
Button(onClick = { }) {
    Icon(Icons.Default.Add, contentDescription = null)
    Text("Add item")
}

// Custom semantic properties
Box(
    Modifier
        .size(100.dp)
        .semantics {
            contentDescription = "Custom box"
            onClick(label = "Activate") { true }
        }
) { }

// Do: always provide contentDescription for images
Image(
    painter = painterResource(id = R.drawable.icon),
    contentDescription = "User avatar"
)

// Don't: forget contentDescription (screen readers won't announce it)
Image(painter = painterResource(id = R.drawable.icon), contentDescription = null) // Wrong
```

Source: `compose/ui/ui/src/commonMain/kotlin/androidx/compose/ui/semantics/Semantics.kt`

## Modifier.testTag — UI Testing

```kotlin
// Add a test tag for finding composables in tests
Box(Modifier.testTag("my_box")) { }

// In tests:
composeTestRule.onNodeWithTag("my_box").performClick()
composeTestRule.onNodeWithTag("my_box").assertIsDisplayed()
```

**Do:** Use unique, descriptive test tags.
**Don't:** Use test tags in production code for business logic.

## Review Checklist: Modifier Ordering Bugs

### Hardcoded Size After Caller's `modifier` Parameter

When a composable accepts `modifier: Modifier = Modifier` and chains fixed `.height()` / `.width()` / `.size()` after it, caller size constraints are silently ignored or clamped.

```kotlin
// BAD: caller's height is outer constraint, component's 172.dp is inner — component always renders at 172dp
@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier          // caller constraints applied first (outer)
            .fillMaxWidth()
            .height(172.dp)          // inner — wins when smaller, clamped when larger
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Green.copy(alpha = 0.08f)),
    )
}

// Caller expects 200dp but gets 172dp:
BannerCard(modifier = Modifier.height(200.dp))

// Caller expects 100dp — component gets clamped/squished:
BannerCard(modifier = Modifier.height(100.dp))
```

**Why it happens:** Modifier chain resolves outer-to-inner (left-to-right). Outer constraint sets max bounds, inner constraint requests within those bounds. First size constraint wins as the ceiling.

**Fix option 1:** Component defaults first, caller can override via `.then(modifier)`:
```kotlin
// GOOD: component sets defaults, caller's modifier applied last and can override
@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(172.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Green.copy(alpha = 0.08f))
            .then(modifier),         // caller can override size
    )
}
```

**Fix option 2:** Use `defaultMinSize` for flexible sizing:
```kotlin
// GOOD: minimum guaranteed, caller can make it larger
@Composable
fun BannerCard(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 172.dp)  // floor, not ceiling
            .clip(RoundedCornerShape(18.dp))
            .background(Color.Green.copy(alpha = 0.08f)),
    )
}
```

**Rule:** When a composable accepts `modifier: Modifier = Modifier`, never chain fixed `.height()` / `.width()` / `.size()` after the caller's modifier — caller constraints become outer bounds and the component's fixed size either ignores or gets clamped by them. Use `.then(modifier)` at the end or `defaultMinSize` for flexible sizing. Flag in every PR review.

---

## Anti-patterns

### Creating Modifiers in Composition

```kotlin
// Don't: creates a new Modifier every recomposition
@Composable
fun BadModifier() {
    Box(Modifier.padding(16.dp).background(Color.Blue)) { }
}

// Do: extract to a variable or parameter
@Composable
fun GoodModifier(modifier: Modifier = Modifier) {
    Box(modifier.padding(16.dp).background(Color.Blue)) { }
}
```

### Conditional Modifier Chains Done Wrong

```kotlin
// Don't: breaks type checking and readability
val mod = if (isSelected) Modifier.background(Color.Blue) else Modifier
Box(mod.padding(16.dp)) { }

// Do: use then() for conditional chaining
Box(
    Modifier
        .padding(16.dp)
        .then(if (isSelected) Modifier.background(Color.Blue) else Modifier)
) { }
```

---

**Summary:** Master modifier ordering, prefer `Modifier.Node` over `composed`, use `graphicsLayer` for animations, and always consider the semantic layer for accessibility.
