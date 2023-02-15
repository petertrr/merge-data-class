package io.github.petertrr.merge.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import io.github.petertrr.merge.ksp.generators.copyParametersAsNullable
import io.github.petertrr.merge.ksp.generators.createMergeMethod
import io.github.petertrr.plugin.BuildFromPartial

class Processor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getSymbolsWithAnnotation(BUILD_FROM_PARTIAL_ANNOTATION_NAME)
            .filterIsInstance<KSClassDeclaration>()
            .filterNot { it.primaryConstructor == null }
            .forEach { ksClassDeclaration ->
                logger.info("Processing class annotated with `@$BUILD_FROM_PARTIAL_ANNOTATION_NAME`", ksClassDeclaration)

                val packageName = ksClassDeclaration.packageName.asString()
                val originalClassName = ksClassDeclaration.simpleName.asString()
                val partialClassName = ClassName(
                    packageName,
                    "${ksClassDeclaration.simpleName.asString()}Partial"
                )
                val descriptor = Descriptor.from(ksClassDeclaration)

                val generatedFileSpec = FileSpec.builder(
                    packageName = packageName,
                    fileName = partialClassName.simpleName
                )
                val partialClassSpec = TypeSpec.classBuilder(
                    className = partialClassName
                )

                val ctorBuilder = FunSpec.constructorBuilder()
                partialClassSpec.copyParametersAsNullable(
                    ctorBuilder,
                    descriptor.properties
                )
                partialClassSpec.createMergeMethod(
                    descriptor,
                    ClassName(packageName, originalClassName),
                    partialClassName,
                )

                partialClassSpec.primaryConstructor(
                    ctorBuilder.build()
                )
                generatedFileSpec.addType(
                    partialClassSpec.build()
                )
                val text = generatedFileSpec.build()

                logger.info("Writing generated code into ${generatedFileSpec.name}")
                codeGenerator.createNewFile(
                    dependencies = Dependencies(
                        aggregating = true,
                        sources = arrayOf(ksClassDeclaration.containingFile!!)
                    ),
                    packageName = packageName,
                    fileName = generatedFileSpec.name,
                ).bufferedWriter().use {
                    text.writeTo(it)
                }
            }

        return emptyList()
    }

    companion object {
        private val BUILD_FROM_PARTIAL_ANNOTATION_NAME = BuildFromPartial::class.qualifiedName!!
    }
}
