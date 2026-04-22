# Compose Multiplatform (CMP) Reference

Reference: `compose-multiplatform` (JetBrains), `androidx.compose` (Google)

## CMP Architecture Overview

Compose Multiplatform uses a three-layer architecture:

### Layer 1: commonMain (Shared UI Runtime)

All Compose runtime, foundation, material3, and navigation APIs live here. Rendering uses Skia (via Skiko) on non-Android platforms, and the native Android Compose renderer on Android.

```kotlin
// commonMain/kotlin/App.kt
@Composable
fun App() {
    MaterialTheme {
        var count by remember { mutableIntStateOf(0) }
        Button(onClick = { count++ }) {
            Text("Clicked $count times")
        }
    }
}
```

This single composable renders natively on Android, Desktop (JVM), iOS, and WebAssembly.

### Layer 2: Platform Source Sets

Platform-specific code lives in `androidMain`, `desktopMain`, `iosMain`, `wasmJsMain`. Use `expect`/`actual` to bridge.

```
src/
  commonMain/kotlin/       # Shared UI + logic
  androidMain/kotlin/      # Android-specific (AndroidView, Context)
  desktopMain/kotlin/      # Desktop-specific (Window, MenuBar)
  iosMain/kotlin/          # iOS-specific (UIKitView, NSBundle)
  wasmJsMain/kotlin/       # Web-specific (ComposeViewport)
```

### Layer 3: Platform Entry Points

Each platform has a different entry point to host the shared `App()` composable.

```kotlin
// androidMain — Standard Android Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { App() }
    }
}

// desktopMain — JVM window
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "My App"
    ) {
        App()
    }
}

// iosMain — UIKit integration
fun MainViewController(): UIViewController =
    ComposeUIViewController { App() }

// wasmJsMain — Browser canvas
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        App()
    }
}
```

---

## API Availability Matrix

| API | commonMain | Android | Desktop | iOS | Web |
|-----|:----------:|:-------:|:-------:|:---:|:---:|
| **Runtime** (`@Composable`, `remember`, `mutableStateOf`, `LaunchedEffect`, `derivedStateOf`) | Yes | Yes | Yes | Yes | Yes |
| **Foundation** (`Box`, `Column`, `Row`, `LazyColumn`, `TextField`, `Canvas`) | Yes | Yes | Yes | Yes | Yes |
| **Material3** (`Button`, `Card`, `Scaffold`, `TopAppBar`, `NavigationBar`) | Yes | Yes | Yes | Yes | Yes |
| **Navigation Compose** (type-safe `@Serializable` routes, `NavHost`) | Yes | Yes | Yes | Yes | Yes |
| **ViewModel** (`lifecycle-viewmodel-compose:2.10.0+`) | Yes | Yes | Yes | Yes | Yes |
| **`collectAsState()`** | Yes | Yes | Yes | Yes | Yes |
| **Compose Resources** (`Res.drawable.*`, `Res.string.*`, `Res.font.*`) | Yes | Yes | Yes | Yes | Yes |
| `AndroidView` | -- | Yes | -- | -- | -- |
| `BackHandler` | -- | Yes | -- | -- | -- |
| `dynamicColorScheme()` | -- | Yes | -- | -- | -- |
| `LocalContext` | -- | Yes | -- | -- | -- |
| `collectAsStateWithLifecycle()` | -- | Yes | -- | -- | -- |
| `hiltViewModel()` | -- | Yes | -- | -- | -- |
| Baseline Profiles / Macrobenchmark | -- | Yes | -- | -- | -- |
| `Window`, `MenuBar`, `Tray` | -- | -- | Yes | -- | -- |
| `DialogWindow` | -- | -- | Yes | -- | -- |
| `ComposePanel`, `SwingPanel` | -- | -- | Yes | -- | -- |
| Scrollbar composables | -- | -- | Yes | -- | -- |
| Keyboard shortcuts (`KeyShortcut`) | -- | -- | Yes | -- | -- |
| Desktop notifications | -- | -- | Yes | -- | -- |
| `UIKitView`, `UIKitViewController` | -- | -- | -- | Yes | -- |
| `ComposeUIViewController`, `ComposeUIView` | -- | -- | -- | Yes | -- |
| `PlatformImeOptions` | -- | -- | -- | Yes | -- |
| `LocalUIViewController`, `LocalUIView` | -- | -- | -- | Yes | -- |
| `ComposeViewport` | -- | -- | -- | -- | Yes |
| Browser history integration | -- | -- | -- | -- | Yes |

