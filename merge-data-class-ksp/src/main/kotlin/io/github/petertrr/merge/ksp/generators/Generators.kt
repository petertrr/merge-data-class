package io.github.petertrr.merge.ksp.generators

import com.google.devtools.ksp.symbol.KSClassifierReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.squareup.kotlinpoet.*
import io.github.petertrr.merge.ksp.Descriptor

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

internal fun TypeSpec.Builder.createMergeMethod(
    descriptor: Descriptor,
    originalClassName: ClassName,
    newClassName: ClassName,
): TypeSpec.Builder {
    val params = descriptor.properties.joinToString(separator = ",\n") {
        val name = it.name!!.asString()
        val isNullable = it.type.resolve().isMarkedNullable
        buildString {
            append("$name ?: other.$name")
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
