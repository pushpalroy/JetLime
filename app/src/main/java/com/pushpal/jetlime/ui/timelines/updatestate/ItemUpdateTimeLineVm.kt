package com.pushpal.jetlime.ui.timelines.updatestate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pushpal.jetlime.ui.timelines.updatestate.data.Item
import com.pushpal.jetlime.ui.timelines.updatestate.data.getFakeItems
import com.pushpal.jetlime.ui.timelines.updatestate.data.modify
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ItemUpdateTimeLineViewModel : ViewModel() {

  var itemsListState = MutableStateFlow<ItemsListState>(ItemsListState.Empty)
    private set

  init {
    fetchItems()
  }

  private fun fetchItems() {
    viewModelScope.launch {
      itemsListState.value = ItemsListState.Loading
      itemsListState.value = ItemsListState.Success(itemsList = getFakeItems())

      // Modify items list after 2 seconds delay
      delay(2000)
      itemsListState.value = ItemsListState.Success(itemsList = getFakeItems().modify(2))

      // Modify items list after 3 seconds delay
      delay(3000)
      itemsListState.value = ItemsListState.Success(itemsList = getFakeItems().modify(3))
    }
  }
}

sealed class ItemsListState {
  object Empty : ItemsListState()
  object Loading : ItemsListState()
  data class Success(val itemsList: List<Item>) : ItemsListState()
}