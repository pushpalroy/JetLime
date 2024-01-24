import java.util.Properties
import java.io.FileInputStream

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.android)
}

android {
  namespace = "com.pushpal.jetlime.sample"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.pushpal.jetlime.sample"
    minSdk = 21
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
      isMinifyEnabled = true
      isShrinkResources = true
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
  }
}

dependencies {

  implementation(project(":jetlime"))
  //implementation(libs.jetlime)

  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui.tooling)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.lifecycle.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)

  implementation(libs.kotlinx.collections.immutable)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
}