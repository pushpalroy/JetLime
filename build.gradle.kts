// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
  repositories {
    google()
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
  }
  dependencies {
    classpath(DependingOn.Gradle.androidGradlePlugin)
    classpath(DependingOn.Gradle.kotlinGradlePlugin)
    classpath(DependingOn.Gradle.ktlintGradlePlugin)
    classpath(DependingOn.Gradle.vanniktechGradlePlugin)
  }
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}

subprojects {
  apply(plugin = Plugins.ktlint)
}