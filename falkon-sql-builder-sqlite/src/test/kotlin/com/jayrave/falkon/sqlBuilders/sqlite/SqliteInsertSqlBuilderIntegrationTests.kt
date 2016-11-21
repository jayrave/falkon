package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.test.insert.TestInsert
import org.junit.Test

class SqliteInsertSqlBuilderIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun `can insert both null & non null values`() {
        TestInsert(SqliteInsertSqlBuilder(), db).`can insert both null & non null values`()
    }
}