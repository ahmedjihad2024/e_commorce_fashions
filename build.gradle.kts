
// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral() // https://oss.sonatype.org/content/repositories/snapshots/
//        maven {
//            url = uri("https://oss.jfrog.org/artifactory/oss-snapshot-local")
//        }
//        maven {
//            url = uri("https://cardinalcommerceprod.jfrog.io/artifactory/android")
//            credentials {
//                username = "John Doe"
//                password = "AM8eSy-369zGZhyburmqKvKw2IgEAa1Lkd-k4ZwD2VzSH9karDtR8K1F"
//            }
//        }

    }
    dependencies {
        // Dependency Injection Dagger - Hilt
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.44.2")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.gms.google.services) apply false
}