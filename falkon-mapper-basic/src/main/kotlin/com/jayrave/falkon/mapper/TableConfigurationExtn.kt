package com.jayrave.falkon.mapper


/**
 * Registers converters for the following types
 *
 *      **Primitives**
 *          - [Byte]
 *          - [Char]
 *          - [Short]
 *          - [Int]
 *          - [Long]
 *          - [Float]
 *          - [Double]
 *          - [Boolean]
 *
 *      **Non primitives - both nullable (?) & non-null forms**
 *          - [Byte]
 *          - [Char]
 *          - [Short]
 *          - [Int]
 *          - [Long]
 *          - [Float]
 *          - [Double]
 *          - [Boolean]
 *          - [String]
 *          - [ByteArray]
 *
 * CAUTION: Previously registers converters will be overwritten
 */
fun TableConfigurationImpl.registerDefaultConverters() {
    val nullableByteConverter = NullableByteConverter()
    val nullableCharConverter = NullableCharConverter()
    val nullableShortConverter = NullableShortConverter()
    val nullableIntConverter = NullableIntConverter()
    val nullableLongConverter = NullableLongConverter()
    val nullableFloatConverter = NullableFloatConverter()
    val nullableDoubleConverter = NullableDoubleConverter()
    val nullableBooleanConverter = NullableBooleanConverter()

    val nonNullByteConverter = NullableToNonNullConverter(nullableByteConverter)
    val nonNullCharConverter = NullableToNonNullConverter(nullableCharConverter)
    val nonNullShortConverter = NullableToNonNullConverter(nullableShortConverter)
    val nonNullIntConverter = NullableToNonNullConverter(nullableIntConverter)
    val nonNullLongConverter = NullableToNonNullConverter(nullableLongConverter)
    val nonNullFloatConverter = NullableToNonNullConverter(nullableFloatConverter)
    val nonNullDoubleConverter = NullableToNonNullConverter(nullableDoubleConverter)
    val nonNullBooleanConverter = NullableToNonNullConverter(nullableBooleanConverter)

    // Register for primitives => Byte, Char, Short, Int, Long, Float, Double & Boolean
    registerForNonNullValues(Byte::class.javaPrimitiveType!!, nonNullByteConverter)
    registerForNonNullValues(Char::class.javaPrimitiveType!!, nonNullCharConverter)
    registerForNonNullValues(Short::class.javaPrimitiveType!!, nonNullShortConverter)
    registerForNonNullValues(Int::class.javaPrimitiveType!!, nonNullIntConverter)
    registerForNonNullValues(Long::class.javaPrimitiveType!!, nonNullLongConverter)
    registerForNonNullValues(Float::class.javaPrimitiveType!!, nonNullFloatConverter)
    registerForNonNullValues(Double::class.javaPrimitiveType!!, nonNullDoubleConverter)
    registerForNonNullValues(Boolean::class.javaPrimitiveType!!, nonNullBooleanConverter)

    // Register for non nullable non-primitives => Byte, Char, Short, Int, Long,
    // Float, Double & Boolean
    registerForNonNullValues(Byte::class.javaObjectType, nonNullByteConverter)
    registerForNonNullValues(Char::class.javaObjectType, nonNullCharConverter)
    registerForNonNullValues(Short::class.javaObjectType, nonNullShortConverter)
    registerForNonNullValues(Int::class.javaObjectType, nonNullIntConverter)
    registerForNonNullValues(Long::class.javaObjectType, nonNullLongConverter)
    registerForNonNullValues(Float::class.javaObjectType, nonNullFloatConverter)
    registerForNonNullValues(Double::class.javaObjectType, nonNullDoubleConverter)
    registerForNonNullValues(Boolean::class.javaObjectType, nonNullBooleanConverter)

    // Register for nullable non-primitives => Byte?, Char?, Short?, Int?, Long?,
    // Float?, Double? & Boolean?
    registerForNullableValues(Byte::class.javaObjectType, nullableByteConverter, false)
    registerForNullableValues(Char::class.javaObjectType, nullableCharConverter, false)
    registerForNullableValues(Short::class.javaObjectType, nullableShortConverter, false)
    registerForNullableValues(Int::class.javaObjectType, nullableIntConverter, false)
    registerForNullableValues(Long::class.javaObjectType, nullableLongConverter, false)
    registerForNullableValues(Float::class.javaObjectType, nullableFloatConverter, false)
    registerForNullableValues(Double::class.javaObjectType, nullableDoubleConverter, false)
    registerForNullableValues(Boolean::class.javaObjectType, nullableBooleanConverter, false)

    // Register for nullable & non nullable non-primitives => String, String?,
    // ByteArray, ByteArray?
    registerForNullableValues(String::class.javaObjectType, NullableStringConverter(), true)
    registerForNullableValues(ByteArray::class.javaObjectType, NullableBlobConverter(), true)
}

