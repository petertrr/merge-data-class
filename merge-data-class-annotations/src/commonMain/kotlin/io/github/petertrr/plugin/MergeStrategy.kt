package io.github.petertrr.plugin

import kotlin.reflect.KClass

interface Merge<T> {
    fun merge(into: T, from: T): T
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class MergeStrategy<T>(
    val with: KClass<out Merge<in T>>,
)

//class M : Merge<String>
//class L : Merge<List<String>>
//class K : Merge<List<Any>>
//
//class Foo(
//    @MergeStrategy<String>(with = M::class) val s: String,
//    @MergeStrategy<List<String>>(with = L::class) val l: List<String>,
//    @MergeStrategy<List<String>>(with = K::class) val k: List<String>,
//)