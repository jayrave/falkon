package com.jayrave.falkon.dao.query

import com.jayrave.falkon.Column
import com.jayrave.falkon.Table
import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.Source
import java.util.*

class QueryBuilderImpl<T : Any>(override val table: Table<T, *, *, *>) : QueryBuilder<T> {

    private var distinct: Boolean = false
    private var selectedColumns: Iterable<Column<T, *>>? = null
    private var whereBuilder: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>? = null
    private var groupByColumns: Iterable<Column<T, *>>? = null
    private var orderByInfoList: MutableList<OrderInfo>? = null
    private var limitCount: Long? = null
    private var offsetCount: Long? = null

    override fun distinct(): QueryBuilder<T> {
        distinct = true
        return this
    }

    override fun select(column: Column<T, *>, vararg others: Column<T, *>): QueryBuilder<T> {
        selectedColumns = combine(column, *others)
        return this
    }

    override fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
        whereBuilder = WhereBuilderImpl { PredicateAdderOrEnderImpl(it) }
        return whereBuilder!!
    }

    override fun groupBy(column: Column<T, *>, vararg others: Column<T, *>): QueryBuilder<T> {
        groupByColumns = combine(column, *others)
        return this
    }

    override fun orderBy(column: Column<T, *>, ascending: Boolean): QueryBuilder<T> {
        if (orderByInfoList == null) {
            orderByInfoList = LinkedList()
        }

        // Don't add if column is already present in the list
        val columnAlreadyAdded = orderByInfoList!!.any { it.column == column }
        if (!columnAlreadyAdded) {
            orderByInfoList!!.add(OrderInfo(column, ascending))
        }

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

    override fun query(): Source {
        val columns = selectedColumns?.map { it.name }
        val where = whereBuilder?.build()
        val groupBy = groupByColumns?.map { it.name }
        val orderBy = orderByInfoList?.map { Pair(it.column.name, it.ascending) }

        return table.configuration.engine.query(
                table.name, distinct, columns, where?.clause, where?.arguments, groupBy, null,
                orderBy, limitCount, offsetCount
        )
    }

    private fun combine(column: Column<T, *>, vararg others: Column<T, *>): List<Column<T, *>> {
        val result = LinkedList<Column<T, *>>()
        result.add(column)
        result.addAll(others)
        return result
    }


    private inner class PredicateAdderOrEnderImpl(
            private val delegate: WhereBuilderImpl<T, PredicateAdderOrEnder<T>>) :
            PredicateAdderOrEnder<T> {

        override fun and(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            delegate.and()
            return delegate
        }

        override fun or(): AfterSimpleConnectorAdder<T, PredicateAdderOrEnder<T>> {
            delegate.or()
            return delegate
        }

        override fun distinct(): AdderOrEnderAfterWhere<T> {
            this@QueryBuilderImpl.distinct()
            return this
        }

        override fun select(column: Column<T, *>, vararg others: Column<T, *>): AdderOrEnderAfterWhere<T> {
            this@QueryBuilderImpl.select(column, *others)
            return this
        }

        override fun groupBy(column: Column<T, *>, vararg others: Column<T, *>): AdderOrEnderAfterWhere<T> {
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

        override fun query(): Source {
            return this@QueryBuilderImpl.query()
        }
    }


    private data class OrderInfo(val column: Column<*, *>, val ascending: Boolean)
}