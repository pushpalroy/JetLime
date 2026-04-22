# Lists and Scrolling in Jetpack Compose

Efficient list rendering and scrolling are core to responsive mobile UIs. Jetpack Compose provides lazy layouts that compose items on-demand, not all at once.

## LazyColumn and LazyRow

These composables only compose visible items, making them efficient for large lists unlike `Column`/`Row` which compose all children upfront.

### LazyColumn (Vertical Scrolling)
```kotlin
LazyColumn(modifier = Modifier.fillMaxSize()) {
  item {
    HeaderComposable()
  }
  items(itemList.size) { index ->
    ListItemComposable(itemList[index])
  }
  item {
    FooterComposable()
  }
}
```

### LazyRow (Horizontal Scrolling)
```kotlin
LazyRow(modifier = Modifier.fillMaxWidth()) {
  items(imageList.size) { index ->
    Image(
      painter = painterResource(imageList[index]),
      contentDescription = null,
      modifier = Modifier.width(200.dp)
    )
  }
}
```

**Key difference from Column/Row:** Items are composed lazily as they enter the viewport, reducing memory and CPU usage.

**Source:** `androidx/compose/foundation/foundation/src/commonMain/kotlin/androidx/compose/foundation/lazy/`

## DSL Patterns: item, items, itemsIndexed

### `item` — Single Composable
```kotlin
LazyColumn {
  item {
    HeaderComposable()
  }
}
```

### `items` — From a List or Count
```kotlin
// From a List
val users = listOf(User("Alice"), User("Bob"))
LazyColumn {
  items(users) { user ->
    UserCard(user)
  }
}

// From a count
LazyColumn {
  items(100) { index ->
    Text("Item $index")
  }
}
```

### `itemsIndexed` — With Index
```kotlin
LazyColumn {
  itemsIndexed(users) { index, user ->
    Text("${index + 1}. ${user.name}")
  }
}
```

## Keys: Critical for Correctness and Performance

The `key` parameter ensures Compose can correctly identify and reuse items even if the list is reordered.

### ✓ Good: Stable Keys
```kotlin
data class User(val id: Long, val name: String)

LazyColumn {
  items(users, key = { it.id }) { user ->
    UserCard(user)
  }
}
```

When `users` list is reordered, Compose knows which item moved because the key (id) is stable.

### ✗ Bad: Index as Key
```kotlin
// AVOID: If list is reordered, state gets mixed up
LazyColumn {
  items(users, key = { index }) { user -> // Wrong!
    var selected by remember { mutableStateOf(false) }
    UserCard(user, selected)
  }
}
```

If you remove item at index 0, the item that was at index 1 moves to index 0 and incorrectly inherits the state.

### ✗ Bad: No Key
```kotlin
// If list changes, item state/animations may misbehave
LazyColumn {
  items(users) { user ->
    UserCard(user)
  }
}
```

Without a key, Compose can't distinguish items reliably if the list changes.

**Rule:** Always provide a stable, unique key when the list can change. Use IDs, not indices.

## Content Types for Recycling Optimization

Use `contentType` to enable layout reuse when rendering different item types:

```kotlin
sealed class ListItem
data class HeaderItem(val title: String) : ListItem()
data class UserItem(val user: User) : ListItem()

LazyColumn {
  items(
    items = listItems,
    key = { it.id },
    contentType = { when (it) {
      is HeaderItem -> "header"
      is UserItem -> "user"
    }}
  ) { item ->
    when (item) {
      is HeaderItem -> HeaderComposable(item)
      is UserItem -> UserCard(item)
    }
  }
}
```

Items with the same `contentType` can reuse layout state, improving performance when types repeat.

## LazyListState: Scroll Position and Animations

Manage scroll position programmatically:

```kotlin
val listState = rememberLazyListState()

LazyColumn(state = listState) {
  items(100) { index ->
    Text("Item $index")
  }
}

// Scroll to item 50
LaunchedEffect(Unit) {
  listState.scrollToItem(50)
}

// Animate scroll
LaunchedEffect(Unit) {
  listState.animateScrollToItem(50)
}

// Read current scroll position
val firstVisibleIndex = listState.firstVisibleItemIndex
val firstVisibleOffset = listState.firstVisibleItemScrollOffset
```

**Use case:** Scroll to a newly added item, or scroll on user action.

## LazyVerticalGrid and LazyHorizontalGrid

### Fixed Columns
```kotlin
LazyVerticalGrid(columns = GridCells.Fixed(3)) {
  items(itemList.size) { index ->
    GridItemComposable(itemList[index])
  }
}
```

### Adaptive Columns (Responsive)
```kotlin
// Column width ~100dp, fills available space with as many columns as fit
LazyVerticalGrid(columns = GridCells.Adaptive(100.dp)) {
  items(itemList.size) { index ->
    GridItemComposable(itemList[index])
  }
}
```

