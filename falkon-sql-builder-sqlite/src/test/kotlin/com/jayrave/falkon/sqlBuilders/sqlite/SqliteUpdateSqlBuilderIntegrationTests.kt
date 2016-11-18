package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.test.update.TestUpdate
import org.junit.Test

class SqliteUpdateSqlBuilderIntegrationTests : BaseClassForIntegrationTests() {

    private val updateSqlBuilder = SqliteUpdateSqlBuilder()

    @Test
    fun `update all records`() {
        TestUpdate(updateSqlBuilder, db).`update all records`()
    }

    @Test
    fun `update individual record using where#eq`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#eq`()
    }

    @Test
    fun `update individual record using where#notEq`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#notEq`()
    }

    @Test
    fun `update individual record using where#greaterThan`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#greaterThan`()
    }

    @Test
    fun `update individual record using where#greaterThanOrEq`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#greaterThanOrEq`()
    }

    @Test
    fun `update individual record using where#lessThan`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#lessThan`()
    }

    @Test
    fun `update individual record using where#lessThanOrEq`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#lessThanOrEq`()
    }

    @Test
    fun `update individual record using where#like`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#like`()
    }

    @Test
    fun `update individual record using where#between`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#between`()
    }

    @Test
    fun `update individual record using where#isIn with list`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#isIn with list`()
    }

    @Test
    fun `update individual record using where#isIn with sub query`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#isIn with sub query`()
    }

    @Test
    fun `update individual record using where#isNotIn with list`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#isNotIn with list`()
    }

    @Test
    fun `update individual record using where#isNotIn with sub query`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#isNotIn with sub query`()
    }

    @Test
    fun `update individual record using where#isNull`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#isNull`()
    }

    @Test
    fun `update individual record using where#isNotNull`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using where#isNotNull`()
    }

    @Test
    fun `update individual record using simple where#and`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using simple where#and`()
    }

    @Test
    fun `update individual record using simple where#or`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using simple where#or`()
    }

    @Test
    fun `update individual record using compound where#and`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using compound where#and`()
    }

    @Test
    fun `update individual record using compound where#or`() {
        TestUpdate(updateSqlBuilder, db).`update individual record using compound where#or`()
    }
}