import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.android.application)
  alias(libs.plugins.jetbrains.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.cocoapods)
  alias(libs.plugins.compose.compiler.report.generator)
}

kotlin {
  js(IR) {
    browser()
    binaries.executable()
  }

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    moduleName = "composeApp"
    browser {
      commonWebpackConfig {
        outputFileName = "composeApp.js"
        devServer =
          (devServer ?: KotlinWebpackConfig.DevServer()).apply {
            static =
              (static ?: mutableListOf()).apply {
                // Serve sources to debug inside browser
                add(project.projectDir.path)
              }
          }
      }
    }
    binaries.executable()
  }

  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  jvm("desktop")

  listOf(
    iosX64(),
    iosArm64(),
    iosSimulatorArm64(),
  ).forEach { iosTarget ->
    iosTarget.binaries.framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  cocoapods {
    version = "1.0.0"
    summary = "JetLime Sample App"
    homepage = "empty"
    ios.deploymentTarget = "14.0"
    podfile = project.file("../iosApp/Podfile")
    framework {
      baseName = "ComposeApp"
      isStatic = true
    }
  }

  sourceSets {
    val desktopMain by getting

    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.resources)
      implementation(compose.components.uiToolingPreview)

      // Local library
      implementation(project(":jetlime"))

      // Maven library - For testing
      // implementation(libs.jetlime)
    }
    desktopMain.dependencies {
      implementation(compose.desktop.currentOs)
    }
    jsMain.dependencies {
      implementation(compose.html.core)
    }
  }
}

android {
  namespace = "com.pushpal.jetlime.sample"
  compileSdk = 34

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    applicationId = "com.pushpal.jetlime.sample"
    minSdk = 21
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  signingConfigs {
    // Uncomment this line if we are generating a release build manually from IDE
    // Not needed while build is generated from CI/CD
    //    maybeCreate("release").apply {
    //        val keystorePropertiesFile = rootProject.file("keystore_release.properties")
    //        val keystoreProperties = Properties()
    //        if (keystorePropertiesFile.exists()) {
    //            keystoreProperties.load(FileInputStream(keystorePropertiesFile))
    //        }
    //        storeFile = file(path = keystoreProperties["storeFile"] as String)
    //        storePassword = keystoreProperties["storePassword"] as String
    //        keyAlias = keystoreProperties["keyAlias"] as String
    //        keyPassword = keystoreProperties["keyPassword"] as String
    //    }
  }

  buildTypes {
    getByName("release") {
      isDebuggable = false
      isMinifyEnabled = false
      isShrinkResources = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
      )
      // signingConfig = signingConfigs.getByName("release")
    }
    getByName("debug") {
      isDebuggable = true
      versionNameSuffix = "-debug"
      signingConfig = signingConfigs.getByName("debug")
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
  }
  dependencies {
    debugImplementation(compose.uiTooling)
  }
}

compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "JetLime Samples"
      packageVersion = "1.0.0"

      windows {
        // Automatically create a desktop shortcut on windows
        shortcut = true
      }
    }
  }
}
