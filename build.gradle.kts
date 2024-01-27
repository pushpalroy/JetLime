plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.kotlin.android) apply false
  alias(libs.plugins.nexus.vanniktech.publish) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.spotless) apply false
}

// Compose Compiler Metrics
// Run ./gradlew assembleRelease -PcomposeCompilerReports=true to generate reports
// https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md#enabling-metrics
subprojects {
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
      if (project.findProperty("composeCompilerReports") == "true") {
        freeCompilerArgs += listOf(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.buildDir.absolutePath}/compose_compiler",
        )
      }
      if (project.findProperty("composeCompilerMetrics") == "true") {
        freeCompilerArgs += listOf(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.buildDir.absolutePath}/compose_compiler",
        )
      }
    }
  }
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("$buildDir/**/*.kt")
      targetExclude("bin/**/*.kt")
      ktlint()
        .setEditorConfigPath("$rootDir/.editorconfig")
        .editorConfigOverride(
          mapOf(
            "indent_size" to "2",
            "continuation_indent_size" to "2",
          ),
        )
        .customRuleSets(
          listOf(
            "io.nlopez.compose.rules:ktlint:0.3.11",
          ),
        )
      licenseHeaderFile(rootProject.file("spotless/copyright.kt"))
      trimTrailingWhitespace()
      endWithNewline()
    }

    kotlinGradle {
      target("*.gradle.kts")
      ktlint()
    }
  }
}

tasks.register("copySpotlessPreCommitHook") {
  doLast {
    copy {
      from("./scripts/run_spotless.sh")
      into("./.git/hooks")
    }
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}