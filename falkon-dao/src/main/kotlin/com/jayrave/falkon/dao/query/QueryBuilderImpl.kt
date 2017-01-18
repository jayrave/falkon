package com.jayrave.falkon.dao.query

import com.jayrave.falkon.dao.where.AfterSimpleConnectorAdder
import com.jayrave.falkon.dao.where.WhereBuilder
import com.jayrave.falkon.dao.where.WhereBuilderImpl
import com.jayrave.falkon.engine.CompiledStatement
import com.jayrave.falkon.engine.Source
import com.jayrave.falkon.mapper.ReadOnlyColumnOfTable
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.dao.query.lenient.QueryBuilderImpl as LenientQueryBuilderImpl
import com.jayrave.falkon.dao.where.AdderOrEnder as WhereAdderOrEnder

internal class QueryBuilderImpl<T : Any>(
        override val table: Table<T, *>, querySqlBuilder: QuerySqlBuilder) :
        QueryBuilder<T> {

    private val lenientQueryBuilderImpl: LenientQueryBuilderImpl
    init {
        lenientQueryBuilderImpl = LenientQueryBuilderImpl(querySqlBuilder)
        lenientQueryBuilderImpl.fromTable(table)
    }

    override fun distinct(): QueryBuilder<T> {
        lenientQueryBuilderImpl.distinct()
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
    override fun select(column: String, alias: String?): QueryBuilder<T> {
        lenientQueryBuilderImpl.select(column, alias)
        return this
    }


    override fun select(column: ReadOnlyColumnOfTable<T, *>, alias: String?): QueryBuilder<T> {
        lenientQueryBuilderImpl.select(column, alias)
        return this
    }


    override fun join(
            column: ReadOnlyColumnOfTable<T, *>, onColumn: ReadOnlyColumnOfTable<*, *>,
            joinType: JoinType): QueryBuilder<T> {

        lenientQueryBuilderImpl.join(column, onColumn, joinType)
        return this
    }


    override fun where(): WhereBuilder<T, PredicateAdderOrEnder<T>> {
        val lenientWhereBuilderImpl = lenientQueryBuilderImpl.where()
        val whereBuilder = WhereBuilderImpl<T, PredicateAdderOrEnder<T>>(
                { PredicateAdderOrEnderImpl(it) }, lenientWhereBuilderImpl
        )

        return whereBuilder
    }


    /**
     * Calling this method again for a column that has been already included will include it
     * again leading to a GROUP BY clause that has this column name twice. For example,
     * if the column name "example_column" is passed twice to this method in the same
     * invocation or even in two separate invocations, it would result in a SELECT that look like
     *
     *      `SELECT ... GROUP BY example_column, ..., example_column, ...`
     */
    override fun groupBy(
            column: ReadOnlyColumnOfTable<T, *>, vararg others: ReadOnlyColumnOfTable<T, *>):
            QueryBuilder<T> {

        lenientQueryBuilderImpl.groupBy(column, *others)
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
    override fun orderBy(column: ReadOnlyColumnOfTable<T, *>, ascending: Boolean): QueryBuilder<T> {
        lenientQueryBuilderImpl.orderBy(column, ascending)
        return this
    }


    override fun limit(count: Long): QueryBuilder<T> {
        lenientQueryBuilderImpl.limit(count)
        return this
    }


    override fun offset(count: Long): QueryBuilder<T> {
        lenientQueryBuilderImpl.offset(count)
        return this
    }


    override fun build(): Query = lenientQueryBuilderImpl.build()
    override fun compile(): CompiledStatement<Source> = lenientQueryBuilderImpl.compile()


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

        override fun select(column: String, alias: String?): AdderOrEnderAfterWhere<T> {
            this@QueryBuilderImpl.select(column, alias)
            return this
        }

        override fun select(
                column: ReadOnlyColumnOfTable<T, *>, alias: String?):
                AdderOrEnderAfterWhere<T> {

            this@QueryBuilderImpl.select(column, alias)
            return this
        }

        override fun join(
                column: ReadOnlyColumnOfTable<T, *>, onColumn: ReadOnlyColumnOfTable<*, *>,
                joinType: JoinType): AdderOrEnderAfterWhere<T> {

            this@QueryBuilderImpl.join(column, onColumn, joinType)
            return this
        }

        override fun groupBy(
                column: ReadOnlyColumnOfTable<T, *>, vararg others: ReadOnlyColumnOfTable<T, *>):
                AdderOrEnderAfterWhere<T> {

            this@QueryBuilderImpl.groupBy(column, *others)
            return this
        }

        override fun orderBy(
                column: ReadOnlyColumnOfTable<T, *>, ascending: Boolean):
                AdderOrEnderAfterWhere<T> {

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

        override fun build(): Query = this@QueryBuilderImpl.build()
        override fun compile(): CompiledStatement<Source> = this@QueryBuilderImpl.compile()
    }
}