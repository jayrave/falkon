package com.jayrave.falkon.mapper.lib

import com.jayrave.falkon.mapper.Column

internal fun Column<*, *>.throwIfNotValidCandidateForExtractFrom() {
    if (!isId) {
        throw IllegalArgumentException("$name is not an ID column")
    }
}


internal fun <R> throwSinceExtractFromHelperDoesNotKnowAboutColumn(
        helperName: String, column: Column<*, *>): R {

    throw IllegalArgumentException("$helperName wasn't informed about column: ${column.name}")
}