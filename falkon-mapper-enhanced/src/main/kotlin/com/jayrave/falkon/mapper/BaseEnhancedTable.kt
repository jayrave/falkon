package com.jayrave.falkon.mapper

import com.jayrave.falkon.dao.Dao
import com.jayrave.falkon.mapper.TableImplementationHelper.buildDefaultExtractorFrom
import com.jayrave.falkon.mapper.TableImplementationHelper.getDefaultNullFromSqlSubstitute
import com.jayrave.falkon.mapper.TableImplementationHelper.computeFormattedNameOf
import com.jayrave.falkon.mapper.TableImplementationHelper.getDefaultNullToSqlSubstitute
import com.jayrave.falkon.mapper.TableImplementationHelper.getConverterForNonNullType
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


    inline fun <reified C> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            converter: Converter<C> = getConverterForNullableType(configuration),
            nullFromSqlSubstitute: NullSubstitute<C> = getDefaultNullFromSqlSubstitute(true),
            nullToSqlSubstitute: NullSubstitute<C> = getDefaultNullToSqlSubstitute(true),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            EnhancedColumn<T, C> {

        return addColumn<C, Any, Any?>(
                name, maxSize, isNonNull, isUnique, null, null, converter,
                nullFromSqlSubstitute, nullToSqlSubstitute, propertyExtractor
        )
    }


    inline fun <reified C : Any> col(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            converter: Converter<C> = getConverterForNonNullType(configuration),
            nullFromSqlSubstitute: NullSubstitute<C> = getDefaultNullFromSqlSubstitute(false),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            EnhancedColumn<T, C> {

        return addColumn<C, Any, Any?>(
                name, maxSize, isNonNull, isUnique, null, null, converter,
                nullFromSqlSubstitute, getDefaultNullToSqlSubstitute(false), propertyExtractor
        )
    }


    inline fun <reified C, FT : Any, FC> foreignCol(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            foreignTable: Table<FT, *>,
            foreignColumn: Column<FT, FC>,
            converter: Converter<C> = getConverterForNullableType(configuration),
            nullFromSqlSubstitute: NullSubstitute<C> = getDefaultNullFromSqlSubstitute(true),
            nullToSqlSubstitute: NullSubstitute<C> = getDefaultNullToSqlSubstitute(true),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            EnhancedColumn<T, C> {

        return addColumn(
                name, maxSize, isNonNull, isUnique, foreignTable, foreignColumn, converter,
                nullFromSqlSubstitute, nullToSqlSubstitute, propertyExtractor
        )
    }


    inline fun <reified C : Any, FT : Any, FC> foreignCol(
            property: KProperty1<T, C>,
            name: String = computeFormattedNameOf(property, configuration),
            maxSize: Int? = DEFAULT_MAX_SIZE,
            isNonNull: Boolean = DEFAULT_IS_NON_NULL_FLAG,
            isUnique: Boolean = DEFAULT_IS_UNIQUE_FLAG,
            foreignTable: Table<FT, *>,
            foreignColumn: Column<FT, FC>,
            converter: Converter<C> = getConverterForNonNullType(configuration),
            nullFromSqlSubstitute: NullSubstitute<C> = getDefaultNullFromSqlSubstitute(false),
            propertyExtractor: PropertyExtractor<T, C> = buildDefaultExtractorFrom(property)):
            EnhancedColumn<T, C> {

        return addColumn(
                name, maxSize, isNonNull, isUnique, foreignTable, foreignColumn, converter,
                nullFromSqlSubstitute, getDefaultNullToSqlSubstitute(false), propertyExtractor
        )
    }


    fun <C, FT : Any, FC> addColumn(
            name: String,
            maxSize: Int?,
            isNonNull: Boolean,
            isUnique: Boolean,
            foreignTable: Table<FT, *>?,
            foreignColumn: Column<FT, FC>?,
            converter: Converter<C>,
            nullFromSqlSubstitute: NullSubstitute<C>,
            nullToSqlSubstitute: NullSubstitute<C>,
            propertyExtractor: PropertyExtractor<T, C>):
            EnhancedColumn<T, C> {

        val column = EnhancedColumnImpl(
                this, name, maxSize, isNonNull, propertyExtractor, converter,
                nullFromSqlSubstitute, nullToSqlSubstitute, configuration.typeTranslator
        )

        // Add extra stuff only if this column is new
        if (allColumnImpls.offer(column)) {
            if (isUnique) {
                uniquenessConstraints.add(listOf(column.name))
            }

            if (foreignTable != null && foreignColumn != null) {
                foreignKeyConstraints.add(ForeignKeyConstraintImpl(
                        name, foreignTable.name, foreignColumn.name
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