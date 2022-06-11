# merge-data-class KSP processor
This is a Kotlin Symbol Processor implementation that is intended to ease some tasks related to data classes by generating
boilerplate code that helps to preserve type-safe nullability when working with incomplete data sources.

Sometimes there are cases when data comes from different sources, e.g. some fields are read from configuration file
and others are set by user on the command line. After application startup those two sets of values sum up into a data class
where all fields have to be present, i.e. they are non-nullable in terms of Kotlin type system. So, to construct such a class
one might want to read all values manually, or create a special logic for iterating over class properties etc.
This approach however leads to a lot of boilerplate.

Or there might be another approach - to make properties nullable, construct data class instances from all data sources 
and then merge them. However, this passes nullable properties down the whole application and introduces a huge number of
unnecessary `!!` all over the code base.

`merge-data-class` serves as a pretty simple code generation solution for this case. Based on a data class it would generate
a class with nullable properties and a merge method, which would return an instance of the original class while also performing
nullability checks.

Consider the code below:
```kotlin
@BuildFromPartial
data class Foo(
    val field1: Type1,
    val field2: Type2?,
)
```

`merge-data-class` will generate the following code:

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