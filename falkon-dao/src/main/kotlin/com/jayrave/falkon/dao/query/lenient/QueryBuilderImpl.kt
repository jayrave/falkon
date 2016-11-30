package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.dao.lib.qualifiedName
import com.jayrave.falkon.dao.query.Query
import com.jayrave.falkon.dao.query.QueryImpl
import com.jayrave.falkon.dao.where.lenient.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.lenient.WhereBuilderImpl
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.iterables.IterableBackedIterable
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import com.jayrave.falkon.sqlBuilders.lib.SelectColumnInfo
import java.util.*
import com.jayrave.falkon.dao.where.lenient.AdderOrEnder as WhereAdderOrEnder

internal class QueryBuilderImpl(private val querySqlBuilder: QuerySqlBuilder) :
        QueryBuilder,
        AdderOrEnderBeforeWhere {

    private lateinit var table: Table<*, *>
    private var distinct: Boolean = false
    private var selectColumnInfoList: MutableList<SelectColumnInfo>? = null
    private var joinInfoList: MutableList<JoinInfoImpl>? = null
    private var whereBuilder: WhereBuilderImpl<PredicateAdderOrEnder>? = null
    private var groupByColumns: MutableList<Column<*, *>>? = null
    private var orderByInfoList: MutableList<OrderInfoImpl>? = null
    private var limitCount: Long? = null
    private var offsetCount: Long? = null

    override fun fromTable(table: Table<*, *>): AdderOrEnderBeforeWhere {
        this.table = table
        return this
    }


    override fun distinct(): AdderOrEnderBeforeWhere {
        distinct = true
        return this
    }


    /**
     * Calling this method again for a column that has been already included will include it
     * again, leading to a SELECT statement that has this column name twice. For example,
     * if this method is called twice for the column "example_column", it would result in
     * a SELECT that looks like
     *
     *      `SELECT example_column, ..., example_column, ... FROM ...`
     */
    override fun select(column: Column<*, *>, alias: String?): AdderOrEnderBeforeWhere {
        if (selectColumnInfoList == null) {
            selectColumnInfoList = LinkedList()
        }

        selectColumnInfoList!!.add(SelectColumnInfoImpl(column.qualifiedName, alias))
        return this
    }


    override fun join(column: Column<*, *>, onColumn: Column<*, *>): AdderOrEnderBeforeWhere {
        if (joinInfoList == null) {
            joinInfoList = LinkedList()
        }

        joinInfoList!!.add(JoinInfoImpl(
                JoinInfo.Type.INNER_JOIN, column.qualifiedName,
                onColumn.qualifiedName, onColumn.table
        ))

        return this
    }


    override fun where(): WhereBuilderImpl<PredicateAdderOrEnder> {
        whereBuilder = WhereBuilderImpl(true) { PredicateAdderOrEnderImpl(it) }
        return whereBuilder!!
    }


    /**
     * Calling this method again for a column that has been already included will include it
     * again leading to a GROUP BY clause that has this column name twice. For example,
     * if the column name "example_column" is passed twice to this method in the same
     * invocation or even in two separate invocations, it would result in a SELECT that look like
     *
     *      `SELECT ... GROUP BY example_column, ..., example_column, ...`
     */
    override fun groupBy(column: Column<*, *>, vararg others: Column<*, *>):
            AdderOrEnderBeforeWhere {

        if (groupByColumns == null) {
            groupByColumns = LinkedList()
        }

        groupByColumns!!.add(column)
        groupByColumns!!.addAll(others)
        return this
    }


    /**
     * Calling this method again for a column that has been already included will include it
     * again leading to an ORDER BY clause that has this column name twice. For example,
     * if the column name "example_column" is passed twice to this method, it would result
     * in a SELECT that look like (where `ASC|DESC` says that it would be either ASC|DESC
     * depending on the passed in flag [ascending])
     *
     *      `SELECT ... ORDER BY example_column ASC|DESC, ..., example_column ASC|DESC, ...`
     */
    override fun orderBy(column: Column<*, *>, ascending: Boolean): AdderOrEnderBeforeWhere {
        if (orderByInfoList == null) {
            orderByInfoList = LinkedList()
        }

        orderByInfoList!!.add(OrderInfoImpl(column.qualifiedName, ascending))
        return this
    }


    override fun limit(count: Long): AdderOrEnderBeforeWhere {
        limitCount = count
        return this
    }


    override fun offset(count: Long): AdderOrEnderBeforeWhere {
        offsetCount = count
        return this
    }


    override fun build(): Query {
        val tableNames = HashSet<String>()
        tableNames.add(table.name)
        joinInfoList?.forEach { tableNames.add(it.tableToJoin.name) }

        val where = whereBuilder?.build()
        val sql = querySqlBuilder.build(
                table.name, distinct, selectColumnInfoList, joinInfoList, where?.whereSections,
                buildGroupByList(), orderByInfoList, limitCount, offsetCount
        )

        return QueryImpl(tableNames, sql, where?.arguments ?: emptyList())
    }


    override fun compile(): CompiledStatement<Source> {
        // Build query
        val query = build()

        // Find all the tables this query is concerned with
        //      - Add default table's name
        //      - Add names of other tables involved in joins
        val concernedTableNames = LinkedHashSet<String>()
        concernedTableNames.add(table.name)
        joinInfoList?.forEach { concernedTableNames.add(it.nameOfTableToJoin) }

        return table.configuration.engine
                .compileQuery(concernedTableNames, query.sql)
                .closeIfOpThrows { bindAll(query.arguments) }
    }


    private fun buildGroupByList(): Iterable<String>? {
        val tempGroupByColumns = groupByColumns
        return when (tempGroupByColumns) {
            null -> null
            else -> IterableBackedIterable.create(tempGroupByColumns) { it.qualifiedName }
        }
    }



    private inner class PredicateAdderOrEnderImpl(
            private val delegate: WhereAdderOrEnder<PredicateAdderOrEnder>) :
            PredicateAdderOrEnder {

        override fun and(): AfterSimpleConnectorAdder<PredicateAdderOrEnder> {
            return delegate.and()
        }


        override fun or(): AfterSimpleConnectorAdder<PredicateAdderOrEnder> {
            return delegate.or()
        }


        override fun distinct(): AdderOrEnderAfterWhere {
            this@QueryBuilderImpl.distinct()
            return this
        }


        override fun select(column: Column<*, *>, alias: String?): AdderOrEnderAfterWhere {
            this@QueryBuilderImpl.select(column, alias)
            return this
        }


        override fun join(column: Column<*, *>, onColumn: Column<*, *>): AdderOrEnderAfterWhere {
            this@QueryBuilderImpl.join(column, onColumn)
            return this
        }


        override fun groupBy(column: Column<*, *>, vararg others: Column<*, *>):
                AdderOrEnderAfterWhere {

            this@QueryBuilderImpl.groupBy(column, *others)
            return this
        }


        override fun orderBy(column: Column<*, *>, ascending: Boolean): AdderOrEnderAfterWhere {
            this@QueryBuilderImpl.orderBy(column, ascending)
            return this
        }


        override fun limit(count: Long): AdderOrEnderAfterWhere {
            this@QueryBuilderImpl.limit(count)
            return this
        }


        override fun offset(count: Long): AdderOrEnderAfterWhere {
            this@QueryBuilderImpl.offset(count)
            return this
        }


        override fun build(): Query {
            return this@QueryBuilderImpl.build()
        }


        override fun compile(): CompiledStatement<Source> {
            return this@QueryBuilderImpl.compile()
        }
    }



    private data class SelectColumnInfoImpl(
            override val columnName: String,
            override val alias: String?
    ) : SelectColumnInfo



    private data class JoinInfoImpl(
            override val type: JoinInfo.Type,
            override val qualifiedLocalColumnName: String,
            override val qualifiedColumnNameFromTableToJoin: String,
            val tableToJoin: Table<*, *>) : JoinInfo {

        override val nameOfTableToJoin: String get() = tableToJoin.name
    }



    private data class OrderInfoImpl(
            override val columnName: String,
            override val ascending: Boolean
    ) : OrderInfo
}