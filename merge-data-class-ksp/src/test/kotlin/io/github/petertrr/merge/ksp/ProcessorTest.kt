package io.github.petertrr.merge.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.github.petertrr.plugin.BuildFromPartial
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.io.File

class ProcessorTest {
    @Test
    fun `simple test`() {
        val source = SourceFile.kotlin(
            "Example.kt",
            """
            package com.example
            
            import ${BuildFromPartial::class.qualifiedName}

            @BuildFromPartial
            data class Example(
                val foo: Int,
                val bar: String?,
            )
            """.trimIndent(),
        )

        val compilation = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            symbolProcessorProviders = listOf(ProcessorProvider())
            kspIncremental = true
        }
        val result = compilation.compile()

        result.exitCode shouldBe KotlinCompilation.ExitCode.OK

        val generatedSourcesDir = compilation.kspSourcesDir
        val generatedFile = File(
            generatedSourcesDir,
            "kotlin/com/example/ExamplePartial.kt"
        )
        @Language("kotlin") val expected = """
            |package com.example
            |
            |import kotlin.Int
            |import kotlin.String
            |
            |public class ExamplePartial(
            |  public val foo: Int?,
            |  public val bar: String?,
            |) {
            |  public fun merge(other: ExamplePartial): Example {
            |                        return Example(
            |                            foo ?: other.foo ?:
            |        error("Property foo is null on both arguments, but is non-nullable in class com.example.Example"),
            |    bar ?: other.bar
            |                        )
            |  }
            |}
            |
        """.trimMargin()

        generatedFile.shouldExist()
        generatedFile.readText() shouldBe expected

        // Checking that the generated file can be compiled as well.
        val secondCompilation = KotlinCompilation().apply {
            sources = listOf(source, SourceFile.fromPath(generatedFile))
            inheritClassPath = true
        }
        val secondResult = secondCompilation.compile()

        secondResult.exitCode shouldBe KotlinCompilation.ExitCode.OK
    }
}
