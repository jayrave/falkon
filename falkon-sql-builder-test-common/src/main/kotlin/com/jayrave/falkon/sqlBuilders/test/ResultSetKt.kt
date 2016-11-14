package com.jayrave.falkon.sqlBuilders.test

import java.sql.ResultSet
import java.util.*

fun ResultSet.extractRecordsAsMap(columnNames: List<String>): List<Map<String, String?>> {
    val allRecords = ArrayList<Map<String, String?>>()
    while (!isAfterLast) {
        allRecords.add(columnNames.associate { columnName ->
            val columnIndex = findColumn(columnName)
            getObject(columnIndex)
            val string: String? = when {
                wasNull() -> null
                else -> getString(columnIndex)
            }

            columnName to string
        })

        next()
    }

    return allRecords
}