package com.jayrave.falkon.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.sql.SQLException

class DaoForInsertsIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testInsertionOfSingleModel() {
        insertAdditionalRandomModels(table, 7)
        val modelForTest = buildModelForTest(5)
        table.dao.insert(modelForTest)

        assertPresenceOf(table, modelForTest)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testInsertionOfVarargModels() {
        insertAdditionalRandomModels(table, 6)
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insert(modelForTest1, modelForTest2)

        assertPresenceOf(table, modelForTest1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testInsertionOfModelIterable() {
        insertAdditionalRandomModels(table, 6)
        val modelForTest1 = buildModelForTest(5)
        val modelForTest2 = buildModelForTest(8)
        table.dao.insert(listOf(modelForTest1, modelForTest2))

        assertPresenceOf(table, modelForTest1, modelForTest2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
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
}