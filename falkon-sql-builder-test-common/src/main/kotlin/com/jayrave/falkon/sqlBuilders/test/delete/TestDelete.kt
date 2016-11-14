package com.jayrave.falkon.sqlBuilders.test.delete

import com.jayrave.falkon.sqlBuilders.DeleteSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.sql.PreparedStatement
import java.util.*

class TestDelete(private val deleteSqlBuilder: DeleteSqlBuilder, db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        dataSource.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$ID_COLUMN_NAME ${db.stringDataType} PRIMARY KEY, " +
                        "$INT_COLUMN_NAME ${db.intDataType}, " +
                        "$STRING_COLUMN_NAME ${db.stringDataType})"
        )
    }


    fun `delete all records`() {
        val idToBeDeleted1 = randomUuid()
        val idToBeDeleted2 = randomUuid()

        // Insert records
        insertRecord(idToBeDeleted1, 5, "test 6")
        insertRecord(idToBeDeleted2, 7, "test 8")

        // Delete all records
        val sql = deleteSqlBuilder.build(TABLE_NAME, null)
        val numberOfRowsDeleted = dataSource.executeDelete(sql, {})
        assertThat(numberOfRowsDeleted).isEqualTo(2)

        // Assert records got deleted
        assertRecordsCount(0)
    }


    fun `delete individual record using where#eq`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.EQ,
                        ID_COLUMN_NAME))) {

            it.setString(1, idToBeDeleted.toString())
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#notEq`() {
        val otherId = randomUuid()
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(otherId, 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.NOT_EQ,
                        ID_COLUMN_NAME))) {

            it.setString(1, otherId.toString())
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#greaterThan`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.GREATER_THAN,
                        INT_COLUMN_NAME))) {

            it.setInt(1, 6)
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#greaterThanOrEq`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.GREATER_THAN_OR_EQ,
                        INT_COLUMN_NAME))) {

            it.setInt(1, 7)
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#lessThan`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(idToBeDeleted, 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.LESS_THAN,
                        INT_COLUMN_NAME))) {

            it.setInt(1, 6)
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#lessThanOrEq`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(idToBeDeleted, 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.LESS_THAN,
                        INT_COLUMN_NAME))) {

            it.setInt(1, 7)
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#like`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.LIKE,
                        ID_COLUMN_NAME))) {

            it.setString(1, idToBeDeleted.toString())
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#between`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.BetweenPredicate(INT_COLUMN_NAME))) {

            it.setInt(1, 6)
            it.setInt(2, 8)
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#isIn with list`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.MultiArgPredicate(
                        WhereSection.Predicate.MultiArgPredicate.Type.IS_IN,
                        ID_COLUMN_NAME, 2))) {

            it.setString(1, idToBeDeleted.toString())
            it.setString(2, randomUuid().toString())
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#isIn with sub query`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        val subQuery = "SELECT $ID_COLUMN_NAME FROM $TABLE_NAME WHERE $INT_COLUMN_NAME = 7"
        deleteIndividualRecord(
                listOf(
                        WhereSection.Predicate.MultiArgPredicateWithSubQuery(
                                WhereSection.Predicate.MultiArgPredicateWithSubQuery.Type.IS_IN,
                                ID_COLUMN_NAME, subQuery, 0
                        )
                ), {}
        )

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#isNotIn with list`() {
        val otherId = randomUuid()
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(otherId, 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(listOf(
                WhereSection.Predicate.MultiArgPredicate(
                        WhereSection.Predicate.MultiArgPredicate.Type.IS_NOT_IN,
                        ID_COLUMN_NAME, 2))) {

            it.setString(1, otherId.toString())
            it.setString(2, randomUuid().toString())
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#isNotIn with sub query`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        val subQuery = "SELECT $ID_COLUMN_NAME FROM $TABLE_NAME WHERE $INT_COLUMN_NAME = 5"
        deleteIndividualRecord(
                listOf(
                        WhereSection.Predicate.MultiArgPredicateWithSubQuery(
                                WhereSection.Predicate.MultiArgPredicateWithSubQuery.Type.IS_NOT_IN,
                                ID_COLUMN_NAME, subQuery, 0)
                ), {}
        )

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#isNull`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, null, "test 8")

        // Delete record
        deleteIndividualRecord(
                listOf(
                        WhereSection.Predicate.NoArgPredicate(
                                WhereSection.Predicate.NoArgPredicate.Type.IS_NULL,
                                INT_COLUMN_NAME)
                ), {}
        )

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using where#isNotNull`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), null, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
        deleteIndividualRecord(
                listOf(
                        WhereSection.Predicate.NoArgPredicate(
                                WhereSection.Predicate.NoArgPredicate.Type.IS_NOT_NULL,
                                INT_COLUMN_NAME)
                ), {}
        )

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using simple where#and`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
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

        deleteIndividualRecord(whereSections) {
            it.setInt(1, 7)
            it.setString(2, "test 8")
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using simple where#or`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
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

        deleteIndividualRecord(whereSections) {
            it.setInt(1, 7)
            it.setInt(2, 17)
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using compound where#and`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
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

        deleteIndividualRecord(whereSections) {
            it.setInt(1, 7)
            it.setString(2, "test 8")
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    fun `delete individual record using compound where#or`() {
        val idToBeDeleted = randomUuid()

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeDeleted, 7, "test 8")

        // Delete record
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

        deleteIndividualRecord(whereSections) {
            it.setInt(1, 7)
            it.setInt(2, 17)
        }

        // Assert record got deleted
        assertRecordsCount(1)
        assertRecordDoesNotExist(idToBeDeleted)
    }


    private fun insertRecord(id: UUID, int: Int?, string: String?) {
        dataSource.execute(
                "INSERT INTO $TABLE_NAME VALUES(${buildArgListForSql(id, int, string)})"
        )
    }


    private fun deleteIndividualRecord(
            whereSections: List<WhereSection>, argsBinder: (PreparedStatement) -> Any?) {

        val deleteSql = deleteSqlBuilder.build(TABLE_NAME, whereSections)
        val numberOfRowsDeleted = dataSource.executeDelete(deleteSql) { argsBinder.invoke(it) }
        assertThat(numberOfRowsDeleted).isEqualTo(1)
    }


    private fun assertRecordsCount(count: Int) {
        assertThat(dataSource.findRecordCountInTable(TABLE_NAME)).isEqualTo(count)
    }


    private fun assertRecordDoesNotExist(id: UUID) {
        val countColumnName = "count"
        val query = "SELECT COUNT(*) AS $countColumnName FROM $TABLE_NAME WHERE " +
                "$ID_COLUMN_NAME = '$id'"

        val count = dataSource.executeQuery(query, {}) {
            it.getInt(it.findColumn(countColumnName))
        }

        assertThat(count).isEqualTo(0)
    }



    companion object {
        private const val TABLE_NAME = "test"
        private const val ID_COLUMN_NAME = "id"
        private const val INT_COLUMN_NAME = "int"
        private const val STRING_COLUMN_NAME = "string"
    }
}