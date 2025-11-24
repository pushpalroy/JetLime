package com.pushpal.jetlime.sample

import android.app.Application
import android.util.Log
import com.skydoves.compose.stability.runtime.ComposeStabilityAnalyzer
import com.skydoves.compose.stability.runtime.RecompositionEvent
import com.skydoves.compose.stability.runtime.RecompositionLogger

class MyApplication : Application() {

  override fun onCreate() {
    super.onCreate()

    // Enable default logger for debug builds
    ComposeStabilityAnalyzer.setEnabled(true)
    setupCustomLogger()
  }

  private fun setupCustomLogger() {
    ComposeStabilityAnalyzer.setLogger(
      object : RecompositionLogger {
        override fun log(event: RecompositionEvent) {
          val message = buildString {
            append("🔄 Recomposition #${event.recompositionCount}")
            append(" - ${event.composableName}")
            if (event.tag.isNotEmpty()) {
              append(" [${event.tag}]")
            }
            appendLine()

            event.parameterChanges.forEach { change ->
              append("   • ${change.name}: ${change.type}")
              when {
                change.changed -> append(" ➡️ CHANGED")
                change.stable -> append(" ✅ STABLE")
                else -> append(" ⚠️ UNSTABLE")
              }
              appendLine()
            }

            if (event.unstableParameters.isNotEmpty()) {
              append("   ⚠️ Unstable: ${event.unstableParameters.joinToString()}")
            }
          }
          Log.d("CustomRecomposition", message)
        }
      },
    )
  }
}