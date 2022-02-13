plugins {
    kotlin("jvm")
}

repositories {
    mavenCentral()
}

dependencies {
//    compileOnly("org.jetbrains.kotlin:kotlin-compiler")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
}