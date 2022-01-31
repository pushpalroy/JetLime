package com.pushpal.jetlime.ui.util.multifab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.pushpal.jetlime.ui.util.multifab.MultiFabState.Collapsed

sealed class MultiFabState {
  object Collapsed : MultiFabState()
  object Expand : MultiFabState()

  fun isExpanded() = this == Expand

  fun toggleValue() = if (isExpanded()) {
    Collapsed
  } else {
    Expand
  }
}

@Composable
fun rememberMultiFabState() = remember { mutableStateOf<MultiFabState>(Collapsed) }