plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.android.library) apply false
  alias(libs.plugins.jetbrains.compose) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kotlin.multiplatform) apply false
  alias(libs.plugins.nexus.vanniktech.publish) apply false
  alias(libs.plugins.dokka) apply false
  alias(libs.plugins.spotless) apply false
  alias(libs.plugins.kotlin.cocoapods) apply false
  alias(libs.plugins.compose.compiler.report.generator) apply false
}

// Compose Compiler Metrics
// Run ./gradlew assembleRelease -PcomposeCompilerReports=true to generate reports
// https://github.com/androidx/androidx/blob/androidx-main/compose/compiler/design/compiler-metrics.md#enabling-metrics
subprojects {
  tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
      if (project.findProperty("composeCompilerReports") == "true") {
        freeCompilerArgs.addAll(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:reportsDestination=${project.layout.buildDirectory.get().asFile.absolutePath}/compose_compiler",
        )
      }
      if (project.findProperty("composeCompilerMetrics") == "true") {
        freeCompilerArgs.addAll(
          "-P",
          "plugin:androidx.compose.compiler.plugins.kotlin:metricsDestination=${project.layout.buildDirectory.get().asFile.absolutePath}/compose_compiler",
        )
      }
    }
  }
  apply(plugin = "com.diffplug.spotless")
  configure<com.diffplug.gradle.spotless.SpotlessExtension> {
    kotlin {
      target("**/*.kt")
      targetExclude("${project.layout.buildDirectory.get()}/**/*.kt")
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
