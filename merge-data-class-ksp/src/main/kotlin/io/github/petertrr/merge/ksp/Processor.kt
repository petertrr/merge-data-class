package io.github.petertrr.merge.ksp

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.github.petertrr.merge.ksp.generators.copyParametersAsNullable
import io.github.petertrr.merge.ksp.generators.createMergeMethod
import io.github.petertrr.plugin.BuildFromPartial
import io.github.petertrr.plugin.Merge
import io.github.petertrr.plugin.MergeStrategy

class Processor(
    environment: SymbolProcessorEnvironment
) : SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val kspEnvironmentContext = KspEnvironmentContext(
            logger
        )

        resolver.getSymbolsWithAnnotation(BUILD_FROM_PARTIAL_ANNOTATION_NAME)
            .filterIsInstance<KSClassDeclaration>()
            .filterNot { it.primaryConstructor == null }
            .forEach { ksClassDeclaration ->
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
                with(kspEnvironmentContext) {
                    partialClassSpec.createMergeMethod(
                        descriptor,
                        ClassName(packageName, originalClassName),
                        partialClassName,
                    )
                }

                partialClassSpec.primaryConstructor(
                    ctorBuilder.build()
                )
                generatedFileSpec.addType(
                    partialClassSpec.build()
                )

                descriptor.customMergeStrategies.forEach { (t, ksAnnotation) ->
                    logger.info("Processing property ${t.name} with annotation $ksAnnotation")
                    val name = ksAnnotation.arguments.find { it.name?.asString() == "with" }!!.value.toString()
                    generatedFileSpec.addProperty(
                        PropertySpec.builder(
                            name = name.replaceFirstChar { it.lowercase() },
                            type = Merge::class.asClassName()/*.parameterizedBy(
                                WildcardTypeName.producerOf(Any::class)
                            )*/,
                            modifiers = arrayOf(KModifier.PRIVATE)
                        )
                            .initializer("$name()")
                            .build()
                    )
                }

                val text = generatedFileSpec.build()

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
