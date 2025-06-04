import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.*

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.jetbrains.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.cocoapods)
  alias(libs.plugins.nexus.vanniktech.publish)
  alias(libs.plugins.dokka)
}

kotlin {
  cocoapods {
    version = "3.0.1"
    summary = "JetLime KMP Library"
    homepage = "https://github.com/pushpalroy/JetLime"
    ios.deploymentTarget = "14.0"
    framework {
      baseName = "JetLime"
      isStatic = true
    }
  }

  js(IR) {
    browser()
    binaries.library()
  }

  @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
  wasmJs {
    browser {
      testTask {
        enabled = false
      }
    }
    binaries.library()
  }

  androidTarget {
    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
    }
  }

  jvm("desktop")

  iosX64()
  iosArm64()
  iosSimulatorArm64()

  sourceSets {
    val desktopMain by getting

    androidMain.dependencies {
      implementation(compose.preview)
      implementation(libs.androidx.activity.compose)
      implementation(libs.dokka.android)
    }
    commonMain.dependencies {
      implementation(compose.runtime)
      implementation(compose.foundation)
      implementation(compose.material3)
      implementation(compose.ui)
      implementation(compose.components.uiToolingPreview)
      api(libs.kotlinx.collections.immutable)
    }
    desktopMain.dependencies {
      implementation(compose.desktop.currentOs)
    }
  }
}

android {
  namespace = "com.pushpal.jetlime"
  compileSdk = 34

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    minSdk = 21
    testOptions.targetSdk = 34
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    consumerProguardFiles("consumer-rules.pro")
  }
  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
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
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  buildFeatures {
    compose = true
  }

  dependencies {
    debugApi(compose.uiTooling)

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
}


dokka {
  dokkaSourceSets {
    pluginsConfiguration {

    }
    pluginsConfiguration {
      html {
        separateInheritedMembers = true
      }
    }
    commonMain {
      enableAndroidDocumentationLink = false
    }
  }
}

tasks {
  dokkaGeneratePublicationHtml {
    outputDirectory = file("../docs")
  }
}

publishing {
  repositories {
    maven("https://europe-west3-maven.pkg.dev/mik-music/trainyapp") {
      credentials {
        username = "_json_key_base64"
        password = System.getenv("GOOGLE_KEY")?.toByteArray()?.let {
          Base64.getEncoder().encodeToString(it)
        }
      }

      authentication {
        create<BasicAuthentication>("basic")
      }
    }
  }
}

mavenPublishing {
  val artifactId = "jetlime"

  // Define coordinates for the published artifact
  coordinates("com.trainyapp", artifactId, "3.3.0-SNAPSHOT")

  // Configure POM metadata for the published artifact
  pom {
    name.set(artifactId)
    description.set("A simple KMP library for TimeLine view in Compose")
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
    // Specify developer information
    developers {
      developer {
        id.set("pushpalroy")
        name.set("Pushpal Roy")
        url.set("https://github.com/pushpalroy/")
      }
    }
    // Specify SCM information
    scm {
      url.set("https://github.com/pushpalroy/jetlime")
      connection.set("scm:git:git://github.com/pushpalroy/jetlime.git")
      developerConnection.set("scm:git:ssh://git@github.com/pushpalroy/jetlime.git")
    }
  }
}
