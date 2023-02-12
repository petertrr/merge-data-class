plugins {
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

spotless {
    kotlin {
        ktlint("0.45.2")
    }
    kotlinGradle {
        ktlint("0.45.2")
    }
}

detekt {
    config = files("$rootDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
}

