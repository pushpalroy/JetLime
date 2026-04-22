# Platform-Specific APIs and Gotchas (Compose Multiplatform)

Compose Multiplatform shares most UI code across platforms, but entry points, interop APIs, and runtime behavior differ significantly. This reference covers what you need to know per platform.

---

## 1. Desktop (JVM)

### Entry Points

Three ways to launch a Compose Desktop application:

```kotlin
// Standard — blocks the main thread until all windows close
fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "My App") {
        App()
    }
}

// Suspending — same behavior, usable from coroutines
suspend fun main() = awaitApplication {
    Window(onCloseRequest = ::exitApplication, title = "My App") {
        App()
    }
}

// Non-blocking — launches in a CoroutineScope, does not block
fun main() {
    val scope = CoroutineScope(Dispatchers.Default)
    scope.launchApplication {
        Window(onCloseRequest = ::exitApplication, title = "My App") {
            App()
        }
    }
    // scope continues running other coroutines
}
```

### Window Composable

`Window` is the top-level container. Key parameters:

| Parameter | Type | Default | Purpose |
|-----------|------|---------|---------|
| `onCloseRequest` | `() -> Unit` | required | Called when user clicks close. Use `::exitApplication` or custom logic |
| `state` | `WindowState` | auto | Controls size, position, placement (maximized/minimized) |
| `title` | `String` | `""` | Window title bar text |
| `icon` | `Painter?` | `null` | Window and taskbar icon |
| `resizable` | `Boolean` | `true` | Whether user can resize |
| `alwaysOnTop` | `Boolean` | `false` | Pin window above all others |
| `visible` | `Boolean` | `true` | Show/hide without destroying |
| `undecorated` | `Boolean` | `false` | Remove OS title bar and borders |
| `transparent` | `Boolean` | `false` | Transparent background (requires `undecorated = true`) |

Full example with MenuBar:

```kotlin
fun main() = application {
    val windowState = rememberWindowState(
        size = DpSize(800.dp, 600.dp),
        position = WindowPosition(Alignment.Center)
    )

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "My App"
    ) {
        MenuBar {
            Menu("File") {
                Item("New", shortcut = KeyShortcut(Key.N, meta = true)) { /* handle */ }
                Item("Open", shortcut = KeyShortcut(Key.O, meta = true)) { /* handle */ }
                Separator()
                Item("Exit", onClick = ::exitApplication)
            }
            Menu("Edit") {
                Item("Undo", shortcut = KeyShortcut(Key.Z, meta = true)) { /* handle */ }
                Item("Redo", shortcut = KeyShortcut(Key.Z, meta = true, shift = true)) { /* handle */ }
            }
        }
        App()
    }
}
```

### Multi-Window Management

```kotlin
fun main() = application {
    var showSettings by remember { mutableStateOf(false) }

    Window(onCloseRequest = ::exitApplication, title = "Main") {
        Button(onClick = { showSettings = true }) { Text("Settings") }
        App()
    }

    if (showSettings) {
        Window(
            onCloseRequest = { showSettings = false },
            title = "Settings",
            state = rememberWindowState(size = DpSize(400.dp, 300.dp))
        ) {
            SettingsScreen()
        }
    }
}
```

### Tray Icon

```kotlin
fun main() = application {
    var isVisible by remember { mutableStateOf(true) }

    Tray(
        icon = painterResource("app_icon.png"),
        menu = {
            Item("Show/Hide", onClick = { isVisible = !isVisible })
            Item("Exit", onClick = ::exitApplication)
        }
    )

    if (isVisible) {
        Window(onCloseRequest = { isVisible = false }, title = "My App") {
            App()
        }
    }
}
```

**Gotcha:** On macOS, `Tray` responds to right-click only. Left-click shows nothing by default. This is OS behavior, not a bug.

### DialogWindow

```kotlin
var showDialog by remember { mutableStateOf(false) }

if (showDialog) {
    DialogWindow(
        onCloseRequest = { showDialog = false },
        title = "Confirm Action",
        state = rememberDialogState(size = DpSize(350.dp, 200.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Are you sure?")
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                Button(onClick = { /* confirm */ showDialog = false }) { Text("OK") }
            }
        }
    }
}
```

