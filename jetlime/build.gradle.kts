plugins {
  id("com.android.library")
  id("kotlin-android")
}

android {
  compileSdk = 31

  defaultConfig {
    minSdk = 21
    targetSdk = 31

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
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
  }
  buildFeatures {
    compose = true
  }
  composeOptions {
    kotlinCompilerExtensionVersion = "1.0.5"
  }
}

dependencies {
  implementation("androidx.compose.ui:ui:1.0.5")
  implementation("androidx.compose.material:material:1.0.5")
  implementation("androidx.compose.ui:ui-tooling-preview:1.0.5")
  implementation("androidx.activity:activity-compose:1.4.0")
  implementation("androidx.constraintlayout:constraintlayout-compose:1.0.0")

  debugApi("androidx.compose.ui:ui-tooling:1.0.5")

  testImplementation("junit:junit:4.13.2")
  androidTestImplementation("androidx.test.ext:junit:1.1.3")
  androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
  androidTestApi("androidx.compose.ui:ui-test-junit4:1.0.5")
}