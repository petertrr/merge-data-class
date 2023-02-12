plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.7.2")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.21.0-RC1")
}
