package io.github.petertrr.merge.ksp

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSValueParameter

class Descriptor(
    val nullableProperties: List<KSValueParameter>,
    val nonNullableProperties: List<KSValueParameter>,
) {
    lateinit var properties: List<KSValueParameter>

    companion object {
        fun from(ksClassDeclaration: KSClassDeclaration): Descriptor {
            val parameters = ksClassDeclaration.primaryConstructor?.parameters!!
            return parameters.partition {
                it.type.resolve().isMarkedNullable
            }.let { (nullable, nonNullable) ->
                Descriptor(nullable, nonNullable).apply {
                    properties = parameters
                }
            }
        }
    }
}
