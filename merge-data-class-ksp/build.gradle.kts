import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    alias(libs.plugins.ksp)
    id("convention.maven-publishing")
    id("convention.code-style")
}

dependencies {
    compileOnly(project(":merge-data-class-annotations", "jvmArchive"))
    implementation(libs.ksp.api)
    implementation(libs.square.kotlinpoet)

    ksp("dev.zacsweers.autoservice:auto-service-ksp:1.0.0")
    implementation("com.google.auto.service:auto-service-annotations:1.0.1")

    testImplementation(libs.junit.jupiter.engine)
    testImplementation(libs.kotest.assertions)
    testImplementation(libs.tschuchortdev.testing)
    testImplementation(libs.tschuchortdev.testing.ksp)
    testImplementation(project(":merge-data-class-annotations", "jvmArchive"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-Xcontext-receivers"
    }
}

java {
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "${project.group}"
            artifactId = project.name
            version = version
            from(components["java"])
        }
    }
}
