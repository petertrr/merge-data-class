package io.github.petertrr.plugin

/**
 * Indicates that an instance of this class can be constructed by merging incomplete data from different sources.
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class BuildFromPartial

/**
 * Configures exact behavior of merging for this property
 *
 * @property strategy specifies merging mode for this property
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.PROPERTY)
annotation class WithStrategy(
    val strategy: MergeStrategy = MergeStrategy.OVERWRITE,
)
