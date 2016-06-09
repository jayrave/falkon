package com.jayrave.falkon.dao.where

/**
 * [clause] contains the WHERE section of an SQL statement (without the WHERE keyword). Could
 * contain `?` as placeholders for arguments to be bound
 * [arguments] contains arguments for which `?` placeholder was used
 */
internal data class Where(
        val clause: String,
        val arguments: Iterable<Any?>
)