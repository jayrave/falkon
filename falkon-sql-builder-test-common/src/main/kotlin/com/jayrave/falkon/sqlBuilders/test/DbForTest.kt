package com.jayrave.falkon.sqlBuilders.test

/**
 * Execute without using falkon stuff as this is used to test falkon stuff
 */
interface DbForTest {

    val intDataType: String
    val stringDataType: String

    fun execute(sql: String)
    fun execute(sql: List<String>)
    fun findRecordCountInTable(tableName: String): Int
    fun findAllRecordsInTable(tableName: String): List<Map<String, String>>
}
