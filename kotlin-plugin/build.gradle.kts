plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.buildconfig)
}

dependencies {
//    compileOnly("org.jetbrains.kotlin:kotlin-compiler")
    compileOnly("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("com.github.tschuchortdev:kotlin-compile-testing:1.4.7")
    testImplementation(project(":merge-data-class-annotations"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// todo: share logic between subprojects
buildConfig {
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${pluginConfig.pluginId}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}