### Interop: ComposePanel (Compose inside Swing/AWT)

Embed Compose content inside an existing Swing application:

```kotlin
fun main() {
    SwingUtilities.invokeLater {
        val frame = JFrame("Swing + Compose")
        val composePanel = ComposePanel()
        composePanel.setContent {
            MaterialTheme {
                App()
            }
        }
        // Add to any Swing container — JPanel, JLayeredPane, JSplitPane, etc.
        frame.contentPane.add(composePanel, BorderLayout.CENTER)
        frame.setSize(800, 600)
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.isVisible = true
    }
}
```

### Interop: SwingPanel (Swing inside Compose)

Embed a Swing/AWT component inside Compose:

```kotlin
@Composable
fun LegacyTableView(data: List<List<String>>) {
    SwingPanel(
        modifier = Modifier.fillMaxSize(),
        factory = {
            val model = DefaultTableModel(
                data.map { it.toTypedArray() }.toTypedArray(),
                arrayOf("Name", "Email", "Role")
            )
            JScrollPane(JTable(model))
        },
        update = { scrollPane ->
            // Called on recomposition — update the Swing component here
        }
    )
}
```

### Scrollbar (Desktop-Only)

Desktop has explicit scrollbar composables that do not exist on mobile platforms:

```kotlin
@Composable
fun ScrollableContent() {
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(end = 12.dp) // leave room for scrollbar
        ) {
            repeat(100) { Text("Item $it", modifier = Modifier.padding(8.dp)) }
        }

        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            style = ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 8.dp,
                shape = RoundedCornerShape(4.dp),
                hoverDurationMillis = 300,
                unhoverColor = Color.Black.copy(alpha = 0.12f),
                hoverColor = Color.Black.copy(alpha = 0.50f)
            )
        )
    }
}

// For LazyColumn:
val lazyListState = rememberLazyListState()
VerticalScrollbar(
    adapter = rememberScrollbarAdapter(lazyListState),
    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
)
```

`HorizontalScrollbar` works identically for horizontal scroll containers.

**Key scrolling gotcha:** Desktop scrolling is mouse-wheel only. There are no touch physics, no momentum/fling, and no overscroll bounce effect. Scrolling feels "mechanical" compared to mobile. This is expected desktop behavior, not a bug.

---

## 2. iOS

### Entry Point

The iOS entry point creates a `UIViewController` that hosts Compose content:

```kotlin
// In iosMain — typically in a file like MainViewController.kt
fun MainViewController(): UIViewController = ComposeUIViewController { App() }
```

This is called from Swift in your iOS app target:

```swift
// In Swift (e.g., ContentView.swift or AppDelegate)
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }
    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
```

### ComposeUIViewController Configuration

```kotlin
fun MainViewController(): UIViewController = ComposeUIViewController(
    configure = {
        opaque = true               // true = opaque background (better perf), false = transparent
        parallelRendering = false    // experimental: parallel rendering pipeline
        onFocusBehavior = OnFocusBehavior.FocusableAboveKeyboard  // auto-scroll focused field above keyboard
    }
) {
    App()
}
```

### UIKit Interop — UIKitView

Embed native UIKit views inside Compose:

```kotlin
@Composable
fun NativeMapView(latitude: Double, longitude: Double) {
    val region = MKCoordinateRegion(
        CLLocationCoordinate2DMake(latitude, longitude),
        MKCoordinateSpanMake(0.1, 0.1)
    )

    UIKitView(
        factory = { MKMapView() },
        modifier = Modifier.fillMaxSize(),
        update = { mapView ->
            mapView.setRegion(region, animated = true)
            mapView.mapType = MKMapType.MKMapTypeStandard
        },
        properties = UIKitInteropProperties(
            interactionMode = UIKitInteropInteractionMode.Cooperative(delayMillis = 150)
        )
    )
}
```

**Interaction modes:**
- `Cooperative(delayMillis)` — Compose gets first touch. After `delayMillis`, touch passes to UIKit. Use for maps, web views, scrollable native content.
- `NonCooperative` — UIKit gets all touch events. Compose touch handling is blocked in the UIKitView area. Use only when the native view must own all gestures.

