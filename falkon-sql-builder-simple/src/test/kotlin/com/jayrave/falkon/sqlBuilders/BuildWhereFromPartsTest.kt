package com.jayrave.falkon.sqlBuilders

import com.jayrave.falkon.sqlBuilders.lib.WhereSection
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.CompoundConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Connector.SimpleConnector
import com.jayrave.falkon.sqlBuilders.lib.WhereSection.Predicate.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLSyntaxErrorException

class BuildWhereFromPartsTest {

    @Test
    fun testEmptyWhere() {
        val actualWhereClause = listOf<WhereSection>().buildWhereClause(ARG_PLACEHOLDER)
        assertThat(actualWhereClause).isNull()
    }


    @Test
    fun testEq() {
        val actualWhereClause = listOf(OneArgPredicate(
                OneArgPredicate.Type.EQ, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name = ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testNotEq() {
        val actualWhereClause = listOf(OneArgPredicate(
                OneArgPredicate.Type.NOT_EQ, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name != ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testGt() {
        val actualWhereClause = listOf(OneArgPredicate(
                OneArgPredicate.Type.GREATER_THAN, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name > ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testGe() {
        val actualWhereClause = listOf(OneArgPredicate(
                OneArgPredicate.Type.GREATER_THAN_OR_EQ, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name >= ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testLt() {
        val actualWhereClause = listOf(OneArgPredicate(
                OneArgPredicate.Type.LESS_THAN, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name < ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testLe() {
        val actualWhereClause = listOf(OneArgPredicate(
                OneArgPredicate.Type.LESS_THAN_OR_EQ, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name <= ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testBetween() {
        val actualWhereClause = listOf(
                BetweenPredicate("column_name")
        ).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name BETWEEN ? AND ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testLike() {
        val actualWhereClause = listOf(OneArgPredicate(
                OneArgPredicate.Type.LIKE, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name LIKE ?"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testIsNull() {
        val actualWhereClause = listOf(NoArgPredicate(
                NoArgPredicate.Type.IS_NULL, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name IS NULL"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testIsNotNull() {
        val actualWhereClause = listOf(NoArgPredicate(
                NoArgPredicate.Type.IS_NOT_NULL, "column_name"
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name IS NOT NULL"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testIsInWithNegativeNumberOfArgsThrows() {
        listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_IN, "column_name", -1
        )).buildWhereClause(ARG_PLACEHOLDER)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testIsInWithZeroArgsThrows() {
        listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_IN, "column_name", 0
        )).buildWhereClause(ARG_PLACEHOLDER)
    }


    @Test
    fun testIsInWithSingleArg() {
        val actualWhereClause = listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_IN, "column_name", 1
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name IN (?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testIsInWithMultipleArgs() {
        val actualWhereClause = listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_IN, "column_name", 3
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name IN (?, ?, ?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testIsNotInWithNegativeNumberOfArgsThrows() {
        listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_NOT_IN, "column_name", -1
        )).buildWhereClause(ARG_PLACEHOLDER)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testIsNotInWithZeroArgsThrows() {
        listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_NOT_IN, "column_name", 0
        )).buildWhereClause(ARG_PLACEHOLDER)
    }


    @Test
    fun testIsNotInWithSingleArg() {
        val actualWhereClause = listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_NOT_IN, "column_name", 1
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name NOT IN (?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testIsNotInWithMultipleArgs() {
        val actualWhereClause = listOf(MultiArgPredicate(
                MultiArgPredicate.Type.IS_NOT_IN, "column_name", 3
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE column_name NOT IN (?, ?, ?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testSimpleAnd() {
        val actualWhereClause = listOf(SimpleConnector(
                SimpleConnector.Type.AND
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE AND"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testSimpleOr() {
        val actualWhereClause = listOf(SimpleConnector(
                SimpleConnector.Type.OR
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE OR"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testCompoundAndWithNoPredicateThrows() {
        listOf(CompoundConnector(
                CompoundConnector.Type.AND, emptyList()
        )).buildWhereClause(ARG_PLACEHOLDER)
    }


    @Test(expected = SQLSyntaxErrorException::class)
    fun testCompoundOrWithNoPredicateThrows() {
        listOf(CompoundConnector(
                CompoundConnector.Type.OR, emptyList()
        )).buildWhereClause(ARG_PLACEHOLDER)
    }


    @Test
    fun testCompoundAndWithOnePredicate() {
        val actualWhereClause = listOf(CompoundConnector(
                CompoundConnector.Type.AND,
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "column_name"))
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE (column_name = ?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testCompoundOrWithOnePredicate() {
        val actualWhereClause = listOf(CompoundConnector(
                CompoundConnector.Type.OR,
                listOf(OneArgPredicate(OneArgPredicate.Type.EQ, "column_name"))
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE (column_name = ?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testCompoundAndWithMultiplePredicates() {
        val actualWhereClause = listOf(CompoundConnector(
                CompoundConnector.Type.AND,
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
                )
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE (column_name_1 = ? AND column_name_2 = ?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testCompoundOrWithMultiplePredicates() {
        val actualWhereClause = listOf(CompoundConnector(
                CompoundConnector.Type.OR,
                listOf(
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                        OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_2")
                )
        )).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause = "WHERE (column_name_1 = ? OR column_name_2 = ?)"
        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }


    @Test
    fun testWithAllPredicatesAndConnectors() {
        val actualWhereClause = listOf(
                OneArgPredicate(OneArgPredicate.Type.EQ, "column_name_1"),
                SimpleConnector(SimpleConnector.Type.OR),
                OneArgPredicate(OneArgPredicate.Type.NOT_EQ, "column_name_2"),
                SimpleConnector(SimpleConnector.Type.AND),
                OneArgPredicate(OneArgPredicate.Type.GREATER_THAN, "column_name_3"),
                SimpleConnector(SimpleConnector.Type.OR),
                OneArgPredicate(OneArgPredicate.Type.LESS_THAN_OR_EQ, "column_name_4"),
                SimpleConnector(SimpleConnector.Type.AND),
                CompoundConnector(
                        CompoundConnector.Type.OR,
                        listOf(
                                BetweenPredicate("column_name_5"),
                                OneArgPredicate(
                                        OneArgPredicate.Type.GREATER_THAN_OR_EQ, "column_name_6"
                                )
                        )
                ), SimpleConnector(SimpleConnector.Type.OR),
                CompoundConnector(
                        CompoundConnector.Type.AND,
                        listOf(
                                OneArgPredicate(OneArgPredicate.Type.LESS_THAN, "column_name_7"),
                                OneArgPredicate(OneArgPredicate.Type.LIKE, "column_name_8")
                        )
                ), SimpleConnector(SimpleConnector.Type.AND),
                NoArgPredicate(NoArgPredicate.Type.IS_NULL, "column_name_9"),
                SimpleConnector(SimpleConnector.Type.OR),
                NoArgPredicate(NoArgPredicate.Type.IS_NOT_NULL, "column_name_10")
        ).buildWhereClause(ARG_PLACEHOLDER)

        val expectedWhereClause =
                "WHERE column_name_1 = ? OR column_name_2 != ? AND column_name_3 > ? OR " +
                        "column_name_4 <= ? AND " +
                        "(column_name_5 BETWEEN ? AND ? OR column_name_6 >= ?) OR " +
                        "(column_name_7 < ? AND column_name_8 LIKE ?) AND " +
                        "column_name_9 IS NULL OR column_name_10 IS NOT NULL"

        assertThat(actualWhereClause).isEqualTo(expectedWhereClause)
    }



    companion object {
        private const val ARG_PLACEHOLDER = "?"
    }
}