package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.lib.IterableBackedIterable
import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.CompiledQuery
import com.jayrave.falkon.engine.bindAll
import com.jayrave.falkon.engine.closeIfOpThrows
import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.OrderInfo
import java.util.*
import com.jayrave.falkon.dao.where.AdderOrEnder as WhereAdderOrEnder

internal class QueryBuilderImpl<T : Any>(
        override val table: Table<T, *>, private val querySqlBuilder: QuerySqlBuilder,
        private val argPlaceholder: String) : QueryBuilder<T> {

    private var distinct: Boolean = false
    private var selectedColumns: MutableList<Column<T, *>>? = null
    private var whereBuilder: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>? = null
    private var groupByColumns: MutableList<Column<T, *>>? = null
    private var orderByInfoList: MutableList<OrderInfoImpl>? = null
    private var limitCount: Long? = null
    private var offsetCount: Long? = null

    override fun distinct(): QueryBuilder<T> {
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
    override fun select(column: Column<T, *>, vararg others: Column<T, *>): QueryBuilder<T> {
        if (selectedColumns == null) {
            selectedColumns = LinkedList()
        }

        selectedColumns!!.add(column)
        selectedColumns!!.addAll(others)
        return this
    }


    override fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
        whereBuilder = WhereBuilderImpl { PredicateAdderOrEnderImpl(it) }
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
    override fun groupBy(column: Column<T, *>, vararg others: Column<T, *>): QueryBuilder<T> {
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
    override fun orderBy(column: Column<T, *>, ascending: Boolean): QueryBuilder<T> {
        if (orderByInfoList == null) {
            orderByInfoList = LinkedList()
        }

        orderByInfoList!!.add(OrderInfoImpl(column, ascending))
        return this
    }


    override fun limit(count: Long): QueryBuilder<T> {
        limitCount = count
        return this
    }


    override fun offset(count: Long): QueryBuilder<T> {
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
                table.name, distinct, columns, null, where?.whereSections, groupBy,
                orderByInfoList, limitCount, offsetCount, argPlaceholder
        )

        return QueryImpl(sql, where?.arguments ?: emptyList())
    }


    override fun compile(): CompiledQuery {
        val query = build()
        return table.configuration.engine
                .compileQuery(query.sql)
                .closeIfOpThrows { bindAll(query.arguments) }
    }


    private inner class PredicateAdderOrEnderImpl(
            private val delegate: WhereAdderOrEnder<T, PredicateAdderOrEnder<T>>) :
            PredicateAdderOrEnder<T> {

        override fun and(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            return delegate.and()
        }

        override fun or(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            return delegate.or()
        }

        override fun distinct(): AdderOrEnderAfterWhere<T> {
            this@QueryBuilderImpl.distinct()
            return this
        }

        override fun select(column: Column<T, *>, vararg others: Column<T, *>):
                AdderOrEnderAfterWhere<T> {

            this@QueryBuilderImpl.select(column, *others)
            return this
        }

        override fun groupBy(column: Column<T, *>, vararg others: Column<T, *>):
                AdderOrEnderAfterWhere<T> {

            this@QueryBuilderImpl.groupBy(column, *others)
            return this
        }

        override fun orderBy(column: Column<T, *>, ascending: Boolean): AdderOrEnderAfterWhere<T> {
            this@QueryBuilderImpl.orderBy(column, ascending)
            return this
        }

        override fun limit(count: Long): AdderOrEnderAfterWhere<T> {
            this@QueryBuilderImpl.limit(count)
            return this
        }

        override fun offset(count: Long): AdderOrEnderAfterWhere<T> {
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


    private data class OrderInfoImpl(
            val column: Column<*, *>, override val ascending: Boolean) :
            OrderInfo {

        override val columnName: String get() = column.name
    }
}