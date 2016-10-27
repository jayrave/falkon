package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.TableInfo
import java.sql.SQLSyntaxErrorException

class SimpleCreateTableSqlBuilder(private val dialect: Dialect) : CreateTableSqlBuilder {

    override fun build(tableInfo: TableInfo): String {
        // Add basic create table stuff
        val createTableSql = StringBuilder(120)
        createTableSql.append("CREATE TABLE ${tableInfo.name} (")

        // Add remaining parts
        createTableSql.addColumnDefinitionsOrThrow(tableInfo)
        createTableSql.addPrimaryKeyConstraint(tableInfo)
        createTableSql.addUniquenessConstraints(tableInfo)
        createTableSql.addForeignKeyConstraints(tableInfo)
        createTableSql.append(")")

        // Build & return SQL statement
        return createTableSql.toString()
    }


    private fun StringBuilder.addColumnDefinitionsOrThrow(tableInfo: TableInfo) {
        var columnCount = 0
        val columnInfos = tableInfo.columnInfos
        append(columnInfos.joinToString(separator = ", ") {
            columnCount++

            // Add name & data type
            val columnDefinitionBuilder = StringBuilder()
                    .append(it.name)
                    .append(" ")
                    .append(it.dataType)

            // Add size if required
            if (it.maxSize != null) {
                columnDefinitionBuilder
                        .append("(")
                        .append(it.maxSize.toString())
                        .append(")")
            }

            // Add nullability if required
            if (it.isNonNull) {
                columnDefinitionBuilder.append(" NOT NULL")
            }

            // Add expression for auto incrementing if required
            if (it.autoIncrement) {
                columnDefinitionBuilder
                        .append(" ")
                        .append(dialect.autoIncrementExpression)
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
        append(", PRIMARY KEY (${tableInfo.primaryKeyConstraint})")
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
}