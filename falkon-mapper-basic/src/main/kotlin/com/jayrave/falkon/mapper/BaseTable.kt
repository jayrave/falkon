package com.jayrave.falkon.mapper

import com.jayrave.falkon.mapper.TableImplementationHelper.buildDefaultExtractorFrom
import com.jayrave.falkon.mapper.TableImplementationHelper.computeFormattedNameOf
import com.jayrave.falkon.mapper.TableImplementationHelper.getConverterForNullableType
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
            name: String = computeFormattedNameOf(property, configuration),
            converter: Converter<C> = getConverterForNullableType(configuration),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            Column<T, C> {

        return addColumn(name, converter, propertyExtractor)
    }


    fun <C> addColumn(
            name: String, converter: Converter<C>, propertyExtractor: PropertyExtractor<T, C>):
            Column<T, C> {

        val column = ColumnImpl(this, name, propertyExtractor, converter)
        allColumnImpls.offer(column)
        return column
    }
}