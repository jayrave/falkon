package com.jayrave.falkon.dao.insert

internal data class InsertImpl(
        override val sql: String,
        override val arguments: Iterable<Any>?
) : Insert