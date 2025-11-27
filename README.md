# JetLime 🍋

> A simple yet highly customizable UI library to show a timeline view in Compose Multiplatform.

[![Jetbrains Compose](https://img.shields.io/badge/Jetbrains%20Compose-1.9.3-blue?style=for-the-badge&logo=appveyor)](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-compatibility-and-versioning.html#jetpack-compose-artifacts-used)
![Kotlin](https://img.shields.io/badge/Kotlin-2.2.21-blue.svg?color=blue&style=for-the-badge)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.pushpalroy/jetlime?style=for-the-badge&logo=appveyor)](https://search.maven.org/artifact/io.github.pushpalroy/jetlime)
![Stars](https://img.shields.io/github/stars/pushpalroy/jetlime?color=yellowgreen&style=for-the-badge)
![Forks](https://img.shields.io/github/forks/pushpalroy/jetlime?color=yellowgreen&style=for-the-badge)
![Pull Request](https://img.shields.io/github/issues-pr/pushpalroy/jetlime?color=yellowgreen&style=for-the-badge)
![Watchers](https://img.shields.io/github/watchers/pushpalroy/jetlime?color=yellowgreen&style=for-the-badge)
![Issues](https://img.shields.io/github/issues/pushpalroy/jetlime?color=orange&style=for-the-badge)
[![License](https://img.shields.io/github/license/pushpalroy/jetlime?color=blue&style=for-the-badge&logo=appveyor)](https://github.com/pushpalroy/jetlime/blob/master/LICENSE)

![Sample Build CI](https://img.shields.io/github/actions/workflow/status/pushpalroy/jetlime/build.yml?style=for-the-badge&label=Sample%20Build)
![badge][badge-android] ![badge][badge-ios] ![badge][badge-jvm] ![badge][badge-web]

|                  Basic                  | Dashed                                   |                  Dynamic                  |
|:---------------------------------------:|------------------------------------------|:-----------------------------------------:|
|  <img src="art/basic.gif" width=180 />  | <img src="art/dashed.png" width=180 />   |  <img src="art/dynamic.gif" width=180 />  |
|                 Custom                  | Extended                                 |                                           |
| <img src="art/custom.png" width=180 />  | <img src="art/extended.png" width=180 /> |                                           |

### Supported Platform Samples

|                     Android                      | iOS                                          |                     Desktop                     | Web                                         |
|:------------------------------------------------:|----------------------------------------------|:-----------------------------------------------:|---------------------------------------------|
| <img src="art/platform/android.png" width=120 /> | <img src="art/platform/iOS.png" width=120 /> | <img src="art/platform/desktop.png" width=240/> | <img src="art/platform/web.png" width=240/> |

## ✨ Highlights
- Compose Multiplatform timelines: Android, iOS, Desktop (JVM), Web (JS & WASM)
- Vertical and horizontal layouts (JetLimeColumn / JetLimeRow)
- Flexible point placement: START, CENTER, END with continuous line joins
- RTL layout support for JetLimeRow and JetLimeExtendedEvent (mirrors timelines and keeps content visible in right-to-left layouts)
- Dashed/gradient/solid lines via Brush + PathEffect
- Extended events with dual content slots (left/right), icons, and animations
- Small, focused API with sensible defaults (JetLimeDefaults)

## 📦 Installation

In `build.gradle` of shared module, include the following dependency

```gradle
dependencies {
  implementation("io.github.pushpalroy:jetlime:4.1.1")
}
```

## 📖 Usage

### 📍 Add items in a Vertical Timeline

Use the [JetLimeColumn](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-column.html)

```kotlin
val items = remember { mutableListOf(Item1, Item2, Item3) } // Any type of items

JetLimeColumn(
  modifier = Modifier.padding(16.dp),
  itemsList = ItemsList(items),
  key = { _, item -> item.id },
) { index, item, position ->
  JetLimeEvent(
    style = JetLimeEventDefaults.eventStyle(
      position = position
    ),
  ) {
    // Content here
  }
}
```
### 📍 Add items in a Horizontal Timeline

Use the [JetLimeRow](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-row.html)

```kotlin
val items = remember { mutableListOf(Item1, Item2, Item3) } // Any type of items

JetLimeRow(
  modifier = Modifier.padding(16.dp),
  itemsList = ItemsList(items),
  key = { _, item -> item.id },
) { index, item, position ->
  JetLimeEvent(
    style = JetLimeEventDefaults.eventStyle(
      position = position
    ),
  ) {
    // Content here
  }
}
```

Pass the `key` to define factory of stable and unique keys representing the item. Using the same key for multiple items in the list is not allowed.
This key will be used by a LazyColumn or LazyRow internally.

If we want to to add items dynamically from a data source, we should use `mutableStateListOf`, so that our list can be observed as a state:
```kotlin
val items = remember { mutableStateListOf<MyItem>() }
```

### 🧩 Extended Events (Vertical Timeline)

Use the [JetLimeExtendedEvent](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-extended-event.html) with a [JetLimeColumn](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-column.html)
Using this we can pass an additional content to draw on the left side of the timeline.

```kotlin
val items = remember { mutableListOf(Item1, Item2, Item3) } // Any type of items

JetLimeColumn(
  modifier = Modifier.padding(16.dp),
  itemsList = ItemsList(items),
  key = { _, item -> item.id },
  style = JetLimeDefaults.columnStyle(contentDistance = 24.dp),
) { index, item, position ->
  JetLimeExtendedEvent(
    style = JetLimeEventDefaults.eventStyle(
      position = position
    ),
    additionalContent = {
      // Additional content here
    }
  ) {
    // Content here
  }
}
```

### 🎛️ Customize `JetLimeColumn` Style

Use the [JetLimeDefaults.columnStyle()](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-defaults/column-style.html)

```kotlin
JetLimeColumn(
  style = JetLimeDefaults.columnStyle(
    contentDistance = 32.dp,
    itemSpacing = 16.dp,
    lineThickness = 2.dp,
    lineBrush = JetLimeDefaults.lineSolidBrush(color = Color(0xFF2196F3)),
    lineVerticalAlignment = RIGHT,
  ),
) {
  // Code to add events
}
```
### 🎛️ Customize `JetLimeRow` Style

Use the [JetLimeDefaults.rowStyle()](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-defaults/row-style.html)

```kotlin
JetLimeRow(
  style = JetLimeDefaults.rowStyle(
    contentDistance = 32.dp,
    itemSpacing = 16.dp,
    lineThickness = 2.dp,
    lineBrush = JetLimeDefaults.lineSolidBrush(color = Color(0xFF2196F3)),
    lineHorizontalAlignment = BOTTOM,
  ),
) {
  // Code to add events
}
```

### 🎛️ Customize `JetLimeEvent` Style

Use the [JetLimeEventDefaults.eventStyle()](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-event-defaults/event-style.html)

```kotlin
JetLimeEvent(
  style = JetLimeEventDefaults.eventStyle(
    position = position,
    pointColor = Color(0xFF2889D6),
    pointFillColor = Color(0xFFD5F2FF),
    pointRadius = 14.dp,
    pointAnimation = JetLimeEventDefaults.pointAnimation(),
    pointType = EventPointType.filled(0.8f),
    pointStrokeWidth = 2.dp,
    pointStrokeColor = MaterialTheme.colorScheme.onBackground,
  ),
) {
  // Code to add event content
}
```
---

### ⚙️ JetLimeColumn and JetLimeRow Properties

#### 🧭 Alignment

The timeline line and point circles can be set to either side.

For a `JetLimeColumn` the alignment can be set to [LEFT](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-vertical-alignment/index.html#825393495%2FClasslikes%2F-1761194290) or [RIGHT](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-vertical-alignment/index.html#861885460%2FClasslikes%2F-1761194290)

```kotlin
lineVerticalAlignment = LEFT or RIGHT // Default is LEFT
```

For a `JetLimeRow` the alignment can be set to [TOP](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-horizontal-alignment/index.html#769734623%2FClasslikes%2F-1761194290) or [BOTTOM](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-horizontal-alignment/index.html#-1737811223%2FClasslikes%2F-1761194290)

```kotlin
lineHorizontalAlignment = TOP or BOTTOM // Default is TOP
```

#### 🎨 Line Style

The line can be drawn by passing a `Brush` object to [lineBrush](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-style/line-brush.html) in a `columnStyle` or `rowStyle`.
Default values can also be used from [JetLimeDefaults](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-defaults/index.html) and colors can be modified for quick setup:

```kotlin
lineBrush = JetLimeDefaults.lineGradientBrush()

or

lineBrush = JetLimeDefaults.solidBrush()
```

A dashed/dotted line can also be drawn using the `pathEffect` property by passing a `PathEffect` to a `columnStyle` or `rowStyle`.

```kotlin
style = JetLimeDefaults.columnStyle(
        pathEffect = PathEffect.dashPathEffect(
          intervals = floatArrayOf(30f, 30f),
          phase = 0f,
        )
      )
```

#### ↔️ Content Distance

The [contentDistance](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-style/content-distance.html) in `Dp` specifies how far the timeline line should be from the timeline content.

#### ↕️ Item Spacing

The [itemSpacing](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-style/item-spacing.html) in `Dp` specifies the gap between the event items.

#### 📏 Line Thickness

The [lineThickness](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-style/line-thickness.html) in `Dp` the thickness of the timeline line.

---

### 🌍 RTL Layout Support

JetLime supports right-to-left (RTL) layouts out of the box using Compose’s `LayoutDirection.Rtl`.

- **Horizontal timelines (`JetLimeRow` + `JetLimeEvent`)**
  - The timeline direction is mirrored in RTL.
  - Start and end items are correctly connected.
  - Points and lines stay aligned without clipping, and the last item’s line joins cleanly.

- **Extended vertical events (`JetLimeExtendedEvent` inside `JetLimeColumn`)**
  - Additional content remains fully visible on the side nearest the logical start.
  - Main content remains fully visible on the opposite side.
  - The timeline line and point stay between additional and main content without overlapping them.

To preview RTL behavior in your app, wrap your content with a `CompositionLocalProvider`:

```kotlin
CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
  JetLimeColumn(
    itemsList = ItemsList(items),
    key = { _, item -> item.id },
  ) { index, item, position ->
    JetLimeExtendedEvent(
      style = JetLimeEventDefaults.eventStyle(position = position),
      additionalContent = { /* Additional content */ },
    ) {
      // Main content
    }
  }
}
```

|                Basic (RTL)                | Dynamic (RTL)                               |                Extended (RTL)                |
|:-----------------------------------------:|---------------------------------------------|:--------------------------------------------:|
| <img src="art/rtl/basic.png" width=180 /> | <img src="art/rtl/dynamic.png" width=180 /> | <img src="art/rtl/extended.png" width=180 /> |

### ⚙️ JetLimeEvent Properties

#### 📍 Position

We always need to pass the position to the [eventStyle](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-jet-lime-event-defaults/event-style.html) that will be received in the JetLimeColumn lambda.
This is needed so that JetLimeColumn can calculate the position of an event in the list at any time.
Based on the calculation it will assign either of the three [EventPosition](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-event-position/index.html): `START`, `MIDDLE` or `END`.
This classification is needed to render correct lines for start and end items.

```kotlin
JetLimeColumn(
  itemsList = ItemsList(items),
  key = { _, item -> item.id },
) { index, item, position ->
  JetLimeEvent(
    style = JetLimeEventDefaults.eventStyle(
      position = position
    ),
  ) {
    // Content here
  }
}
```

#### 📌 Point Placement

The `pointPlacement` of type [PointPlacement](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-point-placement/index.html) controls where the point renders within the item:
- `START` – near the start edge (top for vertical, left for horizontal). Default.
- `CENTER` – centered within the item.
- `END` – near the end edge (bottom for vertical, right for horizontal).

Examples:
```kotlin
// All items CENTER
JetLimeEventDefaults.eventStyle(position = position, pointPlacement = PointPlacement.CENTER)

// All items END
JetLimeEventDefaults.eventStyle(position = position, pointPlacement = PointPlacement.END)

// Mixed: second-to-last CENTER, rest START
JetLimeEventDefaults.eventStyle(
  position = position,
  pointPlacement = if (index == items.size - 2) PointPlacement.CENTER else PointPlacement.START,
)
```
Notes:
- Lines connect continuously across START/CENTER/END, and stop cleanly at the last item’s point.
- Works for both JetLimeColumn (vertical) and JetLimeRow (horizontal).

#### 🟡 Point Type

The `pointType` of type [EventPointType](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-event-point-type/index.html) specifies the style of the point circle.
It can be any of the three types: `EMPTY`, `FILLED` or `CUSTOM`.

For using `EMPTY`

```kotlin
pointType = EventPointType.EMPTY
```

For using `FILLED`, the [filled()](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-event-point-type/-companion/index.html#-1342152058%2FFunctions%2F-1761194290) function has to be used which takes an optional `fillPercent`

```kotlin
pointType = EventPointType.filled(0.8f)
```
For using `CUSTOM`, the [custom()](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-event-point-type/-companion/index.html#-2135258840%2FFunctions%2F-1761194290) function has to be used which takes an `icon` of `Painter`.
This can be used to use a custom icon instead of the default types defined. An optional `tint` can also be applied on the icon.

```kotlin
pointType = EventPointType.custom(icon = painterResource(id = R.drawable.icon_check), tint = Color.Green)
```

#### 🔁 Point Animation

The `pointAnimation` of type [EventPointAnimation](https://pushpalroy.github.io/JetLime/jetlime/com.pushpal.jetlime/-event-point-animation/index.html) specifies the animation of the point circle.

To enable the default animation

```kotlin
pointAnimation = JetLimeEventDefaults.pointAnimation()
```
To use a custom animation `initialValue`, `targetValue` and `animationSpec` can be passed to `pointAnimation()`.
`animationSpec` should be of the type `InfiniteRepeatableSpec<Float>`.

#### 🎨 Point Color

The `pointColor` is the color of the event point circle background.

#### 🎨 Point Fill Color

The `pointFillColor` is the fill color of the event point circle which is drawn over the `pointColor`.

#### 📐 Point Radius

The `pointRadius` in `Dp` is the radius of the point circle.

#### 🖊️ Point Stroke Width

The `pointStrokeWidth` in `Dp` is the width of the circle border.

#### 🖍️ Point Stroke Color

The `pointStrokeColor` is the color of the circle border.

## 📚 Documentation
The full API documentation is available here: [JetLime Documentation](https://pushpalroy.github.io/JetLime/index.html)

## 💡 Inspiration

- [Timeline-View by Vipul Asri](https://github.com/vipulasri/Timeline-View)
- [This amazing blog by Vita Sokolova on Timeline component with Jetpack Compose](https://proandroiddev.com/a-step-by-step-guide-to-building-a-timeline-component-with-jetpack-compose-358a596847cb)

## 🤝 Contribution

Would love to receive contributions! Read [contribution guidelines](CONTRIBUTING.md) for more information regarding contribution.

## 💬 Discuss?

Have any questions, doubts or want to present your opinions, views? You're always welcome. You can [start discussions](https://github.com/pushpalroy/jetlime/discussions).

## 📝 License

```
MIT License

Copyright (c) 2024 Pushpal Roy

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
[badge-android]: http://img.shields.io/badge/Android-grey?style=for-the-badge&logo=appveyor
[badge-ios]: http://img.shields.io/badge/iOS-grey?style=for-the-badge&logo=appveyor
[badge-jvm]: http://img.shields.io/badge/JVM-grey?style=for-the-badge&logo=appveyor
[badge-web]: http://img.shields.io/badge/Web-grey?style=for-the-badge&logo=appveyor