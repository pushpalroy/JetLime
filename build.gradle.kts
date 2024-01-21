// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.nexus.vanniktech.publish) apply false
}

tasks.register("clean", Delete::class) {
  delete(rootProject.buildDir)
}