package com.jayrave.falkon.sqlBuilders.test

import java.sql.PreparedStatement
import java.sql.ResultSet
import javax.sql.DataSource

fun DataSource.execute(sql: List<String>) {
    sql.forEach { execute(it) }
}


fun DataSource.execute(sql: String) {
    val connection = connection
    val preparedStatement = connection.prepareStatement(sql)
    try {
        preparedStatement.execute()
    } finally {
        preparedStatement.close()
        connection.close()
    }
}


fun DataSource.execute(sql: String, argsBinder: (PreparedStatement) -> Any?) {
    privateExecute(sql, argsBinder) { it.execute() }
}


fun DataSource.executeUpdate(sql: String, argsBinder: (PreparedStatement) -> Any?): Int {
    return privateExecute(sql, argsBinder, PreparedStatement::executeUpdate)
}


fun DataSource.executeDelete(sql: String, argsBinder: (PreparedStatement) -> Any?): Int {
    return executeUpdate(sql, argsBinder)
}


fun <R> DataSource.executeQuery(
        sql: String, argsBinder: (PreparedStatement) -> Any?, op: (ResultSet) -> R): R {

    return privateExecute(sql, {}) { ps ->
        argsBinder.invoke(ps)
        val resultSet = ps.executeQuery()
        resultSet.next() // To point to the first row in the result set
        val result = op.invoke(resultSet)
        resultSet.close()
        result
    }
}


fun DataSource.findRecordCountInTable(tableName: String): Int {
    val countColumnName = "count"
    return executeQuery("SELECT COUNT(*) AS $countColumnName FROM $tableName", {}) {
        it.getInt(it.findColumn(countColumnName))
    }
}


fun DataSource.findAllRecordsInTable(
        tableName: String, columnNames: List<String>):
        List<Map<String, String?>> {

    val columnsSelector = when {
        columnNames.isEmpty() -> "*"
        else -> columnNames.joinToString()
    }

    return executeQuery("SELECT $columnsSelector FROM $tableName", {}) { resultSet ->
        resultSet.extractRecordsAsMap(columnNames)
    }
}


private fun <R> DataSource.privateExecute(
        sql: String, argsBinder: (PreparedStatement) -> Any?,
        executor: (PreparedStatement) -> R): R {

    val connection = connection
    val preparedStatement = connection.prepareStatement(sql)
    return try {
        argsBinder.invoke(preparedStatement)
        executor.invoke(preparedStatement)
    } finally {
        preparedStatement.close()
        connection.close()
    }
}