package com.jayrave.falkon

import com.jayrave.falkon.dao.Dao
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * An abstract extension of [Table] that could be sub-classed for easy & pain-free
 * implementation of [Table]
 */
abstract class BaseTable<T : Any, ID : Any, D : Dao<T, ID>>(
        override val name: String, override val configuration: TableConfiguration) :
        Table<T, ID, D> {

    override final val allColumns: Set<Column<T, *>> = Collections.newSetFromMap(
            ConcurrentHashMap()
    )


    inline fun <reified C> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property),
            converter: Converter<C> = configuration.getConverterForNullableType(getJavaClassFor(C::class)),
            nullFromSqlSubstitute: NullSubstitute<C> = buildNullFromSqlSubstitute(true),
            nullToSqlSubstitute: NullSubstitute<C> = buildNullToSqlSubstitute(true),
            propertyExtractor: PropertyExtractor<T, C> = buildPropertyExtractorFrom(property)):
            Column<T, C> {

        return addColumn(
                name, converter, nullFromSqlSubstitute, nullToSqlSubstitute, propertyExtractor
        )
    }


    inline fun <reified C : Any> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property),
            converter: Converter<C> = configuration.getConverterForNonNullType(getJavaClassFor(C::class)),
            nullFromSqlSubstitute: NullSubstitute<C> = buildNullFromSqlSubstitute(false),
            propertyExtractor: PropertyExtractor<T, C> = buildPropertyExtractorFrom(property)):
            Column<T, C> {

        return addColumn(
                name, converter, nullFromSqlSubstitute, buildNullToSqlSubstitute(false),
                propertyExtractor
        )
    }


    fun <C> addColumn(
            name: String, converter: Converter<C>, nullFromSqlSubstitute: NullSubstitute<C>,
            nullToSqlSubstitute: NullSubstitute<C>, propertyExtractor: PropertyExtractor<T, C>):
            Column<T, C> {

        val column = ColumnImpl(
                name, propertyExtractor, converter, nullFromSqlSubstitute, nullToSqlSubstitute
        )

        (allColumns as MutableSet).add(column)
        return column
    }


    fun <C> computeFormattedNameOf(property: KProperty1<T, C>): String {
        return configuration.nameFormatter.format(property.name)
    }


    fun <C> buildPropertyExtractorFrom(property: KProperty1<T, C>): PropertyExtractor<T, C> {
        return SimplePropertyExtractor(property)
    }



    companion object {

        val nullReturningNullSubstitute = object : NullSubstitute<Any?> {
            override fun value() = null
        }

        val throwingNullFromSqlSubstitute = object : NullSubstitute<Any> {
            override fun value() = throw NullPointerException(
                    "Trying to assign return null for a non-null type property!!. Solution: " +
                            "make db column non-null or provide a valid ${NullSubstitute::class}"
            )
        }

        val throwingNullToSqlSubstitute = object : NullSubstitute<Any> {
            override fun value() = throw NullPointerException(
                    "Something is wrong!! Let the developer know"
            )
        }


        fun <C> buildNullFromSqlSubstitute(isNullableProperty: Boolean): NullSubstitute<C> {
            @Suppress("UNCHECKED_CAST")
            return when (isNullableProperty) {
                true -> nullReturningNullSubstitute
                false -> throwingNullFromSqlSubstitute
            } as NullSubstitute<C>
        }


        fun <C> buildNullToSqlSubstitute(isNullableProperty: Boolean): NullSubstitute<C> {
            @Suppress("UNCHECKED_CAST")
            return when (isNullableProperty) {
                true -> nullReturningNullSubstitute
                false -> throwingNullToSqlSubstitute
            } as NullSubstitute<C>
        }


        /**
         * This is used as a work around to get Class<> from a generic type that is upper
         * bounded by Any?
         *
         * **CAUTION: Don't use this unless you perfectly know what this method does &
         * what it is used for**
         */
        fun <C> getJavaClassFor(clazz: KClass<*>): Class<C> {
            @Suppress("CAST_NEVER_SUCCEEDS")
            return clazz.java as Class<C>
        }
    }
}