package com.pushpal.jetlime.ui.timelines.updatestate.data

data class Item(
  val id: Int = 0,
  var name: String,
  val description: String? = null,
  var activeState: Boolean = false
)

fun getFakeItems(): MutableList<Item> {
  return mutableListOf(
    Item(
      id = 0,
      name = "Season 1"
    ),
    Item(
      id = 1,
      name = "Season 2"
    ),
    Item(
      id = 2,
      name = "Season 3"
    ),
    Item(
      id = 3,
      name = "Season 4"
    ),
    Item(
      id = 4,
      name = "Season 5"
    ),
    Item(
      id = 5,
      name = "Season 6"
    )
  )
}

fun MutableList<Item>.modify(index: Int): MutableList<Item> {
  return map { item ->
    if (item.id == index) {
      item.copy(
        activeState = true,
        name = "${item.name} (Now watching)"
      )
    } else item
  }.toMutableList()
}