package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.mapper.TableImplementationHelper.buildDefaultExtractorFor
import com.jayrave.falkon.mapper.TableImplementationHelper.computeFormattedNameOf
import com.jayrave.falkon.mapper.TableImplementationHelper.getConverterFor
import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.ForeignKeyConstraint
import com.jayrave.falkon.sqlBuilders.lib.TableInfo
import java.util.*
import kotlin.reflect.KProperty1

/**
 * An abstract extension of [EnhancedTable] that could be sub-classed for easy & pain-free
 * implementation of [EnhancedTable] & [Table] by extension
 *
 * *NOTE:* All columns should be declared up front, before accessing [allColumns],
 * [idColumns] or [nonIdColumns]. Adding new columns via any method after that will
 * result in an exception being thrown
 */
abstract class BaseEnhancedTable<T : Any, ID : Any, out D : Dao<T, ID>>(
        override val name: String, override val configuration: TableConfiguration,
        private val createTableSqlBuilder: CreateTableSqlBuilder) :
        EnhancedTable<T, ID, D> {

    private var newColumnsCanBeAdded = true
    private val tempColumns = LinkedHashSet<EnhancedColumnImpl<T, *>>()
    private val uniquenessConstraints: MutableList<List<String>> = LinkedList()
    private val foreignKeyConstraints: MutableList<ForeignKeyConstraint> = LinkedList()

    override final val allColumns: Collection<EnhancedColumn<T, *>> by lazy {
        tempColumns.toImmutable()
    }

    override final val idColumns: Collection<EnhancedColumn<T, *>> by lazy {
        allColumns.filter { it.isId }
    }

    override final val nonIdColumns: Collection<EnhancedColumn<T, *>> by lazy {
        allColumns.filterNot { it.isId }
    }


    private fun <Z> lazy(operation: () -> Z) = kotlin.lazy(LazyThreadSafetyMode.NONE) {
        newColumnsCanBeAdded = false
        operation.invoke()
    }


    override fun buildCreateTableSql(): List<String> {
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
     * @param name of this column. If it isn't provided, [TableConfiguration.nameFormatter]
     * formatted name of [property] is used
     * @param isId whether this column is (or part of) the primary key for this table.
     * By default it is `false`
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values; `false` by default
     * @param isUnique whether this column expects all values to be unique; `false` by default
     * @param autoIncrement whether to insert an auto incremented value if nothing is set
     * explicitly; `false` by default
     * @param converter to convert [C] from/to appropriate SQL type. If it isn't
     * provided, whatever [configuration] returns for [property]'s type is used
     * @param propertyExtractor to extract the property from an instance of [T]. If it isn't
     * provided, [KProperty1.get] is used
     */
    fun <C> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            isId: Boolean = DEFAULT_IS_ID_FLAG,
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            autoIncrement: Boolean = DEFAULT_AUTO_INCREMENT_FLAG,
            converter: Converter<C> = getConverterFor(property, configuration),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFor(property)):
            EnhancedColumn<T, C> {

        return addColumn(
                name, isId, maxSize, isNonNull, isUnique, autoIncrement,
                converter, propertyExtractor
        )
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param property the kotlin property this column corresponds to
     * @param name of this column. If it isn't provided, [TableConfiguration.nameFormatter]
     * formatted name of [property] is used
     * @param isId whether this column is (or part of) the primary key for this table.
     * By default it is `false`
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values; `false` by default
     * @param isUnique whether this column expects all values to be unique; `false` by default
     * @param autoIncrement whether to insert an auto incremented value if nothing is set
     * explicitly; `false` by default
     * @param foreignColumn column from a foreign table this column corresponds to
     * @param converter to convert [C] from/to appropriate SQL type. If it isn't
     * provided, whatever [configuration] returns for [property]'s type is used
     * @param propertyExtractor to extract the property from an instance of [T]. If it isn't
     * provided, [KProperty1.get] is used
     */
    fun <C, FT : Any, FC> foreignCol(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            isId: Boolean = DEFAULT_IS_ID_FLAG,
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            autoIncrement: Boolean = DEFAULT_AUTO_INCREMENT_FLAG,
            foreignColumn: Column<FT, FC>,
            converter: Converter<C> = getConverterFor(property, configuration),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFor(property)):
            EnhancedColumn<T, C> {

        return addForeignColumn(
                name, isId, maxSize, isNonNull, isUnique, autoIncrement,
                foreignColumn, converter, propertyExtractor
        )
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param name of this column
     * @param isId whether this column is (or part of) the primary key for this table.
     * By default it is `false`
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values; `false` by default
     * @param isUnique whether this column expects all values to be unique; `false` by default
     * @param autoIncrement whether to insert an auto incremented value if nothing is set
     * explicitly; `false` by default
     * @param converter to convert [C] from/to appropriate SQL type
     * @param propertyExtractor to extract the property from an instance of [T]
     */
    fun <C> addColumn(
            name: String,
            isId: Boolean = DEFAULT_IS_ID_FLAG,
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            autoIncrement: Boolean = DEFAULT_AUTO_INCREMENT_FLAG,
            converter: Converter<C>,
            propertyExtractor: PropertyExtractor<T, C>):
            EnhancedColumn<T, C> {

        return privateAddColumn<C, Any, Any>(
                name, isId, maxSize, isNonNull, isUnique,
                autoIncrement, null, converter, propertyExtractor
        )
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param name of this column
     * @param isId whether this column is (or part of) the primary key for this table.
     * By default it is `false`
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values; `false` by default
     * @param isUnique whether this column expects all values to be unique; `false` by default
     * @param autoIncrement whether to insert an auto incremented value if nothing is set
     * explicitly; `false` by default
     * @param foreignColumn column from a foreign table this column corresponds to
     * @param converter to convert [C] from/to appropriate SQL type
     * @param propertyExtractor to extract the property from an instance of [T]
     */
    fun <C, FT : Any, FC> addForeignColumn(
            name: String,
            isId: Boolean = DEFAULT_IS_ID_FLAG,
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            autoIncrement: Boolean = DEFAULT_AUTO_INCREMENT_FLAG,
            foreignColumn: Column<FT, FC>?,
            converter: Converter<C>,
            propertyExtractor: PropertyExtractor<T, C>):
            EnhancedColumn<T, C> {

        return privateAddColumn(
                name, isId, maxSize, isNonNull, isUnique, autoIncrement,
                foreignColumn, converter, propertyExtractor
        )
    }


    /**
     * Any [Column] created by calling this method will be automatically added to [allColumns]
     *
     * @param name of this column
     * @param isId whether this column is (or part of) the primary key for this table
     * @param maxSize of this column. If it is `null`, column size isn't bounded
     * @param isNonNull whether this column accepts `null` values
     * @param isUnique whether this column expects all values to be unique
     * @param autoIncrement whether to insert an auto incremented value if nothing is set explicitly
     * @param foreignColumn column from a foreign table this column corresponds to
     * @param converter to convert [C] from/to appropriate SQL type
     * @param propertyExtractor to extract the property from an instance of [T]
     */
    private fun <C, FT : Any, FC> privateAddColumn(
            name: String,
            isId: Boolean,
            maxSize: Int?,
            isNonNull: Boolean,
            isUnique: Boolean,
            autoIncrement: Boolean,
            foreignColumn: Column<FT, FC>?,
            converter: Converter<C>,
            propertyExtractor: PropertyExtractor<T, C>):
            EnhancedColumn<T, C> {

        when {
            !newColumnsCanBeAdded -> throw IllegalStateException(
                    "It's too late to add new columns now. All columns must be declared up front"
            )

            else -> {
                val column = EnhancedColumnImpl(
                        this, name, isId, maxSize, isNonNull, autoIncrement, propertyExtractor,
                        converter, configuration.typeTranslator
                )

                if (tempColumns.add(column)) {
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
        }
    }



    /**
     * Make sure to pass in iterables that aren't modified after creating this [TableInfo]
     */
    private inner class TableInfoImpl : TableInfo {
        override val name: String = this@BaseEnhancedTable.name
        override val columnInfos: Iterable<ColumnInfo> = LinkedList(tempColumns)
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
        const val DEFAULT_IS_ID_FLAG = false
        const val DEFAULT_IS_NON_NULL_FLAG = false
        const val DEFAULT_IS_UNIQUE_FLAG = false
        const val DEFAULT_AUTO_INCREMENT_FLAG = false

        private fun<Z> Collection<Z>.toImmutable(): Collection<Z> {
            return Collections.unmodifiableCollection(this)
        }
    }
}