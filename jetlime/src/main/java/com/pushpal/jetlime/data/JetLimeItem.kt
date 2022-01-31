package com.pushpal.jetlime.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.pushpal.jetlime.data.config.JetLimeItemConfig

data class JetLimeItem(
  val indicator: ImageVector = Icons.Filled.CheckCircle,
  val title: String,
  val description: String? = null,
  val imageUrls: List<String>? = null,
  val jetLimeItemConfig: JetLimeItemConfig = JetLimeItemConfig(position = 0),
  val content: @Composable () -> Unit = {}
)
