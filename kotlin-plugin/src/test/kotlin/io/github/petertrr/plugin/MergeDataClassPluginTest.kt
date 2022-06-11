package io.github.petertrr.plugin

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.File

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

    @Test
    fun `basic example`() {
        val result = KotlinCompilation().apply {
            sources = listOf(SourceFile.kotlin(
                "main.kt", """
                import io.github.petertrr.plugin.BuildFromPartial
                    
                @BuildFromPartial
                data class Foo(
                    val field1: Int,
                    val field2: String?,
                )
                """.trimIndent()
            ))
            classpaths = listOf(
                File("C:\\Users\\Peter\\IdeaProjects\\merge-data-class\\merge-data-class-annotations\\build\\libs\\merge-data-class-annotations-0.1.0-SNAPSHOT.jar")
            )
            compilerPlugins = listOf(MyComponentRegistrar())
            commandLineProcessors = listOf(CliProcessor())

            inheritClassPath = true  // ???
            messageOutputStream = System.out // see diagnostics in real time
        }.compile()
        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        result.compiledClassAndResourceFiles
    }
}
