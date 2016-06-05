package com.jayrave.falkon.engine

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class DeleteKtTest {

    private val tableName = "test"

    @Test
    fun testDeleteWithoutWhere() {
        val whereClause = null
        val whereArgs = null
        val compiledStatement = delete(
                tableName, whereClause, whereArgs,
                statementCompiler, argsBinder
        )

        assertCompiledStatement(compiledStatement, tableName, whereClause, whereArgs)
    }


    @Test
    fun testUpdateWithWhere() {
        val whereClause = "anything = ? AND nothing = ?"
        val whereArgs = listOf("test", 7)
        val compiledStatement = delete(
                tableName, whereClause, whereArgs,
                statementCompiler, argsBinder
        )

        assertCompiledStatement(compiledStatement, tableName, whereClause, whereArgs)
    }



    companion object {
        private fun assertCompiledStatement(
                cs: CompiledStatementForTest, tableName: String, whereClause: String?,
                whereArgs: Iterable<Any?>?) {

            var partialSql = cs.sql
            val mutableBoundArgs = LinkedList(cs.boundArgs)

            // Check statement start
            partialSql = partialSql.removePrefixOrThrow("DELETE FROM $tableName")

            // Setup for asserting WHERE if required
            if (!whereClause.isNullOrBlank()) {
                partialSql = partialSql.removePrefixOrThrow(" ") // A space before WHERE
            }

            // Assert where clause & args
            partialSql = assertAndRemoveWhereRelatedInfo(
                    whereClause, whereArgs, partialSql, mutableBoundArgs
            )

            // Final assertions
            assertThat(partialSql).isEmpty()
            assertThat(mutableBoundArgs).isEmpty()
        }
    }
}