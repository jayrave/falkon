package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.test.insert.TestInsert
import com.jayrave.falkon.sqlBuilders.test.insert.TestInsertOrReplace
import org.junit.Test

class SqliteInsertSqlBuilderIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun `can insert both null & non null values`() {
        TestInsert(SqliteInsertSqlBuilder(), db).`can insert both null & non null values`()
    }

    @Test
    fun `new records are inserted via insert or replace with both null & non null values`() {
        TestInsertOrReplace(SqliteInsertSqlBuilder(), db).`new records are inserted via insert or replace with both null & non null values`()
    }

    @Test
    fun `existing records are updated via insert or replace with both null & non null values`() {
        TestInsertOrReplace(SqliteInsertSqlBuilder(), db).`existing records are updated via insert or replace with both null & non null values`()
    }
}