package io.github.petertrr.plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class MergeDataClassPluginTest {
    @Test
    fun `IR plugin success`() {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.kotlin(
                "main.kt", """
                fun main() {
                  println(debug())
                }
                fun debug() = "Hello, World!"
                """.trimIndent()
            ))
        }.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }
}