Adaptive is preferable for responsive layouts.

## LazyVerticalStaggeredGrid

For Pinterest-style layouts with variable heights:

```kotlin
LazyVerticalStaggeredGrid(
  columns = StaggeredGridCells.Fixed(2),
  modifier = Modifier.fillMaxSize()
) {
  items(images.size) { index ->
    AsyncImage(
      model = images[index].url,
      contentDescription = null,
      modifier = Modifier.fillMaxWidth()
    )
  }
}
```

Items flow into the column with the shortest current height, creating a natural staggered appearance.

## HorizontalPager and VerticalPager

Page-by-page horizontal or vertical swiping:

```kotlin
val pagerState = rememberPagerState(pageCount = { pages.size })

HorizontalPager(state = pagerState) { page ->
  PageComposable(pages[page])
}

// Programmatic scroll to page
LaunchedEffect(Unit) {
  pagerState.scrollToPage(2)
}

// Animate to page
LaunchedEffect(Unit) {
  pagerState.animateScrollToPage(2)
}
```

## Sticky Headers in Lazy Lists

Headers that remain visible at the top while scrolling:

```kotlin
LazyColumn {
  stickyHeader {
    SectionHeaderComposable("Section A")
  }
  items(itemsA) { item ->
    ItemComposable(item)
  }

  stickyHeader {
    SectionHeaderComposable("Section B")
  }
  items(itemsB) { item ->
    ItemComposable(item)
  }
}
```

## Nested Scrolling: Pitfalls

### ✗ Avoid Scrollable Inside LazyColumn
```kotlin
// Bad: nested scroll behavior is unpredictable
LazyColumn {
  item {
    LazyRow { // Nested lazy is OK, but...
      items(innerList) { item ->
        InnerItem(item)
      }
    }
  }
}
```

Nested lazy composables are acceptable but require careful thought about scroll precedence.

### ✗ Avoid verticalScroll Modifier Inside LazyColumn
```kotlin
// Bad: two scroll containers fight for input
LazyColumn {
  item {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
      Text("This is scrollable twice!")
    }
  }
}
```

Don't wrap lazy children in scrollable modifiers; use nested lazy composables if you need multiple scroll axes.

### ✓ Use nestedScroll for Complex Scenarios
```kotlin
val scrollState = rememberScrollState()
val nestedScrollConnection = remember {
  object : NestedScrollConnection {
    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
      // Custom scroll handling
      return Offset.Zero
    }
  }
}

LazyColumn(modifier = Modifier.nestedScroll(nestedScrollConnection)) {
  items(100) { index ->
    Text("Item $index")
  }
}
```

## Performance: Scroll-Dependent UI

### ✗ Bad: Heavy Computation in Item Lambda
```kotlin
LazyColumn {
  items(users) { user ->
    val processedData = expensiveComputation(user) // Runs every recomposition!
    UserCard(user, processedData)
  }
}
```

### ✓ Good: Use derivedStateOf for Scroll-Dependent Logic
```kotlin
val listState = rememberLazyListState()
val showScrollToTop = remember {
  derivedStateOf { listState.firstVisibleItemIndex > 0 }
}

LazyColumn(state = listState) {
  items(100) { index ->
    Text("Item $index")
  }
}

if (showScrollToTop.value) {
  Button(onClick = { /* scroll up */ }) { Text("Top") }
}
```

`derivedStateOf` derives a new value when scroll state changes without recomposing the entire list.

## Anti-Patterns

### ✗ Using LazyColumn for Small Fixed Lists
```kotlin
// Bad: overkill for 5 items
LazyColumn {
  items(5) { index ->
    Text("Item $index")
  }
}
```

**Better:** Use `Column` for small fixed lists.

### ✗ No Keys + List Mutations
```kotlin
var items by remember { mutableStateOf(initialList) }
LazyColumn {
  items(items) { item -> // No key!
    ItemComposable(item, onDelete = {
      items = items.filter { it.id != item.id }
    })
  }
}
```

Without keys, removing an item corrupts the state of remaining items.

### ✗ Creating New Objects in Keys
```kotlin
// Bad: key creates new object each recomposition
LazyColumn {
  items(users, key = { User(it.id, it.name) }) { user ->
    UserCard(user)
  }
}
```

**Better:** Use primitive stable identifiers.

## Key Takeaways

1. Always provide stable, unique keys when using `items` on mutable lists
2. Use `contentType` for multi-type lists to enable layout reuse
3. Prefer `GridCells.Adaptive` for responsive grid layouts
4. Avoid nested scrollables; use `nestedScroll` for complex scroll behavior
5. Use `derivedStateOf` to avoid recomposing the entire list for scroll-dependent logic
6. `LazyColumn`/`LazyRow` are for large or unbounded lists; use `Column`/`Row` for small fixed lists
7. Never use indices as keys; list mutations will corrupt item state

