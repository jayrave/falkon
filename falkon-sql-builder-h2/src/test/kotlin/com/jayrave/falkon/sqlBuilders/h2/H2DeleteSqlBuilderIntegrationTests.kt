package com.jayrave.falkon.sqlBuilders.h2

import com.jayrave.falkon.sqlBuilders.test.delete.TestDelete
import org.junit.Test

class H2DeleteSqlBuilderIntegrationTests : BaseClassForTesting() {

    private val deleteSqlBuilder = H2DeleteSqlBuilder()

    @Test
    fun `delete all records`() {
        TestDelete(deleteSqlBuilder, db).`delete all records`()
    }

    @Test
    fun `delete individual record using where#eq`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#eq`()
    }

    @Test
    fun `delete individual record using where#notEq`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#notEq`()
    }

    @Test
    fun `delete individual record using where#greaterThan`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#greaterThan`()
    }

    @Test
    fun `delete individual record using where#greaterThanOrEq`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#greaterThanOrEq`()
    }

    @Test
    fun `delete individual record using where#lessThan`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#lessThan`()
    }

    @Test
    fun `delete individual record using where#lessThanOrEq`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#lessThanOrEq`()
    }

    @Test
    fun `delete individual record using where#like`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#like`()
    }

    @Test
    fun `delete individual record using where#between`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#between`()
    }

    @Test
    fun `delete individual record using where#isIn with list`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#isIn with list`()
    }

    @Test
    fun `delete individual record using where#isIn with sub query`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#isIn with sub query`()
    }

    @Test
    fun `delete individual record using where#isNotIn with list`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#isNotIn with list`()
    }

    @Test
    fun `delete individual record using where#isNotIn with sub query`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#isNotIn with sub query`()
    }

    @Test
    fun `delete individual record using where#isNull`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#isNull`()
    }

    @Test
    fun `delete individual record using where#isNotNull`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using where#isNotNull`()
    }

    @Test
    fun `delete individual record using simple where#and`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using simple where#and`()
    }

    @Test
    fun `delete individual record using simple where#or`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using simple where#or`()
    }

    @Test
    fun `delete individual record using compound where#and`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using compound where#and`()
    }

    @Test
    fun `delete individual record using compound where#or`() {
        TestDelete(deleteSqlBuilder, db).`delete individual record using compound where#or`()
    }
}