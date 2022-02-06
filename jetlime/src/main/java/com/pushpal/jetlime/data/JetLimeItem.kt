package com.pushpal.jetlime.data

import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.vector.ImageVector
import com.pushpal.jetlime.data.config.JetLimeItemConfig

class JetLimeItemsModel(list: List<JetLimeItem> = listOf()) {
  private val _items: MutableList<JetLimeItem> = mutableStateListOf()
  val items: List<JetLimeItem> = _items
  private var lastItemId = 0

  init {
    for (item in list) {
      addItem(item)
    }
  }

  data class JetLimeItem(
    val indicator: ImageVector = Icons.Filled.CheckCircle,
    val title: String,
    val description: String? = null,
    val imageUrls: List<String>? = null,
    val jetLimeItemConfig: JetLimeItemConfig = JetLimeItemConfig(position = 0),
    var itemId: Int = 0,
    var visible: MutableTransitionState<Boolean> = MutableTransitionState(false),
    val content: @Composable () -> Unit = {}
  )

  fun addItem(jetLimeItem: JetLimeItem) {
    lastItemId++
    _items.add(jetLimeItem.apply {
      visible = MutableTransitionState(false).apply { targetState = true }
      itemId = lastItemId
    })
  }

  fun removeItem(jetLimeItem: JetLimeItem?) {
    jetLimeItem?.let { safeJetLimeItem ->
      safeJetLimeItem.visible.targetState = false
      lastItemId--
    }
  }

  @OptIn(ExperimentalTransitionApi::class)
  fun pruneItems() {
    _items.removeAll(items.filter { it.visible.isIdle && !it.visible.targetState })
  }

  fun removeAll() {
    _items.forEach {
      it.visible.targetState = false
    }
  }
}