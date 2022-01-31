import Lib.Networking

/** This file contains versions of all the dependencies used in the module  */

object BuildPlugins {
  private const val TOOLS_BUILD = "7.0.4"
  private const val KT_LINT = "9.2.1"
  private const val SAFE_ARGS = "2.3.5"

  const val TOOLS_BUILD_GRADLE = "com.android.tools.build:gradle:${TOOLS_BUILD}"
  const val KTLINT_GRADLE_PLUGIN = "org.jlleitschuh.gradle:ktlint-gradle:${KT_LINT}"
  const val SAFE_ARGS_GRADLE_PLUGIN =
    "androidx.navigation:navigation-safe-args-gradle-plugin:${SAFE_ARGS}"
  const val DAGGER_HILT_PLUGIN = "com.google.dagger:hilt-android-gradle-plugin:2.38.1"
  const val KOTLIN_GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10"
  const val ANDROID_APPLICATION_PLUGIN = "com.android.application"
  const val ANDROID_LIBRARY_PLUGIN = "com.android.library"
  const val KOTLIN_ANDROID_PLUGIN = "kotlin-android"
  const val KOTLIN_PARCELABLE_PLUGIN = "kotlin-parcelize"
  const val KOTLIN_KAPT = "kotlin-kapt"
  const val DAGGER_HILT = "dagger.hilt.android.plugin"
  const val SAFE_ARGS_KOTLIN = "androidx.navigation.safeargs.kotlin"
}

object Lib {

  object Kotlin {
    const val KOTLIN_VERSION = "1.6.0"
    private const val KTX_CORE_VERSION = "1.2.0"

    const val KT_STD = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${KOTLIN_VERSION}"
    const val KTX_CORE = "androidx.core:core-ktx:${KTX_CORE_VERSION}"
    const val DATE_TIME = "org.jetbrains.kotlinx:kotlinx-datetime:0.3.2"
  }

  object Android {
    const val COMPOSE_VERSION = "1.1.0-rc01"
    const val COMPOSE_COMPILER_VERSION = "1.1.0-rc02"
    private const val FRAGMENT_NAVIGATION_VERSION = "2.3.5"
    private const val MATERIAL_DESIGN_VERSION = "1.4.0"
    private const val COMPOSE_ACTIVITY_VERSION = "1.4.0"
    private const val COMPOSE_CONSTRAINT_LAYOUT_VERSION = "1.0.0-rc02"
    private const val COMPOSE_NAVIGATION_VERSION = "2.4.0-rc01"
    private const val COMPOSE_COIL_VERSION = "1.4.0"
    private const val ACCOMPANIST_VERSION = "0.22.0-rc"

    const val APP_COMPAT = "androidx.appcompat:appcompat:1.3.0-beta01"
    const val NAVIGATION_FRAGMENT =
      "androidx.navigation:navigation-fragment-ktx:${FRAGMENT_NAVIGATION_VERSION}"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:1.4.0"

    // Compose
    const val ACTIVITY_COMPOSE = "androidx.activity:activity-compose:${COMPOSE_ACTIVITY_VERSION}"
    const val CONSTRAINT_LAYOUT_COMPOSE =
      "androidx.constraintlayout:constraintlayout-compose:${COMPOSE_CONSTRAINT_LAYOUT_VERSION}"
    const val COMPOSE_UI = "androidx.compose.ui:ui:${COMPOSE_VERSION}"
    const val COIL_COMPOSE = "io.coil-kt:coil-compose:${COMPOSE_COIL_VERSION}"
    const val COMPOSE_LIVEDATA = "androidx.compose.runtime:runtime-livedata:$COMPOSE_VERSION"
    const val COMPOSE_MATERIAL = "androidx.compose.material:material:${COMPOSE_VERSION}"
    const val COMPOSE_TOOLING = "androidx.compose.ui:ui-tooling-preview:${COMPOSE_VERSION}"
    const val COMPOSE_DEBUG_TOOLING = "androidx.compose.ui:ui-tooling:${COMPOSE_VERSION}"
    const val COMPOSE_NAVIGATION =
      "androidx.navigation:navigation-compose:${COMPOSE_NAVIGATION_VERSION}"
    const val MATERIAL_DESIGN = "com.google.android.material:material:${MATERIAL_DESIGN_VERSION}"
    const val ACCOMPANIST_INSETS =
      "com.google.accompanist:accompanist-insets:${ACCOMPANIST_VERSION}"
  }

