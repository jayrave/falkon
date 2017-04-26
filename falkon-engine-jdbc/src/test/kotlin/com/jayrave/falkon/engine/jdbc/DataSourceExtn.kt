package com.jayrave.falkon.engine.jdbc

import java.sql.PreparedStatement
import javax.sql.DataSource

fun <R> DataSource.forStatement(sql: String, op: (PreparedStatement) -> R): R {
    val connection = connection
    val preparedStatement = connection.prepareStatement(sql)
    return try {
        op.invoke(preparedStatement)
    } finally {
        preparedStatement.close()
        connection.close()
    }
}