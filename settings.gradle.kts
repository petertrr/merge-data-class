import org.gradle.api.internal.FeaturePreviews.Feature

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("com.gradle.enterprise") version "3.12.3"
}

rootProject.name = "merge-data-class"
includeBuild("gradle/build-logic")
include("merge-data-class-annotations")
include("merge-data-class-ksp")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

enableFeaturePreview(Feature.TYPESAFE_PROJECT_ACCESSORS.name)

gradleEnterprise {
    @Suppress("AVOID_NULL_CHECKS")
    if (System.getenv("CI") != null) {
        buildScan {
            publishAlways()
            termsOfServiceUrl = "https://gradle.com/terms-of-service"
            termsOfServiceAgree = "yes"
        }
    }
}
