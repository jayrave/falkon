package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.mapper.Column
import com.jayrave.falkon.mapper.Table

/**
 * Utility class to simplify implementation of [Table.extractFrom] for tables with
 * simple ids i.e., for tables where just one column serves as the primary key
 */
class SimpleIdExtractFromHelper<in ID : Any>(private val column: Column<*, ID>) {

    init {
        column.throwIfNotValidCandidateForExtractFrom()
    }

    fun <C> extractFrom(id: ID, column: Column<*, C>): C {
        column.throwIfNotValidCandidateForExtractFrom()

        @Suppress("UNCHECKED_CAST")
        return when (column.name == this.column.name) {
            true -> id as C
            else -> throwSinceExtractFromHelperDoesNotKnowAboutColumn(
                    SimpleIdExtractFromHelper::class.qualifiedName!!, column
            )
        }
    }
}