  object Di {
    private const val DAGGER_VERSION = "2.40.5"
    const val hilt = "com.google.dagger:hilt-android:${DAGGER_VERSION}"
    const val hiltAndroidCompiler = "com.google.dagger:hilt-android-compiler:${DAGGER_VERSION}"

    const val viewmodel = "androidx.hilt:hilt-lifecycle-viewmodel:1.0.0-alpha03"
    const val hiltCompiler = "androidx.hilt:hilt-compiler:1.0.0"
    const val hiltNavigationCompose = "androidx.hilt:hilt-navigation-compose:1.0.0-rc01"
  }

  object Paging {
    private const val PAGING_VERSION = "3.1.0"
    const val PAGING_3 = "androidx.paging:paging-runtime:${PAGING_VERSION}"
  }

  object Glide {
    private const val GLIDE_VERSION = "4.12.0"
    const val GLIDE = "com.github.bumptech.glide:glide:${GLIDE_VERSION}"
    const val GLIDE_COMPILER = "com.github.bumptech.glide:compiler:${GLIDE_VERSION}"
  }

  object Async {
    private const val COROUTINES_VERSION = "1.6.0"

    const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${COROUTINES_VERSION}"
    const val COROUTINES_ANDROID =
      "org.jetbrains.kotlinx:kotlinx-coroutines-android:${COROUTINES_VERSION}"
  }

  object Networking {
    private const val RETROFIT_VERSION = "2.9.0"
    const val OKHTTP_VERSION = "4.7.2"
    const val RETROFIT = "com.squareup.retrofit2:retrofit:${RETROFIT_VERSION}"
    const val RETROFIT_GSON = "com.squareup.retrofit2:converter-gson:${RETROFIT_VERSION}"
    const val LOGGING = "com.squareup.okhttp3:logging-interceptor:${OKHTTP_VERSION}"
  }

  object Serialization {
    private const val GSON_VERSION = "2.8.8"
    const val GSON = "com.google.code.gson:gson:${GSON_VERSION}"
  }

  object Logger {
    private const val TIMBER_VERSION = "4.7.1"
    const val TIMBER = "com.jakewharton.timber:timber:${TIMBER_VERSION}"
  }
}

object TestLib {
  private const val COROUTINES_VERSION = "1.3.7"
  private const val ANDROID_JUNIT_VERSION = "1.0.0"
  private const val ROBO_ELECTRIC_VERSION = "4.3"
  private const val ARCH_CORE_VERSION = "2.1.0"
  private const val CORE_TEST_VERSION = "1.2.0"
  private const val JUNIT_VERSION = "4.13"
  private const val nav_version = "2.3.5"

  const val COROUTINES = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${COROUTINES_VERSION}"
  const val ROBO_ELECTRIC = "org.robolectric:robolectric:${ROBO_ELECTRIC_VERSION}"
  const val MOCK_WEB_SERVER = "com.squareup.okhttp3:mockwebserver:${Networking.OKHTTP_VERSION}"
  const val CORE_TEST = "androidx.test:core-ktx:${CORE_TEST_VERSION}"
  const val JUNIT = "junit:junit:${JUNIT_VERSION}"
  const val ANDROID_JUNIT = "androidx.test.ext:junit:${ANDROID_JUNIT_VERSION}"
  const val ARCH_CORE = "androidx.arch.core:core-testing:${ARCH_CORE_VERSION}"
  const val MOCKK = "io.mockk:mockk:1.10.5"
  const val NAVIGATION_TESTING = "androidx.navigation:navigation-testing:$nav_version"
  const val TURBINE = "app.cash.turbine:turbine:0.7.0"
}

object DebugLib {
  private const val LEAK_CANARY_VERSION = "2.3"
  const val LEAK_CANARY = "com.squareup.leakcanary:leakcanary-android:${LEAK_CANARY_VERSION}"
}
