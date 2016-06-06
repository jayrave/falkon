package com.jayrave.falkon.engine

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.util.*

class InsertKtTest {

    private val tableName = "test"

    @Test
    fun testCompileInsertStatementReturnsNullForEmptyMap() {
        val compiledStatement = compileInsertStatement(
                "test", emptyMap(), statementCompiler, argsBinder
        )

        assertThat(compiledStatement).isNull()
    }


    @Test
    fun testCompileInsertStatement() {
        val columnNameToValuesMap = buildColumnNameToValuesMap()
        val compiledStatement = compileInsertStatement(
                tableName, columnNameToValuesMap, statementCompiler, argsBinder
        )

        assertCompiledStatement(compiledStatement!!, tableName, columnNameToValuesMap)
    }


    companion object {

        private fun buildColumnNameToValuesMap(): Map<String, Any?> {
            return mapOf("int" to 5, "string" to "test 6", "blob" to byteArrayOf(7))
        }


        private fun assertCompiledStatement(
                cs: CompiledStatementForTest, tableName: String,
                columnNamesToValuesMap: Map<String, Any?>) {

            var partialSql = cs.sql
            val mutableBoundArgs = LinkedList(cs.boundArgs)
            val mutableColumnNamesToValuesMap = HashMap(columnNamesToValuesMap)

            // Check statement start
            partialSql = partialSql.removePrefixOrThrow("INSERT INTO $tableName ")

            // Check column name section
            val openParenthesisIndex = partialSql.indexOf('(')
            val closeParenthesisIndex = partialSql.indexOf(')')
            assertThat(openParenthesisIndex).isEqualTo(0) // '(' should be the first char now
            assertThat(openParenthesisIndex).isLessThan(closeParenthesisIndex)
            val columnNameListWithParenthesis = partialSql.substring(
                    openParenthesisIndex, closeParenthesisIndex + 1
            )

            // Extract column names
            partialSql = partialSql.removePrefixOrThrow(columnNameListWithParenthesis)
            val columnNames = columnNameListWithParenthesis
                    .substring(1, columnNameListWithParenthesis.length - 1)
                    .split(delimiters = ", ")

            // Check values section
            partialSql = partialSql.removePrefixOrThrow(columnNames.joinToString(
                    separator = ", ", prefix = " VALUES (", postfix = ")", transform = { "?" }
            ))

            // Make sure all values were bound in the correct order
            columnNames.forEach {
                assertThat(mutableColumnNamesToValuesMap).containsKey(it)
                assertThat(mutableBoundArgs.removeFirst()).isEqualTo(
                        mutableColumnNamesToValuesMap.remove(it)
                )
            }

            // Final assertions
            assertThat(partialSql).isEmpty()
            assertThat(mutableBoundArgs).isEmpty()
        }
    }
}