### Production Crash Patterns

#### indexOf() Inside items {} — O(n^2) and Crashes

```kotlin
// BAD: O(n^2) total, returns -1 on recreated objects → IndexOutOfBoundsException
items(list.size) { index ->
    val item = list[index]
    val position = list.indexOf(item) // -1 if object was recreated!
}

// GOOD: use items() with key for stable identity
items(list, key = { it.id }) { item ->
    ItemRow(item)
}
```

Root cause: `indexOf()` uses `equals()`. If list items are recreated (new object instances without proper `equals()` implementation), `indexOf()` returns -1.

#### Duplicate LazyColumn Keys

Backend sends items without unique IDs, or WebSocket reconnects deliver duplicates → `IllegalArgumentException: Key X was already used`.

```kotlin
// BAD: backend IDs may not be unique
items(messages, key = { it.id }) { msg -> ... }

// GOOD: add dedup index for safety
items(messages, key = { "${it.id}_${it.dedupIndex}" }) { msg -> ... }
```

The `dedupIndex` pattern: Add a field to the data class that is excluded from `equals()`/`hashCode()` but included in the LazyColumn key:

```kotlin
data class ChatMessage(
    val id: String,
    val text: String,
    val timestamp: Long
) {
    // NOT in data class constructor — excluded from equals/hashCode
    var dedupIndex: Int = 0
}

// When processing messages from backend:
fun deduplicateKeys(messages: List<ChatMessage>): List<ChatMessage> {
    val seen = mutableMapOf<String, Int>()
    return messages.map { msg ->
        val count = seen.getOrPut(msg.id) { 0 }
        seen[msg.id] = count + 1
        msg.also { it.dedupIndex = count }
    }
}
```

#### derivedStateOf Driving Collection Size → IOOB

```kotlin
// BAD: derived count can be stale when items{} reads
val itemCount by remember { derivedStateOf { filterItems(allItems).size } }
LazyColumn {
    items(itemCount) { index ->
        val item = filterItems(allItems)[index] // IOOB if allItems changed!
    }
}

// GOOD: derive the full filtered list, not just the count
val filteredItems by remember { derivedStateOf { filterItems(allItems) } }
LazyColumn {
    items(filteredItems, key = { it.id }) { item ->
        ItemRow(item)
    }
}
```

Rule: `derivedStateOf` for scroll direction, visibility, form validation — **never for item counts that drive LazyList rendering**.

### LazyList Hardening

#### Multi-Field Keys with Collision Prefixes

When a LazyList mixes items from different data sources, IDs can collide:

```kotlin
// BAD: id=42 in both liveItems and archivedItems → crash
LazyColumn {
    items(liveItems, key = { it.id }) { ... }
    items(archivedItems, key = { it.id }) { ... }
}

// GOOD: prefix keys by source
LazyColumn {
    items(liveItems, key = { "live_${it.id}" }) { ... }
    items(archivedItems, key = { "archived_${it.id}" }) { ... }
    items(pinnedItems, key = { "pinned_${it.id}" }) { ... }
}
```

#### items() with key Preferred Over itemsIndexed()

`items(list, key = { it.id })` gives each item a stable identity across recompositions. This enables:
- Correct `animateItem()` animations
- Efficient diffing (only changed items recompose)
- Proper state preservation per item

Use `itemsIndexed()` only when you genuinely need the index for display (e.g., numbered list).

#### animateItem() Parameters

```kotlin
items(items, key = { it.id }) { item ->
    ItemRow(
        item = item,
        modifier = Modifier.animateItem(
            fadeInSpec = tween(durationMillis = 250),
            placementSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessMediumLow
            ),
            fadeOutSpec = tween(durationMillis = 150)
        )
    )
}
```

#### ReportDrawnWhen for Startup Performance

Signal to the system that the first meaningful content is visible:

```kotlin
@Composable
fun ConversationListScreen(items: List<Conversation>) {
    ReportDrawnWhen { items.isNotEmpty() }

    LazyColumn {
        items(items, key = { it.id }) { item ->
            ConversationRow(item)
        }
    }
}
```

This improves Time To Initial Display (TTID) and Time To Full Display (TTFD) metrics in Android vitals.

#### Device-Specific Pagination

Some devices (notably Samsung) handle scroll events differently, requiring fewer scroll confirmations for lazy load triggers. When implementing infinite scroll, test on multiple OEMs and consider a configurable scroll threshold.

```kotlin
val shouldLoadMore by remember {
    derivedStateOf {
        val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
        val totalItems = listState.layoutInfo.totalItemsCount
        lastVisibleItem >= totalItems - PREFETCH_THRESHOLD // e.g., 5
    }
}

LaunchedEffect(shouldLoadMore) {
    if (shouldLoadMore) { viewModel.loadNextPage() }
}
```
