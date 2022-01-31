package com.pushpal.jetlime.ui.util.multifab

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pushpal.jetlime.ui.theme.JetLimeTheme
import com.pushpal.jetlime.ui.util.multifab.MultiFabState.Expand

@ExperimentalAnimationApi
@Composable
fun MultiFloatingActionButton(
  modifier: Modifier = Modifier,
  items: List<MultiFabItem>,
  fabState: MutableState<MultiFabState> = rememberMultiFabState(),
  fabIcon: FabIcon,
  fabOption: FabOption = FabOption(),
  onFabItemClicked: (fabItem: MultiFabItem) -> Unit,
  stateChanged: (fabState: MultiFabState) -> Unit = {}
) {
  val rotation by animateFloatAsState(
    if (fabState.value == Expand) {
      fabIcon.iconRotate ?: 0f
    } else {
      0f
    }
  )

  Column(
    modifier = modifier.wrapContentSize(),
    horizontalAlignment = Alignment.End
  ) {
    AnimatedVisibility(
      visible = fabState.value.isExpanded(),
      enter = fadeIn() + expandVertically(),
      exit = fadeOut()
    ) {
      LazyColumn(
        modifier = Modifier.wrapContentSize(),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(15.dp)
      ) {
        items(items.size) { index ->
          MiniFabItem(
            item = items[index],
            fabOption = fabOption,
            onFabItemClicked = onFabItemClicked
          )
        }
        item {}
      }
    }

    FloatingActionButton(
      onClick = {
        fabState.value = fabState.value.toggleValue()
        stateChanged(fabState.value)
      },
      backgroundColor = fabOption.iconBackgroundTint,
      contentColor = fabOption.iconTint
    ) {
      Icon(
        imageVector = fabIcon.iconRes,
        contentDescription = "FAB",
        modifier = Modifier.rotate(rotation),
        tint = fabOption.iconTint
      )
    }
  }
}

@Composable
fun MiniFabItem(
  item: MultiFabItem,
  fabOption: FabOption,
  onFabItemClicked: (item: MultiFabItem) -> Unit
) {
  Row(
    modifier = Modifier
      .wrapContentSize()
      .padding(end = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(10.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    if (fabOption.showLabel) {
      Text(
        text = item.label,
        color = JetLimeTheme.colors.uiFloated,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .background(fabOption.textBackgroundTint)
          .padding(horizontal = 6.dp, vertical = 4.dp)
      )
    }

    FloatingActionButton(
      onClick = {
        onFabItemClicked(item)
      },
      modifier = Modifier.size(40.dp),
      backgroundColor = fabOption.iconBackgroundTint,
      contentColor = fabOption.iconTint
    ) {
      Icon(
        imageVector = item.iconRes,
        contentDescription = "Float Icon",
        tint = fabOption.iconTint
      )
    }
  }
}