**Key insight:** The vast majority of the Compose API surface is available in `commonMain`. Platform-specific APIs exist for interop (embedding native views) and OS-level features (window management, system themes).

---

## expect/actual Patterns

### Pattern 1: isSystemInDarkTheme()

The theme detection API is `expect` in commonMain with platform-specific implementations.

```kotlin
// commonMain
@Composable
expect fun isSystemInDarkTheme(): Boolean
```

```kotlin
// skikoMain (Desktop, iOS, Web shared actual)
@Composable
actual fun isSystemInDarkTheme(): Boolean {
    val systemTheme = LocalSystemTheme.current
    return systemTheme == SystemTheme.Dark
}
```

```kotlin
// androidMain
@Composable
actual fun isSystemInDarkTheme(): Boolean {
    val uiMode = LocalContext.current.resources.configuration.uiMode
    return (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
}
```

### Pattern 2: ResourceReader

The resource reading interface has four platform actuals because each platform loads files differently.

```kotlin
// commonMain
internal expect fun getPlatformResourceReader(): ResourceReader

interface ResourceReader {
    suspend fun read(path: String): ByteArray
    suspend fun readPart(path: String, offset: Long, size: Long): ByteArray
    fun getUri(path: String): String
}
```

```kotlin
// androidMain — uses AssetManager
internal actual fun getPlatformResourceReader(): ResourceReader =
    object : ResourceReader {
        override suspend fun read(path: String): ByteArray =
            context.assets.open(path).use { it.readBytes() }
        override fun getUri(path: String): String =
            "file:///android_asset/$path"
        // ...
    }

// desktopMain — uses ClassLoader
internal actual fun getPlatformResourceReader(): ResourceReader =
    object : ResourceReader {
        override suspend fun read(path: String): ByteArray =
            this::class.java.classLoader!!.getResourceAsStream(path)!!.readBytes()
        override fun getUri(path: String): String =
            this::class.java.classLoader!!.getResource(path)!!.toURI().toString()
        // ...
    }

// iosMain — uses NSBundle + NSFileManager
internal actual fun getPlatformResourceReader(): ResourceReader =
    object : ResourceReader {
        override suspend fun read(path: String): ByteArray {
            val nsPath = NSBundle.mainBundle.resourcePath + "/" + path
            return NSFileManager.defaultManager.contentsAtPath(nsPath)!!.toByteArray()
        }
        // ...
    }

// wasmJsMain — uses window.fetch()
internal actual fun getPlatformResourceReader(): ResourceReader =
    object : ResourceReader {
        override suspend fun read(path: String): ByteArray {
            val response = window.fetch(path).await()
            return response.arrayBuffer().await().toByteArray()
        }
        // ...
    }
```

### Pattern 3: rememberResourceState (Sync vs Async)

JVM and iOS can load resources synchronously. JS/WASM must load asynchronously (returns default value first, then updates).

```kotlin
// JVM/iOS (skikoMain without web)
@Composable
internal actual fun <T> rememberResourceState(
    key: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T> {
    // Can block briefly to load synchronously on first composition
    return remember(key) { mutableStateOf(runBlocking { block() }) }
}

// wasmJsMain
@Composable
internal actual fun <T> rememberResourceState(
    key: Any,
    getDefault: () -> T,
    block: suspend () -> T
): State<T> {
    val state = remember(key) { mutableStateOf(getDefault()) }
    LaunchedEffect(key) {
        state.value = block()  // Async update after initial render
    }
    return state
}
```

**Pitfall:** On WASM, resources loaded with `Res.*` may flash a default value before the actual resource loads. Design UIs to handle this gracefully (use placeholders or loading states).

### Pattern 4: Font Loading

Each platform uses its native font system, so font instantiation is platform-specific.

