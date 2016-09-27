package com.jayrave.falkon.engine

/**
 * Represents all types natively supported by the engine. All engines can handle
 * `null` in addition to those listed here (usually the type null is associated
 * with is required => use [TypedNull] for this purpose)
 */
enum class Type {
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE,
    STRING,
    BLOB
}