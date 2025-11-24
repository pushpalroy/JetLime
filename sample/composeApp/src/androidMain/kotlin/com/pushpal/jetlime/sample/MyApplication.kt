/*
* MIT License
*
* Copyright (c) 2024 Pushpal Roy
*
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
*
* The above copyright notice and this permission notice shall be included in all
* copies or substantial portions of the Software.
*
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
* SOFTWARE.
*
*/
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
