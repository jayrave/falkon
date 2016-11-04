package com.jayrave.falkon.mapper

import com.jayrave.falkon.mapper.exceptions.MissingConverterException
import java.lang.reflect.Type
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.javaType

@Suppress("unused")
object TableImplementationHelper {

    // ------------------------------------- Name format -------------------------------------------

    fun computeFormattedNameOf(
            property: KProperty1<*, *>, configuration: TableConfiguration):
            String = configuration.nameFormatter.format(property.name)

    // ------------------------------------- Name format -------------------------------------------


    // -------------------------------------- Converters -------------------------------------------

    // For building converters for enum
    private enum class DummyEnum


    fun <C> getConverterFor(
            property: KProperty1<*, C>, configuration: TableConfiguration):
            Converter<C> {

        val kType = property.returnType
        val javaType = kType.javaType
        val converter = when (kType.isMarkedNullable) {
            true -> configuration.getConverterForNullableValuesOf<Any>(javaType)
            else -> configuration.getConverterForNonNullValuesOf<Any>(javaType)
        } ?: buildConverterIfEnum(javaType, true)

        @Suppress("UNCHECKED_CAST")
        return converter as Converter<C>? ?: throw buildMissingConverterException(javaType, true)
    }


    fun buildConverterIfEnum(type: Type, isNullable: Boolean): Converter<*>? {
        return when (type) {
            !is Class<*> -> null
            else -> when {
                !type.isEnum -> null
                else -> {
                    @Suppress("UNCHECKED_CAST")
                    val nullableConverter = NullableEnumByNameConverter(type as Class<DummyEnum>)
                    when (isNullable) {
                        true -> nullableConverter
                        else -> NullableToNonNullConverter(nullableConverter)
                    }
                }
            }
        }
    }


    fun buildMissingConverterException(type: Type, isNullable: Boolean): MissingConverterException {
        val nullability = when (isNullable) {
            true -> "nullable"
            else -> "non-null"
        }

        return MissingConverterException("Converter not found for $nullability form of $type")
    }

    // -------------------------------------- Converters -------------------------------------------


    // ---------------------------------- Property extractor ---------------------------------------

    /**
     * @return a [PropertyExtractor] that just does a get for the property on the instance.
     * For eg., if `item` is the instance & `price` is the property, the returned property
     * extractor does the equivalent of `item.price`
     */
    fun <T : Any, C> buildDefaultExtractorFor(property: KProperty1<T, C>):
            PropertyExtractor<T, C> = SimplePropertyExtractor(property)

    // ---------------------------------- Property extractor ---------------------------------------
}
