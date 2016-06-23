package com.jayrave.falkon.engine.jdbc

import com.jayrave.falkon.engine.executeAndClose
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class SqlWithoutBoundArgsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testCompileSqlWithoutBoundArgs() {
        // Execute using engine
        engine.compileSql("CREATE TABLE test (column_name_1 INTEGER)").executeAndClose()

        // Insert using data source
        val numberOfRowsInserted = dataSource
                .connection
                .prepareStatement("INSERT INTO test (column_name_1) VALUES (1)")
                .executeUpdate()

        // Insert would work only if compileSql had worked
        assertThat(numberOfRowsInserted).isEqualTo(1)
    }


    @Test
    fun testCompileInsertWithoutBoundArgs() {
        // Create table using data source
        executeStatementUsingDataSource("CREATE TABLE test (column_name_1 INTEGER)")

        // Insert stuff using engine
        engine.compileSql("INSERT INTO test (column_name_1) VALUES (1)").executeAndClose()
        engine.compileSql("INSERT INTO test (column_name_1) VALUES (2)").executeAndClose()

        // Query via data source & perform assertions
        assertCountUsingDataSource("test", 2)
    }


    @Test
    fun testCompileUpdateWithoutBoundArgs() {
        // Create table & insert stuff using data source
        executeStatementUsingDataSource("CREATE TABLE test (column_name_1 INTEGER)")
        executeStatementUsingDataSource("INSERT INTO test (column_name_1) VALUES (1)")

        // Update stuff using engine
        engine.compileSql("UPDATE test SET column_name_1 = 5 WHERE column_name_1 = 1").execute()

        // Query via data source & perform assertions
        val resultSet = dataSource.connection.prepareStatement("SELECT * FROM test").executeQuery()
        assertThat(resultSet.first()).isTrue()
        assertThat(resultSet.getInt("column_name_1")).isEqualTo(5)
        assertThat(resultSet.next()).isFalse()
    }


    @Test
    fun testCompileDeleteWithoutBoundArgs() {
        // Create table & insert stuff using data source
        executeStatementUsingDataSource("CREATE TABLE test (column_name_1 INTEGER)")
        executeStatementUsingDataSource("INSERT INTO test (column_name_1) VALUES (1)")

        // Store count before performing delete
        val countBeforeDelete = countNumberOfRowsUsingDataSource("test")

        // Delete stuff using engine
        engine.compileSql("DELETE FROM test").executeAndClose()

        // Get count after delete & perform assertions
        val countAfterDelete = countNumberOfRowsUsingDataSource("test")
        assertThat(countBeforeDelete).isGreaterThan(0)
        assertThat(countAfterDelete).isEqualTo(0)
    }


    @Test
    fun testCompileQueryWithoutBoundArgs() {
        // Create table & insert stuff using data source
        executeStatementUsingDataSource("CREATE TABLE test (column_name_1 INTEGER)")
        executeStatementUsingDataSource("INSERT INTO test (column_name_1) VALUES (1)")
        executeStatementUsingDataSource("INSERT INTO test (column_name_1) VALUES (2)")
        executeStatementUsingDataSource("INSERT INTO test (column_name_1) VALUES (3)")

        // Query using engine
        val source = engine.compileQuery("SELECT * FROM test ORDER BY column_name_1").execute()

        // Assert source's result set
        assertThat(source.moveToFirst()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(1)
        assertThat(source.moveToNext()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(2)
        assertThat(source.moveToNext()).isEqualTo(true)
        assertThat(source.getInt(source.getColumnIndex("column_name_1"))).isEqualTo(3)
        assertThat(source.moveToNext()).isEqualTo(false)
    }
}