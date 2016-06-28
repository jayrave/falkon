package com.jayrave.falkon.dao

import com.jayrave.falkon.Column
import com.jayrave.falkon.dao.testLib.ModelForTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLException

class DaoForInsertsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testInsertionOfSingleModel() {
        val modelForTest = buildModelForTest(5)
        table.dao.insert(modelForTest)
        assertPresenceOfOnly(modelForTest)
    }


    @Test
    fun testInsertionOfVarargModels() {
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insert(modelForTest1, modelForTest2)
        assertPresenceOfOnly(modelForTest1, modelForTest2)
    }


    @Test
    fun testInsertionOfModelIterable() {
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insert(listOf(modelForTest1, modelForTest2))
        assertPresenceOfOnly(modelForTest1, modelForTest2)
    }


    @Test
    fun testInsertionThrowsIfModelAlreadyExists() {
        val modelForTest = buildModelForTest(5)
        table.dao.insert(modelForTest)

        var exceptionCaught = false
        try {
            table.dao.insert(modelForTest)
        } catch (e: SQLException) {
            exceptionCaught = true
        }

        // Second insert must have thrown
        assertThat(exceptionCaught).isTrue()
    }


    private fun assertPresenceOfOnly(
            vararg models: ModelForTest, orderedBy: Column<ModelForTest, *> = table.short) {

        val compiledQuery = table.dao.queryBuilder().orderBy(orderedBy, true).build()
        val source = compiledQuery.execute()

        models.forEach {
            assertThat(source.moveToNext()).isTrue()
            assertCurrentRowCorrespondsTo(source, it, table)
        }

        assertThat(source.moveToNext()).isFalse()
        source.close()
        compiledQuery.close()
    }
}