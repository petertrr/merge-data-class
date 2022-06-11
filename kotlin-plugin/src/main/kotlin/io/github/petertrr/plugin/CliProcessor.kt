package io.github.petertrr.plugin

import io.github.petertrr.BuildConfig
import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor

class CliProcessor(override val pluginId: String = BuildConfig.KOTLIN_PLUGIN_ID) : CommandLineProcessor {
    override val pluginOptions: Collection<AbstractCliOption>
        get() = emptyList()
}
