package com.jayrave.falkon.mapper

import com.jayrave.falkon.mapper.exceptions.MissingConverterException
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

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


    @Suppress("UNCHECKED_CAST")
    inline fun <reified C> getConverterForType(
            property: KProperty1<*, C>, configuration: TableConfiguration):
            Converter<C> {

        val javaClass: Class<C> = (C::class as KClass<*>).java as Class<C>
        val converter = when (property.returnType.isMarkedNullable) {
            true -> configuration.getConverterForNullableType(javaClass)
            else -> configuration.getConverterForNonNullType(javaClass as Class<Any>)
        } as Converter<C>?

        return converter ?:
                buildConverterIfEnum(javaClass, true) ?:
                throw buildMissingConverterException(javaClass, true)
    }


    inline fun <reified C> getConverterForNullableType(
            configuration: TableConfiguration): Converter<C> {

        @Suppress("UNCHECKED_CAST")
        val javaClass: Class<C> = (C::class as KClass<*>).java as Class<C>
        return configuration.getConverterForNullableType(javaClass) ?:
                buildConverterIfEnum(javaClass, true) ?:
                throw buildMissingConverterException(javaClass, true)
    }


    inline fun <reified C : Any> getConverterForNonNullType(
            configuration: TableConfiguration): Converter<C> {

        val javaClass: Class<C> = C::class.java
        return configuration.getConverterForNonNullType(javaClass) ?:
                buildConverterIfEnum(javaClass, false) ?:
                throw buildMissingConverterException(javaClass, false)
    }


    fun <C> buildConverterIfEnum(clazz: Class<C>, isNullable: Boolean): Converter<C>? {
        // Can't cast C to Enum<C> due to recursive type, so cast to any enum
        @Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
        return when (clazz.isEnum) {
            false -> null
            else -> {
                val nullableConverter = NullableEnumByNameConverter(clazz as Class<DummyEnum>)
                when (isNullable) {
                    true -> nullableConverter
                    else -> NullableToNonNullConverter(nullableConverter)
                } as Converter<C>
            }
        }
    }


    fun buildMissingConverterException(
            clazz: Class<*>, isNullable: Boolean):
            MissingConverterException {

        val nullability = when (isNullable) {
            true -> "nullable"
            else -> "non-null"
        }

        return MissingConverterException("Converter not found for $nullability form of $clazz")
    }

    // -------------------------------------- Converters -------------------------------------------


    // ---------------------------------- Property extractor ---------------------------------------

    /**
     * @return a [PropertyExtractor] that just does a get for the property on the instance.
     * For eg., if `item` is the instance & `price` is the property, the returned property
     * extractor does the equivalent of `item.price`
     */
    fun <T : Any, C> buildDefaultExtractorFrom(property: KProperty1<T, C>):
            PropertyExtractor<T, C> = SimplePropertyExtractor(property)

    // ---------------------------------- Property extractor ---------------------------------------
}
