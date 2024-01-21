import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  alias(libs.plugins.nexus.vanniktech.publish)
}

android {
  namespace = "com.pushpal.jetlime"
  compileSdk = 34

  defaultConfig {
    minSdk = 21
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
    freeCompilerArgs =
      freeCompilerArgs + "-Xopt-in=kotlin.RequiresOptIn" + "-Xopt-in=kotlin.Experimental"
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
  implementation(libs.ui)
  implementation(libs.androidx.material)
  implementation(libs.androidx.ui.tooling)
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.constraintlayout.compose)

  debugApi(libs.androidx.ui.tooling)

  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.espresso.core)
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01)
  signAllPublications()
  val artifactId = "jetlime"
  coordinates("io.github.pushpalroy", artifactId, "1.0.4")

  pom {
    name.set(artifactId)
    description.set("A simple library for TimeLine view in Android")
    inceptionYear.set("2022")
    packaging = "aar"
    url.set("https://github.com/pushpalroy/jetlime/")
    licenses {
      license {
        name.set("MIT License")
        url.set("https://github.com/pushpalroy/jetlime/blob/main/LICENSE")
        distribution.set("repo")
      }
    }
    developers {
      developer {
        id.set("pushpalroy")
        name.set("Pushpal Roy")
        url.set("https://github.com/pushpalroy/")
      }
    }
    scm {
      url.set("https://github.com/pushpalroy/jetlime")
      connection.set("scm:git:git://github.com/pushpalroy/jetlime.git")
      developerConnection.set("scm:git:ssh://git@github.com/pushpalroy/jetlime.git")
    }
  }
}