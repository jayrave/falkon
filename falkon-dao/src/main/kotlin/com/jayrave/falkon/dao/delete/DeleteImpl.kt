package com.jayrave.falkon.dao.delete

internal data class DeleteImpl(
        override val sql: String,
        override val arguments: Iterable<Any>?
) : Delete