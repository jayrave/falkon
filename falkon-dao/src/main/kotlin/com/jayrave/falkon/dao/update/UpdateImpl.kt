package com.jayrave.falkon.dao.update

internal data class UpdateImpl(
        override val tableName: String,
        override val sql: String,
        override val arguments: Iterable<Any>
) : Update