```kotlin
// commonMain
@Composable
expect fun Font(resource: FontResource, weight: FontWeight, style: FontStyle): Font

// androidMain — Typeface from assets
@Composable
actual fun Font(resource: FontResource, weight: FontWeight, style: FontStyle): Font {
    val typeface = remember(resource) { Typeface.createFromAsset(context.assets, resource.path) }
    return AndroidFont(typeface, weight, style)
}

// desktopMain — java.awt.Font from classpath
// iosMain — CTFontCreateWithFontDescriptor from bundle
// wasmJsMain — FontFace API loaded via fetch
```

### Pattern 5: getSystemEnvironment

Returns locale, theme, and density information per platform.

```kotlin
// commonMain
internal expect fun getSystemEnvironment(): ResourceEnvironment

data class ResourceEnvironment(
    val language: LanguageQualifier,
    val region: RegionQualifier,
    val theme: ThemeQualifier,
    val density: DensityQualifier
)

// androidMain — reads from Configuration + DisplayMetrics
// desktopMain — reads from Locale.getDefault() + system theme detection
// iosMain — reads from NSLocale.currentLocale + UITraitCollection
// wasmJsMain — reads from navigator.language + matchMedia("(prefers-color-scheme: dark)")
```

---

## Resource System (Res.*)

### Directory Structure

Place resources under `commonMain/composeResources/`:

```
commonMain/
  composeResources/
    drawable/
      icon.xml              # Vector drawable (works on ALL platforms)
      logo.png
    drawable-dark/
      icon.xml              # Dark theme variant (auto-selected)
    font/
      roboto_regular.ttf
      roboto_bold.ttf
    values/
      strings.xml           # Default strings
    values-fr/
      strings.xml           # French localization
    files/
      data.json             # Raw files
```

### Usage

```kotlin
// Images
@Composable
fun AppLogo() {
    Image(
        painter = painterResource(Res.drawable.logo),
        contentDescription = "App logo"
    )
}

// Strings (with arguments)
@Composable
fun Greeting(name: String) {
    Text(stringResource(Res.string.greeting, name))
}

// Fonts
@Composable
fun StyledText() {
    val fontFamily = FontFamily(Font(Res.font.roboto_regular))
    Text("Hello", fontFamily = fontFamily)
}

// Raw files
val bytes: ByteArray = Res.readBytes("files/data.json")
```

### Gotchas

**Lottie is NOT KMP-compatible.** The standard `com.airbnb.lottie:lottie-compose` only works on Android. For multiplatform Lottie animations, use:
- **Kottie** (`io.github.ismai117:kottie`) -- all CMP targets
- **Compottie** (`io.github.alexzhirkevich:compottie`) -- all CMP targets

**Multi-module font loading:** Fonts must be declared in the module that owns the `composeResources/` directory. In multi-module projects, place shared fonts in the top-level (app) module or create a shared resources module. Child modules cannot reference parent module resources directly.

**Android XML vectors work everywhere.** Android-format `VectorDrawable` XML files placed in `drawable/` render correctly on all platforms via the CMP Skia-based renderer. No conversion needed.

**Replace R.* with Res.*:**

```kotlin
// Android-only (will not compile in commonMain)
painterResource(R.drawable.icon)
stringResource(R.string.greeting)

// CMP (works in commonMain)
painterResource(Res.drawable.icon)
stringResource(Res.string.greeting)
```

---

## Migration from Android-Only to CMP

### Dependency Replacement Table

| Android-Only | CMP Replacement | Notes |
|-------------|-----------------|-------|
| Hilt (`hiltViewModel()`) | Koin (`koinViewModel()`) | Koin has first-class KMP support. Koin 4.0+ has Compose annotations. |
| Retrofit | Ktor Client | Ktor has multiplatform HTTP engines per platform. |
| Room | SQLDelight | SQLDelight generates Kotlin from SQL. Room KMP is experimental (2.7.0-alpha). |
| Coil 2.x | Coil 3.x KMP | Coil 3.0+ is fully multiplatform. Same API. |
| Lottie | Kottie / Compottie | See Lottie gotcha above. |
| `R.drawable.*`, `R.string.*` | `Res.drawable.*`, `Res.string.*` | Compose Resources replaces Android resources. |
| `collectAsStateWithLifecycle()` | `collectAsState()` | `collectAsState()` is available in commonMain. Lifecycle-awareness is Android-specific. |
| `BackHandler` | `expect`/`actual` | Implement back handling per platform. Desktop/iOS have different back concepts. |
| `LocalContext.current` | `expect`/`actual` | No universal replacement. Abstract platform needs behind an interface. |

