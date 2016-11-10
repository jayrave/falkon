package com.jayrave.falkon.sqlBuilders.test

import java.sql.ResultSet
import java.util.*
import javax.sql.DataSource

class DbForTestViaJdbc(private val dataSource: DataSource) : DbForTest {

    override val intDataType: String = "INTEGER"
    override val stringDataType: String = "VARCHAR"

    override fun execute(sql: String) {
        val connection = dataSource.connection
        val preparedStatement = connection.prepareStatement(sql)
        preparedStatement.execute()
        preparedStatement.close()
        connection.close()
    }


    override fun execute(sql: List<String>) {
        sql.forEach { execute(it) }
    }


    override fun findRecordCountInTable(tableName: String): Int {
        val countColumnName = "count"
        return executeQuery("SELECT COUNT(*) AS $countColumnName FROM $tableName") {
            it.getInt(it.findColumn(countColumnName))
        }
    }


    override fun findAllRecordsInTable(tableName: String): List<Map<String, String>> {
        return executeQuery("SELECT * FROM $tableName") { resultSet ->
            val allRecords = ArrayList<Map<String, String>>()
            val metaData = resultSet.metaData
            while (!resultSet.isAfterLast) {
                val record = HashMap<String, String>()
                (1..metaData.columnCount).forEach { columnIndex ->
                    val columnName = metaData.getColumnName(columnIndex)
                    record[columnName] = resultSet.getString(columnName)
                }

                allRecords.add(record)
                resultSet.next()
            }

            allRecords
        }
    }


    private fun <R> executeQuery(sql: String, op: (ResultSet) -> R): R {
        val connection = dataSource.connection
        val preparedStatement = connection.prepareStatement(sql)
        val resultSet = preparedStatement.executeQuery()

        resultSet.first()
        val result = op.invoke(resultSet)
        resultSet.close()
        preparedStatement.close()
        connection.close()

        return result
    }
}