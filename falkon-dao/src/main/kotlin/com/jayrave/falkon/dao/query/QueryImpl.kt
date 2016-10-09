package com.jayrave.falkon.dao.query

internal data class QueryImpl(
        override val tableNames: Iterable<String>,
        override val sql: String,
        override val arguments: Iterable<Any>
) : Query