### Top 5 Migration Pitfalls

**1. `LocalContext.current` sprinkled everywhere**

There is no KMP replacement for Android `Context`. Every usage must be audited and abstracted.

```kotlin
// Bad: Context usage scattered in composables
@Composable
fun ShareButton(text: String) {
    val context = LocalContext.current  // Android-only!
    Button(onClick = {
        val intent = Intent(Intent.ACTION_SEND).apply { putExtra(Intent.EXTRA_TEXT, text) }
        context.startActivity(intent)
    }) { Text("Share") }
}

// Good: Abstract behind expect/actual
// commonMain
expect fun shareText(text: String)

// androidMain
actual fun shareText(text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply { putExtra(Intent.EXTRA_TEXT, text) }
    applicationContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

// iosMain
actual fun shareText(text: String) {
    val controller = UIActivityViewController(listOf(text), null)
    UIApplication.sharedApplication.keyWindow?.rootViewController
        ?.presentViewController(controller, true, null)
}
```

**2. Compose Compiler 2.0.0 incorrect stability inference on non-JVM targets**

The Compose compiler may incorrectly mark classes as unstable on iOS/WASM targets, causing excessive recomposition. If you see performance issues on non-Android targets:

```kotlin
// Explicitly annotate shared data classes
@Immutable
data class UiState(
    val items: List<Item>,
    val isLoading: Boolean
)
```

Always check compiler stability reports for all targets, not just Android.

**3. Don't migrate bottom-up -- start from the app module**

Migrating leaf modules first creates a broken build that stays broken for weeks. Instead:
1. Add KMP plugin to the app module
2. Move composables to `commonMain` one screen at a time
3. Create `expect`/`actual` stubs for platform dependencies
4. Migrate feature modules once the app module compiles

**4. `rememberSaveable` + `Bundle` + `@Parcelize` is Android-only**

`Bundle` and `@Parcelize` do not exist on non-Android targets. Use `@Serializable` with a custom `Saver` instead.

```kotlin
// Android-only (will not compile in commonMain)
@Parcelize
data class FormState(val name: String, val email: String) : Parcelable

var state by rememberSaveable { mutableStateOf(FormState("", "")) }

// CMP-compatible
@Serializable
data class FormState(val name: String, val email: String)

val formStateSaver = Saver<FormState, String>(
    save = { Json.encodeToString(it) },
    restore = { Json.decodeFromString(it) }
)

var state by rememberSaveable(stateSaver = formStateSaver) {
    mutableStateOf(FormState("", ""))
}
```

**5. Version lockstep: Compose, Kotlin, Gradle, AGP**

CMP has strict version compatibility requirements. Mismatched versions produce cryptic compiler errors.

```kotlin
// build.gradle.kts -- versions must be compatible
plugins {
    kotlin("multiplatform") version "2.1.20"             // Kotlin version
    id("org.jetbrains.compose") version "1.8.0"          // CMP plugin version
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.20" // Must match Kotlin
}

// Check compatibility at:
// https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html
```

---

## Navigation in CMP

### Navigation Compose (Official -- Recommended)

The official `androidx.navigation:navigation-compose` is fully multiplatform as of Navigation 2.8.0+. Use `@Serializable` type-safe routes.

```kotlin
// commonMain -- works on all platforms
@Serializable
data object Home

@Serializable
data class Details(val id: Int)

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = Home) {
        composable<Home> {
            HomeScreen(onItemClick = { id -> navController.navigate(Details(id)) })
        }
        composable<Details> { backStackEntry ->
            val route = backStackEntry.toRoute<Details>()
            DetailsScreen(itemId = route.id)
        }
    }
}
```

**Deep link handling differs per platform.** On Android, deep links integrate with `Intent` and `AndroidManifest.xml`. On other platforms, deep links must be wired manually (e.g., custom URL scheme on iOS via `application(_:open:options:)`, browser URL on Web).

