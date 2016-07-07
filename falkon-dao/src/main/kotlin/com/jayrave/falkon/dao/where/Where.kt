package com.jayrave.falkon.dao.where

import com.jayrave.falkon.sqlBuilders.lib.WhereSection

/**
 * [whereSections] contains the WHERE sections of an SQL statement
 * [arguments] contains arguments for placeholders used in sections
 */
internal data class Where(
        val whereSections: Iterable<WhereSection>,
        val arguments: Iterable<Any>
)