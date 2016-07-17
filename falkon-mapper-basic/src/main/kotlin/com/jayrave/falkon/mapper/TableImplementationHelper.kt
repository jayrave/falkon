package com.jayrave.falkon.mapper

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * This is mostly used by abstract base implementations of [Table] like [BaseTable].
 * Concrete implementations are better off not using this
 */
object TableImplementationHelper {

    // ------------------------------ Null substitute properties -----------------------------------

    private val nullReturningNullSubstitute = object : NullSubstitute<Any?> {
        override fun value() = null
    }

    private val throwingNullFromSqlSubstitute = object : NullSubstitute<Any> {
        override fun value() = throw NullPointerException(
                "Trying to assign return null for a non-null type property!!. Solution: " +
                        "make db column non-null or provide a valid ${NullSubstitute::class}"
        )
    }

    private val throwingNullToSqlSubstitute = object : NullSubstitute<Any> {
        override fun value() = throw NullPointerException(
                "Something is wrong!! Let the developer know"
        )
    }

    // ------------------------------ Null substitute properties -----------------------------------


    /**
     * @return a [NullSubstitute] that returns `null` if [isNullableProperty] is `true` &
     * a [NullSubstitute] that throws a [NullPointerException] if [isNullableProperty] is `false`
     */
    fun <C> getDefaultNullFromSqlSubstitute(isNullableProperty: Boolean): NullSubstitute<C> {
        @Suppress("UNCHECKED_CAST")
        return when (isNullableProperty) {
            true -> nullReturningNullSubstitute
            false -> throwingNullFromSqlSubstitute
        } as NullSubstitute<C>
    }


    /**
     * @return a [NullSubstitute] that returns `null` if [isNullableProperty] is `true` &
     * a [NullSubstitute] that throws a [NullPointerException] if [isNullableProperty] is `false`
     */
    fun <C> getDefaultNullToSqlSubstitute(isNullableProperty: Boolean): NullSubstitute<C> {
        @Suppress("UNCHECKED_CAST")
        return when (isNullableProperty) {
            true -> nullReturningNullSubstitute
            false -> throwingNullToSqlSubstitute
        } as NullSubstitute<C>
    }


    /**
     * @return a [PropertyExtractor] that just does a get for the property on the instance.
     * For eg., if `item` is the instance & `price` is the property, the returned property
     * extractor does the equivalent of `item.price`
     */
    fun <T : Any, C> buildDefaultExtractorFrom(property: KProperty1<T, C>):
            PropertyExtractor<T, C> {

        return SimplePropertyExtractor(property)
    }


    /**
     * This is used as a work around to get Class<> from a generic type that is upper
     * bounded by Any?
     *
     * **CAUTION: Don't use this unless you perfectly know what this method does &
     * what it is used for**
     */
    fun <C> getJavaClassFor(clazz: KClass<*>): Class<C> {
        @Suppress("UNCHECKED_CAST")
        return clazz.java as Class<C>
    }
}
