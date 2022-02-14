package com.pushpal.jetlime.ui.timelines.updatestate

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.ExperimentalTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pushpal.jetlime.data.JetLimeItemsModel
import com.pushpal.jetlime.data.JetLimeItemsModel.JetLimeItem
import com.pushpal.jetlime.data.config.IconAnimation
import com.pushpal.jetlime.data.config.JetLimeItemConfig
import com.pushpal.jetlime.data.config.JetLimeViewConfig
import com.pushpal.jetlime.ui.JetLimeView
import com.pushpal.jetlime.ui.theme.JetLimeSurface
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.timelines.updatestate.ItemsListState.Success
import com.pushpal.jetlime.ui.timelines.updatestate.util.viewModelProviderFactoryOf

@OptIn(ExperimentalAnimationApi::class, ExperimentalTransitionApi::class)
@ExperimentalAnimationApi
@Composable
fun ItemUpdateTimeLine() {
  val viewModel: ItemUpdateTimeLineViewModel = viewModel(
    factory = viewModelProviderFactoryOf {
      ItemUpdateTimeLineViewModel()
    }
  )
  val itemsListState by viewModel.itemsListState.collectAsState()

  JetLimeSurface(
    color = JetLimeTheme.colors.uiBackground,
    modifier = Modifier.fillMaxSize()
  ) {
    val jetLimeItemsModel by remember {
        derivedStateOf {
          val jetItemList: MutableList<JetLimeItem> = mutableStateListOf()
          when (itemsListState) {
            is Success -> {
              (itemsListState as Success).itemsList.forEach { item ->
                jetItemList.add(
                  JetLimeItem(
                    title = item.name,
                    jetLimeItemConfig = JetLimeItemConfig(
                      position = item.id,
                      iconAnimation = if (item.activeState) IconAnimation() else null
                    )
                  )
                )
              }
            }
            else -> {
              // Do nothing
            }
          }
          JetLimeItemsModel(jetItemList)
        }
    }

    JetLimeView(
      jetLimeItemsModel = jetLimeItemsModel,
      jetLimeViewConfig = JetLimeViewConfig()
    )
  }
}