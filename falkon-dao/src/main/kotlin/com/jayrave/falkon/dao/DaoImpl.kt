package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.delete.DeleteBuilder
import com.jayrave.falkon.dao.delete.DeleteBuilderImpl
import com.jayrave.falkon.dao.insert.InsertBuilder
import com.jayrave.falkon.dao.insert.InsertBuilderImpl
import com.jayrave.falkon.dao.query.QueryBuilder
import com.jayrave.falkon.dao.query.QueryBuilderImpl
import com.jayrave.falkon.dao.update.UpdateBuilder
import com.jayrave.falkon.dao.update.UpdateBuilderImpl
import com.jayrave.falkon.mapper.Table
import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.InsertSqlBuilder
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.dao.query.lenient.QueryBuilder as LenientQueryBuilder
import com.jayrave.falkon.dao.query.lenient.QueryBuilderImpl as LenientQueryBuilderImpl

/**
 * @param [argPlaceholder] to use as placeholders to prevent SQL injection
 * @param [insertSqlBuilder] to build SQL `INSERT INTO...` statements from components
 * @param [updateSqlBuilder] to build SQL `UPDATE...` statements from components
 * @param [deleteSqlBuilder] to build SQL `DELETE FROM...` statements from components
 * @param [querySqlBuilder] to build SQL `SELECT FROM...` statements from components
 */
open class DaoImpl<T : Any, ID : Any>(
        override val table: Table<T, ID>,
        private var argPlaceholder: String,
        private val insertSqlBuilder: InsertSqlBuilder,
        private val updateSqlBuilder: UpdateSqlBuilder,
        private val deleteSqlBuilder: DeleteSqlBuilder,
        private val querySqlBuilder: QuerySqlBuilder) : Dao<T, ID> {

    override final fun insertBuilder(): InsertBuilder<T> = InsertBuilderImpl(
            table, insertSqlBuilder, argPlaceholder
    )

    override final fun updateBuilder(): UpdateBuilder<T> = UpdateBuilderImpl(
            table, updateSqlBuilder, argPlaceholder
    )

    override final fun deleteBuilder(): DeleteBuilder<T>  = DeleteBuilderImpl(
            table, deleteSqlBuilder, argPlaceholder
    )

    override final fun queryBuilder(): QueryBuilder<T> = QueryBuilderImpl(
            table, querySqlBuilder, argPlaceholder
    )

    /**
     * Use this when [QueryBuilder] is too restrictive
     */
    fun lenientQueryBuilder(): LenientQueryBuilder = lenientQueryBuilder(
            querySqlBuilder, argPlaceholder
    )
}