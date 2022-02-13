plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.buildconfig)
}

dependencies {
    implementation(kotlin("gradle-plugin-api"))
}

buildConfig {
    val project = project(":kotlin-plugin")
    packageName(project.group.toString())
    buildConfigField("String", "KOTLIN_PLUGIN_ID", "\"${pluginConfig.pluginId}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_GROUP", "\"${project.group}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_NAME", "\"${project.name}\"")
    buildConfigField("String", "KOTLIN_PLUGIN_VERSION", "\"${project.version}\"")
}

gradlePlugin {
    plugins {
        create("kotlinIrPluginTemplate") {
            id = pluginConfig.pluginId
            displayName = "merge-data-class Kotlin Compiler Plugin"
            description = "merge-data-class Kotlin Compiler Plugin"
            implementationClass = "io.github.petertrr.plugin.MergeDataClassGradlePlugin"
        }
    }
}
