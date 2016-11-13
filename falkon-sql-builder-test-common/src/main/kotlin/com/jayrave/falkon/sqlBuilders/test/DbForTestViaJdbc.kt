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


    override fun findAllRecordsInTable(
            tableName: String, columnNames: List<String>):
            List<Map<String, String?>> {

        val columnsSelector = when {
            columnNames.isEmpty() -> "*"
            else -> columnNames.joinToString()
        }

        return executeQuery("SELECT $columnsSelector FROM $tableName") { resultSet ->
            val allRecords = ArrayList<Map<String, String?>>()
            while (!resultSet.isAfterLast) {
                allRecords.add(columnNames.associate { columnName ->
                    val columnIndex = resultSet.findColumn(columnName)
                    resultSet.getObject(columnIndex)
                    val string: String? = when {
                        resultSet.wasNull() -> null
                        else -> resultSet.getString(columnIndex)
                    }

                    columnName to string
                })

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