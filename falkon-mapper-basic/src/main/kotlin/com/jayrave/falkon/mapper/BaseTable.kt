package com.jayrave.falkon.mapper

import com.jayrave.falkon.mapper.TableImplementationHelper.buildDefaultExtractorFrom
import com.jayrave.falkon.mapper.TableImplementationHelper.computeFormattedNameOf
import com.jayrave.falkon.mapper.TableImplementationHelper.getConverterForType
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


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param property the kotlin property this column corresponds to
     * @param name of this column. If it isn't provided, [configuration.nameFormatter]
     * formatted name of [property] is used
     * @param converter to convert [C] from/to appropriate SQL type. If it isn't
     * provided, whatever [configuration] returns for [property]'s type is used
     * @param propertyExtractor to extract the property from an instance of [T]. If it isn't
     * provided, [property.get] is used
     */
    inline fun <reified C> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            converter: Converter<C> = getConverterForType(property, configuration),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            Column<T, C> {

        return addColumn(name, converter, propertyExtractor)
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param name of this column
     * @param converter to convert [C] from/to appropriate SQL type
     * @param propertyExtractor to extract the property from an instance of [T]
     */
    fun <C> addColumn(
            name: String, converter: Converter<C>, propertyExtractor: PropertyExtractor<T, C>):
            Column<T, C> {

        val column = ColumnImpl(this, name, propertyExtractor, converter)
        allColumnImpls.offer(column)
        return column
    }
}