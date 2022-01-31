plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdk = 31

  defaultConfig {
    applicationId = ProjectProperties.APPLICATION_ID
    minSdk = 21
    targetSdk = 31
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro"
      )
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }
  kotlinOptions {
    jvmTarget = "1.8"
    freeCompilerArgs = freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn"
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.0.5"
  }
}

dependencies {

  implementation(project(":jetlime"))
  implementation("androidx.core:core-ktx:1.7.0")
  implementation("androidx.appcompat:appcompat:1.4.1")
  implementation("com.google.android.material:material:1.5.0")

  implementation("androidx.compose.ui:ui:1.0.5")
  implementation("androidx.compose.material:material:1.0.5")
  implementation("androidx.compose.ui:ui-tooling-preview:1.0.5")
  implementation("androidx.activity:activity-compose:1.4.0")
  implementation("io.coil-kt:coil-compose:1.4.0")
  implementation("academy.compose.companion:multi-fab:1.2")
  // implementation("com.google.accompanist:accompanist-pager:0.24.1-alpha")
  implementation("com.google.accompanist:accompanist-insets:0.20.3")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}