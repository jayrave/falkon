package com.jayrave.falkon.dao.query.lenient

import com.jayrave.falkon.dao.lib.IterableBackedIterable
import com.jayrave.falkon.dao.lib.getAppropriateName
import com.jayrave.falkon.dao.query.Query
import com.jayrave.falkon.dao.query.QueryImpl
import com.jayrave.falkon.dao.where.lenient.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.lenient.WhereBuilder
import com.jayrave.falkon.dao.where.lenient.WhereBuilderImpl
import com.jayrave.falkon.engine.CompiledQuery
import com.jayrave.falkon.engine.Engine
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import java.util.*
import com.jayrave.falkon.dao.where.lenient.AdderOrEnder as WhereAdderOrEnder

internal class QueryBuilderImpl(
        private val engine: Engine, private val querySqlBuilder: QuerySqlBuilder,
        private val argPlaceholder: String, private val qualifyColumnNames: Boolean) :
        QueryBuilder,
        AdderOrEnderBeforeWhere {

    private lateinit var table: Table<*, *>
    private var distinct: Boolean = false
    private var selectedColumns: MutableList<Column<*, *>>? = null
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
     * again leading to the SELECT statement that has this column name twice. For example,
     * if the column name "example_column" is passed twice to this method in the same
     * invocation or even in two separate invocations, it would result in a SELECT that look like
     *
     *      `SELECT example_column, ..., example_column, ... FROM ...`
     */
    override fun select(column: Column<*, *>, vararg others: Column<*, *>):
            AdderOrEnderBeforeWhere {

        if (selectedColumns == null) {
            selectedColumns = LinkedList()
        }

        selectedColumns!!.add(column)
        selectedColumns!!.addAll(others)
        return this
    }


    override fun join(column: Column<*, *>, onColumn: Column<*, *>): AdderOrEnderBeforeWhere {
        if (joinInfoList == null) {
            joinInfoList = LinkedList()
        }

        joinInfoList!!.add(JoinInfoImpl(
                JoinInfo.Type.INNER_JOIN, column.getAppropriateName(qualifyColumnNames),
                onColumn.table.name, onColumn.getAppropriateName(qualifyColumnNames)
        ))

        return this
    }


    override fun where(): WhereBuilder<PredicateAdderOrEnder> {
        whereBuilder = WhereBuilderImpl(qualifyColumnNames) { PredicateAdderOrEnderImpl(it) }
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

        orderByInfoList!!.add(OrderInfoImpl(
                column.getAppropriateName(qualifyColumnNames), ascending
        ))

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
        val where = whereBuilder?.build()

        val tempSelectedColumns = selectedColumns
        val columns = when (tempSelectedColumns) {
            null -> null
            else -> IterableBackedIterable(tempSelectedColumns) { it.name }
        }

        val tempGroupByColumns = groupByColumns
        val groupBy = when (tempGroupByColumns) {
            null -> null
            else -> IterableBackedIterable(tempGroupByColumns) { it.name }
        }

        val sql = querySqlBuilder.build(
                table.name, distinct, columns, joinInfoList, where?.whereSections, groupBy,
                orderByInfoList, limitCount, offsetCount, argPlaceholder
        )

        return QueryImpl(sql, where?.arguments ?: emptyList())
    }


    override fun compile(): CompiledQuery {
        val query = build()
        return engine
                .compileQuery(query.sql)
                .closeIfOpThrows { bindAll(query.arguments) }
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


        override fun select(column: Column<*, *>, vararg others: Column<*, *>):
                AdderOrEnderAfterWhere {

            this@QueryBuilderImpl.select(column, *others)
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


        override fun compile(): CompiledQuery {
            return this@QueryBuilderImpl.compile()
        }
    }



    private data class JoinInfoImpl(
            override val type: JoinInfo.Type,
            override val qualifiedLocalColumnName: String,
            override val nameOfTableToJoin: String,
            override val qualifiedColumnNameFromTableToJoin: String
    ) : JoinInfo



    private data class OrderInfoImpl(
            override val columnName: String,
            override val ascending: Boolean
    ) : OrderInfo
}