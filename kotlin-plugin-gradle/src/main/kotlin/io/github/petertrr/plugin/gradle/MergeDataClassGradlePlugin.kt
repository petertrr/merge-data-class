package io.github.petertrr.plugin.gradle

import io.github.petertrr.BuildConfig
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class MergeDataClassGradlePlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        TODO("Not yet implemented")
    }

    override fun getCompilerPluginId(): String {
        return BuildConfig.KOTLIN_PLUGIN_ID
    }

    override fun getPluginArtifact(): SubpluginArtifact {
        return SubpluginArtifact(
            groupId = BuildConfig.KOTLIN_PLUGIN_GROUP,
            artifactId = BuildConfig.KOTLIN_PLUGIN_NAME,
            version = BuildConfig.KOTLIN_PLUGIN_VERSION,
        )
    }

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean {
        return true
    }
}
