package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.test.insert.TestInsert
import com.jayrave.falkon.sqlBuilders.test.insert.TestInsertOrReplace
import org.junit.Test

class H2InsertSqlBuilderIntegrationTests : BaseClassForTesting() {

    @Test
    fun `can insert both null & non null values`() {
        TestInsert(H2InsertSqlBuilder(), db).`can insert both null & non null values`()
    }

    @Test
    fun `new records are inserted via insert or replace with both null & non null values`() {
        TestInsertOrReplace(H2InsertSqlBuilder(), db).`new records are inserted via insert or replace with both null & non null values`()
    }

    @Test
    fun `existing records are updated via insert or replace with both null & non null values`() {
        TestInsertOrReplace(H2InsertSqlBuilder(), db).`existing records are updated via insert or replace with both null & non null values`()
    }
}