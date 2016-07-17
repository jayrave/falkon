package com.jayrave.falkon.mapper

import com.jayrave.falkon.mapper.TableImplementationHelper.buildDefaultExtractorFrom
import com.jayrave.falkon.mapper.TableImplementationHelper.getDefaultNullFromSqlSubstitute
import com.jayrave.falkon.mapper.TableImplementationHelper.getDefaultNullToSqlSubstitute
import com.jayrave.falkon.mapper.TableImplementationHelper.getJavaClassFor
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KProperty1

/**
 * An abstract extension of [Table] that could be sub-classed for easy & pain-free
 * implementation of [Table]
 */
abstract class BaseTable<T : Any, ID : Any>(
        override val name: String, override val configuration: TableConfiguration) :
        Table<T, ID> {

    private val allColumnImpls = ConcurrentLinkedQueue<Column<T, *>>()
    override final val allColumns: Collection<Column<T, *>> = allColumnImpls


    inline fun <reified C> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property),
            converter: Converter<C> = getConverterForNullableType(),
            nullFromSqlSubstitute: NullSubstitute<C> = getDefaultNullFromSqlSubstitute(true),
            nullToSqlSubstitute: NullSubstitute<C> = getDefaultNullToSqlSubstitute(true),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            Column<T, C> {

        return addColumn(
                name, converter, nullFromSqlSubstitute, nullToSqlSubstitute, propertyExtractor
        )
    }


    inline fun <reified C : Any> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property),
            converter: Converter<C> = getConverterForNonNullType(),
            nullFromSqlSubstitute: NullSubstitute<C> = getDefaultNullFromSqlSubstitute(false),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            Column<T, C> {

        return addColumn(
                name, converter, nullFromSqlSubstitute, getDefaultNullToSqlSubstitute(false),
                propertyExtractor
        )
    }


    fun <C> addColumn(
            name: String,
            converter: Converter<C>,
            nullFromSqlSubstitute: NullSubstitute<C>,
            nullToSqlSubstitute: NullSubstitute<C>,
            propertyExtractor: PropertyExtractor<T, C>):
            Column<T, C> {

        val column = ColumnImpl(
                name, propertyExtractor, converter,
                nullFromSqlSubstitute, nullToSqlSubstitute
        )

        allColumnImpls.offer(column)
        return column
    }


    fun <C> computeFormattedNameOf(property: KProperty1<T, C>): String {
        return configuration.nameFormatter.format(property.name)
    }


    inline fun <reified C> getConverterForNullableType(): Converter<C> {
        return configuration.getConverterForNullableType(getJavaClassFor(C::class))
    }


    inline fun <reified C : Any> getConverterForNonNullType(): Converter<C> {
        return configuration.getConverterForNonNullType(C::class.java)
    }
}