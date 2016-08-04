package com.jayrave.falkon.dao

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class DaoForUpdatesIntegrationTests : BaseClassForIntegrationTests() {

    @Test
    fun testUpdateOfSingleModel() {
        val modelToBeUpdated = buildModelForTest(1)
        insertModelUsingInsertBuilder(table, modelToBeUpdated)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 7)

        val updatedModel = buildModelForTest(88, modelToBeUpdated.id)
        val numberOfRowsUpdated = table.dao.update(updatedModel)

        assertPresenceOf(table, updatedModel)
        assertThat(numberOfRowsUpdated).isEqualTo(1)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testUpdateOfVarargModels() {
        val modelToBeUpdated1 = buildModelForTest(1)
        val modelToBeUpdated2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeUpdated1, modelToBeUpdated2)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 6)

        val updatedModel1 = buildModelForTest(66, modelToBeUpdated1.id)
        val updatedModel2 = buildModelForTest(99, modelToBeUpdated2.id)
        val numberOfRowsUpdated = table.dao.update(updatedModel1, updatedModel2)

        assertPresenceOf(table, updatedModel1, updatedModel2)
        assertThat(numberOfRowsUpdated).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testUpdateOfModelIterable() {
        val modelToBeUpdated1 = buildModelForTest(1)
        val modelToBeUpdated2 = buildModelForTest(2)
        insertModelsUsingInsertBuilder(table, modelToBeUpdated1, modelToBeUpdated2)
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 6)

        val updatedModel1 = buildModelForTest(55, modelToBeUpdated1.id)
        val updatedModel2 = buildModelForTest(77, modelToBeUpdated2.id)
        val numberOfRowsUpdated = table.dao.update(listOf(updatedModel1, updatedModel2))

        assertPresenceOf(table, updatedModel1, updatedModel2)
        assertThat(numberOfRowsUpdated).isEqualTo(2)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }


    @Test
    fun testUpdateHasNoEffectIfModelDoesNotExist() {
        insertAdditionalRandomModelsUsingInsertBuilder(table, count = 8)
        val nonExistingModel = buildModelForTest(1)
        val numberOfRowsUpdated = table.dao.update(nonExistingModel)

        assertAbsenceOf(table, nonExistingModel)
        assertThat(numberOfRowsUpdated).isEqualTo(0)
        assertThat(getNumberOfModelsInTableForTest(table)).isEqualTo(8)
    }
}