package io.github.petertrr.merge.ksp.generators

import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.*
import io.github.petertrr.merge.ksp.Descriptor
import io.github.petertrr.merge.ksp.KspEnvironmentContext
import io.github.petertrr.plugin.MergeStrategy

internal fun TypeSpec.Builder.copyParametersAsNullable(
    ctorBuilder: FunSpec.Builder,
    parameters: List<KSValueParameter>
): TypeSpec.Builder {
    parameters.forEach { originalParameter ->
        val type = originalParameter.type
        val resolvedType = originalParameter.type.resolve()
        val className = ClassName(
            resolvedType.declaration.packageName.asString(),
            resolvedType.declaration.simpleName.asString(),
        )
            .copy(nullable = true)
        val typeName = if (resolvedType.arguments.isNotEmpty()) {
            with(ParameterizedTypeName.Companion) {
                (className as ClassName).parameterizedBy(
                    typeArguments = resolvedType.arguments.map {
                        TypeVariableName.Companion.invoke(it.type.toString())
                    }
                )
            }
        } else {
            className
        }
            .copy(nullable = true)

        val originalParameterName = originalParameter.name!!.asString()
        if (type is KSClassifierReference && resolvedType.isMarkedNullable) {
            ctorBuilder.addParameter(
                name = originalParameterName,
                type = typeName,
            )
        } else {
            ctorBuilder.addParameter(
                name = originalParameterName,
                type = typeName,
            )
        }

        // Parameters need to be duplicated as properties for kotlinpoet to merge them into a primary constructor: https://square.github.io/kotlinpoet/#constructors.
        addProperty(
            PropertySpec.builder(originalParameterName, typeName)
                .initializer(originalParameterName)
                .build()
        )
    }

    return this
}

context(KspEnvironmentContext)
internal fun TypeSpec.Builder.createMergeMethod(
    descriptor: Descriptor,
    originalClassName: ClassName,
    newClassName: ClassName,
): TypeSpec.Builder {
    val params = descriptor.properties.joinToString(separator = ",\n") { parameter ->
        val name = parameter.name!!.asString()
        val isNullable = parameter.type.resolve().isMarkedNullable
        val mergeStrategy = descriptor.customMergeStrategies[parameter]
        buildString {
            if (mergeStrategy != null) {
                logger.info("Using custom merging strategy on property $name")
                val mergeStrategyInstanceName = mergeStrategy.arguments
                    .find { it.name?.asString() == "with" }!!
                    .value
                    .toString()
                    .replaceFirstChar { it.lowercase() }
                append("if ($name != null && other.$name != null) $mergeStrategyInstanceName.merge($name, other.$name) as ${parameter.type.resolve().declaration.qualifiedName?.asString()}<${parameter.type.resolve().declaration.typeParameters.map { it.name.asString() }}> " +
                        "else $name ?: other.$name")
            } else {
                append("$name ?: other.$name")
            }
            if (!isNullable) {
                // Non-breaking spaces because of https://square.github.io/kotlinpoet/#spaces-wrap-by-default
                append(
                    " ?: error(\"Property·$name·is·null·on·both·arguments,·but·is·non-nullable·in·class·${originalClassName.canonicalName}\")"
                )
            }
        }
    }

    addFunction(
        FunSpec.builder("merge")
            .addParameter(
                ParameterSpec("other", newClassName)
            )
            .returns(originalClassName)
            .addCode(
                """
                    return ${originalClassName.simpleName}(
                        $params
                    )
                """.trimIndent()
            )
            .build()
    )
    return this
}
