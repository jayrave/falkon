package com.jayrave.falkon.sqlBuilders.test

import java.sql.ResultSet
import java.util.*

fun ResultSet.extractRecordsAsMap(columnNamesInQuery: List<String>): List<Map<String, String?>> {
    val allRecords = ArrayList<Map<String, String?>>()
    while (!isAfterLast) {
        allRecords.add(columnNamesInQuery.associate { columnName ->
            val columnIndex = findColumn(columnName)
            val string: String? = when {
                getObject(columnIndex) == null -> null
                else -> getString(columnIndex)
            }

            columnName to string
        })

        next()
    }

    return allRecords
}