import org.gradle.api.internal.FeaturePreviews.Feature

rootProject.name = "merge-data-class"
include("merge-data-class-annotations")
include("merge-data-class-ksp")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

enableFeaturePreview(Feature.TYPESAFE_PROJECT_ACCESSORS.name)
