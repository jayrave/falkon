package com.jayrave.falkon.sqlBuilders.sqlite

import com.jayrave.falkon.sqlBuilders.lib.JoinInfo
import com.jayrave.falkon.sqlBuilders.test.query.*
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class SqliteQuerySqlBuilderIntegrationTests : BaseClassForIntegrationTests() {

    private val querySqlBuilder = SqliteQuerySqlBuilder()

    @Test
    fun `select all columns without specifying any`() {
        TestQueryWithVariousColumnSelections(querySqlBuilder, db).`select all columns without specifying any`()
    }

    @Test
    fun `select all columns by explicitly specifying every column`() {
        TestQueryWithVariousColumnSelections(querySqlBuilder, db).`select all columns by explicitly specifying every column`()
    }

    @Test
    fun `select only a few columns by explicitly specifying it`() {
        TestQueryWithVariousColumnSelections(querySqlBuilder, db).`select only a few columns by explicitly specifying it`()
    }

    @Test
    fun `select columns by using alias`() {
        TestQueryWithVariousColumnSelections(querySqlBuilder, db).`select columns by using alias`()
    }

    @Test
    fun `select with duplicates`() {
        TestDistinct(querySqlBuilder, db).`select with duplicates`()
    }

    @Test
    fun `select distinct`() {
        TestDistinct(querySqlBuilder, db).`select distinct`()
    }

    @Test
    fun `query individual record using where#eq`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#eq`()
    }

    @Test
    fun `query individual record using where#notEq`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#notEq`()
    }

    @Test
    fun `query individual record using where#greaterThan`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#greaterThan`()
    }

    @Test
    fun `query individual record using where#greaterThanOrEq`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#greaterThanOrEq`()
    }

    @Test
    fun `query individual record using where#lessThan`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#lessThan`()
    }

    @Test
    fun `query individual record using where#lessThanOrEq`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#lessThanOrEq`()
    }

    @Test
    fun `query individual record using where#like`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#like`()
    }

    @Test
    fun `query individual record using where#between`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#between`()
    }

    @Test
    fun `query individual record using where#isIn with list`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#isIn with list`()
    }

    @Test
    fun `query individual record using where#isIn with sub query`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#isIn with sub query`()
    }

    @Test
    fun `query individual record using where#isNotIn with list`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#isNotIn with list`()
    }

    @Test
    fun `query individual record using where#isNotIn with sub query`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#isNotIn with sub query`()
    }

    @Test
    fun `query individual record using where#isNull`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#isNull`()
    }

    @Test
    fun `query individual record using where#isNotNull`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using where#isNotNull`()
    }

    @Test
    fun `query individual record using simple where#and`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using simple where#and`()
    }

    @Test
    fun `query individual record using simple where#or`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using simple where#or`()
    }

    @Test
    fun `query individual record using compound where#and`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using compound where#and`()
    }

    @Test
    fun `query individual record using compound where#or`() {
        TestQueryWithWhereConditions(querySqlBuilder, db).`query individual record using compound where#or`()
    }

    @Test
    fun `select with inner join`() {
        TestJoin(querySqlBuilder, db).`select with inner join`()
    }

    @Test
    fun `select with left outer join`() {
        TestJoin(querySqlBuilder, db).`select with left outer join`()
    }

    @Test(expected = SQLSyntaxErrorException::class)
    fun `select with right outer join throws`() {
        val joinInfo = JoinInfoForTest(JoinInfo.Type.RIGHT_OUTER_JOIN, "col", "join_table", "f_col")
        querySqlBuilder.build("test", false, null, listOf(joinInfo), null, null, null, null, null)
    }

    @Test
    fun `select with group by`() {
        TestGroupBy(querySqlBuilder, db).`select with group by`()
    }

    @Test
    fun `select with order by clause`() {
        TestOrderBy(querySqlBuilder, db).`select with order by clause`()
    }

    @Test
    fun `select with limit`() {
        TestLimit(querySqlBuilder, db).`select with limit`()
    }

    @Test
    fun `select with offset`() {
        TestOffset(querySqlBuilder, db).`select with offset`()
    }
}