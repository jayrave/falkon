package com.jayrave.falkon.mapper

import com.jayrave.falkon.mapper.TableImplementationHelper.buildDefaultExtractorFor
import com.jayrave.falkon.mapper.TableImplementationHelper.computeFormattedNameOf
import com.jayrave.falkon.mapper.TableImplementationHelper.getConverterFor
import java.util.*
import kotlin.reflect.KProperty1

/**
 * An abstract extension of [Table] that could be sub-classed for easy & pain-free
 * implementation of [Table]
 *
 * *NOTE:* All columns should be declared up front, before accessing [allColumns],
 * [idColumns] or [nonIdColumns]. Adding new columns via any method after that will
 * result in an exception being thrown
 */
abstract class BaseTable<T : Any, in ID : Any>(
        override val name: String, override val configuration: TableConfiguration) :
        Table<T, ID> {

    private var newColumnsCanBeAdded = true
    private val tempColumns = LinkedHashSet<Column<T, *>>()
    override final val allColumns: Collection<Column<T, *>> by lazy { tempColumns.toImmutable() }
    override final val idColumns: Collection<Column<T, *>> by lazy { allColumns.filter { it.isId } }
    override final val nonIdColumns: Collection<Column<T, *>> by lazy {
        allColumns.filterNot { it.isId }
    }


    private fun <Z> lazy(operation: () -> Z) = kotlin.lazy(LazyThreadSafetyMode.NONE) {
        newColumnsCanBeAdded = false
        operation.invoke()
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param property the kotlin property this column corresponds to
     * @param name of this column. If it isn't provided, [TableConfiguration.nameFormatter]
     * formatted name of [property] is used
     * @param isId whether this column is (or part of) the primary key for this table.
     * By default it is `false`
     * @param converter to convert [C] from/to appropriate SQL type. If it isn't
     * provided, whatever [configuration] returns for [property]'s type is used
     * @param propertyExtractor to extract the property from an instance of [T]. If it isn't
     * provided, [KProperty1.get] is used
     */
    fun <C> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            isId: Boolean = DEFAULT_IS_ID_FLAG,
            converter: Converter<C> = getConverterFor(property, configuration),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFor(property)):
            Column<T, C> {

        return addColumn(name, isId, converter, propertyExtractor)
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param name of this column
     * @param isId whether this column is (or part of) the primary key for this table.
     * By default it is `false`
     * @param converter to convert [C] from/to appropriate SQL type
     * @param propertyExtractor to extract the property from an instance of [T]
     */
    fun <C> addColumn(
            name: String,
            isId: Boolean = DEFAULT_IS_ID_FLAG,
            converter: Converter<C>,
            propertyExtractor: PropertyExtractor<T, C>):
            Column<T, C> {

        when {
            !newColumnsCanBeAdded -> throw IllegalStateException(
                    "It's too late to add new columns now. All columns must be declared up front"
            )

            else -> {
                val column = ColumnImpl(this, name, isId, propertyExtractor, converter)
                tempColumns.add(column)
                return column
            }
        }
    }


    companion object {
        const val DEFAULT_IS_ID_FLAG = false
        private fun<Z> Collection<Z>.toImmutable(): Collection<Z> {
            return Collections.unmodifiableCollection(this)
        }
    }
}