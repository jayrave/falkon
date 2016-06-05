package com.jayrave.falkon.engine

import org.assertj.core.api.Assertions.assertThat
import java.util.*

/**
 * Expects the following
 *      - [partialSql] to start with WHERE clause if there is any
 *      - [mutableBoundArgs] to start with args for WHERE clause if any
 *
 * @param whereClause WHERE clause to check against
 * @param whereArgs args to check against
 * @param partialSql SQL statement that has been checked till WHERE clause
 * @param mutableBoundArgs mutable args that are yet to be checked
 *
 * @return [partialSql] that doesn't have its WHERE clause anymore
 */
fun assertAndRemoveWhereRelatedInfo(
        whereClause: String?, whereArgs: Iterable<Any?>?, partialSql: String,
        mutableBoundArgs: LinkedList<Any?>): String {

    var mutableSql = partialSql

    // Assert where clause if required
    if (!whereClause.isNullOrBlank()) {
        mutableSql = mutableSql.removePrefixOrThrow("WHERE $whereClause")
    }

    // Assert where args if required
    whereArgs?.forEach { assertThat(mutableBoundArgs.removeFirst()).isEqualTo(it) }

    // Return mutated SQL
    return mutableSql
}