plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ksp)
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
