package com.jayrave.falkon.sqlBuilders.test.query

import com.jayrave.falkon.sqlBuilders.QuerySqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.sql.PreparedStatement
import java.util.*

class TestQueryWithWhereConditions(private val querySqlBuilder: QuerySqlBuilder, db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        dataSource.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$ID_COLUMN_NAME ${db.stringDataType} PRIMARY KEY, " +
                        "$INT_COLUMN_NAME ${db.intDataType}, " +
                        "$STRING_COLUMN_NAME ${db.stringDataType})"
        )
    }


    fun `query individual record using where#eq`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.OneArgPredicate(
                WhereSection.Predicate.OneArgPredicate.Type.EQ, ID_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setString(1, idToBeQueried.toString())
        }
    }


    fun `query individual record using where#notEq`() {
        val otherId = randomUuid()
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(otherId, 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.OneArgPredicate(
                WhereSection.Predicate.OneArgPredicate.Type.NOT_EQ, ID_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setString(1, otherId.toString())
        }
    }


    fun `query individual record using where#greaterThan`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.OneArgPredicate(
                WhereSection.Predicate.OneArgPredicate.Type.GREATER_THAN, INT_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, 40)
        }
    }


    fun `query individual record using where#greaterThanOrEq`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.OneArgPredicate(
                WhereSection.Predicate.OneArgPredicate.Type.GREATER_THAN_OR_EQ, INT_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, intValueFromQuery)
        }
    }


    fun `query individual record using where#lessThan`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)
        insertRecord(randomUuid(), 99, "test 8")

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.OneArgPredicate(
                WhereSection.Predicate.OneArgPredicate.Type.LESS_THAN, INT_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, 44)
        }
    }


    fun `query individual record using where#lessThanOrEq`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)
        insertRecord(randomUuid(), 99, "test 8")

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.OneArgPredicate(
                WhereSection.Predicate.OneArgPredicate.Type.LESS_THAN_OR_EQ, INT_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, intValueFromQuery)
        }
    }


    fun `query individual record using where#like`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.OneArgPredicate(
                WhereSection.Predicate.OneArgPredicate.Type.LIKE, ID_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setString(1, idToBeQueried.toString())
        }
    }


    fun `query individual record using where#between`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery,
                listOf(WhereSection.Predicate.BetweenPredicate(INT_COLUMN_NAME))) {
            it.setInt(1, 40)
            it.setInt(2, 44)
        }
    }


    fun `query individual record using where#isIn with list`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.MultiArgPredicate(
                WhereSection.Predicate.MultiArgPredicate.Type.IS_IN, ID_COLUMN_NAME, 2
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setString(1, idToBeQueried.toString())
            it.setString(2, randomUuid().toString())
        }
    }


    fun `query individual record using where#isIn with sub query`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val subQuery = "SELECT $ID_COLUMN_NAME FROM $TABLE_NAME WHERE " +
                "$INT_COLUMN_NAME = $intValueFromQuery"

        val whereSections = listOf(WhereSection.Predicate.MultiArgPredicateWithSubQuery(
                WhereSection.Predicate.MultiArgPredicateWithSubQuery.Type.IS_IN,
                ID_COLUMN_NAME, subQuery, 0
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections, {}
        )
    }


    fun `query individual record using where#isNotIn with list`() {
        val otherId = randomUuid()
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(otherId, 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.MultiArgPredicate(
                WhereSection.Predicate.MultiArgPredicate.Type.IS_NOT_IN, ID_COLUMN_NAME, 2
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setString(1, otherId.toString())
            it.setString(2, randomUuid().toString())
        }
    }


    fun `query individual record using where#isNotIn with sub query`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val subQuery = "SELECT $ID_COLUMN_NAME FROM $TABLE_NAME WHERE $INT_COLUMN_NAME = 5"
        val whereSections = listOf(WhereSection.Predicate.MultiArgPredicateWithSubQuery(
                WhereSection.Predicate.MultiArgPredicateWithSubQuery.Type.IS_NOT_IN,
                ID_COLUMN_NAME, subQuery, 0
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections, {}
        )
    }


    fun `query individual record using where#isNull`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = null
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.NoArgPredicate(
                WhereSection.Predicate.NoArgPredicate.Type.IS_NULL, INT_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections, {}
        )
    }


    fun `query individual record using where#isNotNull`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), null, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(WhereSection.Predicate.NoArgPredicate(
                WhereSection.Predicate.NoArgPredicate.Type.IS_NOT_NULL, INT_COLUMN_NAME
        ))

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections, {}
        )
    }


    fun `query individual record using simple where#and`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.EQ, INT_COLUMN_NAME
                ),

                WhereSection.Connector.SimpleConnector(
                        WhereSection.Connector.SimpleConnector.Type.AND
                ),

                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.EQ, STRING_COLUMN_NAME
                )
        )

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, intValueFromQuery)
            it.setString(2, stringValueFromQuery)
        }
    }


    fun `query individual record using simple where#or`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.EQ, INT_COLUMN_NAME
                ),

                WhereSection.Connector.SimpleConnector(
                        WhereSection.Connector.SimpleConnector.Type.OR
                ),

                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.EQ, INT_COLUMN_NAME
                )
        )

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, intValueFromQuery)
            it.setInt(2, 17)
        }
    }


    fun `query individual record using compound where#and`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(
                WhereSection.Connector.CompoundConnector(
                        WhereSection.Connector.CompoundConnector.Type.AND,
                        listOf(
                                WhereSection.Predicate.OneArgPredicate(
                                        WhereSection.Predicate.OneArgPredicate.Type.EQ,
                                        INT_COLUMN_NAME
                                ),

                                WhereSection.Predicate.OneArgPredicate(
                                        WhereSection.Predicate.OneArgPredicate.Type.EQ,
                                        STRING_COLUMN_NAME
                                )
                        )
                )
        )

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, intValueFromQuery)
            it.setString(2, stringValueFromQuery)
        }
    }


    fun `query individual record using compound where#or`() {
        val idToBeQueried = randomUuid()
        val intValueFromQuery = 42
        val stringValueFromQuery = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeQueried, intValueFromQuery, stringValueFromQuery)

        // Assert record is returned by the query
        val whereSections = listOf(
                WhereSection.Connector.CompoundConnector(
                        WhereSection.Connector.CompoundConnector.Type.OR,
                        listOf(
                                WhereSection.Predicate.OneArgPredicate(
                                        WhereSection.Predicate.OneArgPredicate.Type.EQ,
                                        INT_COLUMN_NAME
                                ),

                                WhereSection.Predicate.OneArgPredicate(
                                        WhereSection.Predicate.OneArgPredicate.Type.EQ,
                                        INT_COLUMN_NAME
                                )
                        )
                )
        )

        assertOnlyOneRecordIsInQueryResult(
                idToBeQueried, intValueFromQuery, stringValueFromQuery, whereSections) {
            it.setInt(1, intValueFromQuery)
            it.setInt(2, 17)
        }
    }


    private fun insertRecord(id: UUID, int: Int?, string: String?) {
        dataSource.execute(
                "INSERT INTO $TABLE_NAME VALUES(${buildArgListForSql(id, int, string)})"
        )
    }


    private fun assertOnlyOneRecordIsInQueryResult(
            id: UUID, int: Int?, string: String?, whereSections: List<WhereSection>,
            argsBinder: (PreparedStatement) -> Any?) {

        val columnNames = listOf(ID_COLUMN_NAME, INT_COLUMN_NAME, STRING_COLUMN_NAME)
        val selectColumnInfoList = columnNames.map { SelectColumnInfoForTest(it, null) }

        val sql = querySqlBuilder.build(
                TABLE_NAME, false, selectColumnInfoList, null, whereSections, null,
                null, null, null
        )

        val records = dataSource.executeQuery(sql, argsBinder) {
            it.extractRecordsAsMap(columnNames)
        }

        assertThat(records).hasSize(1)
        assertThat(records.first()[ID_COLUMN_NAME]).isEqualTo(id.toString())
        assertThat(records.first()[INT_COLUMN_NAME]).isEqualTo(int?.toString())
        assertThat(records.first()[STRING_COLUMN_NAME]).isEqualTo(string)
    }



    companion object {
        private const val TABLE_NAME = "test"
        private const val ID_COLUMN_NAME = "id"
        private const val INT_COLUMN_NAME = "int"
        private const val STRING_COLUMN_NAME = "string"
    }
}