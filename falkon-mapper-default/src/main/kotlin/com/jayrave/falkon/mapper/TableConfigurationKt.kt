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
    registerForNonNullType(Byte::class.javaPrimitiveType!!, nonNullByteConverter)
    registerForNonNullType(Char::class.javaPrimitiveType!!, nonNullCharConverter)
    registerForNonNullType(Short::class.javaPrimitiveType!!, nonNullShortConverter)
    registerForNonNullType(Int::class.javaPrimitiveType!!, nonNullIntConverter)
    registerForNonNullType(Long::class.javaPrimitiveType!!, nonNullLongConverter)
    registerForNonNullType(Float::class.javaPrimitiveType!!, nonNullFloatConverter)
    registerForNonNullType(Double::class.javaPrimitiveType!!, nonNullDoubleConverter)
    registerForNonNullType(Boolean::class.javaPrimitiveType!!, nonNullBooleanConverter)

    // Register for non nullable non-primitives => Byte, Char, Short, Int, Long,
    // Float, Double & Boolean
    registerForNonNullType(Byte::class.javaObjectType, nonNullByteConverter)
    registerForNonNullType(Char::class.javaObjectType, nonNullCharConverter)
    registerForNonNullType(Short::class.javaObjectType, nonNullShortConverter)
    registerForNonNullType(Int::class.javaObjectType, nonNullIntConverter)
    registerForNonNullType(Long::class.javaObjectType, nonNullLongConverter)
    registerForNonNullType(Float::class.javaObjectType, nonNullFloatConverter)
    registerForNonNullType(Double::class.javaObjectType, nonNullDoubleConverter)
    registerForNonNullType(Boolean::class.javaObjectType, nonNullBooleanConverter)

    // Register for nullable non-primitives => Byte?, Char?, Short?, Int?, Long?,
    // Float?, Double? & Boolean?
    registerForNullableType(Byte::class.javaObjectType, nullableByteConverter, false)
    registerForNullableType(Char::class.javaObjectType, nullableCharConverter, false)
    registerForNullableType(Short::class.javaObjectType, nullableShortConverter, false)
    registerForNullableType(Int::class.javaObjectType, nullableIntConverter, false)
    registerForNullableType(Long::class.javaObjectType, nullableLongConverter, false)
    registerForNullableType(Float::class.javaObjectType, nullableFloatConverter, false)
    registerForNullableType(Double::class.javaObjectType, nullableDoubleConverter, false)
    registerForNullableType(Boolean::class.javaObjectType, nullableBooleanConverter, false)

    // Register for nullable & non nullable non-primitives => String, String?,
    // ByteArray, ByteArray?
    registerForNullableType(String::class.javaObjectType, NullableStringConverter(), true)
    registerForNullableType(ByteArray::class.javaObjectType, NullableBlobConverter(), true)
}

