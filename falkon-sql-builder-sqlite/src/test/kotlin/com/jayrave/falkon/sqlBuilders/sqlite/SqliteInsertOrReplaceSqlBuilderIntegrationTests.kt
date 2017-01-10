package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.test.insertOrReplace.TestInsertOrReplace
import org.junit.Test

class SqliteInsertOrReplaceSqlBuilderIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun `new records are inserted via insert or replace with both null & non null values`() {
        TestInsertOrReplace(SqliteInsertOrReplaceSqlBuilder(), db).`new records are inserted via insert or replace with both null & non null values`()
    }

    @Test
    fun `existing records are updated via insert or replace with both null & non null values`() {
        TestInsertOrReplace(SqliteInsertOrReplaceSqlBuilder(), db).`existing records are updated via insert or replace with both null & non null values`()
    }

    @Test
    fun `can insert or replace into table with only id columns`() {
        TestInsertOrReplace(SqliteInsertOrReplaceSqlBuilder(), db).`can insert or replace into table with only id columns`()
    }
}