### CompositionLocals for iOS

```kotlin
// Access the hosting UIViewController
val viewController = LocalUIViewController.current
viewController.presentViewController(picker, animated = true, completion = null)

// Access the underlying UIView
val uiView = LocalUIView.current
```

### Key Gotchas

**DisposableEffect cleanup unreliable with UINavigationController:**
`DisposableEffect.onDispose` may not fire when a Compose screen is popped from a `UINavigationController`. Do not rely on it for critical cleanup (e.g., releasing resources, stopping location updates). Use ViewModel `onCleared()` or explicit lifecycle observation instead.

**Keyboard Done button inserts newline instead of submitting** (JetBrains/compose-multiplatform#3473):
On iOS, pressing "Done" on a `TextField` with `ImeAction.Done` may insert `\n` instead of triggering `onImeAction`. Workaround:

```kotlin
TextField(
    value = text,
    onValueChange = { newText ->
        // Filter out newlines inserted by Done key
        if ("\n" in newText && "\n" !in text) {
            onSubmit()
        } else {
            text = newText
        }
    },
    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
)
```

**TextField in scrollable Column pushes TopAppBar off screen** (JetBrains/compose-multiplatform#3621):
When a `TextField` inside a scrollable `Column` receives focus and the keyboard appears, the entire content shifts up, pushing the `TopAppBar` out of view. Mitigate by using `LazyColumn` or wrapping the scrollable area below the app bar with `imePadding()`.

**Touch delay in Cooperative mode:**
With `UIKitInteropInteractionMode.Cooperative(delayMillis = 150)`, there is a noticeable ~150ms delay before UIKit views receive touch. Users may perceive maps/web views as unresponsive. Reduce `delayMillis` if the native view does not conflict with Compose gestures, but be aware that lower values increase accidental UIKit touch interception.

**ProMotion 120Hz requires Info.plist entry:**
Compose renders at 60Hz by default on ProMotion displays (iPhone 13 Pro+, iPad Pro). Add this to `Info.plist` to enable 120Hz:

```xml
<key>CADisableMinimumFrameDurationOnPhone</key>
<true/>
```

Without this, animations and scrolling will feel noticeably less smooth than native SwiftUI on the same hardware.

**App size overhead:**
A minimal Compose Multiplatform iOS app is approximately 24.8 MB, compared to ~1.7 MB for an equivalent native SwiftUI app. The Skia rendering engine adds 15-20 MB. This is a fixed cost that does not grow significantly with app complexity, but it matters for markets sensitive to download size.

**Flows continue running in background** (JetBrains/compose-multiplatform#3889):
Unlike Android (where lifecycle-aware collection pauses in the background), Compose for iOS does not automatically suspend `Flow` collection when the app moves to background. Flows will keep collecting, potentially wasting CPU and battery. You must observe `UIApplication` lifecycle notifications manually:

```kotlin
@Composable
fun LifecycleAwareCollection(flow: Flow<Data>) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val data by flow.collectAsStateWithLifecycle(
        initialValue = Data.Empty,
        lifecycle = lifecycle
    )
    // Only collects when in foreground
}
```

If `collectAsStateWithLifecycle` is not available on your CMP version, use a `DisposableEffect` that observes `NSNotificationCenter` for `UIApplicationDidEnterBackgroundNotification` and `UIApplicationWillEnterForegroundNotification`.

---

## 3. Web / WASM

### Entry Point

```kotlin
fun main() {
    ComposeViewport(viewportContainerId = "root") {
        App()
    }
}
```

The corresponding HTML needs a container element:

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My App</title>
    <script src="skiko.js"></script>
    <script src="composeApp.js"></script>
</head>
<body>
    <div id="root" style="width: 100vw; height: 100vh;"></div>
</body>
</html>
```

### Fundamental Limitation: Canvas-Only Rendering

Compose for Web renders EVERYTHING to a single `<canvas>` element. There are zero DOM elements for your UI content. This has severe consequences:

| Feature | Works? | Explanation |
|---------|--------|-------------|
| Ctrl+F / Cmd+F text search | No | Canvas pixels, not DOM text |
| Browser translate (Google Translate) | No | No DOM text to translate |
| HTML form autofill | No | No `<input>` elements |
| Browser context menus | No | Canvas intercepts right-click |
| Password manager autofill | No | No `<input type="password">` |
| SEO / search engine indexing | No | Blank `<canvas>` to crawlers |
| Server-side rendering (SSR) | No | Requires JS + WASM runtime |
| Screen reader accessibility | Partial | Compose semantics mapped to ARIA, but limited |
| Copy/paste text | Partial | Works within Compose, not with browser native |

**If you need SEO, text search, or browser-native form behavior, Compose for Web is not the right choice.** Use Kobweb (Compose HTML) or a traditional web framework for content-heavy pages.

### Navigation and Browser History

Routes integrate with the browser's History API:

```kotlin
// Routes update the URL bar — Back/Forward buttons work
// The exact API depends on your navigation library (e.g., Voyager, Decompose)
// Key behavior: browser URL updates reflect Compose navigation state
```

### Browser Compatibility

Compose WASM requires WASM GC (Garbage Collection) support:

| Browser | Minimum Version |
|---------|----------------|
| Chrome | 119+ |
| Firefox | 120+ |
| Safari | 18.2+ |
| Edge | 119+ (Chromium-based) |

Older browsers will show a blank page. There is no graceful fallback.

### Bundle Size

Expect multi-megabyte downloads before first paint:
- Skiko WASM module: ~5-8 MB
- Application code: varies, but 2-5 MB typical
- Total initial load: often 8-15 MB

This makes Compose WASM unsuitable for landing pages or content sites where first-paint speed matters. It works best for internal tools, dashboards, or app-like experiences where users accept a loading phase.

---

## 4. Performance Across Platforms

### Rendering Stack

Each platform uses a different rendering backend, but all go through Skia:

| Platform | Skia Source | Graphics API | Notes |
|----------|------------|--------------|-------|
| Android | Built into the OS | OpenGL ES / Vulkan | No extra binary size. Vulkan on Android 10+ |
| iOS | Bundled with app (Skiko) | Metal | Adds 15-20 MB to app size |
| Desktop | Bundled with app (Skiko) | OpenGL / Metal (macOS) / DirectX (Windows) | Auto-selects best backend |
| Web | Compiled to WASM (Skiko) | WebGL / WebGPU | Single `<canvas>`, all rendering via GPU |

### iOS Performance (CMP 1.8+)

Performance has improved significantly since early versions:

- **Startup time:** Comparable to native SwiftUI. Cold start overhead is minimal once Skia is initialized.
- **Scrolling:** On par with SwiftUI, including 120Hz on ProMotion displays (with the Info.plist entry above).
- **Complex animations:** Generally smooth, but frame drops can occur with deeply nested animated composables or heavy `Canvas` drawing during transitions. Profile with Instruments if you see jank.

### Configurable Frame Rate

For battery optimization, you can configure the target frame rate:

```kotlin
// Reduce frame rate when idle or showing static content
// Platform-specific API — check your CMP version for exact usage
ComposeUIViewController(
    configure = {
        // platformLayers configuration varies by version
    }
) {
    App()
}
```

On iOS, this is particularly useful for apps that show mostly static content but occasionally animate. Reducing from 120Hz to 60Hz (or even 30Hz for static screens) can meaningfully improve battery life.

### Cross-Platform Performance Tips

1. **Avoid `Modifier.graphicsLayer` with `clip = true` on iOS** unless needed. Clipping with Skia on Metal has higher cost than native UIKit clipping.

2. **Image decoding is synchronous on iOS** by default. Use `rememberAsyncImagePainter` (Coil) or similar to avoid blocking the main thread on image-heavy screens.

3. **Desktop: disable vsync for benchmarking** but never in production. Without vsync, frame rates become erratic and tearing is visible.

4. **Web: minimize composable count.** Every composable draws to canvas via WebGL. Complex UIs hit GPU limits faster than on native platforms.

5. **Shared code performance is generally the same across platforms.** The Kotlin compiler generates platform-optimized bytecode. Performance differences come from the rendering backend, not from your composable logic.
