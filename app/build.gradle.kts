plugins {
  id(Plugins.application)
  id(Plugins.kotlinAndroid)
}

android {
  compileSdk = 31

  defaultConfig {
    applicationId = ProjectProperties.APPLICATION_ID
    minSdk = 21
    targetSdk = 31
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = DependingOn.AndroidTest.androidJUnitRunner
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
  //implementation("io.github.pushpalroy:jetlime:1.0.1")

  implementation(DependingOn.Android.material)

  implementation(DependingOn.AndroidX.coreKtx)
  implementation(DependingOn.AndroidX.appCompat)

  implementation(DependingOn.AndroidX.Compose.ui)
  implementation(DependingOn.AndroidX.Compose.material)
  implementation(DependingOn.AndroidX.Compose.uiTooling)
  implementation(DependingOn.AndroidX.Compose.uiToolingPreview)
  implementation(DependingOn.AndroidX.Compose.activity)
  implementation(DependingOn.AndroidX.Compose.coil)

  implementation(DependingOn.Accompanist.insets)
  implementation(DependingOn.ThirdParty.multiFab)

  testImplementation(DependingOn.Test.jUnit)
  androidTestImplementation(DependingOn.AndroidTest.jUnitExtensions)
  androidTestImplementation(DependingOn.AndroidTest.espressoCore)
}