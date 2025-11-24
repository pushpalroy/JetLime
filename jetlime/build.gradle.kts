plugins {
  alias(libs.plugins.android.library)
  alias(libs.plugins.kotlin.multiplatform)
  alias(libs.plugins.jetbrains.compose)
  alias(libs.plugins.compose.compiler)
  alias(libs.plugins.kotlin.cocoapods)
  alias(libs.plugins.nexus.vanniktech.publish)
  alias(libs.plugins.dokka)
}

// Dokka V2 extension
// Shared configuration without deprecated properties
dokka {
  moduleName.set("jetlime")
  dokkaSourceSets.configureEach {
    enableAndroidDocumentationLink.set(true)
    val moduleDoc = rootProject.file("dokkaModule.md")
    val packageDoc = rootProject.file("dokkaPackage.md")
    if (moduleDoc.exists()) includes.from(moduleDoc.path)
    if (packageDoc.exists()) includes.from(packageDoc.path)
  }
}

kotlin {
  cocoapods {
    version = "4.1.1"
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
    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
    compilerOptions {
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
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
  compileSdk = 36

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    minSdk = 23
    testOptions.targetSdk = 36
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

    // Android UI tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test)
    debugImplementation(libs.androidx.ui.test.manifest)
    // Others
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.truth)
  }
}

// Compose Compiler metrics/reports
composeCompiler {
  reportsDestination = layout.buildDirectory.dir("compose_compiler/reports")
  metricsDestination = layout.buildDirectory.dir("compose_compiler/metrics")
}

mavenPublishing {
  // Configure publishing to Maven Central
  publishToMavenCentral()

  // Enable GPG signing for all publications
  signAllPublications()

  val artifactId = "jetlime"

  // Define coordinates for the published artifact
  coordinates("io.github.pushpalroy", artifactId, "4.1.1")

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

// Copy Dokka output into root docs directory (legacy location expected by project)
tasks.register<Copy>("syncDokkaToDocs") {
  description = "Sync Dokka HTML output to root docs directory"
  group = "documentation"
  dependsOn("dokkaGenerateHtml")
  val srcDir = layout.buildDirectory.dir("dokka/html")
  val destDir = rootProject.layout.projectDirectory.dir("docs")
  doFirst {
    delete(destDir)
  }
  from(srcDir)
  into(destDir)
}
