package com.pushpal.jetlime.ui.timelines.updatestate

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pushpal.jetlime.ui.timelines.updatestate.data.Item
import com.pushpal.jetlime.ui.timelines.updatestate.data.getFakeItems
import com.pushpal.jetlime.ui.timelines.updatestate.data.modifyActiveState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ItemUpdateTimeLineViewModel : ViewModel() {

  private val _itemsListState = MutableStateFlow(ItemsListState(loading = false))
  var itemsListState: StateFlow<ItemsListState> = _itemsListState.asStateFlow()

  init {
    fetchItems()
  }

  private fun fetchItems() {
    _itemsListState.update { it.copy(loading = true) }
    viewModelScope.launch {

      // Fetch initial items
      _itemsListState.update { it.copy(itemsList = getFakeItems(), loading = false) }

      // Modify the 2nd item after 2 seconds delay
      delay(2000)
      _itemsListState.update { it.copy(itemsList = getFakeItems().modifyActiveState(2)) }

      // Modify the 3rd item after 2 seconds delay
      delay(2000)
      _itemsListState.update { it.copy(itemsList = getFakeItems().modifyActiveState(3)) }

      // Modify the 4th item after 3 seconds delay
      delay(3000)
      _itemsListState.update { it.copy(itemsList = getFakeItems().modifyActiveState(4)) }
    }
  }
}

@Immutable
data class ItemsListState(
  val itemsList: List<Item> = listOf(),
  val loading: Boolean = false,
)