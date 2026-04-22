# Compose Multiplatform Source Reference

> API signatures from [JetBrains/compose-multiplatform-core](https://github.com/JetBrains/compose-multiplatform-core) (branch: jb-main).
> Each signature includes its source file path as a comment.

---

## Desktop APIs

### Application Lifecycle

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Application.desktop.kt
fun application(
    exitProcessOnExit: Boolean = true,
    content: @Composable ApplicationScope.() -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Application.desktop.kt
suspend fun awaitApplication(
    content: @Composable ApplicationScope.() -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Application.desktop.kt
@Stable
interface ApplicationScope {
    fun exitApplication()
}
```

### Window

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Window.desktop.kt
@Composable
fun Window(
    onCloseRequest: () -> Unit,
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    undecorated: Boolean = false,
    transparent: Boolean = false,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    alwaysOnTop: Boolean = false,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable FrameWindowScope.() -> Unit
)
```

### WindowState

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/WindowState.desktop.kt
@Stable
interface WindowState {
    var placement: WindowPlacement
    var isMinimized: Boolean
    var position: WindowPosition
    var size: DpSize
}

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/WindowState.desktop.kt
@Composable
fun rememberWindowState(
    placement: WindowPlacement = WindowPlacement.Floating,
    isMinimized: Boolean = false,
    position: WindowPosition = WindowPosition.PlatformDefault,
    size: DpSize = DpSize(800.dp, 600.dp)
): WindowState

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/WindowPlacement.desktop.kt
enum class WindowPlacement {
    Floating,
    Maximized,
    Fullscreen
}

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/WindowPosition.desktop.kt
@Immutable
sealed class WindowPosition {
    object PlatformDefault : WindowPosition()

    class Aligned(val alignment: Alignment) : WindowPosition()

    class Absolute(val x: Dp, val y: Dp) : WindowPosition()
}
```

### Tray

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Tray.desktop.kt
@Composable
fun ApplicationScope.Tray(
    icon: Painter,
    state: TrayState = rememberTrayState(),
    tooltip: String? = null,
    onAction: () -> Unit = {},
    menu: @Composable MenuScope.() -> Unit = {}
)
```

### MenuBar and Menu Items

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/MenuBar.desktop.kt
@Composable
fun FrameWindowScope.MenuBar(
    content: @Composable MenuBarScope.() -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Menu.desktop.kt
@Composable
fun MenuScope.Menu(
    text: String,
    enabled: Boolean = true,
    mnemonic: Char? = null,
    content: @Composable MenuScope.() -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Menu.desktop.kt
@Composable
fun MenuScope.Item(
    text: String,
    icon: Painter? = null,
    enabled: Boolean = true,
    mnemonic: Char? = null,
    shortcut: KeyShortcut? = null,
    onClick: () -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Menu.desktop.kt
@Composable
fun MenuScope.CheckboxItem(
    text: String,
    checked: Boolean,
    icon: Painter? = null,
    enabled: Boolean = true,
    mnemonic: Char? = null,
    shortcut: KeyShortcut? = null,
    onCheckedChange: (Boolean) -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Menu.desktop.kt
@Composable
fun MenuScope.RadioButtonItem(
    text: String,
    selected: Boolean,
    icon: Painter? = null,
    enabled: Boolean = true,
    mnemonic: Char? = null,
    shortcut: KeyShortcut? = null,
    onClick: () -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Menu.desktop.kt
@Composable
fun MenuScope.Separator()
```

### DialogWindow

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Dialog.desktop.kt
@Composable
fun DialogWindow(
    onCloseRequest: () -> Unit,
    state: DialogState = rememberDialogState(),
    visible: Boolean = true,
    title: String = "Untitled",
    icon: Painter? = null,
    undecorated: Boolean = false,
    transparent: Boolean = false,
    resizable: Boolean = true,
    enabled: Boolean = true,
    focusable: Boolean = true,
    onPreviewKeyEvent: (KeyEvent) -> Boolean = { false },
    onKeyEvent: (KeyEvent) -> Boolean = { false },
    content: @Composable DialogWindowScope.() -> Unit
)

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/DialogState.desktop.kt
@Stable
interface DialogState {
    var position: WindowPosition
    var size: DpSize
}

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/DialogState.desktop.kt
@Composable
fun rememberDialogState(
    position: WindowPosition = WindowPosition.PlatformDefault,
    size: DpSize = DpSize(400.dp, 300.dp)
): DialogState
```

### Notification

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/window/Notification.desktop.kt
class Notification(
    val title: String,
    val message: String,
    val type: Type = Type.None
) {
    enum class Type {
        None,
        Info,
        Warning,
        Error
    }
}
```

### Swing / AWT Interop

```kotlin
// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/awt/ComposePanel.desktop.kt
class ComposePanel : JLayeredPane() {
    fun setContent(content: @Composable () -> Unit)
    fun dispose()
}

// Source: compose/ui/ui/src/desktopMain/kotlin/androidx/compose/ui/awt/SwingPanel.desktop.kt
@Composable
fun <T : Component> SwingPanel(
    factory: () -> T,
    modifier: Modifier = Modifier,
    update: (T) -> Unit = {},
    background: Color = Color.White
)
```

### Scrollbar

```kotlin
// Source: compose/foundation/foundation/src/desktopMain/kotlin/androidx/compose/foundation/Scrollbar.desktop.kt
@Composable
fun VerticalScrollbar(
    adapter: ScrollbarAdapter,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    style: ScrollbarStyle = LocalScrollbarStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
)

// Source: compose/foundation/foundation/src/desktopMain/kotlin/androidx/compose/foundation/Scrollbar.desktop.kt
@Composable
fun HorizontalScrollbar(
    adapter: ScrollbarAdapter,
    modifier: Modifier = Modifier,
    reverseLayout: Boolean = false,
    style: ScrollbarStyle = LocalScrollbarStyle.current,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
)

// Source: compose/foundation/foundation/src/desktopMain/kotlin/androidx/compose/foundation/ScrollbarStyling.desktop.kt
@Immutable
data class ScrollbarStyle(
    val minimalHeight: Dp,
    val thickness: Dp,
    val shape: Shape,
    val hoverDurationMillis: Int,
    val unhoverColor: Color,
    val hoverColor: Color
)

// Source: compose/foundation/foundation/src/desktopMain/kotlin/androidx/compose/foundation/ScrollbarStyling.desktop.kt
@Composable
fun defaultScrollbarStyle(): ScrollbarStyle

// Source: compose/foundation/foundation/src/desktopMain/kotlin/androidx/compose/foundation/ScrollbarStyling.desktop.kt
val LocalScrollbarStyle = staticCompositionLocalOf { defaultScrollbarStyle() }

// Source: compose/foundation/foundation/src/desktopMain/kotlin/androidx/compose/foundation/Scrollbar.desktop.kt
@Composable
fun rememberScrollbarAdapter(
    scrollState: ScrollState
): ScrollbarAdapter

@Composable
fun rememberScrollbarAdapter(
    scrollState: LazyListState
): ScrollbarAdapter

@Composable
fun rememberScrollbarAdapter(
    scrollState: LazyGridState
): ScrollbarAdapter
```

---

## iOS APIs

### ComposeUIViewController

```kotlin
// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/window/ComposeUIViewController.uikit.kt
fun ComposeUIViewController(
    content: @Composable () -> Unit
): UIViewController

// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/window/ComposeUIViewController.uikit.kt
fun ComposeUIViewController(
    configure: ComposeUIViewControllerConfiguration.() -> Unit = {},
    content: @Composable () -> Unit
): UIViewController
```

### ComposeUIView (Experimental)

```kotlin
// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/window/ComposeUIView.uikit.kt
@ExperimentalComposeApi
fun ComposeUIView(
    content: @Composable () -> Unit
): UIView

// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/window/ComposeUIView.uikit.kt
@ExperimentalComposeApi
fun ComposeUIView(
    configure: ComposeUIViewControllerConfiguration.() -> Unit = {},
    content: @Composable () -> Unit
): UIView
```

### ComposeContainerConfiguration

```kotlin
// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/window/ComposeContainerConfiguration.uikit.kt
class ComposeUIViewControllerConfiguration {
    var onFocusBehavior: OnFocusBehavior = OnFocusBehavior.FocusableAboveKeyboard
    var opaque: Boolean = true
    var parallelRendering: Boolean = false
    var endEdgePanGestureBehavior: EndEdgePanGestureBehavior = EndEdgePanGestureBehavior.Disabled
}

// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/window/OnFocusBehavior.uikit.kt
sealed interface OnFocusBehavior {
    object DoNothing : OnFocusBehavior
    object FocusableAboveKeyboard : OnFocusBehavior
}

// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/window/EndEdgePanGestureBehavior.uikit.kt
sealed interface EndEdgePanGestureBehavior {
    object Disabled : EndEdgePanGestureBehavior
    object Back : EndEdgePanGestureBehavior
    object Forward : EndEdgePanGestureBehavior
}
```

### UIKit Interop

```kotlin
// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/interop/UIKitView.uikit.kt
@Composable
fun <T : UIView> UIKitView(
    factory: () -> T,
    modifier: Modifier = Modifier,
    update: (T) -> Unit = {},
    onRelease: (T) -> Unit = {},
    onReset: ((T) -> Unit)? = null,
    properties: UIKitInteropProperties = UIKitInteropProperties()
)

// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/interop/UIKitView.uikit.kt
@Composable
fun <T : UIViewController> UIKitViewController(
    factory: () -> T,
    modifier: Modifier = Modifier,
    update: (T) -> Unit = {},
    onRelease: (T) -> Unit = {},
    onReset: ((T) -> Unit)? = null,
    properties: UIKitInteropProperties = UIKitInteropProperties()
)

// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/interop/UIKitInteropProperties.uikit.kt
@Immutable
class UIKitInteropProperties(
    val interactionMode: UIKitInteropInteractionMode = UIKitInteropInteractionMode.Cooperative(),
    val isNativeAccessibilityEnabled: Boolean = true,
    val placedAsOverlay: Boolean = false
)

// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/interop/UIKitInteropInteractionMode.uikit.kt
sealed interface UIKitInteropInteractionMode {
    object NonCooperative : UIKitInteropInteractionMode
    class Cooperative(val delayMillis: Long = 150) : UIKitInteropInteractionMode
}
```

### iOS CompositionLocals

```kotlin
// Source: compose/ui/ui/src/uikitMain/kotlin/androidx/compose/ui/interop/LocalUIKitInterop.uikit.kt
val LocalUIViewController: ProvidableCompositionLocal<UIViewController> =
    staticCompositionLocalOf { error("CompositionLocal LocalUIViewController not provided") }

val LocalUIView: ProvidableCompositionLocal<UIView> =
    staticCompositionLocalOf { error("CompositionLocal LocalUIView not provided") }
```

---

## Web / WASM APIs

### ComposeViewport

```kotlin
// Source: compose/ui/ui/src/wasmJsMain/kotlin/androidx/compose/ui/window/ComposeViewport.wasmJs.kt
fun ComposeViewport(
    viewportContainerId: String,
    configure: ComposeViewportConfiguration.() -> Unit = {},
    content: @Composable () -> Unit
)

// Source: compose/ui/ui/src/wasmJsMain/kotlin/androidx/compose/ui/window/ComposeViewport.wasmJs.kt
fun ComposeViewport(
    viewportContainer: Element,
    configure: ComposeViewportConfiguration.() -> Unit = {},
    content: @Composable () -> Unit
)

// Source: compose/ui/ui/src/wasmJsMain/kotlin/androidx/compose/ui/window/ComposeViewportConfiguration.wasmJs.kt
class ComposeViewportConfiguration {
    var isA11YEnabled: Boolean = true
}
```

---

## Shared / Skiko APIs

### System Theme

```kotlin
// Source: compose/ui/ui/src/skikoMain/kotlin/androidx/compose/ui/SystemTheme.skiko.kt
enum class SystemTheme {
    Light,
    Dark,
    Unknown
}

// Source: compose/ui/ui/src/skikoMain/kotlin/androidx/compose/ui/SystemTheme.skiko.kt
val LocalSystemTheme: ProvidableCompositionLocal<SystemTheme> =
    staticCompositionLocalOf { SystemTheme.Unknown }
```

### ComposeScene

```kotlin
// Source: compose/ui/ui/src/skikoMain/kotlin/androidx/compose/ui/scene/ComposeScene.skiko.kt
sealed interface ComposeScene : AutoCloseable {
    val focusManager: FocusManager
    var density: Density
    var layoutDirection: LayoutDirection
    var size: IntSize?
    fun setContent(content: @Composable () -> Unit)
    fun render(canvas: Canvas, nanoTime: Long)
    fun sendPointerEvent(/* ... */)
    fun sendKeyEvent(keyEvent: KeyEvent): Boolean
    override fun close()
}
```

### ImageComposeScene (Headless Rendering)

```kotlin
// Source: compose/ui/ui/src/skikoMain/kotlin/androidx/compose/ui/scene/ImageComposeScene.skiko.kt
fun ImageComposeScene(
    width: Int,
    height: Int,
    density: Density = Density(1f),
    coroutineContext: CoroutineContext = Dispatchers.Unconfined,
    content: @Composable () -> Unit = {}
): ComposeScene
```

### isSystemInDarkTheme (expect/actual)

```kotlin
// Source: compose/foundation/foundation/src/commonMain/kotlin/androidx/compose/foundation/DarkTheme.kt
@Composable
expect fun isSystemInDarkTheme(): Boolean

// Source: compose/foundation/foundation/src/desktopMain/kotlin/androidx/compose/foundation/DarkTheme.desktop.kt
@Composable
actual fun isSystemInDarkTheme(): Boolean =
    LocalSystemTheme.current == SystemTheme.Dark

// Source: compose/foundation/foundation/src/uikitMain/kotlin/androidx/compose/foundation/DarkTheme.uikit.kt
@Composable
actual fun isSystemInDarkTheme(): Boolean { /* UIScreen.mainScreen traitCollection check */ }

// Source: compose/foundation/foundation/src/wasmJsMain/kotlin/androidx/compose/foundation/DarkTheme.wasmJs.kt
@Composable
actual fun isSystemInDarkTheme(): Boolean { /* window.matchMedia("(prefers-color-scheme: dark)") */ }
```

---

## Resource System

### Resource Types

```kotlin
// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/Resource.kt
@Immutable
sealed class Resource(
    internal val id: String,
    internal val items: Set<ResourceItem>
)

// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/ResourceItem.kt
@Immutable
class ResourceItem(
    internal val qualifiers: Set<Qualifier>,
    internal val path: String,
    internal val offset: Long,
    internal val size: Long
)
```

### ResourceReader (Platform Actuals)

```kotlin
// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/ResourceReader.kt
interface ResourceReader {
    suspend fun read(path: String): ByteArray
    suspend fun readPart(path: String, offset: Long, size: Long): ByteArray
    fun getUri(path: String): String
}

// Platform actuals:
// Source: components/resources/library/src/jvmMain/kotlin/org/jetbrains/compose/resources/ResourceReader.jvm.kt
// Source: components/resources/library/src/iosMain/kotlin/org/jetbrains/compose/resources/ResourceReader.ios.kt
// Source: components/resources/library/src/wasmJsMain/kotlin/org/jetbrains/compose/resources/ResourceReader.wasmJs.kt
// Source: components/resources/library/src/jsMain/kotlin/org/jetbrains/compose/resources/ResourceReader.js.kt
```

### rememberResourceState (expect/actual)

```kotlin
// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/ResourceState.kt
@Composable
expect fun <T> rememberResourceState(
    key1: Any,
    getDefault: () -> T,
    block: suspend (ResourceEnvironment) -> T
): State<T>

// JVM/iOS actual: synchronous (blocking read, returns value immediately)
// Source: components/resources/library/src/blockingMain/kotlin/org/jetbrains/compose/resources/ResourceState.blocking.kt

// JS/WASM actual: asynchronous (suspending read, returns default then updates)
// Source: components/resources/library/src/webMain/kotlin/org/jetbrains/compose/resources/ResourceState.web.kt
```

### Resource Composables

```kotlin
// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/StringResources.kt
@Composable
fun stringResource(resource: StringResource): String

@Composable
fun stringResource(resource: StringResource, vararg formatArgs: Any): String

// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/ImageResources.kt
@Composable
fun painterResource(resource: DrawableResource): Painter

@Composable
fun imageResource(resource: DrawableResource): ImageBitmap

// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/VectorResources.kt
@Composable
fun vectorResource(resource: DrawableResource): ImageVector

// Source: components/resources/library/src/commonMain/kotlin/org/jetbrains/compose/resources/FontResources.kt
@Composable
expect fun Font(
    resource: FontResource,
    weight: FontWeight = FontWeight.Normal,
    style: FontStyle = FontStyle.Normal
): Font
```
