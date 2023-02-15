package io.github.petertrr.merge.ksp

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.kspSourcesDir
import com.tschuchort.compiletesting.symbolProcessorProviders
import io.github.petertrr.plugin.BuildFromPartial
import io.github.petertrr.plugin.MergeStrategy
import io.github.petertrr.plugin.WithStrategy
import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.io.File

class ProcessorTest {
    @Test
    fun `simple test`() {
        generateCodeFromAndCompare(
            source = """
            |package com.example
            |
            |import ${BuildFromPartial::class.qualifiedName}
            |import ${WithStrategy::class.qualifiedName}
            |import ${MergeStrategy::class.qualifiedName}
            |
            |@BuildFromPartial
            |data class Example(
            |    val foo: Int,
            |    val bar: String?,
            |    @WithStrategy(strategy = MergeStrategy.MERGE_COLLECTION)
            |    val baz: List<String>,
            |)
            """.trimMargin(),
            expectedGeneratedSource = """
            |package com.example
            |
            |import kotlin.Int
            |import kotlin.String
            |import kotlin.collections.List
            |
            |public class ExamplePartial(
            |  public val foo: Int?,
            |  public val bar: String?,
            |  public val baz: List<String>?,
            |) {
            |  public fun merge(other: ExamplePartial): Example {
            |                        return Example(
            |                            foo ?: other.foo ?:
            |        error("Property foo is null on both arguments, but is non-nullable in class com.example.Example"),
            |    bar ?: other.bar,
            |    baz?.run { other.baz?.let { plus(it) } ?: this } ?: other.baz ?:
            |        error("Property baz is null on both arguments, but is non-nullable in class com.example.Example"),
            |                        )
            |  }
            |}
            |
            """.trimMargin(),
        )
    }
}

private fun generateCodeFromAndCompare(
    @Language("kotlin") source: String,
    @Language("kotlin") expectedGeneratedSource: String,
): KotlinCompilation {
    val fileBaseName = "Test"
    // fixme: class name is taken from [source]
    val classBaseName = "Example"
    val sourceFile = SourceFile.kotlin("$fileBaseName.kt", source)
    val compilation = KotlinCompilation().apply {
        sources = listOf(sourceFile)
        inheritClassPath = true
        symbolProcessorProviders = listOf(ProcessorProvider())
        kspIncremental = true
    }
    val result = compilation.compile()

    result.exitCode shouldBe KotlinCompilation.ExitCode.OK

    val generatedSourcesDir = compilation.kspSourcesDir
    val generatedFile = File(
        generatedSourcesDir,
        "kotlin/com/example/${classBaseName}Partial.kt"
    )

    generatedFile.shouldExist()
    generatedFile.readText() shouldBe expectedGeneratedSource

    // Checking that the generated file can be compiled as well.
    val secondCompilation = KotlinCompilation().apply {
        sources = compilation.sources + SourceFile.fromPath(generatedFile)
        inheritClassPath = true
    }
    val secondResult = secondCompilation.compile()

    secondResult.exitCode shouldBe KotlinCompilation.ExitCode.OK

    return compilation
}
