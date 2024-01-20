import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id(Plugins.library)
  id(Plugins.kotlinAndroid)
  id(Plugins.vanniktechPublish)
}

android {
  namespace = "com.pushpal.jetlime"
  compileSdk = 34

  defaultConfig {
    minSdk = 21
    targetSdk = 34

    testInstrumentationRunner = DependingOn.AndroidTest.androidJUnitRunner
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
    kotlinCompilerExtensionVersion = Versions.compose
  }
}

dependencies {

  implementation(DependingOn.AndroidX.Compose.ui)
  implementation(DependingOn.AndroidX.Compose.material)
  implementation(DependingOn.AndroidX.Compose.uiToolingPreview)
  implementation(DependingOn.AndroidX.Compose.activity)
  implementation(DependingOn.AndroidX.Compose.constraintLayout)

  debugApi(DependingOn.AndroidX.Compose.uiTooling)

  testImplementation(DependingOn.Test.jUnit)
  androidTestImplementation(DependingOn.AndroidTest.jUnitExtensions)
  androidTestImplementation(DependingOn.AndroidTest.espressoCore)
  androidTestApi(DependingOn.AndroidTest.uiTestJunit)
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