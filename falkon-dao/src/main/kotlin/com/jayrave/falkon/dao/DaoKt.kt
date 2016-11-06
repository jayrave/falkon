package com.jayrave.falkon.dao

import com.jayrave.falkon.dao.query.QueryBuilder
import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.dao.query.lenient.QueryBuilder as LenientQueryBuilder
import com.jayrave.falkon.dao.query.lenient.QueryBuilderImpl as LenientQueryBuilderImpl

/**
 * Use this when [QueryBuilder] is too restrictive
 */
fun lenientQueryBuilder(querySqlBuilder: QuerySqlBuilder): LenientQueryBuilder {
    return LenientQueryBuilderImpl(querySqlBuilder)
}