### Navigation 3 (Experimental -- CMP 1.10+)

Navigation 3 is a ground-up redesign. `navigation3-common` is multiplatform, but `navigation3-ui` is not yet fully KMP.

```kotlin
// Available in CMP 1.10+ (experimental)
// navigation3-common: multiplatform
// navigation3-ui: limited platform support (check release notes)
```

Wait for stable releases before using in production multiplatform projects.

### Voyager (Third-Party)

Simple screen-based navigation. Good for small to medium apps.

```kotlin
// commonMain
class HomeScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        Button(onClick = { navigator.push(DetailsScreen(42)) }) {
            Text("Go to Details")
        }
    }
}

class DetailsScreen(private val id: Int) : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { DetailsScreenModel(id) }
        // ScreenModel is Voyager's equivalent of ViewModel
        Text("Details for $id")
    }
}

// Entry point
Navigator(HomeScreen())
```

### Decompose (Third-Party)

Separates navigation logic from UI. Steeper learning curve but maximum testability and control. Navigation state is held in platform-agnostic `ComponentContext` objects.

```kotlin
// Navigation logic (pure Kotlin, no Compose dependency)
interface RootComponent {
    val childStack: Value<ChildStack<*, Child>>
    sealed class Child {
        data class Home(val component: HomeComponent) : Child()
        data class Details(val component: DetailsComponent) : Child()
    }
}

// UI layer (Compose)
@Composable
fun RootContent(component: RootComponent) {
    val childStack by component.childStack.subscribeAsState()
    Children(childStack) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.Home -> HomeContent(instance.component)
            is RootComponent.Child.Details -> DetailsContent(instance.component)
        }
    }
}
```

### Navigation Decision Guide

| Criteria | Navigation Compose | Voyager | Decompose |
|----------|:-----------------:|:-------:|:---------:|
| Official support | Yes (Google + JetBrains) | Community | Community |
| Learning curve | Low | Low | High |
| Type-safe routes | Yes (`@Serializable`) | Manual | Yes |
| Testability | Moderate | Moderate | High |
| All CMP targets | Yes | Yes | Yes |
| ViewModel integration | Yes (`lifecycle-viewmodel`) | ScreenModel | ComponentContext |

---

## Anti-Patterns

### Don't: Use hiltViewModel() in shared code

```kotlin
// Will not compile in commonMain -- Hilt is Android-only
@Composable
fun ProfileScreen() {
    val viewModel: ProfileViewModel = hiltViewModel()  // Android-only!
}

// Use lifecycle-viewmodel-compose (KMP) or Koin
@Composable
fun ProfileScreen() {
    val viewModel = viewModel { ProfileViewModel() }       // KMP ViewModel
    // or
    val viewModel = koinViewModel<ProfileViewModel>()      // Koin KMP
}
```

### Don't: Use @Preview from the wrong package in commonMain

```kotlin
// Will not compile in commonMain
import androidx.compose.ui.tooling.preview.Preview  // Android-only package!

@Preview
@Composable
fun MyPreview() { /* ... */ }

// CMP preview support varies by IDE and target
// Use Android Studio previews in androidMain only
// For Desktop, run the app directly (hot reload is fast)
```

### Don't: Use R.* in shared code

```kotlin
// Will not compile in commonMain
Image(painter = painterResource(R.drawable.icon), contentDescription = null)
Text(stringResource(R.string.title))

// Use Compose Resources
Image(painter = painterResource(Res.drawable.icon), contentDescription = null)
Text(stringResource(Res.string.title))
```

### Don't: Assume collectAsStateWithLifecycle exists in commonMain

```kotlin
// Will not compile in commonMain
val state by viewModel.uiState.collectAsStateWithLifecycle()  // Android-only!

// Use collectAsState() instead -- available everywhere
val state by viewModel.uiState.collectAsState()

// collectAsState() is sufficient for CMP. The lifecycle awareness of
// collectAsStateWithLifecycle() is an Android optimization that stops
// collection when the app is backgrounded. On Desktop/Web this is
// unnecessary; on iOS, CMP handles lifecycle automatically.
```
