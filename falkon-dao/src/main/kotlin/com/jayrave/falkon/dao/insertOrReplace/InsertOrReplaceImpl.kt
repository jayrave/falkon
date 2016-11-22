package com.jayrave.falkon.dao.insertOrReplace

internal data class InsertOrReplaceImpl(
        override val tableName: String,
        override val sql: String,
        override val arguments: Iterable<Any>
) : InsertOrReplace