plugins {
    kotlin("multiplatform")
    id("convention.maven-publishing")
    id("convention.code-style")
}

kotlin {
    jvm()
    js(BOTH) {
        browser()
        nodejs()
    }
    iosArm64()
    iosX64()
    macosX64()
    tvosArm64()
    tvosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()
    linuxX64()
    mingwX64()
}

val jvmArchive by configurations.creating
val jvmJar by tasks.getting
artifacts.add(jvmArchive.name, jvmJar)
