import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.jetbrains.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.nexus.vanniktech.publish)
  alias(libs.plugins.dokka)
}

kotlin {
  @OptIn(ExperimentalWasmDsl::class)
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
      jvmTarget.set(JvmTarget.JVM_1_8)
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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

tasks.dokkaHtml.configure {
  outputDirectory.set(file("../docs"))
  pluginsMapConfiguration.set(
    mapOf("org.jetbrains.dokka.base.DokkaBase" to """{ "separateInheritedMembers": true}"""),
  )
  dokkaSourceSets {
    named("commonMain") {
      noAndroidSdkLink.set(false)
    }
  }
}

mavenPublishing {
  publishToMavenCentral(SonatypeHost.S01)
  signAllPublications()
  val artifactId = "jetlime"
  coordinates("io.github.pushpalroy", artifactId, "2.3.0")

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
