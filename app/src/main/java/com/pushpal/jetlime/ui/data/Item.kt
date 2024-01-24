package com.pushpal.jetlime.ui.data

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
      name = "Season 2",
      description = "This is the 2nd season of the amazing JetLime series."
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