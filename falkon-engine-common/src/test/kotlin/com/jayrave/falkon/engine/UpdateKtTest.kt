package com.jayrave.falkon.engine

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class UpdateKtTest {

    private val tableName = "test"

    @Test
    fun testUpdateReturnsNullForEmptyMap() {
        val compiledStatement = update(
                "test", emptyMap(), null, null, statementCompiler, argsBinder
        )

        assertThat(compiledStatement).isNull()
    }


    @Test
    fun testUpdateWithoutWhere() {
        val columnNameToValuesMap = buildColumnNameToValuesMap()
        val whereClause = null
        val whereArgs = null
        val compiledStatement = update(
                tableName, columnNameToValuesMap, whereClause, whereArgs,
                statementCompiler, argsBinder
        )

        assertCompiledStatement(
                compiledStatement!!, tableName, columnNameToValuesMap,
                whereClause, whereArgs
        )
    }


    @Test
    fun testUpdateWithWhere() {
        val columnNameToValuesMap = buildColumnNameToValuesMap()
        val whereClause = "anything = ? AND nothing = ?"
        val whereArgs = listOf("test", 7)
        val compiledStatement = update(
                tableName, columnNameToValuesMap, whereClause, whereArgs,
                statementCompiler, argsBinder
        )

        assertCompiledStatement(
                compiledStatement!!, tableName, columnNameToValuesMap,
                whereClause, whereArgs
        )
    }


    companion object {

        private val regex = Regex("(\\S+) = \\?,?\\s?")


        private fun buildColumnNameToValuesMap(): Map<String, Any?> {
            return mapOf("int" to 5, "string" to "test 6", "blob" to byteArrayOf(7))
        }


        private fun assertCompiledStatement(
                cs: CompiledStatementForTest, tableName: String,
                columnNamesToValuesMap: Map<String, Any?>, whereClause: String?,
                whereArgs: Iterable<Any?>?) {

            var mutableSql = cs.sql
            val mutableBoundArgs = LinkedList(cs.boundArgs)
            val mutableColumnNamesToValuesMap = HashMap(columnNamesToValuesMap)

            // Check statement start & setup for asserting columns
            mutableSql = mutableSql.removePrefixOrThrow("UPDATE $tableName SET ")
            assertThat(mutableSql.indexOf('?')).isGreaterThan(0) // At least one column must be set

            // Assert columns
            for (loopIndex in 0..columnNamesToValuesMap.size - 1) {
                val matchResult = regex.find(mutableSql)

                // There should be a match & should start at index 0
                assertThat(matchResult).isNotNull()
                assertThat(matchResult!!.range.start).isEqualTo(0)

                // Make sure that the matched column's value was bound at the appropriate index
                // and remove it from the mutable map as we don't want it to be matched again
                val columnName = matchResult.groups[1]!!.value
                assertThat(mutableColumnNamesToValuesMap).containsKey(columnName)
                assertThat(mutableBoundArgs.removeFirst()).isEqualTo(
                        mutableColumnNamesToValuesMap.remove(columnName)
                )

                // Setup for asserting next column
                mutableSql = mutableSql.removePrefixOrThrow(matchResult.groups[0]!!.value)
            }

            // Assert where clause & args
            mutableSql = assertAndRemoveWhereRelatedInfo(
                    whereClause, whereArgs, mutableSql, mutableBoundArgs
            )

            // Final assertions
            assertThat(mutableSql).isEmpty()
            assertThat(mutableBoundArgs).isEmpty()
        }
    }
}