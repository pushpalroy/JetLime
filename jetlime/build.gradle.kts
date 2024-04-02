import com.vanniktech.maven.publish.SonatypeHost

plugins {
  id(libs.plugins.android.library.get().pluginId)
  id(libs.plugins.kotlin.android.get().pluginId)
  alias(libs.plugins.nexus.vanniktech.publish)
  alias(libs.plugins.dokka)
}

android {
  namespace = "com.pushpal.jetlime"
  compileSdk = 34

  defaultConfig {
    minSdk = 21
    testOptions.targetSdk = 34
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(
        getDefaultProguardFile("proguard-android-optimize.txt"),
        "proguard-rules.pro",
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
    kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
  }
}

dependencies {
  // Compose
  // BOM
  implementation(platform(libs.androidx.compose.bom))
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.material3)

  // Compose
  // Not in BOM
  implementation(libs.androidx.activity.compose)

  // Non Compose
  implementation(libs.dokka.android)
  implementation(libs.kotlinx.collections.immutable)
  debugApi(libs.androidx.compose.ui.tooling)

  // Test
  // Compose BOM
  androidTestImplementation(libs.androidx.compose.ui.test)
  debugImplementation(libs.androidx.ui.test.manifest)
  // Others
  testImplementation(libs.junit)
  androidTestImplementation(libs.androidx.test.runner)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.truth)
}

tasks.dokkaHtml.configure {
  outputDirectory.set(file("../docs"))
  pluginsMapConfiguration.set(
    mapOf("org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": true}"""),
  )
  dokkaSourceSets {
    named("main") {
      noAndroidSdkLink.set(false)
    }
  }
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01)
  signAllPublications()
  val artifactId = "jetlime"
  coordinates("io.github.pushpalroy", artifactId, "2.2.0")

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
