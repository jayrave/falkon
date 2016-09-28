package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.mapper.TableImplementationHelper.buildDefaultExtractorFrom
import com.jayrave.falkon.mapper.TableImplementationHelper.computeFormattedNameOf
import com.jayrave.falkon.mapper.TableImplementationHelper.getConverterForNullableType
import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.ForeignKeyConstraint
import com.jayrave.falkon.sqlBuilders.lib.TableInfo
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KProperty1

abstract class BaseEnhancedTable<T : Any, ID : Any, out D : Dao<T, ID>>(
        override val name: String, override val configuration: TableConfiguration,
        private val createTableSqlBuilder: CreateTableSqlBuilder) :
        EnhancedTable<T, ID, D> {

    override final val allColumns: Collection<EnhancedColumn<T, *>> get() = allColumnImpls

    private val uniquenessConstraints: MutableList<List<String>> = LinkedList()
    private val foreignKeyConstraints: MutableList<ForeignKeyConstraint> = LinkedList()
    private val allColumnImpls = ConcurrentLinkedQueue<EnhancedColumnImpl<T, *>>()

    override fun buildCreateTableSql(): String {
        return createTableSqlBuilder.build(TableInfoImpl())
    }


    /**
     * To just make one column unique, use [col] or [foreignCol]. This is for cases where
     * more than one column should be involved. Eg., UNIQUE(column_1, column_2)
     */
    fun addUniquenessConstraint(
            firstColumn: Column<T, *>, secondColumn: Column<T, *>,
            vararg remainingColumnsInConstraint: Column<T, *>) {

        val constraint = LinkedList<String>()
        constraint.add(firstColumn.name)
        constraint.add(secondColumn.name)
        remainingColumnsInConstraint.forEach { constraint.add(it.name) }
        uniquenessConstraints.add(constraint)
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param property the kotlin property this column corresponds to
     * @param name of this column. If it isn't provided, [configuration.nameFormatter]
     * formatted name of [property] is used
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values; `false` by default
     * @param isUnique whether this column expects all values to be unique; `false` by default
     * @param converter to convert [C] from/to appropriate SQL type. If it isn't
     * provided, whatever [configuration] returns for [property]'s type is used
     * @param propertyExtractor to extract the property from an instance of [T]. If it isn't
     * provided, [property.get] is used
     */
    inline fun <reified C> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            converter: Converter<C> = getConverterForNullableType(configuration),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            EnhancedColumn<T, C> {

        return addColumn<C, Any, Any?>(
                name, maxSize, isNonNull, isUnique, null,
                converter, propertyExtractor
        )
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param property the kotlin property this column corresponds to
     * @param name of this column. If it isn't provided, [configuration.nameFormatter]
     * formatted name of [property] is used
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values; `false` by default
     * @param isUnique whether this column expects all values to be unique; `false` by default
     * @param foreignColumn column from a foreign table this column corresponds to
     * @param converter to convert [C] from/to appropriate SQL type. If it isn't
     * provided, whatever [configuration] returns for [property]'s type is used
     * @param propertyExtractor to extract the property from an instance of [T]. If it isn't
     * provided, [property.get] is used
     */
    inline fun <reified C, FT : Any, FC> foreignCol(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            foreignColumn: Column<FT, FC>,
            converter: Converter<C> = getConverterForNullableType(configuration),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            EnhancedColumn<T, C> {

        return addColumn(
                name, maxSize, isNonNull, isUnique,
                foreignColumn, converter, propertyExtractor
        )
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param name of this column
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values; `false` by default
     * @param isUnique whether this column expects all values to be unique; `false` by default
     * @param foreignColumn column from a foreign table this column corresponds to
     * @param converter to convert [C] from/to appropriate SQL type
     * @param propertyExtractor to extract the property from an instance of [T]
     */
    fun <C, FT : Any, FC> addColumn(
            name: String,
            maxSize: Int?,
            isNonNull: Boolean,
            isUnique: Boolean,
            foreignColumn: Column<FT, FC>?,
            converter: Converter<C>,
            propertyExtractor: PropertyExtractor<T, C>):
            EnhancedColumn<T, C> {

        val column = EnhancedColumnImpl(
                this, name, maxSize, isNonNull, propertyExtractor, converter,
                configuration.typeTranslator
        )

        if (allColumnImpls.offer(column)) {
            if (isUnique) {
                uniquenessConstraints.add(listOf(column.name))
            }

            if (foreignColumn != null) {
                foreignKeyConstraints.add(ForeignKeyConstraintImpl(
                        name, foreignColumn.table.name, foreignColumn.name
                ))
            }
        }

        return column
    }



    /**
     * Make sure to pass in iterables that aren't modified after creating this [TableInfo]
     */
    private inner class TableInfoImpl : TableInfo {
        override val name: String = this@BaseEnhancedTable.name
        override val columnInfos: Iterable<ColumnInfo> = LinkedList(allColumnImpls)
        override val primaryKeyConstraint: String = idColumn.name
        override val uniquenessConstraints: Iterable<Iterable<String>> = LinkedList(
                this@BaseEnhancedTable.uniquenessConstraints
        )

        override val foreignKeyConstraints: Iterable<ForeignKeyConstraint> = LinkedList(
                this@BaseEnhancedTable.foreignKeyConstraints
        )
    }



    private class ForeignKeyConstraintImpl(
            override val columnName: String,
            override val foreignTableName: String,
            override val foreignColumnName: String
    ) : ForeignKeyConstraint



    companion object {
        val DEFAULT_MAX_SIZE: Int? = null
        const val DEFAULT_IS_NON_NULL_FLAG = false
        const val DEFAULT_IS_UNIQUE_FLAG = false
    }
}