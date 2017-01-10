package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.test.insertOrReplace.TestInsertOrReplace
import org.junit.Test

class H2InsertOrReplaceSqlBuilderIntegrationTests : BaseClassForTesting() {

    @Test
    fun `new records are inserted via insert or replace with both null & non null values`() {
        TestInsertOrReplace(H2InsertOrReplaceSqlBuilder(), db).`new records are inserted via insert or replace with both null & non null values`()
    }

    @Test
    fun `existing records are updated via insert or replace with both null & non null values`() {
        TestInsertOrReplace(H2InsertOrReplaceSqlBuilder(), db).`existing records are updated via insert or replace with both null & non null values`()
    }

    @Test
    fun `can insert or replace into table with only id columns`() {
        TestInsertOrReplace(H2InsertOrReplaceSqlBuilder(), db).`can insert or replace into table with only id columns`()
    }
}