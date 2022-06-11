# merge-data-class Kotlin Compiler Plugin
```kotlin
@BuildFromPartial
data class Foo(
    val field1: Type1,
    val field2: Type2?,  // can be explicitly set to @Optional
)
```
generated code:

```kotlin
data class FooPartial(
    val field1: Type1?,
    val field2: Type2?,
) {
    fun merge(other: FooPartial): Foo {
        return Foo(
            field1 ?: other.field1,
            field2 ?: other.field2
        )
    }

    fun mergeToPartial(vararg other: FooPartial): FooPartial {
        val current = other.first()
        return if (other.size == 1) current
        else mergeToPartial(other.drop(1)).let {
            FooPartial(
                current.field1 ?: it.field1,
                current.field2 ?: it.field2,
            )
        }
    }
    
    fun validate(): Foo {
        return Foo(
            requireNotNull(field1),
            field2
        )
    }
}
```

# Using it in a gradle project
This plugin can be used as any other KSP processor: apply the KSP plugin and add required dependencies:
```kotlin
plugins{
    kotlin("ksp") version "1.6.21"
}

dependencies {
    compileOnly("io.github.petertrr:merge-data-class-annotations:0.1.0")
    ksp("io.github.petertrr:merge-data-class-ksp:0.1.0")
}
```