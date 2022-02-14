import org.gradle.kotlin.dsl.provideDelegate

object Plugins {
  val application by lazy { "com.android.application" }
  val library by lazy { "com.android.library" }
  val android by lazy { "android" }
  val kotlinAndroid by lazy { "kotlin-android" }
  val kotlinKapt by lazy { "kotlin-kapt" }
  val vanniktechPublish by lazy { "com.vanniktech.maven.publish" }
  val ktlint by lazy { "org.jlleitschuh.gradle.ktlint" }
}

object DependingOn {

  object Android {
    val material by lazy { "com.google.android.material:material:${Versions.material}" }
  }

  object AndroidX {

    val coreKtx by lazy { "androidx.core:core-ktx:${Versions.coreKtx}" }
    val appCompat by lazy { "androidx.appcompat:appcompat:${Versions.appCompat}" }

    object Compose {
      val ui by lazy { "androidx.compose.ui:ui:${Versions.compose}" }
      val uiTooling by lazy { "androidx.compose.ui:ui-tooling:${Versions.compose}" }
      val uiToolingPreview by lazy { "androidx.compose.ui:ui-tooling-preview:${Versions.compose}" }
      val material by lazy { "androidx.compose.material:material:${Versions.compose}" }
      val activity by lazy { "androidx.activity:activity-compose:${Versions.activity}" }
      val coil by lazy { "io.coil-kt:coil-compose:${Versions.coil}" }
      val constraintLayout by lazy { "androidx.constraintlayout:constraintlayout-compose:${Versions.constraintLayout}" }
    }
  }

  object Accompanist {
    val insets by lazy { "com.google.accompanist:accompanist-insets:${Versions.accompanistInsets}" }
  }

  object ThirdParty {
    val multiFab by lazy { "academy.compose.companion:multi-fab:${Versions.multiFab}" }
  }

  object Lifecycle {
    val runtime by lazy { "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifeCycle}" }
    val viewModelCompose by lazy { "androidx.lifecycle:lifecycle-viewmodel-compose:${Versions.lifeCycle}" }
    val viewmodel by lazy { "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifeCycle}" }
  }

  object Test {
    val jUnit by lazy { "junit:junit:${Versions.jUnit}" }
  }

  object AndroidTest {
    val jUnitExtensions by lazy { "androidx.test.ext:junit:${Versions.jUnitExtensions}" }
    val espressoCore by lazy { "androidx.test.espresso:espresso-core:${Versions.espresso}" }
    val uiTestJunit by lazy { "androidx.compose.ui:ui-test-junit4:${Versions.compose}" }

    val androidJUnitRunner by lazy { "androidx.test.runner.AndroidJUnitRunner" }
  }

  object Gradle {
    val kotlinGradlePlugin by lazy { "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}" }
    val androidGradlePlugin by lazy { "com.android.tools.build:gradle:${Versions.androidGradlePlugin}" }
    val vanniktechGradlePlugin by lazy { "com.vanniktech:gradle-maven-publish-plugin:${Versions.vanniktechGradlePlugin}" }
    val ktlintGradlePlugin by lazy { "org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlint}" }
  }
}