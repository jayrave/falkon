package com.jayrave.falkon.sqlBuilders.test.update

import com.jayrave.falkon.sqlBuilders.UpdateSqlBuilder
import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.test.*
import org.assertj.core.api.Assertions.assertThat
import java.sql.PreparedStatement
import java.util.*

class TestUpdate(private val updateSqlBuilder: UpdateSqlBuilder, db: DbForTest) {

    private val dataSource = db.dataSource
    init {
        dataSource.execute(
                "CREATE TABLE $TABLE_NAME (" +
                        "$ID_COLUMN_NAME ${db.stringDataType} PRIMARY KEY, " +
                        "$INT_COLUMN_NAME ${db.intDataType}, " +
                        "$STRING_COLUMN_NAME ${db.stringDataType})"
        )
    }


    fun `update all records`() {
        val idToBeUpdated1 = randomUuid()
        val idToBeUpdated2 = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(idToBeUpdated1, 5, "test 6")
        insertRecord(idToBeUpdated2, 7, "test 8")

        // Update all records
        val sql = updateSqlBuilder.build(
                TABLE_NAME, listOf(INT_COLUMN_NAME, STRING_COLUMN_NAME), null
        )

        val numberOfRowsUpdated = dataSource.executeUpdate(sql) {
            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
        }

        assertThat(numberOfRowsUpdated).isEqualTo(2)

        // Assert record got updated
        assertRecord(idToBeUpdated1, intValueAfterUpdate, stringValueAfterUpdate)
        assertRecord(idToBeUpdated2, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#eq`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.EQ,
                        ID_COLUMN_NAME
                ))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setString(3, idToBeUpdated.toString())
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#notEq`() {
        val otherId = randomUuid()
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(otherId, 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.NOT_EQ,
                        ID_COLUMN_NAME
                ))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setString(3, otherId.toString())
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#greaterThan`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.GREATER_THAN,
                        INT_COLUMN_NAME
                ))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 6)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#greaterThanOrEq`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.GREATER_THAN_OR_EQ,
                        INT_COLUMN_NAME
                ))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 7)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#lessThan`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(idToBeUpdated, 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.LESS_THAN,
                        INT_COLUMN_NAME
                ))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 6)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#lessThanOrEq`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(idToBeUpdated, 5, "test 6")
        insertRecord(randomUuid(), 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.LESS_THAN,
                        INT_COLUMN_NAME
                ))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 7)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#like`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.OneArgPredicate(
                        WhereSection.Predicate.OneArgPredicate.Type.LIKE,
                        ID_COLUMN_NAME
                ))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setString(3, idToBeUpdated.toString())
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#between`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.BetweenPredicate(INT_COLUMN_NAME))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 6)
            it.setInt(4, 8)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#isIn with list`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.MultiArgPredicate(
                        WhereSection.Predicate.MultiArgPredicate.Type.IS_IN,
                        ID_COLUMN_NAME, 2))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setString(3, idToBeUpdated.toString())
            it.setString(4, randomUuid().toString())
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#isIn with sub query`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        val subQuery = "SELECT $ID_COLUMN_NAME FROM $TABLE_NAME WHERE $INT_COLUMN_NAME = 7"
        updateIndividualRecord(listOf(
                WhereSection.Predicate.MultiArgPredicateWithSubQuery(
                        WhereSection.Predicate.MultiArgPredicateWithSubQuery.Type.IS_IN,
                        ID_COLUMN_NAME, subQuery, 0))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#isNotIn with list`() {
        val otherId = randomUuid()
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(otherId, 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.MultiArgPredicate(
                        WhereSection.Predicate.MultiArgPredicate.Type.IS_NOT_IN,
                        ID_COLUMN_NAME, 2))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setString(3, otherId.toString())
            it.setString(4, randomUuid().toString())
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#isNotIn with sub query`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        val subQuery = "SELECT $ID_COLUMN_NAME FROM $TABLE_NAME WHERE $INT_COLUMN_NAME = 5"
        updateIndividualRecord(listOf(
                WhereSection.Predicate.MultiArgPredicateWithSubQuery(
                        WhereSection.Predicate.MultiArgPredicateWithSubQuery.Type.IS_NOT_IN,
                        ID_COLUMN_NAME, subQuery, 0))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#isNull`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, null, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.NoArgPredicate(
                        WhereSection.Predicate.NoArgPredicate.Type.IS_NULL,
                        INT_COLUMN_NAME))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using where#isNotNull`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), null, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
        updateIndividualRecord(listOf(
                WhereSection.Predicate.NoArgPredicate(
                        WhereSection.Predicate.NoArgPredicate.Type.IS_NOT_NULL,
                        INT_COLUMN_NAME))) {

            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using simple where#and`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
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

        updateIndividualRecord(whereSections) {
            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 7)
            it.setString(4, "test 8")
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using simple where#or`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
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

        updateIndividualRecord(whereSections) {
            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 7)
            it.setInt(4, 17)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using compound where#and`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
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

        updateIndividualRecord(whereSections) {
            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 7)
            it.setString(4, "test 8")
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    fun `update individual record using compound where#or`() {
        val idToBeUpdated = randomUuid()
        val intValueAfterUpdate = 42
        val stringValueAfterUpdate = "test 42"

        // Insert records
        insertRecord(randomUuid(), 5, "test 6")
        insertRecord(idToBeUpdated, 7, "test 8")

        // Update record
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

        updateIndividualRecord(whereSections) {
            it.setInt(1, intValueAfterUpdate)
            it.setString(2, stringValueAfterUpdate)
            it.setInt(3, 7)
            it.setInt(4, 17)
        }

        // Assert record got updated
        assertRecord(idToBeUpdated, intValueAfterUpdate, stringValueAfterUpdate)
    }


    private fun insertRecord(id: UUID, int: Int?, string: String?) {
        dataSource.execute(
                "INSERT INTO $TABLE_NAME VALUES(${buildArgListForSql(id, int, string)})"
        )
    }


    private fun updateIndividualRecord(
            whereSections: List<WhereSection>, argsBinder: (PreparedStatement) -> Any?) {

        val updateSql = updateSqlBuilder.build(
                TABLE_NAME, listOf(INT_COLUMN_NAME, STRING_COLUMN_NAME), whereSections
        )

        val numberOfRowsUpdated = dataSource.executeUpdate(updateSql) { argsBinder.invoke(it) }
        assertThat(numberOfRowsUpdated).isEqualTo(1)
    }


    private fun assertRecord(id: UUID, int: Int, string: String) {
        val countColumnName = "count"
        val query = "SELECT COUNT(*) AS $countColumnName FROM $TABLE_NAME WHERE " +
                "$ID_COLUMN_NAME = '$id' AND " +
                "$INT_COLUMN_NAME = $int AND " +
                "$STRING_COLUMN_NAME = '$string'"

        val count = dataSource.executeQuery(query, {}) {
            it.getInt(it.findColumn(countColumnName))
        }

        assertThat(count).isEqualTo(1)
    }



    companion object {
        private const val TABLE_NAME = "test"
        private const val ID_COLUMN_NAME = "id"
        private const val INT_COLUMN_NAME = "int"
        private const val STRING_COLUMN_NAME = "string"
    }
}