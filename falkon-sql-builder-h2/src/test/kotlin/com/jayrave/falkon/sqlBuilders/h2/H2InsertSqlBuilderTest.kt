package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.test.insert.TestInsert
import org.junit.Test

class H2InsertSqlBuilderTest : BaseClassForTesting() {

    @Test
    fun `can insert both null & non null values`() {
        TestInsert(H2InsertSqlBuilder(), db).`can insert both null & non null values`()
    }
}