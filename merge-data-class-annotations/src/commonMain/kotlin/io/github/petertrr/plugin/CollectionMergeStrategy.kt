package io.github.petertrr.plugin

/**
 * Indicates a mode of merging class field when merging two class instances
 */
enum class MergeStrategy {
    /**
     * If property in left-hand side operand is not null, it will completely replace property of the
     * right-hand side operand, even if it is not null as well.
     */
    OVERWRITE,

    /**
     * If properties in both instances are not null, they will be merged using `plus` function.
     */
    MERGE_COLLECTION,
    ;
}
