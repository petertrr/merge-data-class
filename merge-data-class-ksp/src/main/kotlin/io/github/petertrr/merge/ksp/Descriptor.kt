package io.github.petertrr.merge.ksp

import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter
import io.github.petertrr.plugin.MergeStrategy

class Descriptor(
    val nullableProperties: List<KSValueParameter>,
    val nonNullableProperties: List<KSValueParameter>,
    val customMergeStrategies: Map<KSValueParameter, KSAnnotation>,
) {
    lateinit var properties: List<KSValueParameter>

    companion object {
        fun from(ksClassDeclaration: KSClassDeclaration): Descriptor {
            val parameters = ksClassDeclaration.primaryConstructor?.parameters!!
            return parameters.partition {
                it.type.resolve().isMarkedNullable
            }.let { (nullable, nonNullable) ->
                val parametersWithCustomStrategy = parameters.associateWith {
                    it.annotations.firstOrNull {
                        it.annotationType.resolve().declaration.qualifiedName?.asString() == MergeStrategy::class.qualifiedName
                    }
                }.filterValues {
                    it != null
                }.mapValues { (_, value) ->
                    value!!
                }
                Descriptor(
                    nullable,
                    nonNullable,
                    parametersWithCustomStrategy
                ).apply {
                    properties = parameters
                }
            }
        }
    }
}
