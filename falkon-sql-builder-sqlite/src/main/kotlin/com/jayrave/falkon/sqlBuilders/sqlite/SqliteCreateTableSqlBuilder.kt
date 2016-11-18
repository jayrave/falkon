package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.CreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.common.SimpleCreateTableSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.ColumnInfo
import com.jayrave.falkon.sqlBuilders.lib.TableInfo
import java.sql.SQLSyntaxErrorException

class SqliteCreateTableSqlBuilder : CreateTableSqlBuilder {

    /**
     * In Sqlite, `AUTOINCREMENT` is a part of the `PRIMARY KEY` expression that belongs in
     * column-constraint & also it isn't allowed to have multiple `PRIMARY KEY` expressions.
     *
     * This means that Sqlite doesn't allow the following
     *  - A non primary, auto incremented column
     *  - Multiple auto incremented columns in a table
     *  - A composite primary key with one of the attributes auto incremented
     *
     * This also means that [SimpleCreateTableSqlBuilder] can't be used here straight up
     */


    /**
     * Builds a `CREATE TABLE ...` statement with the passed in info
     */
    override fun build(tableInfo: TableInfo): List<String> {
        val includePrimaryKeyInColumnConstraint = verifyAutoIncrementPrimaryKeyCombo(tableInfo)

        // Add basic create table stuff
        val createTableSql = StringBuilder(120)
        createTableSql.append("CREATE TABLE ${tableInfo.name} (")

        // Add remaining parts
        createTableSql.addColumnDefinitionsOrThrow(
                tableInfo, "AUTOINCREMENT", includePrimaryKeyInColumnConstraint
        )

        if (!includePrimaryKeyInColumnConstraint) {
            createTableSql.addPrimaryKeyConstraint(tableInfo)
        }

        createTableSql.addUniquenessConstraints(tableInfo)
        createTableSql.addForeignKeyConstraints(tableInfo)
        createTableSql.append(")")

        // Build & return SQL statement
        return listOf(createTableSql.toString())
    }



    companion object {

        private fun StringBuilder.addColumnDefinitionsOrThrow(
                tableInfo: TableInfo, phraseForAutoIncrement: String,
                includePrimaryKeyExpression: Boolean) {

            var columnCount = 0
            val columnInfos = tableInfo.columnInfos
            append(columnInfos.joinToString(separator = ", ") {
                columnCount++

                // Add name & data type
                val columnDefinitionBuilder = StringBuilder()
                        .append(it.name)
                        .append(" ")
                        .append(it.dataType)

                // Throw if max size is specified
                if (it.maxSize != null) {
                    throw SQLSyntaxErrorException(
                            "Sqlite ignores the max size parameter as documented at " +
                                    "https://www.sqlite.org/datatype3.html"
                    )
                }

                // Add nullability if required
                if (it.isNonNull) {
                    columnDefinitionBuilder.append(" NOT NULL")
                }

                if (it.isId && includePrimaryKeyExpression) {
                    columnDefinitionBuilder.append(" PRIMARY KEY")
                }

                // Add expression for auto incrementing if required
                if (it.autoIncrement) {
                    columnDefinitionBuilder
                            .append(" ")
                            .append(phraseForAutoIncrement)
                }

                columnDefinitionBuilder.toString()
            })

            if (columnCount == 0) {
                throw SQLSyntaxErrorException(
                        "CREATE TABLE SQL without any columns for table: ${tableInfo.name}"
                )
            }
        }


        private fun StringBuilder.addPrimaryKeyConstraint(tableInfo: TableInfo) {
            val clause = tableInfo
                    .columnInfos
                    .filter { it.isId }
                    .joinToStringIfHasItems(
                            prefix = ", PRIMARY KEY (", postfix = ")", transform = ColumnInfo::name
                    )

            if (clause != null) {
                append(clause)
            }
        }


        private fun StringBuilder.addUniquenessConstraints(tableInfo: TableInfo) {
            val clause = tableInfo
                    .uniquenessConstraints
                    .joinToStringIfHasItems(prefix = ", ") { columnNames ->
                        columnNames.joinToStringIfHasItems(prefix = "UNIQUE (", postfix = ")") {
                            it
                        } ?: throw SQLSyntaxErrorException("Empty iterable!!")
                    }

            if (clause != null) {
                append(clause)
            }
        }


        private fun StringBuilder.addForeignKeyConstraints(tableInfo: TableInfo) {
            val clause = tableInfo
                    .foreignKeyConstraints
                    .joinToStringIfHasItems(prefix = ", ") { constraint ->
                        "FOREIGN KEY (${constraint.columnName}) REFERENCES " +
                                "${constraint.foreignTableName}(${constraint.foreignColumnName})"
                    }

            if (clause != null) {
                append(clause)
            }
        }


        private fun <T> Iterable<T>?.joinToStringIfHasItems(
                separator: CharSequence = ", ", prefix: CharSequence = "",
                postfix: CharSequence = "", transform: ((T) -> CharSequence)): String? {

            return when (this) {
                null -> null
                else -> {
                    var count = 0
                    val string = joinToString(separator, prefix, postfix) {
                        count++
                        transform.invoke(it)
                    }

                    when (count) {
                        0 -> null
                        else -> string
                    }
                }
            }
        }


        /**
         * @return `true` if table has auto incremented primary key column; `false` otherwise
         * @throws SQLSyntaxErrorException if required combo conditions aren't satisfied
         */
        private fun verifyAutoIncrementPrimaryKeyCombo(tableInfo: TableInfo): Boolean {

            var hasAutoIncrementedPrimaryKeyColumn = false
            var attributesCountInPrimaryKey = 0

            tableInfo.columnInfos.forEach {

                // Auto incrementing column should be also a primary key
                if (it.autoIncrement && !it.isId) {
                    throw buildExceptionForVerifyAutoIncrementPrimaryKeyComboFailure()
                }

                // Only one column can be marked to be auto incrementing in a table &
                // auto increment and composite primary key can't live together (as only
                // one primary key expression is allowed per table)
                if ((hasAutoIncrementedPrimaryKeyColumn && it.isId) ||
                        (attributesCountInPrimaryKey > 0 && it.autoIncrement)) {

                    throw buildExceptionForVerifyAutoIncrementPrimaryKeyComboFailure()
                }

                if (it.isId) {
                    attributesCountInPrimaryKey++

                    if (it.autoIncrement) {
                        hasAutoIncrementedPrimaryKeyColumn = true
                    }
                }
            }

            return hasAutoIncrementedPrimaryKeyColumn
        }


        private fun buildExceptionForVerifyAutoIncrementPrimaryKeyComboFailure(): Exception {
            return SQLSyntaxErrorException(
                    "Sqlite doesn't allow the following\n" +
                            " - A non primary, auto incremented column\n" +
                            " - Multiple auto incremented columns in a table\n" +
                            " - A composite primary key with auto incrementing column\n" +
                            "Take a look @ http://sqlite.org/autoinc.html & " +
                            "https://www.sqlite.org/lang_createtable.html for further explanation"
            )
        }
    }
}